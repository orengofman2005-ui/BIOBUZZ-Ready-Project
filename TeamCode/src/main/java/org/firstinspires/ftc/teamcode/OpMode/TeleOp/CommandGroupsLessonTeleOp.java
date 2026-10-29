package org.firstinspires.ftc.teamcode.OpMode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.seattlesolvers.solverslib.gamepad.GamepadKeys;
import org.firstinspires.ftc.teamcode.Commands.IntakeCommandGroup;
import org.firstinspires.ftc.teamcode.Commands.TimedFeedGroup;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMOpMode;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;

/** שיעור קבוצות פקודות: רק intake ו־transfer. feedOnly היא הרחבה לימודית. */
@TeleOp(name = "Command Groups Lesson")
public class CommandGroupsLessonTeleOp extends MMOpMode {

    @Override
    public void initialize() {
        MMRobot.getInstance().initJeruRobot()
             .feedOnly()
             .build(this);
        if (MMRobot.getInstance().intakeSubsystem == null || MMRobot.getInstance().transferSubsystem == null) {
            telemetry.addLine("Intake or transfer did not initialize. Check the configuration.");
            return;
        }
        MMRobot.getInstance().gamepadEx1.getGamepadButton(GamepadKeys.Button.A)
             .whenPressed(new TimedFeedGroup(MMRobot.getInstance().intakeSubsystem,
                                            MMRobot.getInstance().transferSubsystem, 0.3, 1000))
             .whenReleased(IntakeCommandGroup.stopIntake());
        telemetry.addLine("A: feed for at most 1000 ms. Releasing A also stops the feed.");
    }

    @Override
    public void end() { super.end(); }
}
