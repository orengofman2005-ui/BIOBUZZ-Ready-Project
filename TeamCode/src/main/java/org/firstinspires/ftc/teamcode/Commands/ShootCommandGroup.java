package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.ParallelCommandGroup;

import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;

public class ShootCommandGroup {
    public static Command shootAll() {
        return new ParallelCommandGroup(
                MMRobot.getInstance().intakeSubsystem.setPowerCommand(1),
                MMRobot.getInstance().transferSubsystem.setPowerCommand(1)
        );

    }

}
