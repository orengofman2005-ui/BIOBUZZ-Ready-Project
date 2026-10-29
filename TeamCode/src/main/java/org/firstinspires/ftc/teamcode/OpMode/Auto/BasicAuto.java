package org.firstinspires.ftc.teamcode.OpMode.Auto;

import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMOpMode;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.Utils.OpModeType;
import org.firstinspires.ftc.teamcode.Libraries.pedroPathing.FollowPathCommand;
import org.firstinspires.ftc.teamcode.training.auto.DemoPathFactory;

/** דוגמת שיעור 10. הסרת Disabled דורשת כיול והצבה מתאימה לניסוי. */
@Disabled
@Autonomous(name = "Training Short Line", group = "Learning")
public class BasicAuto extends MMOpMode {
    private PathChain path;
    private boolean started;

    @Override public void initialize() {
        MMRobot.getInstance().initJeruRobot().opModeType(OpModeType.AUTO).build(this);
        path = DemoPathFactory.shortLine(
                MMRobot.getInstance().driveTrain.getFollower());
    }

    @Override public void run() {
        // ב־SNAPSHOT אין preRun. הדגל מתזמן פעם אחת, במחזור הראשון אחרי START.
        if (!started) {
            started = true;
            MMRobot.getInstance().driveTrain.getFollower().setStartingPose(new Pose(0, 0, 0));
            schedule(new FollowPathCommand(
                    MMRobot.getInstance().driveTrain.getFollower(), path, false, 0.3));
        }
        super.run();
    }
}
