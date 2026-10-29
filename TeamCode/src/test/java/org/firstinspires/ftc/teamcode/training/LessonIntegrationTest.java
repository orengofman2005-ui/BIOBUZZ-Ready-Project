package org.firstinspires.ftc.teamcode.training;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMOpMode;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;
import org.firstinspires.ftc.teamcode.OpMode.TeleOp.manualDrive;
import org.firstinspires.ftc.teamcode.OpMode.TeleOp.MecanumTeleOp;
import org.firstinspires.ftc.teamcode.OpMode.TeleOp.ElevatorTeleOp;
import org.firstinspires.ftc.teamcode.OpMode.TeleOp.ServoTeleOp;
import org.firstinspires.ftc.teamcode.SubSystems.ElevatorSubsystem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;

/** Exercises the real lesson entry points and shared scheduler with mocked hardware. */
public class LessonIntegrationTest {
    @Before public void resetBefore() {
        CommandScheduler.getInstance().reset();
        MMRobot.getInstance().resetRobot();
    }

    @After public void stopAfter() {
        mock(MMOpMode.class, CALLS_REAL_METHODS).end();
    }

    private <T extends OpMode> T opMode(Class<T> type, Map<String, Object> devices) {
        T op = mock(type, CALLS_REAL_METHODS);
        op.hardwareMap = mock(HardwareMap.class);
        op.gamepad1 = mock(Gamepad.class);
        op.gamepad2 = mock(Gamepad.class);
        op.telemetry = mock(Telemetry.class);
        doAnswer(invocation -> {
            Class<?> requestedType = invocation.getArgument(0);
            String name = invocation.getArgument(1);
            Object device = devices.get(name);
            if (!requestedType.isInstance(device)) {
                throw new AssertionError("Unexpected hardware lookup: " + name);
            }
            return device;
        }).when(op.hardwareMap).get(any(Class.class), anyString());
        return op;
    }

    private Map<String, Object> motorMap(String... names) {
        Map<String, Object> devices = new HashMap<>();
        for (String name : names) devices.put(name, mock(DcMotor.class));
        return devices;
    }

    @Test public void singleMotorUsesJoystickAndStopsBeforeNextLesson() {
        Map<String, Object> devices = motorMap("testMotor");
        manualDrive op = opMode(manualDrive.class, devices);
        DcMotor motor = (DcMotor) devices.get("testMotor");
        op.initialize();
        MMRobot previousRobot = MMRobot.getInstance();
        CommandScheduler previousScheduler = CommandScheduler.getInstance();
        op.gamepad1.left_stick_y = -0.8f;
        previousScheduler.run(); // schedule default command
        previousScheduler.run(); // execute joystick command
        verify(motor).setPower(0.8f);
        verify(op.hardwareMap).get(DcMotor.class, "testMotor");
        verifyNoMoreInteractions(op.hardwareMap);
        op.end();
        verify(motor, atLeastOnce()).setPower(0);
        assertNotSame(previousRobot, MMRobot.getInstance());
        assertNotSame(previousScheduler, CommandScheduler.getInstance());
        clearInvocations(motor);
        CommandScheduler.getInstance().run();
        verifyNoInteractions(motor); // no old periodic/default command survives
    }

    @Test public void mecanumInitializesOnlyFourMotorsAndStopsAllFour() {
        Map<String, Object> devices = motorMap("FL", "FR", "BL", "BR");
        MecanumTeleOp op = opMode(MecanumTeleOp.class, devices);
        op.initialize();
        assertNotNull(MMRobot.getInstance().mecanumSubsystem);
        assertNull(MMRobot.getInstance().driveTrain);
        assertNull(MMRobot.getInstance().localizer);
        op.gamepad1.left_stick_y = -1;
        CommandScheduler.getInstance().run();
        CommandScheduler.getInstance().run();
        for (Object device : devices.values()) verify((DcMotor) device).setPower(1);
        for (String name : devices.keySet()) verify(op.hardwareMap).get(DcMotor.class, name);
        verifyNoMoreInteractions(op.hardwareMap);
        op.end();
        for (Object device : devices.values()) verify((DcMotor) device, atLeastOnce()).setPower(0);
    }

    @Test public void elevatorRequiresHomeAndRespectsBothLimitSwitches() {
        Map<String, Object> devices = motorMap("lift");
        TouchSensor bottom = mock(TouchSensor.class), top = mock(TouchSensor.class);
        devices.put("liftBottom", bottom);
        devices.put("liftTop", top);
        ElevatorTeleOp op = opMode(ElevatorTeleOp.class, devices);
        op.initialize();
        DcMotor motor = (DcMotor) devices.get("lift");
        ElevatorSubsystem lift = MMRobot.getInstance().elevatorSubsystem;
        lift.setTarget(1000);
        assertFalse(lift.isHomed());
        assertEquals(ElevatorSubsystem.Mode.DISABLED, lift.getMode());
        lift.manual(0.15);
        when(top.isPressed()).thenReturn(true);
        clearInvocations(motor);
        lift.periodic();
        verify(motor).setPower(0);
        when(top.isPressed()).thenReturn(false);
        when(bottom.isPressed()).thenReturn(true);
        lift.homeHeld(true);
        lift.periodic();
        assertTrue(lift.isHomed());
        verify(motor).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        when(top.isPressed()).thenReturn(true);
        lift.periodic();
        assertFalse(lift.isHomed());
        assertEquals(ElevatorSubsystem.Mode.DISABLED, lift.getMode());
        op.end();
    }

    @Test public void servoDoesNotMoveOnInitOrStopAndClipsSelectedTarget() {
        Map<String, Object> devices = new HashMap<>();
        Servo claw = mock(Servo.class);
        devices.put("claw", claw);
        ServoTeleOp op = opMode(ServoTeleOp.class, devices);
        op.initialize();
        verify(claw, never()).setPosition(anyDouble());
        MMRobot.getInstance().servoSubsystem.moveTo(1);
        verify(claw).setPosition(0.55);
        clearInvocations(claw);
        op.end();
        verifyNoInteractions(claw);
    }

    @Test public void failedInitializeStillStopsAndResetsRobot() throws InterruptedException {
        Map<String, Object> devices = motorMap("testMotor");
        manualDrive op = opMode(manualDrive.class, devices);
        doAnswer(invocation -> {
            invocation.callRealMethod();
            MMRobot.getInstance().motorSubsystem.setPower(0.6);
            throw new IllegalStateException("simulated initialization failure");
        }).when(op).initialize();
        try {
            op.runOpMode();
            fail("Expected simulated failure");
        } catch (IllegalStateException expected) {
            assertEquals("simulated initialization failure", expected.getMessage());
        }
        DcMotor motor = (DcMotor) devices.get("testMotor");
        verify(motor).setPower(0.6);
        verify(motor, atLeast(2)).setPower(0);
        assertNull(MMRobot.getInstance().motorSubsystem);
    }
}
