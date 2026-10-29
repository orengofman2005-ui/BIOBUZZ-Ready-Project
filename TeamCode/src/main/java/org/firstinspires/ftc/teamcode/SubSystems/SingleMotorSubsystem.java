package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import java.util.function.DoubleSupplier;

/** Controls one DC motor. The name must match the robot configuration. */
public class SingleMotorSubsystem extends SubsystemBase {
    private final DcMotor motor;

    public SingleMotorSubsystem(HardwareMap hardwareMap, String motorName) {
        motor = hardwareMap.get(DcMotor.class, motorName);
        motor.setPower(0);
        motor.setDirection(DcMotor.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setPower(double power) {
        motor.setPower(Range.clip(power, -1.0, 1.0));
    }

    /** Read the joystick on every scheduler cycle, and stop when interrupted. */
    public Command joystickCommand(DoubleSupplier joystick) {
        return new RunCommand(() -> {
            double power = joystick.getAsDouble();
            setPower(Math.abs(power) < 0.05 ? 0.0 : power);
        }, this) {
            @Override
            public void end(boolean interrupted) {
                SingleMotorSubsystem.this.stop();
            }
        };
    }

    public double getPower() {
        return motor.getPower();
    }

    public void stop() {
        motor.setPower(0);
    }
}
