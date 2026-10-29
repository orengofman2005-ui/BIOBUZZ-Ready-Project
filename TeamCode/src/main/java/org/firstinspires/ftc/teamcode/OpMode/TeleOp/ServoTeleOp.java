package org.firstinspires.ftc.teamcode.OpMode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMOpMode;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;
import org.firstinspires.ftc.teamcode.SubSystems.ServoTuning;

@TeleOp(name = "Positional Servo Lesson", group = "Learning")
public class ServoTeleOp extends MMOpMode {
    private final ElapsedTime timer = new ElapsedTime();
    private boolean previousA, previousB, previousX;
    private double previousTime = Double.NaN;

    @Override
    public void initialize() {
        MMRobot.getInstance().initJeruRobot().servoOnly().build(this);
    }

    @Override
    public void run() {
        double now = timer.seconds();
        double dt = Double.isNaN(previousTime) ? 0 : now - previousTime;
        previousTime = now;
        if (gamepad1.a && !previousA) MMRobot.getInstance().servoSubsystem.openCommand().schedule();
        if (gamepad1.b && !previousB) MMRobot.getInstance().servoSubsystem.closeCommand().schedule();
        if (gamepad1.x && !previousX) MMRobot.getInstance().servoSubsystem.moveTo(0.50);
        if (gamepad1.right_bumper && dt > 0 && dt <= 0.2) {
            double stick = -gamepad1.left_stick_y;
            if (Math.abs(stick) >= 0.05) {
                MMRobot.getInstance().servoSubsystem.adjust(stick * ServoTuning.ADJUST_RATE * dt);
            }
        }
        previousA = gamepad1.a;
        previousB = gamepad1.b;
        previousX = gamepad1.x;
        super.run();
        telemetry.addData("Target selected", MMRobot.getInstance().servoSubsystem.hasTarget());
        telemetry.addData("Target", MMRobot.getInstance().servoSubsystem.getTarget());
        telemetry.addData("SDK last request", MMRobot.getInstance().servoSubsystem.getLastRequestedPosition());
        telemetry.addData("Control", "A open / B close / X center test");
        telemetry.addData("Adjust", "Hold right bumper + left Y after selecting a target");
        telemetry.update();
    }

    @Override
    public void end() { super.end(); }
}
