package org.firstinspires.ftc.teamcode.training.sensors;

/** Time debounce with a monotonic clock in seconds supplied by the caller. */
public final class StableBoolean {
    private final double dwell;
    private boolean initialized, raw, stable;
    private double changedAt, lastTime;

    public StableBoolean(double dwellSeconds) {
        if (!Double.isFinite(dwellSeconds) || dwellSeconds < 0) {
            throw new IllegalArgumentException("Invalid dwell time");
        }
        dwell = dwellSeconds;
    }
    public boolean update(boolean sample, double nowSeconds) {
        if (!Double.isFinite(nowSeconds) || (initialized && nowSeconds < lastTime)) {
            throw new IllegalArgumentException("Clock must be finite and monotonic");
        }
        if (!initialized || sample != raw) {
            raw = sample;
            changedAt = nowSeconds;
        }
        initialized = true;
        lastTime = nowSeconds;
        if (nowSeconds - changedAt >= dwell) stable = raw;
        return stable;
    }
}
