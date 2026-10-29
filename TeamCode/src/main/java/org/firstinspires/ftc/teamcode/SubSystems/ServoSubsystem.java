package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;

/** Positional servo example. It has targets, not a motor-style stop position. */
public class ServoSubsystem extends SubsystemBase {
    private final Servo claw;
    private double target = Double.NaN;

    public ServoSubsystem(HardwareMap hardwareMap) {
        claw = hardwareMap.get(Servo.class, "claw");
        claw.setDirection(Servo.Direction.FORWARD);
        // No setPosition here: driver selects the first target after START.
    }

    public void moveTo(double position) {
        double min = ServoTuning.MIN_POSITION;
        double max = ServoTuning.MAX_POSITION;
        if (!Double.isFinite(position) || !Double.isFinite(min)
                || !Double.isFinite(max) || min < 0 || max > 1 || min > max) {
            throw new IllegalArgumentException("Servo bounds must satisfy 0 <= min <= max <= 1");
        }
        target = Math.max(min, Math.min(max, position));
        claw.setPosition(target);
    }

    public void open() { moveTo(ServoTuning.OPEN_POSITION); }
    public void close() { moveTo(ServoTuning.CLOSED_POSITION); }

    public void adjust(double delta) {
        // Start by selecting a known test target, rather than trusting getPosition.
        if (!hasTarget() || !Double.isFinite(delta)) return;
        moveTo(target + delta);
    }

    public Command openCommand() { return new InstantCommand(this::open, this); }
    public Command closeCommand() { return new InstantCommand(this::close, this); }
    public boolean hasTarget() { return Double.isFinite(target); }
    public double getTarget() { return target; }
    /** Last requested SDK value. This is NOT measured shaft position. */
    public double getLastRequestedPosition() { return claw.getPosition(); }
}
