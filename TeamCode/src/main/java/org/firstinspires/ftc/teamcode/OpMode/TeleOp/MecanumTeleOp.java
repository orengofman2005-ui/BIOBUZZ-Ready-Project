package org.firstinspires.ftc.teamcode.OpMode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMOpMode;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;

/** סגנון הפעלה של Biobuzz; נהיגה ביחס לרובוט עם ארבעה מנועים וללא Pinpoint. */
@TeleOp(name = "Mecanum Lesson", group = "Learning")
public class MecanumTeleOp extends MMOpMode {
    @Override
    public void initialize() {
        MMRobot.getInstance().initJeruRobot()
                .mecanumOnly()
                .build(this);

        MMRobot.getInstance().mecanumSubsystem.setDefaultCommand(
                MMRobot.getInstance().mecanumSubsystem.joystickCommand(
                        MMRobot.getInstance().gamepadEx1::getLeftY,
                        MMRobot.getInstance().gamepadEx1::getLeftX,
                        MMRobot.getInstance().gamepadEx1::getRightX));
    }

    @Override
    public void run() {
        super.run();
        double[] powers = MMRobot.getInstance().mecanumSubsystem.getPowers();
        telemetry.addData("FL / FR", "%.2f / %.2f", powers[0], powers[1]);
        telemetry.addData("BL / BR", "%.2f / %.2f", powers[2], powers[3]);
        telemetry.addData("Drive mode", "Robot centric / no Pinpoint");
        telemetry.update();
    }

    @Override
    public void end() { super.end(); }
}
