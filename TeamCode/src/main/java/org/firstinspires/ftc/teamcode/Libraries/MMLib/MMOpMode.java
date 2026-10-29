package org.firstinspires.ftc.teamcode.Libraries.MMLib;

// הסבר לימודי: מחזור ההפעלה המשותף, קריאת חיישנים, הרצת Scheduler, עדכון Pedro וניקוי בסיום או בכשל אתחול.

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.seattlesolvers.solverslib.command.CommandOpMode;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import java.util.ArrayList;
import java.util.List;

//import org.firstinspires.ftc.teamcode.Libraries.RoadRunner.Drawing;


/** מחזור הפעלה משותף: קריאת חיישנים, פקודות, עדכון Pedro ועצירה בסיום. */
public abstract class MMOpMode extends CommandOpMode {
    /** כוללת גם כשל בזמן initialize במסלול הניקוי. */
    @Override
    public void runOpMode() throws InterruptedException {
        try {
            initialize();
            while (opModeInInit()) {
                initialize_loop();
                idle();
            }
            if (opModeIsActive()) {
                while (opModeIsActive()) {
                    run();
                    idle();
                }
            }
        } finally {
            try { end(); } finally { reset(); }
        }
    }
    @Override
    public abstract void initialize();

    @Override
    public void run () {
        MMRobot robot = MMRobot.getInstance();
        // Read bulk data before commands consume sensor values.
        if (robot.controlHub != null) {
            robot.controlHub.pullBulkData();
        }
        if (robot.expansionHub != null) {
            robot.expansionHub.pullBulkData();
        }
        super.run();
        if (robot.driveTrain != null && robot.driveTrain.getFollower() != null) {
            robot.driveTrain.updateFollower();
        }
        //fieldOverlay dashboard
        TelemetryPacket packet = new TelemetryPacket();
        packet.fieldOverlay().setStroke("#3F51B5");
//        Drawing.drawRobot(packet.fieldOverlay(), JeruRobot.getInstance().localizer.getPositionRR());
        FtcDashboard.getInstance().sendTelemetryPacket(packet);

        //update outputs
        telemetry.update();
        FtcDashboard.getInstance().getTelemetry().update();
    }
    @Override
    public void end() {
        MMRobot robot = MMRobot.getInstance();
        List<Runnable> cleanup = new ArrayList<>();
        cleanup.add(() -> CommandScheduler.getInstance().cancelAll());
        if (robot.motorSubsystem != null) cleanup.add(robot.motorSubsystem::stop);
        if (robot.mecanumSubsystem != null) cleanup.add(robot.mecanumSubsystem::stop);
        if (robot.elevatorSubsystem != null) cleanup.add(robot.elevatorSubsystem::disable);
        if (robot.intakeSubsystem != null) cleanup.add(robot.intakeSubsystem::stop);
        if (robot.transferSubsystem != null) cleanup.add(robot.transferSubsystem::stop);
        if (robot.shooterSubsystem != null) cleanup.add(robot.shooterSubsystem::stop);
        if (robot.driveTrain != null) {
            if (robot.driveTrain.getFollower() != null) {
                cleanup.add(() -> robot.driveTrain.getFollower().breakFollowing());
            }
            cleanup.add(() -> robot.driveTrain.drive(0, 0, 0));
        }
        if (robot.limelight != null) cleanup.add(robot.limelight::stop);
        // סרבו מיקום שומר את היעד האחרון; אין קפיצה ליעד חדש בעת STOP.
        RuntimeException failure = null;
        for (Runnable action : cleanup) {
            try { action.run(); }
            catch (RuntimeException e) {
                if (failure == null) failure = e;
                else failure.addSuppressed(e);
            }
        }
        CommandScheduler.getInstance().reset();
        robot.resetRobot();
        if (failure != null) throw failure;
    }
}
