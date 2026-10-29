package org.firstinspires.ftc.teamcode.training.limelight;

/** Pure P-control preview. Hardware sign and gains require robot trials. */
public final class AimMath {
    private AimMath() { }
    public static double turn(boolean usable, double txDeg, double desiredTxDeg,
                              double kP, double sign, double maxTurn,
                              double toleranceDeg) {
        if (!Double.isFinite(kP) || kP < 0 || (sign != 1 && sign != -1)
                || !Double.isFinite(maxTurn) || maxTurn < 0 || maxTurn > 1
                || !Double.isFinite(toleranceDeg) || toleranceDeg < 0
                || !Double.isFinite(desiredTxDeg)) {
            throw new IllegalArgumentException("Invalid controller configuration");
        }
        if (!usable || !Double.isFinite(txDeg)) return 0;
        double error = txDeg - desiredTxDeg;
        if (!Double.isFinite(error)) return 0;
        if (Math.abs(error) <= toleranceDeg) return 0;
        return Math.max(-maxTurn, Math.min(maxTurn, sign * kP * error));
    }

    /** Tracks camera progression using a separate monotonic robot clock. */
    public static final class FrameWatchdog {
        private double lastStamp = Double.NaN;
        private double advancedAt = Double.NaN;
        private double lastNow = Double.NaN;
        private boolean resetRequired;
        public boolean fresh(double cameraStamp, double nowSeconds, double limitSeconds) {
            if (!Double.isFinite(cameraStamp) || !Double.isFinite(nowSeconds)
                    || !Double.isFinite(limitSeconds) || limitSeconds <= 0) return false;
            if (resetRequired) return false;
            if ((Double.isFinite(lastNow) && nowSeconds < lastNow)
                    || (Double.isFinite(lastStamp) && cameraStamp < lastStamp)) {
                resetRequired = true;
                return false;
            }
            lastNow = nowSeconds;
            if (!Double.isFinite(lastStamp) || cameraStamp > lastStamp) {
                lastStamp = cameraStamp;
                advancedAt = nowSeconds;
            }
            return nowSeconds - advancedAt <= limitSeconds;
        }
    }
}
