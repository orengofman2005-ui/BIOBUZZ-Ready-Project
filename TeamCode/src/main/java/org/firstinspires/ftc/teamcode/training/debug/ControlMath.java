package org.firstinspires.ftc.teamcode.training.debug;

public final class ControlMath {
    private ControlMath() {}
    public static double clip(double value, double min, double max) {
        if (!Double.isFinite(value) || !Double.isFinite(min)
                || !Double.isFinite(max) || min > max) {
            throw new IllegalArgumentException("Finite, ordered bounds required");
        }
        return Math.max(min, Math.min(max, value));
    }
}
