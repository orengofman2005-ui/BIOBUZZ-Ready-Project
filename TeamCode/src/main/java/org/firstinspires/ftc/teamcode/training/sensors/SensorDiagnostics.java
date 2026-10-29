package org.firstinspires.ftc.teamcode.training.sensors;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.TouchSensor;

/** Read-only diagnostic loop. Match these two names to the robot configuration. */
@TeleOp(name = "Training Sensor Diagnostics")
public class SensorDiagnostics extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, "intake");
        TouchSensor bottom = hardwareMap.get(TouchSensor.class, "liftBottom");
        motor.setPower(0);
        try {
            waitForStart();
            while (opModeIsActive()) {
                telemetry.addData("Encoder ticks", motor.getCurrentPosition());
                telemetry.addData("Velocity ticks/s", motor.getVelocity());
                telemetry.addData("Bottom pressed", bottom.isPressed());
                telemetry.update();
                idle();
            }
        } finally {
            motor.setPower(0);
        }
    }
}
