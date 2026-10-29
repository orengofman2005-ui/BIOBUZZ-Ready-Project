package org.firstinspires.ftc.teamcode.training.commands;

import com.seattlesolvers.solverslib.command.CommandBase;
import com.seattlesolvers.solverslib.command.Subsystem;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;

/** Generic training command. Does not construct hardware or define lift holding behavior. */
public final class ReachSensorCommand extends CommandBase {
    public enum Result { RUNNING, SUCCESS, TIMEOUT, CANCELLED }
    private final BooleanSupplier reached;
    private final DoubleConsumer output;
    private final DoubleSupplier clockSeconds;
    private final double power, timeout;
    private double started;
    private Result result = Result.CANCELLED;

    public ReachSensorCommand(Subsystem mechanism, BooleanSupplier reached,
            DoubleConsumer output, DoubleSupplier clockSeconds,
            double power, double timeoutSeconds) {
        Objects.requireNonNull(mechanism, "mechanism");
        this.reached = Objects.requireNonNull(reached, "reached");
        this.output = Objects.requireNonNull(output, "output");
        this.clockSeconds = Objects.requireNonNull(clockSeconds, "clock");
        if (!Double.isFinite(power) || Math.abs(power) > 1
                || !Double.isFinite(timeoutSeconds) || timeoutSeconds <= 0) {
            throw new IllegalArgumentException("Invalid output or timeout");
        }
        this.power = power;
        timeout = timeoutSeconds;
        addRequirements(mechanism);
    }
    @Override public void initialize() {
        output.accept(0);
        started = clockSeconds.getAsDouble();
        if (!Double.isFinite(started)) throw new IllegalStateException("Invalid clock");
        result = Result.RUNNING;
    }
    @Override public void execute() {
        if (result != Result.RUNNING) return;
        double now = clockSeconds.getAsDouble();
        if (!Double.isFinite(now) || now < started) result = Result.TIMEOUT;
        else if (reached.getAsBoolean()) result = Result.SUCCESS;
        else if (now - started >= timeout) result = Result.TIMEOUT;
        output.accept(result == Result.RUNNING ? power : 0);
    }
    @Override public boolean isFinished() { return result != Result.RUNNING; }
    @Override public void end(boolean interrupted) {
        if (interrupted) result = Result.CANCELLED;
        output.accept(0);
    }
    public Result result() { return result; }
}
