package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.seattlesolvers.solverslib.command.SubsystemBase;

/** One motor, two physical limit switches, ticks increasing upward. */
public class ElevatorSubsystem extends SubsystemBase {
    public enum Mode { DISABLED, HOMING, MANUAL, HOLD }
    private final DcMotor motor;
    private final TouchSensor bottom, top;
    private final LiftPid pid = new LiftPid();
    private final ElapsedTime clock = new ElapsedTime();
    private Mode mode = Mode.DISABLED;
    private boolean homed, previousHomeHeld;
    private double target, manualPower, homeStart, previousTime;
    private String status = "Home before position control";

    public ElevatorSubsystem(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "lift");
        bottom = hardwareMap.get(TouchSensor.class, "liftBottom");
        top = hardwareMap.get(TouchSensor.class, "liftTop");
        motor.setPower(0);
        // Verify that positive power RAISES the carriage and ticks INCREASE.
        motor.setDirection(DcMotor.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        previousTime = clock.seconds();
    }

    /** Starts once per press. Release cancels an unfinished home. */
    public void homeHeld(boolean held) {
        if (held && !previousHomeHeld) {
            homed = false;
            mode = Mode.HOMING;
            homeStart = clock.seconds();
            pid.reset();
            status = "Homing: hold X";
        }
        if (!held && mode == Mode.HOMING) {
            disable();
            status = "Homing cancelled";
        }
        previousHomeHeld = held;
    }

    private void zeroAtBottom() {
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        homed = true;
        target = 0;
        pid.reset();
        mode = Mode.HOLD;
        status = "Homed at bottom";
    }

    public void setTarget(double ticks) {
        if (!homed || ElevatorTuning.MAX_TICKS <= 0) {
            status = "Need home and measured MAX_TICKS";
            return;
        }
        target = LiftPid.clip(ticks, 0, ElevatorTuning.MAX_TICKS);
        pid.reset();
        mode = Mode.HOLD;
        status = "Position hold";
    }

    /** Low-power direction/travel test, only while the operator holds controls. */
    public void manual(double power) {
        manualPower = LiftPid.clip(power, -0.15, 0.15);
        mode = Mode.MANUAL;
        pid.reset();
        status = "Manual test";
    }

    public void releaseManual() {
        if (mode == Mode.MANUAL) disable();
    }

    @Override
    public void periodic() {
        double now = clock.seconds();
        double dt = now - previousTime;
        previousTime = now;
        if (bottom.isPressed() && top.isPressed()) {
            homed = false;
            disable();
            status = "Check limit switches: both pressed";
            return;
        }
        if (mode == Mode.DISABLED) {
            motor.setPower(0);
            return;
        }
        if (mode == Mode.HOMING) {
            if (bottom.isPressed()) {
                zeroAtBottom();
            } else if (now - homeStart >= ElevatorTuning.HOME_TIMEOUT_SECONDS) {
                disable();
                status = "Homing timeout: release X, inspect, retry";
            } else {
                applyLimits(-Math.min(0.15, Math.abs(ElevatorTuning.HOME_POWER)));
            }
            return;
        }
        if (mode == Mode.MANUAL) {
            applyLimits(manualPower);
            return;
        }
        if (!homed || ElevatorTuning.MAX_TICKS <= 0) {
            motor.setPower(0);
            return;
        }
        target = LiftPid.clip(target, 0, ElevatorTuning.MAX_TICKS);
        if (bottom.isPressed() && target == 0) {
            pid.reset();
            motor.setPower(0);
            return;
        }
        double power = pid.update(target, getPosition(), dt,
                ElevatorTuning.KP, ElevatorTuning.KI,
                ElevatorTuning.KD, ElevatorTuning.KG,
                ElevatorTuning.MAX_POWER);
        applyLimits(power);
    }

    private void applyLimits(double power) {
        int position = getPosition();
        boolean blocked = (bottom.isPressed() && power < 0)
                || (top.isPressed() && power > 0);
        if (homed && ElevatorTuning.MAX_TICKS > 0) {
            blocked |= (position <= 0 && power < 0)
                    || (position >= ElevatorTuning.MAX_TICKS && power > 0);
        }
        if (blocked) {
            power = 0;
            pid.clearIntegral();
        }
        motor.setPower(power);
    }

    public void disable() {
        mode = Mode.DISABLED;
        pid.reset();
        motor.setPower(0);
        status = "Disabled: output zero";
    }
    public int getPosition() { return motor.getCurrentPosition(); }
    public double getTarget() { return target; }
    public double getPower() { return motor.getPower(); }
    public boolean isHomed() { return homed; }
    public boolean atBottom() { return bottom.isPressed(); }
    public boolean atTop() { return top.isPressed(); }
    public Mode getMode() { return mode; }
    public String getStatus() { return status; }
}
