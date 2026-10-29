package org.firstinspires.ftc.teamcode.OpMode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMOpMode;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;
import org.firstinspires.ftc.teamcode.SubSystems.ElevatorTuning;

@TeleOp(name = "Elevator PID Lesson", group = "Learning")
public class ElevatorTeleOp extends MMOpMode {
    private boolean previousA, previousY, previousUp;

    @Override
    public void initialize() {
        MMRobot.getInstance().initJeruRobot().elevatorOnly().build(this);
    }

    @Override
    public void run() {
        MMRobot.getInstance().elevatorSubsystem.homeHeld(gamepad1.x);
        if (gamepad1.b) {
            MMRobot.getInstance().elevatorSubsystem.disable();
        } else if (!gamepad1.x) {
            if (gamepad1.right_bumper && gamepad1.dpad_up) {
                MMRobot.getInstance().elevatorSubsystem.manual(0.15);
            } else if (gamepad1.right_bumper && gamepad1.dpad_down) {
                MMRobot.getInstance().elevatorSubsystem.manual(-0.15);
            } else {
                MMRobot.getInstance().elevatorSubsystem.releaseManual();
                if (gamepad1.a && !previousA) MMRobot.getInstance().elevatorSubsystem.setTarget(0);
                if (gamepad1.y && !previousY)
                    MMRobot.getInstance().elevatorSubsystem.setTarget(ElevatorTuning.HIGH_TICKS);
                if (gamepad1.dpad_up && !previousUp)
                    MMRobot.getInstance().elevatorSubsystem.setTarget(ElevatorTuning.MID_TICKS);
            }
        }
        previousA = gamepad1.a;
        previousY = gamepad1.y;
        previousUp = gamepad1.dpad_up;
        super.run(); // Scheduler calls the subsystem's periodic PID loop.
        telemetry.addData("Position / Target", "%d / %.0f",
                MMRobot.getInstance().elevatorSubsystem.getPosition(), MMRobot.getInstance().elevatorSubsystem.getTarget());
        telemetry.addData("Power", MMRobot.getInstance().elevatorSubsystem.getPower());
        telemetry.addData("Homed / Mode", "%s / %s", MMRobot.getInstance().elevatorSubsystem.isHomed(), MMRobot.getInstance().elevatorSubsystem.getMode());
        telemetry.addData("Bottom / Top", "%s / %s", MMRobot.getInstance().elevatorSubsystem.atBottom(), MMRobot.getInstance().elevatorSubsystem.atTop());
        telemetry.addData("Status", MMRobot.getInstance().elevatorSubsystem.getStatus());
        telemetry.update();
    }

    @Override
    public void end() { super.end(); }
}
