package org.firstinspires.ftc.teamcode.training;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.seattlesolvers.solverslib.command.CommandScheduler;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Commands.TimedFeedGroup;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMOpMode;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;
import org.firstinspires.ftc.teamcode.Libraries.pedroPathing.FollowPathCommand;
import org.firstinspires.ftc.teamcode.SubSystems.DriveTrain;
import org.junit.After;
import org.junit.Test;

public class LegacyAdapterTest {
    @After public void cleanup() { mock(MMOpMode.class, CALLS_REAL_METHODS).end(); }

    @Test public void scaledFollowIsStartedExactlyOnceAndCancellationBreaksFollowing() {
        MMRobot robot = MMRobot.getInstance();
        robot.driveTrain = mock(DriveTrain.class);
        Follower follower = mock(Follower.class);
        PathChain path = mock(PathChain.class);
        FollowPathCommand command = new FollowPathCommand(follower, path, false, .3);
        command.initialize();
        verify(follower).followPath(path, .3, false);
        verifyNoMoreInteractions(follower);
        command.end(true);
        verify(follower).breakFollowing();
    }

    @Test public void feedLessonNeedsOnlyTwoMotorsAndCancellationStopsBoth() {
        CommandScheduler.getInstance().reset();
        MMRobot.getInstance().resetRobot();
        OpMode op = mock(OpMode.class);
        op.hardwareMap = mock(HardwareMap.class);
        op.gamepad1 = mock(Gamepad.class); op.gamepad2 = mock(Gamepad.class);
        op.telemetry = mock(Telemetry.class);
        DcMotorEx intake = mock(DcMotorEx.class), transfer = mock(DcMotorEx.class);
        when(op.hardwareMap.get(DcMotorEx.class, "intake")).thenReturn(intake);
        when(op.hardwareMap.get(DcMotorEx.class, "transfer")).thenReturn(transfer);
        MMRobot robot = MMRobot.getInstance();
        robot.initJeruRobot().feedOnly().build(op);
        assertNull(robot.driveTrain);
        assertNull(robot.localizer);
        verify(op.hardwareMap).get(DcMotorEx.class, "intake");
        verify(op.hardwareMap).get(DcMotorEx.class, "transfer");
        verifyNoMoreInteractions(op.hardwareMap);
        TimedFeedGroup feed = new TimedFeedGroup(robot.intakeSubsystem, robot.transferSubsystem, .3, 1000);
        feed.schedule();
        verify(intake).setPower(.3); verify(transfer).setPower(.3);
        feed.cancel();
        verify(intake).setPower(0); verify(transfer).setPower(0);
    }
}
