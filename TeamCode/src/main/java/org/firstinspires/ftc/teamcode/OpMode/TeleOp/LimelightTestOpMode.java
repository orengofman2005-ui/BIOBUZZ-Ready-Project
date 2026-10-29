package org.firstinspires.ftc.teamcode.OpMode.TeleOp;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.sun.tools.javac.util.List;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

/**
 * Dedicated test OpMode for Limelight 3A.
 * Allows quick diagnosis of camera connection, FPS, target acquisition,
 * pipeline switching, and telemetry feedback.
 */
@TeleOp(name = "Limelight 3A Test", group = "Vision")
public class LimelightTestOpMode extends LinearOpMode {

    private Limelight3A limelight;
    private int currentPipeline = 0;
    private boolean lastDpadUp = false;
    private boolean lastDpadDown = false;

    @Override
    public void runOpMode() {
        telemetry.addLine("Initializing Limelight 3A...");
        telemetry.update();

        try {
            limelight = hardwareMap.get(Limelight3A.class, "limelight");
            limelight.setPollRateHz(100);
            limelight.start();
            limelight.pipelineSwitch(0);
            telemetry.addLine("Limelight 3A Initialized successfully!");
        } catch (Exception e) {
            telemetry.addData("Error finding 'limelight'", e.getMessage());
            telemetry.addLine("Check Robot Configuration on Driver Station!");
        }
        telemetry.addLine("Press Play to begin polling Limelight data.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (limelight == null) {
                telemetry.addLine("ERROR: Limelight 3A is not initialized.");
                telemetry.update();
                sleep(100);
                continue;
            }

            // Pipeline switching via D-Pad
            boolean dpadUp = gamepad1.dpad_up;
            boolean dpadDown = gamepad1.dpad_down;

            if (dpadUp && !lastDpadUp) {
                currentPipeline++;
                limelight.pipelineSwitch(currentPipeline);
            } else if (dpadDown && !lastDpadDown) {
                if (currentPipeline > 0) currentPipeline--;
                limelight.pipelineSwitch(currentPipeline);
            }
            lastDpadUp = dpadUp;
            lastDpadDown = dpadDown;

            // Retrieve status and results
            LLStatus status = limelight.getStatus();
            LLResult result = limelight.getLatestResult();

            String pipeType = (status != null && status.getPipelineType() != null) ? status.getPipelineType() :
                              (result != null && result.getPipelineType() != null) ? result.getPipelineType() : "unknown";

            telemetry.addLine("=== LIMELIGHT 3A STATUS ===");
            telemetry.addData("Connected", limelight.isConnected());
            telemetry.addData("Active Pipeline", currentPipeline + " (" + pipeType + ")");

            if (status != null) {
                telemetry.addData("Status", "FPS: %.1f | Temp: %.1f C | CPU: %.1f%%",
                        status.getFps(), status.getTemp(), status.getCpu());
            }

            // Warning if not configured for AprilTags
            if (!pipeType.equalsIgnoreCase("fiducial") && !pipeType.equalsIgnoreCase("apriltag")) {
                telemetry.addLine("\n[!] WARNING: Pipeline type is NOT 'fiducial'!");
                telemetry.addLine("Set pipeline to 'Fiducial Markers' in Limelight Web UI (http://192.168.43.1:5807)");
            }

            List<LLResultTypes.FiducialResult> fiducials = (result != null) ? (List<LLResultTypes.FiducialResult>) result.getFiducialResults() : null;
            boolean hasFiducials = fiducials != null && !fiducials.isEmpty();

            telemetry.addLine("\n=== TARGET DATA ===");
            if (result != null && (result.isValid() || hasFiducials)) {
                telemetry.addData("Target Found", "YES");
                telemetry.addData("tx (Horizontal)", "%.2f deg", result.getTx());
                telemetry.addData("ty (Vertical)", "%.2f deg", result.getTy());
                telemetry.addData("ta (Area)", "%.2f%%", result.getTa());
                telemetry.addData("Latency", "Parse: %.1fms | Target: %.1fms",
                        result.getParseLatency(), result.getTargetingLatency());

                // AprilTag / Fiducial tracking
                if (hasFiducials) {
                    telemetry.addLine("\n--- AprilTags Detected ---");
                    for (LLResultTypes.FiducialResult fid : fiducials) {
                        telemetry.addData("ID " + fid.getFiducialId(),
                                "X: %.2f deg | Y: %.2f deg | Area: %.2f%%",
                                fid.getTargetXDegrees(), fid.getTargetYDegrees(), fid.getTargetArea());
                    }
                }

                // Object detection / ML
                List<LLResultTypes.DetectorResult> detectors = (List<LLResultTypes.DetectorResult>) result.getDetectorResults();
                if (detectors != null && !detectors.isEmpty()) {
                    telemetry.addLine("\n--- Detectors ---");
                    for (LLResultTypes.DetectorResult det : detectors) {;
                        telemetry.addData("Detector (" + det.getClassName() + ")",
                                "Conf: %.1f%% | X: %.2f deg | Y: %.2f deg",
                                det.getConfidence() * 100.0, det.getTargetXDegrees(), det.getTargetYDegrees());
                    }
                }

                // Botpose 3D Field Localization
                Pose3D botpose = result.getBotpose();
                if (botpose != null && botpose.getPosition() != null) {
                    telemetry.addLine("\n--- Botpose 3D ---");
                    telemetry.addData("Position (X, Y, Z)", "(%.1f, %.1f, %.1f) in",
                            botpose.getPosition().x, botpose.getPosition().y, botpose.getPosition().z);
                }
            } else {
                telemetry.addData("Target Found", "NO TARGET");
            }

            telemetry.addLine("\n[Controls]: Dpad Up/Down to switch pipelines");
            telemetry.update();
        }

        if (limelight != null) {
            limelight.stop();
        }
    }
}
