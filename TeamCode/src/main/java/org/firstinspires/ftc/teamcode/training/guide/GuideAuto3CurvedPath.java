package org.firstinspires.ftc.teamcode.training.guide;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Libraries.pedroPathing.Constants;

/**
 * מדריך Pedro — דוגמה 3: מסלול עקום ורב־שלבי (Bezier Curve + PathChain).
 * המכשול (דמיוני) נמצא בערך ב־(40, 72). הרובוט עוקף אותו מלמעלה בקשת,
 * נוסע ישר, ואז מסתובב בקשת נוספת אל נקודת הסיום — הכל ברצף אחד בלי לעצור.
 */
@Disabled
@Autonomous(name = "Guide 3 - Curved Multi-Segment", group = "Pedro Guide")
public class GuideAuto3CurvedPath extends OpMode {

    // ---- נקודות ציון (Waypoints) ----
    private final Pose startPose  = new Pose(8, 72, Math.toRadians(0));
    private final Pose arcControl = new Pose(30, 110);                   // נקודת בקרה: "מושכת" את הקשת למעלה
    private final Pose pastObstacle = new Pose(60, 100, Math.toRadians(0));
    private final Pose straightEnd  = new Pose(96, 100, Math.toRadians(0));
    private final Pose cornerControl = new Pose(124, 100);              // נקודת בקרה לפנייה
    private final Pose endPose    = new Pose(120, 72, Math.toRadians(-90));

    private Follower follower;
    private PathChain obstacleRun;
    private Timer opModeTimer;
    private int pathState;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);
        opModeTimer = new Timer();
        buildPaths();
    }

    private void buildPaths() {
        obstacleRun = follower.pathBuilder()
                // קטע 1: קשת שעוקפת את המכשול. הרובוט ממשיך לפנות קדימה (0°)
                .addPath(new BezierCurve(startPose, arcControl, pastObstacle))
                .setConstantHeadingInterpolation(Math.toRadians(0))

                // קטע 2: קו ישר. Tangent = החזית פונה לכיוון הנסיעה (כאן +X, כלומר 0°)
                .addPath(new BezierLine(pastObstacle, straightEnd))
                .setTangentHeadingInterpolation()

                // קטע 3: פנייה בקשת אל נקודת הסיום, תוך סיבוב הדרגתי מ־0° ל־(-90°)
                .addPath(new BezierCurve(straightEnd, cornerControl, endPose))
                .setLinearHeadingInterpolation(straightEnd.getHeading(), endPose.getHeading())
                .build();
    }

    @Override
    public void init_loop() {
        follower.update();
        telemetry.addLine("Guide 3 ready. Press START.");
        addPoseTelemetry();
        telemetry.update();
    }

    @Override
    public void start() {
        opModeTimer.resetTimer();
        pathState = 0;
    }

    @Override
    public void loop() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("State", pathState);
        telemetry.addData("Segment (0-2)", follower.getChainIndex());
        telemetry.addData("Time [s]", "%.1f", opModeTimer.getElapsedTimeSeconds());
        addPoseTelemetry();
        telemetry.update();
    }

    private void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(obstacleRun, true);
                pathState = 1;
                break;
            case 1:
                // isBusy נשאר true לאורך כל השרשרת, ונהיה false רק בסוף הקטע האחרון
                if (!follower.isBusy()) pathState = -1;
                break;
            default:
                break;
        }
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
