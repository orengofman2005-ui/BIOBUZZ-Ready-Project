package org.firstinspires.ftc.teamcode.OpMode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMOpMode;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;

/** שיעור מנוע יחיד: כל הפקודות נגישות דרך MMRobot כמו ב־Biobuzz. */
@TeleOp(name = "Single Motor", group = "Learning")
public class manualDrive extends MMOpMode {
    private static final String MOTOR_NAME = "testMotor";

    @Override
    public void initialize() {
        MMRobot.getInstance().initJeruRobot()
                .singleMotor(MOTOR_NAME)
                .build(this);

        MMRobot.getInstance().motorSubsystem.setDefaultCommand(
                MMRobot.getInstance().motorSubsystem.joystickCommand(
                        () -> MMRobot.getInstance().gamepadEx1.getLeftY()));
    }

    @Override
    public void run() {
        telemetry.addData("Motor", MOTOR_NAME);
        telemetry.addData("Power", MMRobot.getInstance().motorSubsystem.getPower());
        super.run();
    }

    @Override
    public void end() { super.end(); }
}
