package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;

import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;

public class IntakeCommandGroup {
    public static Command basicFeed() {
        return new ParallelCommandGroup(
                MMRobot.getInstance().intakeSubsystem.setPowerCommand(1),
                MMRobot.getInstance().transferSubsystem.setPowerCommand(1)
        );
    }

    public static Command stopIntake() {
        return new ParallelCommandGroup(
                MMRobot.getInstance().intakeSubsystem.setPowerCommand(0),
                MMRobot.getInstance().transferSubsystem.setPowerCommand(0)
        );
    }
    public static Command outIntake() {
        return new ParallelCommandGroup(
                MMRobot.getInstance().intakeSubsystem.setPowerCommand(-1),
                MMRobot.getInstance().transferSubsystem.setPowerCommand(-1)
        );
    }
}
