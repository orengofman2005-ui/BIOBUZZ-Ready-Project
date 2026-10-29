
package org.firstinspires.ftc.teamcode.SubSystems;


import com.acmerobotics.dashboard.FtcDashboard;

import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.seattlesolvers.solverslib.command.Command;
import com.seattlesolvers.solverslib.command.InstantCommand;
import com.seattlesolvers.solverslib.command.RunCommand;
import com.seattlesolvers.solverslib.command.SubsystemBase;
import com.seattlesolvers.solverslib.geometry.Vector2d;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.MMRobot;
import org.firstinspires.ftc.teamcode.Libraries.MMLib.Utils.OpModeType;
import org.firstinspires.ftc.teamcode.Libraries.pedroPathing.Constants;

import java.util.function.DoubleSupplier;

public class DriveTrain extends SubsystemBase {
    static double[][] transformationMatrix = {
            {1, 1, 1}, //frontLeft
            {-1, 1, 1}, //backLeft
            {-1, 1, -1}, //frontRight
            {1, 1, -1} //backRight
    };

    private final DcMotorEx motorFR;
    private final DcMotorEx motorFL;
    private final DcMotorEx motorBL;
    private final DcMotorEx motorBR;

    private final double slowmodeMultiplayer = 0.3;
    private final double slowmodeYawMultiplayer = 0.3;

    private final Follower follower;

    public DriveTrain() {
        super(); //register this subsystem, in order to schedule default command later on.

        motorFL = MMRobot.getInstance().hardwareMap.get(DcMotorEx.class, "FL");//2
        motorBL = MMRobot.getInstance().hardwareMap.get(DcMotorEx.class, "BL");//1
        motorBR = MMRobot.getInstance().hardwareMap.get(DcMotorEx.class, "BR");//3
        motorFR = MMRobot.getInstance().hardwareMap.get(DcMotorEx.class, "FR");//0

        motorFL.setDirection(DcMotorEx.Direction.REVERSE);
        motorBL.setDirection(DcMotorEx.Direction.REVERSE);

        motorFL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorFR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motorBR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // TeleOp drives directly from the sticks and does not need the Pinpoint.
        if (MMRobot.getInstance().opModeType == OpModeType.AUTO) {
            follower = Constants.createFollower(MMRobot.getInstance().hardwareMap);
        } else {
            follower = null;
        }
    }
    public Follower getFollower() {
        return follower;
    }
    public void updateFollower(){
        if (follower != null) follower.update();
    }
    public void activeFL(double pow) { motorFL.setPower(pow); }
    public void activeBL(double pow){
        motorBL.setPower(pow);
    }
    public void activeFR(double pow){
        motorFR.setPower(pow);
    }
    public void activeBR(double pow){
        motorBR.setPower(pow);
    }

    public void setYaw(double lastAngle){
        MMRobot.getInstance().localizer.setHeading(lastAngle, AngleUnit.DEGREES);

    }


    private double[] joystickToPower(double x, double y, double yaw) {

        //v = (x, y, yaw)^t (3x1)
        RealVector joystickVector = MatrixUtils.createRealVector(new double[] {
                x,
                y,
                yaw
        });

        RealMatrix matrixT = MatrixUtils.createRealMatrix(transformationMatrix); //4x3

        //calculation of the power needed by T constants
        RealVector powerVector = matrixT.operate(joystickVector); //p = Tv

        double[] powerArray = powerVector.toArray(); //4x1

        //normalize the array
        for(int i = 0; i < powerArray.length; i++) {
            powerArray[i] = powerArray[i] / Math.max(Math.abs(x) + Math.abs(y) + Math.abs(yaw), 1);
        }

        return powerArray;

    }

    private void setMotorPower(double[] power) {
        motorFL.setPower(power[0]);
        motorBL.setPower(power[1]);
        motorFR.setPower(power[2]);
        motorBR.setPower(power[3]);

//        updateTelemetry(power);
    }
    public void drive(double x, double y, double yaw) {
        setMotorPower(joystickToPower(x, y, yaw));
    }

    // Robot-oriented drive: no rotation using the robot heading.
    public Command driveCommand() {
        return new RunCommand(() -> drive(
                Math.pow(MMRobot.getInstance().gamepadEx1.getLeftX(), 3),
                Math.pow(MMRobot.getInstance().gamepadEx1.getLeftY(), 3),
                Math.pow(MMRobot.getInstance().gamepadEx1.getRightX(), 3)
        ), this);
    }

    public Command slowmodeDriveCommand() {
        return new RunCommand(() -> drive(
                Math.pow(MMRobot.getInstance().gamepadEx1.getLeftX(), 5) * slowmodeMultiplayer,
                Math.pow(MMRobot.getInstance().gamepadEx1.getLeftY(), 5) * slowmodeMultiplayer,
                Math.pow(MMRobot.getInstance().gamepadEx1.getRightX(), 5) * slowmodeYawMultiplayer
        ), this);
    }

    public void resetYaw() {
        MMRobot.getInstance().localizer.setHeading(0, AngleUnit.DEGREES);
    }
    public Command resetYawCommand(){
        return new InstantCommand(this::resetYaw);
    }
    private void fieldOrientedDrive(double x, double y, double yaw) {
        Vector2d joystickDirection = new Vector2d(x, y);
        Vector2d fieldOrientedVector = joystickDirection.rotateBy(
                MMRobot.getInstance().startAngle- MMRobot.getInstance().localizer.getHeading(AngleUnit.DEGREES));//TODO:check
        drive(fieldOrientedVector.getX(), fieldOrientedVector.getY(), yaw);
    }

    public Command fieldOrientedDriveCommand(DoubleSupplier x, DoubleSupplier y, DoubleSupplier yaw) {
        return new RunCommand(() -> fieldOrientedDrive(x.getAsDouble(),y.getAsDouble(),yaw.getAsDouble()), this);
    }
    public Command fieldOrientedDriveCommand() {
        return fieldOrientedDriveCommand(
                () -> Math.pow(MMRobot.getInstance().gamepadEx1.getLeftX(), 3),
                () -> Math.pow(MMRobot.getInstance().gamepadEx1.getLeftY(), 3),
                () -> Math.pow(MMRobot.getInstance().gamepadEx1.getRightX(), 3)
        );
    }
    public Command slowmodeFieldOrientedDriveCommand() {
        return slowmodeFieldOrientedDriveCommand(
                () -> MMRobot.getInstance().gamepadEx1.getLeftX(),
                () -> MMRobot.getInstance().gamepadEx1.getLeftY(),
                () -> MMRobot.getInstance().gamepadEx1.getRightX()
        );
    }
    public Command slowmodeFieldOrientedDriveCommand(DoubleSupplier x, DoubleSupplier y, DoubleSupplier yaw) {
        return fieldOrientedDriveCommand(
                () -> Math.pow(x.getAsDouble(),5) * slowmodeMultiplayer,
                () -> Math.pow(y.getAsDouble(),5) * slowmodeMultiplayer,
                () -> Math.pow(yaw.getAsDouble(),5) * slowmodeYawMultiplayer
        );
    }


    public void updateTelemetry(double[] power) {
        FtcDashboard.getInstance().getTelemetry().addData("frontLeft", power[0]);
        FtcDashboard.getInstance().getTelemetry().addData("backLeft", power[1]);
        FtcDashboard.getInstance().getTelemetry().addData("frontRight", power[2]);
        FtcDashboard.getInstance().getTelemetry().addData("backRight", power[3]);
        FtcDashboard.getInstance().getTelemetry().update();
    }

    public void turnWithScale(double turn) {
        double flPower = turn;
        double blPower = turn;
        double frPower = -turn;
        double brPower = -turn;

        // Scale power values safely between -1.0 and 1.0
        double max = Math.max(Math.abs(flPower), Math.abs(blPower));
        max = Math.max(max, Math.abs(frPower));
        max = Math.max(max, Math.abs(brPower));
        if (max > 1.0) {
            flPower /= max; blPower /= max; frPower /= max; brPower /= max;
        }

        motorFL.setPower(flPower);
        motorBL.setPower(blPower);
        motorFR.setPower(frPower);
        motorBR.setPower(brPower);
    }
}
