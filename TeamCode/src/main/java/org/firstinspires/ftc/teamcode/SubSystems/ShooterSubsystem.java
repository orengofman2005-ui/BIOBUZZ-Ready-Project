package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.controller.PIDFController;
import com.seattlesolvers.solverslib.hardware.motors.Motor.Encoder;

import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;

import java.util.function.DoubleSupplier;

public class ShooterSubsystem extends SubsystemBase {
    private final DcMotorEx shooterMotorL;
    private final DcMotorEx shooterMotorR;
    private final Encoder shooterEncoder;
    // how many ticks is one motor rotation
    private final double EncoderResolution = 28;
    // how many motor rotation is one flywil rotation
    private final double girRatio = 25/18;
    // todo: 8/10/26
    private final double kP = 0;
    private final double kI = 0;
    private final double kD = 0;
    private final double kF = 0;
    PIDFController pidf = new PIDFController(kP, kI, kD, kF);
    public ShooterSubsystem() {
        shooterMotorL = MMRobot.getInstance().hardwareMap.get(DcMotorEx.class, "shooterMotorL");
        shooterMotorR = MMRobot.getInstance().hardwareMap.get(DcMotorEx.class, "shooterMotorR");

//        shooterMotorR.setDirection(DcMotorEx.Direction.REVERSE);
        shooterMotorL.setDirection(DcMotorEx.Direction.REVERSE);

        Encoder enc = null;
        try {
            enc = MMRobot.getInstance().hardwareMap.get(Encoder.class, "shooterEncoder");
        } catch (Exception ignored) {
        }
        shooterEncoder = enc;
    }
    private void setPower(double power) {
        shooterMotorL.setPower(power);
        shooterMotorR.setPower(power);
    }
    public double getVel() {
        if (shooterEncoder == null) return 0.0;
        return (shooterEncoder.getPosition()/EncoderResolution)/girRatio;
    }

        public Command setPowerCommand(double power) {
        return new InstantCommand(() -> setPower(power),this);
    }
    public Command setPowerCommand(DoubleSupplier power) {
        return new InstantCommand(() -> setPower(power.getAsDouble()),this);
    }
    public Command getToVelocity(double vel) {
        return setPowerCommand(pidf.calculate(getVel(), vel));
    }
    /** עצירה מיידית, גם מחוץ ללולאת המתזמן. */
    public void stop() { setPower(0); }
}
