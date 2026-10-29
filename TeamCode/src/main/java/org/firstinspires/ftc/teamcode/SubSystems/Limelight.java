package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.RobotLog;
import com.seattlesolvers.solverslib.command.SubsystemBase;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;

import java.util.Collections;
import java.util.List;

/**
 * Subsystem for Limelight 3A vision sensor.
 */
public class Limelight extends SubsystemBase {
    private static final String TAG = "Limelight";
    private Limelight3A limelight;
    private LLResult latestResult;
    private boolean isInitialized = false;

    public Limelight() {
        this("limelight");
    }

    public Limelight(String deviceName) {
        super();
        try {
            if (MMRobot.getInstance() != null && MMRobot.getInstance().hardwareMap != null) {
                initLimelight(MMRobot.getInstance().hardwareMap, deviceName);
            }
        } catch (Exception e) {
            RobotLog.ee(TAG, "Failed initializing Limelight: " + e.getMessage());
            isInitialized = false;
        }
    }

    public Limelight(HardwareMap hardwareMap) {
        this(hardwareMap, "limelight");
    }

    public Limelight(HardwareMap hardwareMap, String deviceName) {
        super();
        initLimelight(hardwareMap, deviceName);
    }

    private void initLimelight(HardwareMap hardwareMap, String deviceName) {
        try {
            limelight = hardwareMap.get(Limelight3A.class, deviceName);
            limelight.setPollRateHz(100);
            limelight.start();
            limelight.pipelineSwitch(0);
            isInitialized = true;
            RobotLog.ii(TAG, "Limelight 3A initialized successfully with name '%s'", deviceName);
        } catch (Exception e) {
            limelight = null;
            isInitialized = false;
            RobotLog.ww(TAG, "Limelight 3A ('%s') could not be initialized: %s", deviceName, e.getMessage());
        }
    }

    @Override
    public void periodic() {
        update();
    }

    public void update() {
        if (limelight != null && limelight.isRunning()) {
            latestResult = limelight.getLatestResult();
        }
    }

    public Limelight3A getDevice() {
        return limelight;
    }

    public boolean isInitialized() {
        return isInitialized && limelight != null;
    }

    public boolean isConnected() {
        return limelight != null && limelight.isConnected();
    }

    public LLResult getLatestResult() {
        if (latestResult == null) {
            update();
        }
        return latestResult;
    }

    public boolean hasTarget() {
        LLResult res = getLatestResult();
        if (res == null) return false;
        if (res.isValid()) return true;
        List<LLResultTypes.FiducialResult> fids = res.getFiducialResults();
        return fids != null && !fids.isEmpty();
    }

    public double getTx() {
        LLResult res = getLatestResult();
        if (res == null) return 0.0;
        if (res.isValid()) return res.getTx();
        List<LLResultTypes.FiducialResult> fids = res.getFiducialResults();
        if (fids != null && !fids.isEmpty()) {
            return fids.get(0).getTargetXDegrees();
        }
        return 0.0;
    }

    public double getTy() {
        LLResult res = getLatestResult();
        if (res == null) return 0.0;
        if (res.isValid()) return res.getTy();
        List<LLResultTypes.FiducialResult> fids = res.getFiducialResults();
        if (fids != null && !fids.isEmpty()) {
            return fids.get(0).getTargetYDegrees();
        }
        return 0.0;
    }

    public double getTa() {
        LLResult res = getLatestResult();
        if (res == null) return 0.0;
        if (res.isValid()) return res.getTa();
        List<LLResultTypes.FiducialResult> fids = res.getFiducialResults();
        if (fids != null && !fids.isEmpty()) {
            return fids.get(0).getTargetArea();
        }
        return 0.0;
    }

    public Pose3D getBotPose() {
        LLResult res = getLatestResult();
        return (res != null && hasTarget()) ? res.getBotpose() : null;
    }

    public Pose3D getBotPoseMT2() {
        LLResult res = getLatestResult();
        return (res != null && hasTarget()) ? res.getBotpose_MT2() : null;
    }

    public List<LLResultTypes.FiducialResult> getFiducialResults() {
        LLResult res = getLatestResult();
        return (res != null) ? res.getFiducialResults() : Collections.emptyList();
    }

    public List<LLResultTypes.ColorResult> getColorResults() {
        LLResult res = getLatestResult();
        return (res != null) ? res.getColorResults() : Collections.emptyList();
    }

    public List<LLResultTypes.DetectorResult> getDetectorResults() {
        LLResult res = getLatestResult();
        return (res != null) ? res.getDetectorResults() : Collections.emptyList();
    }

    public void pipelineSwitch(int pipelineIndex) {
        if (limelight != null) {
            limelight.pipelineSwitch(pipelineIndex);
        }
    }

    public void setPollRateHz(int pollRateHz) {
        if (limelight != null) {
            limelight.setPollRateHz(pollRateHz);
        }
    }

    public void start() {
        if (limelight != null) {
            limelight.start();
        }
    }

    public void pause() {
        if (limelight != null) {
            limelight.pause();
        }
    }

    public void stop() {
        if (limelight != null) {
            limelight.stop();
        }
    }

    public LLStatus getStatus() {
        return limelight != null ? limelight.getStatus() : null;
    }

    /**
     * Sends formatted Limelight 3A telemetry data to the Driver Station.
     */
    public void updateTelemetry(Telemetry telemetry) {
        if (telemetry == null) return;

        if (!isInitialized || limelight == null) {
            telemetry.addLine("[Limelight 3A: NOT CONFIGURED]");
            telemetry.addData("Limelight Status", "Device 'limelight' not found in config");
            return;
        }

        update();

        telemetry.addLine("--- Limelight 3A ---");
        telemetry.addData("LL Connected", isConnected());

        LLStatus status = getStatus();
        LLResult res = getLatestResult();

        String pipeType = "";
        int pipeIdx = 0;
        if (status != null) {
            pipeType = status.getPipelineType() != null ? status.getPipelineType() : "";
            pipeIdx = status.getPipelineIndex();
            telemetry.addData("LL Status", "FPS: %.1f | Temp: %.1fC | Pipe: %d (%s)",
                    status.getFps(), status.getTemp(), pipeIdx, pipeType);
        } else if (res != null) {
            pipeType = res.getPipelineType() != null ? res.getPipelineType() : "";
            pipeIdx = res.getPipelineIndex();
            telemetry.addData("LL Pipeline", "Pipe: %d (%s)", pipeIdx, pipeType);
        }

        // Diagnostic alert if pipeline is not Fiducial / AprilTag
        if (!pipeType.isEmpty() && !pipeType.equalsIgnoreCase("fiducial") && !pipeType.equalsIgnoreCase("apriltag")) {
            telemetry.addData("WARNING", "Pipeline is '%s'! Set to 'Fiducial Markers' in Limelight UI", pipeType);
        }

        List<LLResultTypes.FiducialResult> fiducials = (res != null) ? res.getFiducialResults() : null;
        boolean hasFiducials = fiducials != null && !fiducials.isEmpty();

        if (hasTarget() || hasFiducials) {
            telemetry.addData("LL Target", "DETECTED");
            telemetry.addData("Target X (tx)", "%.2f deg", getTx());
            telemetry.addData("Target Y (ty)", "%.2f deg", getTy());
            telemetry.addData("Target Area (ta)", "%.2f%%", getTa());

            // AprilTags / Fiducials
            if (hasFiducials) {
                telemetry.addData("AprilTags Count", fiducials.size());
                for (LLResultTypes.FiducialResult fid : fiducials) {
                    telemetry.addData("Tag ID " + fid.getFiducialId(), "X: %.2f deg, Y: %.2f deg, Area: %.2f%%",
                            fid.getTargetXDegrees(), fid.getTargetYDegrees(), fid.getTargetArea());
                }
            }

            // Neural Detector results
            List<LLResultTypes.DetectorResult> detectors = (res != null) ? res.getDetectorResults() : null;
            if (detectors != null && !detectors.isEmpty()) {
                for (LLResultTypes.DetectorResult det : detectors) {
                    telemetry.addData("Detector (" + det.getClassName() + ")", "Conf: %.1f%%",
                            det.getConfidence() * 100.0);
                }
            }

            // BotPose 3D (AprilTag field coordinates)
            Pose3D botPose = (res != null) ? res.getBotpose() : null;
            if (botPose != null && botPose.getPosition() != null) {
                telemetry.addData("BotPose (X, Y, Z)", "(%.1f, %.1f, %.1f) in",
                        botPose.getPosition().x, botPose.getPosition().y, botPose.getPosition().z);
            }
        } else {
            telemetry.addData("LL Target", "No Target");
        }
    }

    public void updateTelemetry() {
        if (MMRobot.getInstance() != null && MMRobot.getInstance().telemetry != null) {
            updateTelemetry(MMRobot.getInstance().telemetry);
        }
    }
}
