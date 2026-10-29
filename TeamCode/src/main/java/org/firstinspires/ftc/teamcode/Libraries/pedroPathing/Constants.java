package org.firstinspires.ftc.teamcode.Libraries.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {

    public static FollowerConstants followerConstants =
            new FollowerConstants()
                    .mass(13)
                    .forwardZeroPowerAcceleration(-35)
                    .lateralZeroPowerAcceleration(-66.3)
                    .useSecondaryTranslationalPIDF(false)
                    .useSecondaryDrivePIDF(false)
                    .secondaryHeadingPIDFCoefficients(new PIDFCoefficients(1.7, 0, 0, 0.025))
                    .centripetalScaling(0.0003)
                    .translationalPIDFCoefficients(new PIDFCoefficients(0.17, 0, 0.021, 0.015))
                    .headingPIDFCoefficients(new PIDFCoefficients(1.4, 0, 0.06, 0.025))
                    .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.009, 0, 0.001, 0, 0));

    public static MecanumConstants driveConstants =
            new MecanumConstants()
                    .leftFrontMotorName("FL")
                    .leftRearMotorName("BL")
                    .rightFrontMotorName("FR")
                    .rightRearMotorName("BR")
                    .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
                    .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
                    .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
                    .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
                    .useBrakeModeInTeleOp(true)
                    .xVelocity(85.21)
                    .yVelocity(66);

    public static PinpointConstants localizerConstants =
            new PinpointConstants()
                    .forwardPodY(10 / 2.54)
                    .strafePodX(0 / 2.54)
                    .distanceUnit(DistanceUnit.INCH)
                    .hardwareMapName("pinpoint")
                    .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
                    .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
                    .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .build();
    }
}
