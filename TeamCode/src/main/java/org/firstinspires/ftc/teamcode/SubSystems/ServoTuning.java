package org.firstinspires.ftc.teamcode.SubSystems;
import com.acmerobotics.dashboard.config.Config;

@Config
public class ServoTuning {
    // Starting TEST window, not a guarantee of a safe mechanical range.
    // Test with the horn/linkage detached before fitting the mechanism.
    public static double MIN_POSITION = 0.45;
    public static double MAX_POSITION = 0.55;
    // Set distinct, measured values after testing your mechanism.
    public static double OPEN_POSITION = 0.50;
    public static double CLOSED_POSITION = 0.50;
    // Normalized position units per second, not physical speed.
    public static double ADJUST_RATE = 0.15;
}
