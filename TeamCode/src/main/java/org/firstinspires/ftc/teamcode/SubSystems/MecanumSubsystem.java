package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import java.util.function.DoubleSupplier;

/** Robot-centric drive: positive y is forward, x right, turn clockwise. */
public class MecanumSubsystem extends SubsystemBase {
    private final DcMotor fl, fr, bl, br;

    public MecanumSubsystem(HardwareMap hardwareMap) {
        fl = hardwareMap.get(DcMotor.class, "FL");
        fr = hardwareMap.get(DcMotor.class, "FR");
        bl = hardwareMap.get(DcMotor.class, "BL");
        br = hardwareMap.get(DcMotor.class, "BR");
        // Verify that positive power rolls each wheel physically forward.
        fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.FORWARD);
        for (DcMotor motor : new DcMotor[]{fl, fr, bl, br}) {
            motor.setPower(0);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    /** Max-magnitude normalization preserves wheel ratios. */
    public static double[] calculatePowers(double y, double x, double turn) {
        double fl = y + x + turn;
        double fr = y - x - turn;
        double bl = y - x + turn;
        double br = y + x - turn;
        double max = Math.max(1.0, Math.max(
                Math.max(Math.abs(fl), Math.abs(fr)),
                Math.max(Math.abs(bl), Math.abs(br))));
        return new double[]{fl / max, fr / max, bl / max, br / max};
    }

    public void drive(double y, double x, double turn) {
        double[] p = calculatePowers(y, x, turn);
        fl.setPower(p[0]);
        fr.setPower(p[1]);
        bl.setPower(p[2]);
        br.setPower(p[3]);
    }

    private static double deadband(double value) {
        return Math.abs(value) < 0.05 ? 0.0 : value;
    }

    public Command joystickCommand(DoubleSupplier y, DoubleSupplier x,
                                   DoubleSupplier turn) {
        return new RunCommand(() -> drive(
                deadband(y.getAsDouble()),
                deadband(x.getAsDouble()),
                deadband(turn.getAsDouble())), this) {
            @Override
            public void end(boolean interrupted) {
                MecanumSubsystem.this.stop();
            }
        };
    }

    public double[] getPowers() {
        return new double[]{fl.getPower(), fr.getPower(), bl.getPower(), br.getPower()};
    }

    public void stop() {
        drive(0, 0, 0);
    }
}
