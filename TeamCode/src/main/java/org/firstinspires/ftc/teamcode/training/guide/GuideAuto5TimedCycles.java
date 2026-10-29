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

import org.firstinspires.ftc.teamcode.Libraries.pedroPathing.Constants;

/**
 * מדריך Pedro — דוגמה 5: אוטונומי מורכב עם תזמונים ותנאי יציאה.
 * - פעולה אסינכרונית: ה־intake נדלק באמצע הנסיעה (Parametric Callback).
 * - Threshold: ממשיכים כשהרובוט "קרוב מספיק", בלי לחכות לעצירה מלאה.
 * - Timeouts: אם נסיעה לוקחת יותר מדי זמן או שהרובוט תקוע — מוותרים וממשיכים.
 * - מסלול דינמי: כל מסלול נבנה בזמן ריצה מהמיקום האמיתי של הרובוט.
 * - שעון כללי: כשמתקרבים לסוף 30 השניות — עוזבים הכל ונוסעים לחנייה.
 *
 * חומרה: מנועים "intake" ו־"transfer" (כמו בשיעור Command Groups).
 */
@Disabled
@Autonomous(name = "Guide 5 - Timed Cycles", group = "Pedro Guide")
public class GuideAuto5TimedCycles extends OpMode {

    // ---- זמנים (שניות) ----
    private static final double PATH_TIMEOUT_SEC     = 4.0;  // נסיעה אחת לא אמורה לקחת יותר
    private static final double COLLECT_SEC          = 1.0;  // כמה זמן אוספים בנקודה
    private static final double SCORE_SEC            = 0.8;  // כמה זמן מפעילים transfer
    private static final double LAST_CYCLE_START_SEC = 18.0; // אחרי זה לא מתחילים מחזור חדש
    private static final double PARK_DEADLINE_SEC    = 25.0; // אחרי זה — ישר לחנייה

    // ---- ספים ועוצמות ----
    private static final double ARRIVE_THRESHOLD_IN  = 2.0;  // "הגענו" אם המרחק קטן מזה (אינץ')
    private static final double MIN_PATH_LENGTH_IN   = 1.0;  // לא בונים מסלול קצר מזה
    private static final double INTAKE_POWER         = 1.0;
    private static final double TRANSFER_POWER       = 1.0;

    // ---- נקודות ----
    private final Pose startPose = new Pose(8, 72, Math.toRadians(0));
    private final Pose scorePose = new Pose(14, 72, Math.toRadians(0));
    private final Pose parkPose  = new Pose(40, 72, Math.toRadians(0));
    private final Pose[] collectPoses = {
            new Pose(36, 96, Math.toRadians(0)),
            new Pose(36, 48, Math.toRadians(0)),
    };

    private enum State {
        START_CYCLE, DRIVING_TO_COLLECT, COLLECTING,
        GO_SCORE, DRIVING_TO_SCORE, SCORING,
        GO_PARK, PARKING, DONE
    }

    private Follower follower;
    private DcMotor intake, transfer;
    private Timer stateTimer, opModeTimer;
    private State state;
    private int cycle;
    private int timeouts;          // כמה פעמים ויתרנו — מצוין לדיבאגינג
    private Pose currentTarget;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);

        intake = hardwareMap.get(DcMotor.class, "intake");
        transfer = hardwareMap.get(DcMotor.class, "transfer");
        intake.setPower(0);
        transfer.setPower(0);

        stateTimer = new Timer();
        opModeTimer = new Timer();
    }

    @Override
    public void init_loop() {
        follower.update();
        telemetry.addLine("Guide 5 ready. Press START.");
        telemetry.update();
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        cycle = 0;
        timeouts = 0;
        setState(State.START_CYCLE);
    }

    @Override
    public void loop() {
        follower.update();

        // שעון כללי: מעל הכל. לא משנה איפה אנחנו — כשהזמן נגמר, חונים.
        if (opModeTimer.getElapsedTimeSeconds() > PARK_DEADLINE_SEC
                && state != State.GO_PARK && state != State.PARKING && state != State.DONE) {
            stopMechanisms();
            setState(State.GO_PARK);
        }

        updateStateMachine();

        telemetry.addData("State", state);
        telemetry.addData("Cycle", "%d / %d", cycle, collectPoses.length);
        telemetry.addData("Timeouts", timeouts);
        telemetry.addData("Auto time [s]", "%.1f", opModeTimer.getElapsedTimeSeconds());
        telemetry.addData("State time [s]", "%.1f", stateTimer.getElapsedTimeSeconds());
        Pose p = follower.getPose();
        telemetry.addData("Pose", "(%.1f, %.1f) %.0f°", p.getX(), p.getY(), Math.toDegrees(p.getHeading()));
        if (currentTarget != null) {
            telemetry.addData("Distance to target [in]", "%.1f", p.distanceFrom(currentTarget));
        }
        telemetry.update();
    }

    private void updateStateMachine() {
        switch (state) {
            case START_CYCLE: {
                currentTarget = collectPoses[cycle];
                // ה־intake נדלק כשעוברים 50% מהמסלול — בזמן שהרובוט עדיין נוסע
                PathChain path = pathFromHereTo(currentTarget, 0.5,
                        () -> intake.setPower(INTAKE_POWER));
                if (path != null) follower.followPath(path, true);
                else intake.setPower(INTAKE_POWER); // כבר שם
                setState(State.DRIVING_TO_COLLECT);
                break;
            }

            case DRIVING_TO_COLLECT:
                if (arrived(currentTarget)) {
                    intake.setPower(INTAKE_POWER);   // ליתר ביטחון, אם עקפנו את ה־Callback
                    setState(State.COLLECTING);
                } else if (timedOut()) {
                    timeouts++;
                    follower.breakFollowing();
                    intake.setPower(0);
                    setState(State.GO_SCORE);         // מוותרים על האיסוף, ממשיכים הלאה
                }
                break;

            case COLLECTING:
                if (stateTimer.getElapsedTimeSeconds() > COLLECT_SEC) {
                    intake.setPower(0);
                    setState(State.GO_SCORE);
                }
                break;

            case GO_SCORE: {
                currentTarget = scorePose;
                PathChain path = pathFromHereTo(scorePose, -1, null);
                if (path != null) follower.followPath(path, true);
                setState(State.DRIVING_TO_SCORE);
                break;
            }

            case DRIVING_TO_SCORE:
                if (!follower.isBusy() || timedOut()) {
                    if (follower.isBusy()) {          // יצאנו בגלל Timeout
                        timeouts++;
                        follower.breakFollowing();
                    }
                    transfer.setPower(TRANSFER_POWER);
                    setState(State.SCORING);
                }
                break;

            case SCORING:
                if (stateTimer.getElapsedTimeSeconds() > SCORE_SEC) {
                    transfer.setPower(0);
                    cycle++;
                    boolean morePoses = cycle < collectPoses.length;
                    boolean enoughTime = opModeTimer.getElapsedTimeSeconds() < LAST_CYCLE_START_SEC;
                    setState(morePoses && enoughTime ? State.START_CYCLE : State.GO_PARK);
                }
                break;

            case GO_PARK: {
                currentTarget = parkPose;
                PathChain path = pathFromHereTo(parkPose, -1, null);
                if (path != null) follower.followPath(path, true);
                setState(State.PARKING);
                break;
            }

            case PARKING:
                if (!follower.isBusy()) setState(State.DONE);
                break;

            case DONE:
            default:
                break;
        }
    }

    /**
     * בונה מסלול ישר מהמיקום הנוכחי אל target.
     * callbackT בין 0 ל־1 מפעיל את action באמצע הדרך; ערך שלילי = בלי Callback.
     * מחזיר null אם הרובוט כבר כמעט ביעד (מסלול באורך 0 עלול לגרום לשגיאות).
     */
    private PathChain pathFromHereTo(Pose target, double callbackT, Runnable action) {
        Pose here = follower.getPose();
        if (here.distanceFrom(target) < MIN_PATH_LENGTH_IN) return null;

        if (callbackT >= 0 && action != null) {
            return follower.pathBuilder()
                    .addPath(new BezierLine(here, target))
                    .setLinearHeadingInterpolation(here.getHeading(), target.getHeading())
                    .addParametricCallback(callbackT, action)
                    .build();
        }
        return follower.pathBuilder()
                .addPath(new BezierLine(here, target))
                .setLinearHeadingInterpolation(here.getHeading(), target.getHeading())
                .build();
    }

    /** הגענו = Pedro סיים, או שאנחנו כבר בתוך סף המרחק. */
    private boolean arrived(Pose target) {
        return !follower.isBusy()
                || follower.getPose().distanceFrom(target) < ARRIVE_THRESHOLD_IN;
    }

    /** Timeout = המצב נמשך יותר מדי, או ש־Pedro מזהה שהרובוט תקוע. */
    private boolean timedOut() {
        return stateTimer.getElapsedTimeSeconds() > PATH_TIMEOUT_SEC || follower.isRobotStuck();
    }

    private void stopMechanisms() {
        intake.setPower(0);
        transfer.setPower(0);
    }

    private void setState(State newState) {
        state = newState;
        stateTimer.resetTimer();
    }

    @Override
    public void stop() {
        follower.breakFollowing();
        stopMechanisms();
    }
}
