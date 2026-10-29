package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;

public class IntakeSubsystem extends SubsystemBase {
    private final DcMotorEx intakeMotor;

    public IntakeSubsystem() {
        intakeMotor = MMRobot.getInstance().hardwareMap.get(DcMotorEx.class, "intake");
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    private void setPower(double power) {
        intakeMotor.setPower(power);
    }

    public Command setPowerCommand(double power) {
        return new InstantCommand(() -> setPower(power),this);
    }
    public Command stopCommand() {
        return new InstantCommand(() -> setPower(0),this);
    }
    /** עצירה מיידית, גם מחוץ ללולאת המתזמן. */
    public void stop() { setPower(0); }
}
