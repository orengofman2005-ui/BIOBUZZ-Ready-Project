package org.firstinspires.ftc.teamcode.Commands;

import com.seattlesolvers.solverslib.command.ParallelCommandGroup;
import com.seattlesolvers.solverslib.command.SequentialCommandGroup;
import com.seattlesolvers.solverslib.command.WaitCommand;
import org.firstinspires.ftc.teamcode.SubSystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.SubSystems.TransferSubsystem;
import java.util.Objects;

/** Educational timed feed. Both motors stop on completion or cancellation. */
public class TimedFeedGroup extends SequentialCommandGroup {
    private final IntakeSubsystem intake;
    private final TransferSubsystem transfer;

    public TimedFeedGroup(IntakeSubsystem intake, TransferSubsystem transfer,
                          double power, long durationMillis) {
        this.intake = Objects.requireNonNull(intake, "intake");
        this.transfer = Objects.requireNonNull(transfer, "transfer");
        if (!Double.isFinite(power) || power < -1 || power > 1) {
            throw new IllegalArgumentException("power must be finite and in [-1, 1]");
        }
        if (durationMillis < 0) {
            throw new IllegalArgumentException("durationMillis must be non-negative");
        }
        addCommands(
            new ParallelCommandGroup(intake.setPowerCommand(power),
                                     transfer.setPowerCommand(power)),
            new WaitCommand(durationMillis),
            new ParallelCommandGroup(intake.stopCommand(), transfer.stopCommand())
        );
    }

    @Override
    public void end(boolean interrupted) {
        try {
            super.end(interrupted);
        } finally {
            try {
                intake.stop();
            } finally {
                transfer.stop();
            }
        }
    }
}
