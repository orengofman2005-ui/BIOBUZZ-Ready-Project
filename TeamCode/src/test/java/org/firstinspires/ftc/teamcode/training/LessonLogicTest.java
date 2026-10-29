package org.firstinspires.ftc.teamcode.training;

import static org.junit.Assert.*;
import com.seattlesolvers.solverslib.command.Subsystem;
import org.firstinspires.ftc.teamcode.SubSystems.LiftPid;
import org.firstinspires.ftc.teamcode.SubSystems.MecanumSubsystem;
import org.firstinspires.ftc.teamcode.training.commands.ReachSensorCommand;
import org.firstinspires.ftc.teamcode.training.auto.VisionFreshnessGate;
import org.firstinspires.ftc.teamcode.training.limelight.AimMath;
import org.junit.Test;

public class LessonLogicTest {
    @Test public void mecanumDirectionsAndSaturatedMixKeepRatios() {
        assertArrayEquals(new double[]{1, 1, 1, 1}, MecanumSubsystem.calculatePowers(1, 0, 0), 1e-9);
        assertArrayEquals(new double[]{1, -1, -1, 1}, MecanumSubsystem.calculatePowers(0, 1, 0), 1e-9);
        assertArrayEquals(new double[]{1, -1, 1, -1}, MecanumSubsystem.calculatePowers(0, 0, 1), 1e-9);
        assertArrayEquals(new double[]{1, -1.0/3, 1.0/3, 1.0/3},
                MecanumSubsystem.calculatePowers(1, 1, 1), 1e-9);
    }

    @Test public void pidRejectsBadTimeAndDoesNotWindUpAtSaturation() {
        LiftPid pid = new LiftPid();
        for (int i = 0; i < 500; i++)
            assertEquals(.35, pid.update(1000, 0, .02, 1, 1, 0, 0, .35), 1e-9);
        assertEquals(0, pid.getIntegral(), 0);
        assertEquals(0, pid.update(1000, 0, Double.NaN, 1, 1, 1, 0, .35), 0);
        assertEquals(0, pid.update(0, 0, .02, 1, 1, 1, 0, .35), 0);
    }

    @Test public void sensorCommandStopsOnSuccessTimeoutAndCancellation() {
        double[] now = {0}, output = {0};
        boolean[] hit = {false};
        ReachSensorCommand cmd = new ReachSensorCommand(new Subsystem() {}, () -> hit[0],
                p -> output[0] = p, () -> now[0], .2, 1);
        cmd.initialize(); cmd.execute();
        assertEquals(.2, output[0], 0);
        hit[0] = true; cmd.execute();
        assertEquals(ReachSensorCommand.Result.SUCCESS, cmd.result());
        assertEquals(0, output[0], 0);
        hit[0] = false; cmd.initialize(); now[0] = 1; cmd.execute();
        assertEquals(ReachSensorCommand.Result.TIMEOUT, cmd.result());
        assertEquals(0, output[0], 0);
        cmd.initialize(); cmd.execute(); cmd.end(true);
        assertEquals(ReachSensorCommand.Result.CANCELLED, cmd.result());
        assertEquals(0, output[0], 0);
    }

    @Test public void invalidVisionAndFrozenFramesCannotProduceTurnSuggestion() {
        assertFalse(VisionFreshnessGate.usable(true, 151, 150, 0, 0, 1, 1));
        assertFalse(VisionFreshnessGate.usable(true, 20, 150, 1, 0, 1, 1));
        assertFalse(VisionFreshnessGate.usable(true, 20, 150, 0, 0, 2, 1));
        AimMath.FrameWatchdog watchdog = new AimMath.FrameWatchdog();
        assertTrue(watchdog.fresh(100, 0, .15));
        boolean frozen = watchdog.fresh(100, .2, .15);
        assertFalse(frozen);
        assertEquals(0, AimMath.turn(frozen, 10, 0, .02, 1, .2, 1), 0);
        assertFalse(watchdog.fresh(99, .21, .15));
        assertFalse(watchdog.fresh(101, .22, .15));
        assertEquals(.2, AimMath.turn(true, 90, 0, .02, 1, .2, 1), 0);
    }
}
