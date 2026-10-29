package org.firstinspires.ftc.teamcode.training.auto;

/** Validates an already-measured frame age. Does not compare unrelated clocks. */
public final class VisionFreshnessGate {
    private VisionFreshnessGate() {}
    public static boolean usable(boolean valid, double ageMillis, double maxAgeMillis,
            int pipeline, int expectedPipeline, int tagId, int expectedTagId) {
        return valid && Double.isFinite(ageMillis) && ageMillis >= 0
            && Double.isFinite(maxAgeMillis) && maxAgeMillis >= 0
            && ageMillis <= maxAgeMillis && pipeline == expectedPipeline
            && tagId >= 0 && tagId == expectedTagId;
    }
}
