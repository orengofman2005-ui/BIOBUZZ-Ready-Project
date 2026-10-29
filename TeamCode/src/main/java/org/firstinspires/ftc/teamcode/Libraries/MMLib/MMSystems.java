package org.firstinspires.ftc.teamcode.Libraries.MMLib;

// הסבר לימודי: יצירת החומרה של מצב הלימוד שנבחר בלבד; במצב מלא מאתחלת גם Pedro ומנגנונים.

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.RobotLog;
import com.seattlesolvers.solverslib.gamepad.GamepadEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Libraries.CuttlefishFTCBridge.src.devices.CuttleRevHub;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.Utils.OpModeType;
import org.firstinspires.ftc.teamcode.SubSystems.DriveTrain;
import org.firstinspires.ftc.teamcode.SubSystems.MecanumSubsystem;
import org.firstinspires.ftc.teamcode.SubSystems.ElevatorSubsystem;
import org.firstinspires.ftc.teamcode.SubSystems.ServoSubsystem;
import org.firstinspires.ftc.teamcode.SubSystems.SingleMotorSubsystem;
import org.firstinspires.ftc.teamcode.SubSystems.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.SubSystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.SubSystems.Limelight;
import org.firstinspires.ftc.teamcode.SubSystems.TransferSubsystem;

/** מאתחלת רק את החומרה הנחוצה לשיעור שנבחר ב־MMRobot.Builder. */
public class MMSystems {
    private final String controlHubName = "Control Hub";
    public CuttleRevHub controlHub;
    private final String expansionHubName = "Expansion Hub 2";
    public CuttleRevHub expansionHub;
    private final String servoHub1Name = null;//TODO: Servo Hub 3
    public CuttleRevHub servoHub1;
    private final String servoHub2Name = null;
    public CuttleRevHub servoHub2;


    public HardwareMap hardwareMap;
    public Telemetry telemetry;
    public GamepadEx gamepadEx1;
    public GamepadEx gamepadEx2;
    public VoltageSensor battery;
    public GoBildaPinpointDriver localizer;

    public IntakeSubsystem intakeSubsystem;
    public TransferSubsystem transferSubsystem;
    public ShooterSubsystem shooterSubsystem;
    public DriveTrain driveTrain;
    public SingleMotorSubsystem motorSubsystem;
    public MecanumSubsystem mecanumSubsystem;
    public ElevatorSubsystem elevatorSubsystem;
    public ServoSubsystem servoSubsystem;
    public Limelight limelight;

    private void initDriveTrainDefaultCommand() {
        MMRobot.getInstance().driveTrain.setDefaultCommand(
                MMRobot.getInstance().driveTrain.driveCommand());

    }

    private void initSystems(OpMode opMode) {
        // Drive-only setup: keep the Control Hub; auxiliary hub initialization is commented out.
        this.controlHub = new CuttleRevHub(hardwareMap, controlHubName);
//         if (MMRobot.getInstance().opModeType != OpModeType.EXPERIMENTING_NO_EXPANSION &&
//             MMRobot.getInstance().opModeType != OpModeType.EXPERIMENTING_NO_EXPANSION_NO_SERVOHUB) {
//                 this.expansionHub = new CuttleRevHub(hardwareMap, expansionHubName);
//         }

//         if (MMRobot.getInstance().opModeType != OpModeType.EXPERIMENTING_NO_SERVOHUB ||
//             MMRobot.getInstance().opModeType != OpModeType.EXPERIMENTING_NO_EXPANSION_NO_SERVOHUB) {
//                 if (servoHub1Name != null) {
//                     this.servoHub1 = new CuttleRevHub(hardwareMap, servoHub1Name);
//                 }
//                 if (servoHub2Name != null) {
//                     this.servoHub2 = new CuttleRevHub(hardwareMap, servoHub2Name);
//                 }
//         }

        gamepadEx1 = new GamepadEx(opMode.gamepad1);
        gamepadEx2 = new GamepadEx(opMode.gamepad2);

        battery = hardwareMap.voltageSensor.iterator().next();

        initSubsystems();
    }
    private void initSubsystems(){
        this.driveTrain = new DriveTrain();
        // Drive-only setup: auxiliary subsystem initialization is commented out.
//         try {
//             this.intakeSubsystem = new IntakeSubsystem();
//         } catch (Exception e) {
//             RobotLog.ww("MMSystems", "Failed to init intakeSubsystem: " + e.getMessage());
//         }
//         try {
//             this.transferSubsystem = new TransferSubsystem();
//         } catch (Exception e) {
//             RobotLog.ww("MMSystems", "Failed to init transferSubsystem: " + e.getMessage());
//         }
//         try {
//             this.shooterSubsystem = new ShooterSubsystem();
//         } catch (Exception e) {
//             RobotLog.ww("MMSystems", "Failed to init shooterSubsystem: " + e.getMessage());
//         }
//         this.limelight = new Limelight();
    }
    /** מנקה הפניות מהפעלה קודמת לפני בחירת אחד ממצבי הלימוד. */
    private void clearSystems() {
        controlHub = expansionHub = servoHub1 = servoHub2 = null;
        localizer = null;
        battery = null;
        driveTrain = null;
        intakeSubsystem = null;
        transferSubsystem = null;
        shooterSubsystem = null;
        limelight = null;
        motorSubsystem = null;
        mecanumSubsystem = null;
        elevatorSubsystem = null;
        servoSubsystem = null;
    }

    protected void initJeruSystems(OpMode opMode) {
        MMRobot robot = MMRobot.getInstance();
        clearSystems();
        gamepadEx1 = new GamepadEx(opMode.gamepad1);
        gamepadEx2 = new GamepadEx(opMode.gamepad2);
        // שיעורי חומרה עצמאיים: אין חיפוש של חיישנים או מנגנונים שלא נדרשו.
        if (robot.feedOnly) {
            intakeSubsystem = new IntakeSubsystem();
            transferSubsystem = new TransferSubsystem();
        } else if (robot.servoOnly) {
            servoSubsystem = new ServoSubsystem(hardwareMap);
        } else if (robot.elevatorOnly) {
            elevatorSubsystem = new ElevatorSubsystem(hardwareMap);
        } else if (robot.mecanumOnly) {
            mecanumSubsystem = new MecanumSubsystem(hardwareMap);
        } else if (robot.singleMotorName != null) {
            motorSubsystem = new SingleMotorSubsystem(hardwareMap, robot.singleMotorName);
        } else {
            initSystems(opMode);
            // Pedro מגדיר ומאפס את Pinpoint פעם אחת, דרך Constants.
            if (robot.opModeType == OpModeType.AUTO) {
                localizer = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
            }
            if (robot.initDriveTrain && robot.opModeType != OpModeType.AUTO) {
                initDriveTrainDefaultCommand();
            }
        }
    }
}
