package org.firstinspires.ftc.teamcode.OpMode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

/** Separate SDK example, only for hardware configured as a continuous servo. */
@TeleOp(name = "Continuous Servo Lesson", group = "Learning")
public class ContinuousServoTeleOp extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        CRServo roller = hardwareMap.get(CRServo.class, "roller");
        roller.setPower(0);
        waitForStart();
        try {
            while (opModeIsActive()) {
                double power = -gamepad1.left_stick_y;
                if (Math.abs(power) < 0.05) power = 0;
                roller.setPower(power);
                telemetry.addData("Requested power", power);
                telemetry.update();
                idle();
            }
        } finally {
            roller.setPower(0);
        }
    }
}
