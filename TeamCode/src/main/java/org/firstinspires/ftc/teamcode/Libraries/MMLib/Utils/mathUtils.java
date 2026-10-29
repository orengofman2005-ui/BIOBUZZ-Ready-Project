package org.firstinspires.ftc.teamcode.Libraries.MMLib.Utils;

import static java.util.Objects.requireNonNull;

import org.opencv.core.Point;
import org.opencv.core.Range;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

/**
 * this class represents useful methods that may help you
 */
public abstract class mathUtils {


    /**
     * this method represents f(x),
     * while f is the linear function created by the 2 points.
     * in order to calculate the slope of the line, the method requires 2 points.
     * usually it's easier to think about the starting point, and the ending point,
     * but any 2 points will work.
     *
     * @param x      value
     * @param point1 any point on graph
     * @param point2 any point on graph
     * @return the value of x in the linear function (f(x))
     */
    public static double mapValuesLinear(double x, Point point1, Point point2) {
        double m = (point2.y - point1.y) / (point2.x - point1.x);
        return m * (x - point1.x) + point1.y;
    }

    public static double mapValuesLinear(DoubleSupplier x, Point point1, Point point2) {
        return mapValuesLinear(x.getAsDouble(), point1, point2);
    }

    /**
     * this method should help u map linearly a range of values from one range to another.
     * @param x input values
     * @param rangeInput range of the input
     * @param rangeOutput range of the output
     * @return the output on the graph of the linear function created.
     */
    public static double mapValuesLinearByRange(double x, Range rangeInput, Range rangeOutput) {
        return mapValuesLinear(
                x,
                new Point(
                        rangeInput.start,
                        rangeOutput.start
                ),
                new Point(
                        rangeInput.end,
                        rangeOutput.end
                )
        );
    }

    /**
     * linear transformation between joystick input to servo output
     * @param joystick joystick input
     * @return servo output
     */
    public static double joystickToServo(double joystick) {
        return mapValuesLinearByRange(
                joystick,
                new Range(-1, 1), //joystick range
                new Range(0, 1) //servo range
        );
    }

    /**
     * Requires that a parameter of a method not be null; prints an error message with helpful
     * debugging instructions if the parameter is null.
     *
     * @param <T>        Type of object.
     * @param obj        The parameter that must not be null.
     * @param paramName  The name of the parameter.
     * @param methodName The name of the method.
     * @return The object parameter confirmed not to be null.
     */
    public static <T> T requireNonNullParam(T obj, String paramName, String methodName) {
        return requireNonNull(
                obj,
                "Parameter "
                        + paramName
                        + " in method "
                        + methodName
                        + " was null when it"
                        + " should not have been!  Check the stacktrace to find the responsible line of code - "
                        + "usually, it is the first line of user-written code indicated in the stacktrace.  "
                        + "Make sure all objects passed to the method in question were properly initialized -"
                        + " note that this may not be obvious if it is being called under "
                        + "dynamically-changing conditions!  Please do not seek additional technical assistance"
                        + " without doing this first!"
        );
    }

    /**
     * Returns value clamped between min and max boundaries.
     *
     * @param value value to clamp
     * @param min   minimum valueq
     * @param max   maximum value
     * @return clamped value
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Snaps an angle to whichever of two target angles is closer,
     * measured on a circular range of 0–360 degrees.
     *
     * <p>This is useful for turrets or any rotating mechanism where
     * angles wrap around (e.g. 0° == 360°).</p>
     *
     * <p>Example:
     * <pre>
     * clampInCircle(340, 300, 0) → 0
     * clampInCircle(320, 300, 0) → 300
     * </pre>
     *
     * @param value the angle to clamp (degrees, any value allowed)
     * @param a     first clamp target angle (degrees)
     * @param b     second clamp target angle (degrees)
     * @return the target angle (a or b) that is closer to {@code value}
     */
    public static double clampInCircle(double value, double a, double b) {
        value = normalize(value);
        a = normalize(a);
        b = normalize(b);

        double da = circularDist(value, a);
        double db = circularDist(value, b);

        // If distances are equal, b is returned
        return da < db ? a : b;
    }

    /**
     * Computes the shortest angular distance between two angles
     * on a circle (0–360 degrees).
     *
     * <p>The result is always in the range [0, 180].</p>
     *
     * <p>Examples:
     * <pre>
     * circularDist(350, 10)  → 20
     * circularDist(300, 0)   → 60
     * </pre>
     *
     * @param x first angle (degrees)
     * @param y second angle (degrees)
     * @return shortest distance between {@code x} and {@code y} in degrees
     */
    public static double circularDist(double x, double y) {
        double d = Math.abs(x - y) % 360.0;
        return Math.min(d, 360.0 - d);
    }

    /**
     * Normalizes an angle to the range [0, 360).
     *
     * <p>This ensures consistent behavior when working with angles
     * that may be negative or exceed 360 degrees.</p>
     *
     * <p>Examples:
     * <pre>
     * normalize(-40) → 320
     * normalize(370) → 10
     * </pre>
     *
     * @param angle angle in degrees (any value allowed)
     * @return equivalent angle in the range [0, 360)
     */
    public static double normalize(double angle) {
        angle %= 360.0;
        return angle < 0 ? angle + 360.0 : angle;
    }

    public static boolean is_closer_to(double current, double target, double candidate) {
        return Math.abs(candidate - current) < Math.abs(target - current);
    }

    /**
     * If supplier != null, compares supplier.getAsDouble() to getter.getAsDouble().
     * If they differ, calls setter.accept(newValue).
     *
     * @param supplier  supplies the “debug” value
     * @param getter    a zero‐arg function returning the PID’s current value
     * @param setter    a one‐arg method that applies a new value to the PID
     */
    public static void updateIfChanged(
            DoubleSupplier supplier,
            DoubleSupplier getter,
            Consumer<Double> setter) {
        if (supplier == null) {
            return;
        }
        double newVal = supplier.getAsDouble();
        if (getter.getAsDouble() != newVal) {
            setter.accept(newVal);
        }
    }

    /**
     * Returns modulus of input.
     *
     * @param input Input value to wrap.
     * @param minimumInput The minimum value expected from the input.
     * @param maximumInput The maximum value expected from the input.
     * @return The wrapped value.
     */
    public static double inputModulus(double input, double minimumInput, double maximumInput) {
        double modulus = maximumInput - minimumInput;

        // Wrap input if it's above the maximum input
        int numMax = (int) ((input - minimumInput) / modulus);
        input -= numMax * modulus;

        // Wrap input if it's below the minimum input
        int numMin = (int) ((input - maximumInput) / modulus);
        input -= numMin * modulus;

        return input;
    }
}
