package org.firstinspires.ftc.teamcode.training.guide;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Libraries.pedroPathing.Constants;

/**
 * מדריך Pedro — דוגמה 4: מכונת מצבים (FSM) עם מנגנון.
 * נוסעים לאיסוף -> סוגרים תופסן -> מרימים מעלית -> נוסעים לניקוד -> פותחים -> מורידים וחונים.
 *
 * חומרה (אותם שמות כמו בשיעורי הפרויקט): מנוע "lift", סרבו "claw".
 * ⚠️ כל הקבועים של המנגנון הם ערכי התחלה זהירים. מדדו וכיילו לרובוט שלכם!
 */
@Disabled
@Autonomous(name = "Guide 4 - State Machine + Mechanism", group = "Pedro Guide")
public class GuideAuto4StateMachine extends OpMode {

    // ---- קבועי מנגנון (לכייל!) ----
    private static final int    LIFT_DOWN_TICKS   = 0;
    private static final int    LIFT_SCORE_TICKS  = 300;   // החליפו בגובה שמדדתם
    private static final int    LIFT_TOLERANCE    = 15;    // "קרוב מספיק" בטיקים
    private static final double LIFT_POWER        = 0.6;
    private static final double LIFT_TIMEOUT_SEC  = 2.0;   // רשת ביטחון אם המעלית לא מגיעה
    private static final double CLAW_OPEN         = 0.55;  // טווח זהיר כמו ב־ServoTuning
    private static final double CLAW_CLOSED       = 0.45;
    private static final double CLAW_MOVE_SEC     = 0.5;   // זמן שהסרבו צריך כדי לזוז

    // ---- נקודות ----
    private final Pose startPose  = new Pose(8, 72, Math.toRadians(0));
    private final Pose pickupPose = new Pose(30, 72, Math.toRadians(0));
    private final Pose scorePose  = new Pose(30, 110, Math.toRadians(90));
    private final Pose parkPose   = new Pose(12, 110, Math.toRadians(90));

    // ---- המצבים של האוטונומי ----
    private enum State {
        DRIVE_TO_PICKUP, WAIT_AT_PICKUP, GRAB, LIFT_UP,
        DRIVE_TO_SCORE, RELEASE, LOWER_AND_PARK, DONE
    }

    private Follower follower;
    private DcMotorEx lift;
    private Servo claw;
    private PathChain toPickup, toScore, toPark;
    private Timer stateTimer, opModeTimer;
    private State state;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);

        lift = hardwareMap.get(DcMotorEx.class, "lift");
        lift.setDirection(DcMotorSimple.Direction.FORWARD);      // בדקו כיוון!
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);  // המעלית חייבת להתחיל למטה
        lift.setTargetPosition(LIFT_DOWN_TICKS);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(0);                                      // לא זזים לפני START

        claw = hardwareMap.get(Servo.class, "claw");

        stateTimer = new Timer();
        opModeTimer = new Timer();
        buildPaths();
    }

    private void buildPaths() {
        toPickup = follower.pathBuilder()
                .addPath(new BezierLine(startPose, pickupPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), pickupPose.getHeading())
                .build();
        toScore = follower.pathBuilder()
                .addPath(new BezierLine(pickupPose, scorePose))
                .setLinearHeadingInterpolation(pickupPose.getHeading(), scorePose.getHeading())
                .build();
        toPark = follower.pathBuilder()
                .addPath(new BezierLine(scorePose, parkPose))
                .setConstantHeadingInterpolation(parkPose.getHeading())
                .build();
    }

    @Override
    public void init_loop() {
        follower.update();
        telemetry.addLine("Guide 4 ready. Lift must start at the bottom!");
        telemetry.addData("Lift ticks", lift.getCurrentPosition());
        telemetry.update();
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        claw.setPosition(CLAW_OPEN);
        lift.setPower(LIFT_POWER);
        setState(State.DRIVE_TO_PICKUP);
    }

    @Override
    public void loop() {
        follower.update();
        updateStateMachine();

        telemetry.addData("State", state);
        telemetry.addData("State time [s]", "%.1f", stateTimer.getElapsedTimeSeconds());
        telemetry.addData("Lift ticks / target", "%d / %d",
                lift.getCurrentPosition(), lift.getTargetPosition());
        Pose p = follower.getPose();
        telemetry.addData("Pose", "(%.1f, %.1f) %.0f°", p.getX(), p.getY(), Math.toDegrees(p.getHeading()));
        telemetry.update();
    }

    private void updateStateMachine() {
        switch (state) {
            case DRIVE_TO_PICKUP:
                follower.followPath(toPickup, true);
                setState(State.WAIT_AT_PICKUP);
                break;

            case WAIT_AT_PICKUP:
                if (!follower.isBusy()) {
                    claw.setPosition(CLAW_CLOSED);       // תופסים
                    setState(State.GRAB);
                }
                break;

            case GRAB:
                if (stateTimer.getElapsedTimeSeconds() > CLAW_MOVE_SEC) {
                    lift.setTargetPosition(LIFT_SCORE_TICKS); // מרימים
                    setState(State.LIFT_UP);
                }
                break;

            case LIFT_UP:
                // ממשיכים כשהמעלית הגיעה — או אחרי Timeout, כדי לא להיתקע לנצח
                if (liftAtTarget() || stateTimer.getElapsedTimeSeconds() > LIFT_TIMEOUT_SEC) {
                    follower.followPath(toScore, true);
                    setState(State.DRIVE_TO_SCORE);
                }
                break;

            case DRIVE_TO_SCORE:
                if (!follower.isBusy()) {
                    claw.setPosition(CLAW_OPEN);         // משחררים
                    setState(State.RELEASE);
                }
                break;

            case RELEASE:
                if (stateTimer.getElapsedTimeSeconds() > CLAW_MOVE_SEC) {
                    lift.setTargetPosition(LIFT_DOWN_TICKS);
                    follower.followPath(toPark, true);   // מורידים וחונים במקביל
                    setState(State.LOWER_AND_PARK);
                }
                break;

            case LOWER_AND_PARK:
                if (!follower.isBusy() && (liftAtTarget()
                        || stateTimer.getElapsedTimeSeconds() > LIFT_TIMEOUT_SEC)) {
                    setState(State.DONE);
                }
                break;

            case DONE:
            default:
                break;
        }
    }

    private boolean liftAtTarget() {
        return Math.abs(lift.getCurrentPosition() - lift.getTargetPosition()) <= LIFT_TOLERANCE;
    }

    private void setState(State newState) {
        state = newState;
        stateTimer.resetTimer();
    }

    @Override
    public void stop() {
        follower.breakFollowing();
        lift.setPower(0);
    }
}
