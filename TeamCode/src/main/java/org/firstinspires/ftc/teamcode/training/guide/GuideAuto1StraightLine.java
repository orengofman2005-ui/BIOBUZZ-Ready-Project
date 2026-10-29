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
 * מדריך Pedro — דוגמה 1: נסיעה ישרה מנקודה א' לנקודה ב' ועצירה.
 * מחקו את @Disabled רק אחרי כיול Pedro ובדיקה שהשטח לפני הרובוט פנוי.
 */
@Disabled
@Autonomous(name = "Guide 1 - Straight Line", group = "Pedro Guide")
public class GuideAuto1StraightLine extends OpMode {

    // ---- נקודות (אינצ'ים, זווית ברדיאנים) ----
    private final Pose startPose = new Pose(8, 72, Math.toRadians(0));   // צמוד לקיר, פונה לתוך המגרש
    private final Pose endPose   = new Pose(32, 72, Math.toRadians(0));  // 24 אינץ' קדימה

    // ---- אובייקטים ----
    private Follower follower;
    private PathChain driveForward;
    private Timer opModeTimer;
    private int pathState;

    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);
        buildPaths();
        opModeTimer = new Timer();
    }

    private void buildPaths() {
        driveForward = follower.pathBuilder()
                .addPath(new BezierLine(startPose, endPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), endPose.getHeading())
                .build();
    }

    @Override
    public void init_loop() {
        follower.update(); // מעדכן מיקום בלבד; אין מסלול פעיל ולכן אין תנועה
        telemetry.addLine("Guide 1 ready. Press START.");
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
        follower.update();          // חובה בכל סיבוב!
        autonomousPathUpdate();

        telemetry.addData("State", pathState);
        telemetry.addData("Time [s]", "%.1f", opModeTimer.getElapsedTimeSeconds());
        addPoseTelemetry();
        telemetry.update();
    }

    /** מכונת מצבים מינימלית: מתחילים מסלול, מחכים לסיום, מסיימים. */
    private void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(driveForward, true); // true = להחזיק את הנקודה בסוף
                pathState = 1;
                break;
            case 1:
                if (!follower.isBusy()) {
                    pathState = -1; // סיימנו
                }
                break;
            default:
                // -1: לא עושים כלום. Pedro ממשיך להחזיק את הרובוט בנקודה.
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
