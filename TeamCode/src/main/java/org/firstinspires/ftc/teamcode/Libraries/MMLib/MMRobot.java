package org.firstinspires.ftc.teamcode.Libraries.MMLib;

// הסבר לימודי: האובייקט המרכזי וה־Builder: בחירה במנוע יחיד, מאקאנום, מעלית, סרבו או ברובוט המלא.

import static com.qualcomm.robotcore.util.RobotLog.a;
import static com.qualcomm.robotcore.util.RobotLog.d;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Libraries.MMLib.Utils.AllianceColor;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.Utils.OpModeType;


public class MMRobot extends MMSystems {
    private static MMRobot instance;
    public OpModeType opModeType;
    public AllianceColor allianceColor;
    public Boolean initDriveTrain = true;
    public int startAngle;
    public String singleMotorName;
    public boolean mecanumOnly;
    public boolean elevatorOnly;
    public boolean servoOnly;
    public boolean feedOnly;

    private MMRobot(){}

    public static synchronized MMRobot getInstance() {
        if (instance == null) {
            instance = new MMRobot();
        }
        return instance;
    }

    public void resetRobot() {
        instance = null;
    }

    private void initJeruBasics(OpMode opMode, OpModeType opModeType, AllianceColor allianceColor, int startAngle, boolean initDriveTrain, String singleMotorName, boolean mecanumOnly, boolean elevatorOnly, boolean servoOnly, boolean feedOnly) {
        hardwareMap = opMode.hardwareMap;
        telemetry = opMode.telemetry;

        this.opModeType = opModeType;
        this.allianceColor = allianceColor;
        this.initDriveTrain = initDriveTrain;
        this.startAngle = startAngle;
        this.singleMotorName = singleMotorName;
        this.mecanumOnly = mecanumOnly;
        this.elevatorOnly = elevatorOnly;
        this.servoOnly = servoOnly;
        this.feedOnly = feedOnly;

        initJeruSystems(opMode);
    }

    public Builder initJeruRobot() {
        return new Builder();
    }

    public static class Builder {
        private OpModeType opModeType = OpModeType.TELEOP;
        private AllianceColor allianceColor = AllianceColor.RED;
        private boolean initDriveTrain = true;
        private String singleMotorName;
        private boolean mecanumOnly;
        private boolean elevatorOnly;
        private boolean servoOnly;
        private boolean feedOnly;
        private int  startAngle = 0;

        public Builder opModeType(OpModeType opModeType) {
            this.opModeType = opModeType;
            return this;
        }
        public Builder allianceColor(AllianceColor allianceColor) {
            this.allianceColor = allianceColor;
            return this;
        }
        public Builder angle(int startAngle) {
            this.startAngle = startAngle;
            return this;
        }
        public Builder WithoutDriveTrain() {
            this.initDriveTrain = false;
            return this;
        }

        /** Initialize only one motor and the gamepads, without drivetrain or localization. */
        public Builder singleMotor(String motorName) {
            this.feedOnly = false;
            if (motorName == null || motorName.trim().isEmpty()) {
                throw new IllegalArgumentException("Motor name must match the robot configuration");
            }
            this.singleMotorName = motorName.trim();
            this.mecanumOnly = false;
            this.elevatorOnly = false;
            this.initDriveTrain = false;
            this.servoOnly = false;
            return this;
        }

        /** Lesson-only drivetrain, without Pedro or localization. */
        public Builder mecanumOnly() {
            this.feedOnly = false;
            this.mecanumOnly = true;
            this.elevatorOnly = false;
            this.singleMotorName = null;
            this.initDriveTrain = false;
            this.servoOnly = false;
            return this;
        }

        /** One educational elevator, with encoder and limit switches. */
        public Builder elevatorOnly() {
            this.feedOnly = false;
            this.elevatorOnly = true;
            this.mecanumOnly = false;
            this.singleMotorName = null;
            this.initDriveTrain = false;
            this.servoOnly = false;
            return this;
        }

        /** One positional servo for the servo lesson. */
        public Builder servoOnly() {
            this.feedOnly = false;
            this.servoOnly = true;
            this.elevatorOnly = false;
            this.mecanumOnly = false;
            this.singleMotorName = null;
            this.initDriveTrain = false;
            return this;
        }

        /** הרחבה לימודית: שני מנועי הזנה בלבד, בלי הנעה ובלי Pinpoint. */
        public Builder feedOnly() {
            this.feedOnly = true;
            this.singleMotorName = null;
            this.mecanumOnly = this.elevatorOnly = this.servoOnly = false;
            this.initDriveTrain = false;
            return this;
        }

        public void build(OpMode opMode) {
            MMRobot.getInstance().initJeruBasics(opMode, opModeType, allianceColor, startAngle, initDriveTrain, singleMotorName, mecanumOnly, elevatorOnly, servoOnly, feedOnly);
        }
    }
}
