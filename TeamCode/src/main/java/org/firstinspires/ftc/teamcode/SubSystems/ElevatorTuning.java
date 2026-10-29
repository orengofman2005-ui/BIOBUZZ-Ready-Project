package org.firstinspires.ftc.teamcode.SubSystems;
import com.acmerobotics.dashboard.config.Config;

@Config
public class ElevatorTuning {
    // Set measured travel and presets for YOUR mechanism before position control.
    public static int MAX_TICKS = 0;
    public static int MID_TICKS = 0;
    public static int HIGH_TICKS = 0;
    // These are deliberately zero. Tune on the actual mechanism.
    public static double KP = 0, KI = 0, KD = 0, KG = 0;
    // Output is a setPower fraction, not volts.
    public static double MAX_POWER = 0.35;
    public static double HOME_POWER = 0.12;
    public static double HOME_TIMEOUT_SECONDS = 3.0;
}
