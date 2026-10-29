package org.firstinspires.ftc.teamcode.training.limelight;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import java.util.List;

/** Reads one camera. Displays a turn suggestion and never accesses motors. */
@TeleOp(name = "Limelight Aim Preview", group = "Training")
public final class LimelightAimPreview extends LinearOpMode {
    private static final int PIPELINE = 0;
    private static final int TARGET_ID = 1; // EXAMPLE. Set for your practice tag.
    private static final long MAX_AGE_MS = 150; // Tune with frame rate and motion.

    @Override public void runOpMode() {
        Limelight3A camera = hardwareMap.get(Limelight3A.class, "limelight");
        AimMath.FrameWatchdog watchdog = new AimMath.FrameWatchdog();
        try {
            camera.setPollRateHz(100);
            camera.pipelineSwitch(PIPELINE);
            camera.start();
            telemetry.addLine("Preview only. Configure tag ID and pipeline first.");
            telemetry.update();
            waitForStart();
            while (opModeIsActive()) {
                LLResult frame = camera.getLatestResult();
                boolean connected = camera.isConnected();
                double now = System.nanoTime() / 1e9;
                double tx = Double.NaN;
                boolean found = false;
                long age = frame == null ? Long.MAX_VALUE : frame.getStaleness();
                boolean advancing = frame != null
                        && watchdog.fresh(frame.getTimestamp(), now, .150);
                String state;
                if (!connected) state = "DISCONNECTED";
                else if (frame == null) state = "NO_FRAME";
                else if (age < 0 || age > MAX_AGE_MS) state = "STALE_RECEIPT";
                else if (!advancing) state = "FROZEN_OR_RESET_FRAME";
                else if (frame.getPipelineIndex() != PIPELINE) state = "WAIT_PIPELINE";
                else if (!frame.isValid()) state = "NO_VALID_TARGET";
                else {
                    List<LLResultTypes.FiducialResult> tags = frame.getFiducialResults();
                    if (tags != null) {
                        for (LLResultTypes.FiducialResult tag : tags) {
                            if (tag.getFiducialId() == TARGET_ID) {
                                tx = tag.getTargetXDegrees();
                                found = Double.isFinite(tx);
                                break;
                            }
                        }
                    }
                    state = found ? "USABLE" : "TARGET_ID_MISSING";
                }
                boolean usable = "USABLE".equals(state);
                double turn = AimMath.turn(usable, tx, 0, .02, 1, .20, 1);
                telemetry.addData("State", state);
                telemetry.addData("Connected", connected);
                telemetry.addData("Wanted pipeline / ID", "%d / %d", PIPELINE, TARGET_ID);
                telemetry.addData("Age ms", age);
                telemetry.addData("Camera stamp", frame == null ? "none" : frame.getTimestamp());
                telemetry.addData("Actual pipeline", frame == null ? -1 : frame.getPipelineIndex());
                telemetry.addData("Selected tag tx deg", tx);
                telemetry.addData("Turn suggestion only", turn);
                telemetry.addLine("No motor output. Reset OpMode after camera timestamp reset.");
                telemetry.update();
                idle();
            }
        } finally {
            camera.stop();
        }
    }
}
