package org.firstinspires.ftc.teamcode.training.guide;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Libraries.pedroPathing.Constants;

/**
 * מדריך Pedro — דוגמה 2: שליטה בזווית (Heading Control).
 * שלושה קטעים, ובכל אחד שיטה אחרת לקביעת הזווית:
 *   A. Linear  — הזווית משתנה בהדרגה מ־0° ל־90° במהלך הנסיעה.
 *   B. Constant — הרובוט שומר על 90° ונוסע הצידה (מאקאנום).
 *   C. Tangent + Reversed — הרובוט נוסע "ברוורס", הגב שלו פונה לכיוון התנועה.
 */
@Disabled
@Autonomous(name = "Guide 2 - Heading Control", group = "Pedro Guide")
public class GuideAuto2HeadingControl extends OpMode {

    private static final double PAUSE_SECONDS = 0.5; // הפסקה קצרה בין קטעים, כדי שיהיה קל לראות

    private final Pose startPose = new Pose(8, 72, Math.toRadians(0));
    private final Pose turnPose  = new Pose(32, 96, Math.toRadians(90));
    private final Pose sidePose  = new Pose(56, 96, Math.toRadians(90));
    private final Pose backPose  = new Pose(56, 72, Math.toRadians(90));

    private Follower follower;
    private PathChain linearTurn, constantStrafe, reverseTangent;
    private Timer pathTimer, opModeTimer;
    private int pathState;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);
        pathTimer = new Timer();
        opModeTimer = new Timer();
        buildPaths();
    }

    private void buildPaths() {
        // A: נוסעים באלכסון ומסתובבים בהדרגה מ־0° ל־90°
        linearTurn = follower.pathBuilder()
                .addPath(new BezierLine(startPose, turnPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), turnPose.getHeading())
                .build();

        // B: נוסעים ימינה (בכיוון +X) אבל החזית נשארת על 90°
        constantStrafe = follower.pathBuilder()
                .addPath(new BezierLine(turnPose, sidePose))
                .setConstantHeadingInterpolation(sidePose.getHeading())
                .build();

        // C: נוסעים "למטה" (בכיוון -Y). Tangent לבד היה מסובב את החזית ל־(-90°).
        //    setReversed הופך אותה, כך שהרובוט נשאר על 90° ונוסע אחורה.
        reverseTangent = follower.pathBuilder()
                .addPath(new BezierLine(sidePose, backPose))
                .setTangentHeadingInterpolation()
                .setReversed()
                .build();
    }

    @Override
    public void init_loop() {
        follower.update();
        telemetry.addLine("Guide 2 ready. Press START.");
        addPoseTelemetry();
        telemetry.update();
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        setPathState(0);
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("State", pathState);
        telemetry.addData("Time [s]", "%.1f", opModeTimer.getElapsedTimeSeconds());
        addPoseTelemetry();
        telemetry.update();
    }

    private void autonomousPathUpdate() {
        switch (pathState) {
            case 0: // קטע A
                follower.followPath(linearTurn, true);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) setPathState(2);
                break;
            case 2: // הפסקה, ואז קטע B
                if (pathTimer.getElapsedTimeSeconds() > PAUSE_SECONDS) {
                    follower.followPath(constantStrafe, true);
                    setPathState(3);
                }
                break;
            case 3:
                if (!follower.isBusy()) setPathState(4);
                break;
            case 4: // הפסקה, ואז קטע C
                if (pathTimer.getElapsedTimeSeconds() > PAUSE_SECONDS) {
                    follower.followPath(reverseTangent, true);
                    setPathState(5);
                }
                break;
            case 5:
                if (!follower.isBusy()) setPathState(-1);
                break;
            default:
                break;
        }
    }

    /** מחליפים מצב ומאפסים את הטיימר של המצב — תמיד דרך הפונקציה הזו. */
    private void setPathState(int newState) {
        pathState = newState;
        pathTimer.resetTimer();
    }

    private void addPoseTelemetry() {
        Pose p = follower.getPose();
        telemetry.addData("X", "%.1f", p.getX());
        telemetry.addData("Y", "%.1f", p.getY());
        telemetry.addData("Heading [deg]", "%.1f", Math.toDegrees(p.getHeading()));
    }

    @Override
    public void stop() {
        follower.breakFollowing();
    }
}
