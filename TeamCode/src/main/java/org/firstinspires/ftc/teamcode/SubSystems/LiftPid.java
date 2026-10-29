package org.firstinspires.ftc.teamcode.SubSystems;

/** Educational position PID, with D on measurement and limited integral. */
public class LiftPid {
    private double integral, previousPosition;
    private boolean first = true;

    public void reset() {
        integral = 0;
        first = true;
    }

    public double update(double target, double position, double dt,
                         double kp, double ki, double kd, double kg,
                         double maxPower) {
        if (!Double.isFinite(dt) || dt <= 0 || dt > 0.2) {
            reset();
            return 0;
        }
        double error = target - position;
        double velocity = first ? 0 : (position - previousPosition) / dt;
        first = false;
        previousPosition = position;
        double p = kp * error;
        double d = -kd * velocity;
        double cap = Math.min(1.0, Math.abs(maxPower));
        double nextIntegral = 0;
        if (ki > 0) {
            double integralLimit = 0.15 / ki;
            nextIntegral = clip(integral + error * dt,
                                -integralLimit, integralLimit);
        }
        double candidate = p + ki * nextIntegral + d + kg;
        // Do not accumulate error further into a saturated output.
        if (Math.abs(candidate) <= cap || error * candidate < 0) {
            integral = nextIntegral;
        }
        if (ki <= 0) integral = 0;
        return clip(p + ki * integral + d + kg, -cap, cap);
    }

    public void clearIntegral() { integral = 0; }
    public double getIntegral() { return integral; }
    public static double clip(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
