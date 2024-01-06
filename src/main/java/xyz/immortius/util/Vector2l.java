package xyz.immortius.util;

import com.google.common.base.Objects;
import org.joml.Options;
import org.joml.Runtime;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Vector2l implements Vector2lc {

    /**
     * The x component of the vector.
     */
    public long x;
    /**
     * The y component of the vector.
     */
    public long y;

    /**
     * Create a new {@link Vector2l} of <code>(0, 0, 0)</code>.
     */
    public Vector2l() {
    }

    /**
     * Create a new {@link Vector2l} and initialize all components with
     * the given value.
     *
     * @param d
     *          the value of all components
     */
    public Vector2l(long d) {
        this.x = d;
        this.y = d;
    }

    /**
     * Create a new {@link Vector2l} with the given component values.
     *
     * @param x
     *          the value of x
     * @param y
     *          the value of y
     */
    public Vector2l(long x, long y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Create a new {@link Vector2l} with the same values as <code>v</code>.
     *
     * @param v
     *          the {@link Vector2lc} to copy the values from
     */
    public Vector2l(Vector2lc v) {
        this.x = v.x();
        this.y = v.y();
    }

    /**
     * Create a new {@link Vector2l} and initialize its components from the first
     * elements of the given array.
     *
     * @param xy
     *          the array containing at least two elements
     */
    public Vector2l(long[] xy) {
        this.x = xy[0];
        this.y = xy[1];
    }

    public long x() {
        return this.x;
    }

    public long y() {
        return this.y;
    }

    /**
     * Set the x and y components to match the supplied vector.
     *
     * @param v
     *          contains the values of x and y to set
     * @return this
     */
    public Vector2l set(Vector2lc v) {
        x = v.x();
        y = v.y();
        return this;
    }

    /**
     * Set the x and y components to the supplied value.
     *
     * @param d
     *          the value of all two components
     * @return this
     */
    public Vector2l set(long d) {
        this.x = d;
        this.y = d;
        return this;
    }

    /**
     * Set the x and y components to the supplied values.
     *
     * @param x
     *          the x component
     * @param y
     *          the y component
     * @return this
     */
    public Vector2l set(long x, long y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Set the two components of this vector to the first two elements of the given array.
     *
     * @param xy
     *          the array containing at least two elements
     * @return this
     */
    public Vector2l set(int[] xy) {
        this.x = xy[0];
        this.y = xy[1];
        return this;
    }

    public long get(int component) throws IllegalArgumentException {
        switch (component) {
            case 0:
                return x;
            case 1:
                return y;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Set the value of the specified component of this vector.
     *
     * @param component
     *          the component whose value to set, within <code>[0..2]</code>
     * @param value
     *          the value to set
     * @return this
     * @throws IllegalArgumentException if <code>component</code> is not within <code>[0..2]</code>
     */
    public Vector2l setComponent(int component, long value) throws IllegalArgumentException {
        switch (component) {
            case 0 -> x = value;
            case 1 -> y = value;
            default -> throw new IllegalArgumentException();
        }
        return this;
    }

    /**
     * Subtract the supplied vector from this one and store the result in
     * <code>this</code>.
     *
     * @param v
     *          the vector to subtract
     * @return this
     */
    public Vector2l sub(Vector2lc v) {
        this.x = this.x - v.x();
        this.y = this.y - v.y();
        return this;
    }

    public Vector2l sub(Vector2lc v, Vector2l dest) {
        dest.x = x - v.x();
        dest.y = y - v.y();
        return dest;
    }

    /**
     * Decrement the components of this vector by the given values.
     *
     * @param x
     *          the x component to subtract
     * @param y
     *          the y component to subtract
     * @return this
     */
    public Vector2l sub(long x, long y) {
        this.x = this.x - x;
        this.y = this.y - y;
        return this;
    }

    public Vector2l sub(long x, long y, Vector2l dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        return dest;
    }

    /**
     * Add the supplied vector to this one.
     *
     * @param v
     *          the vector to add
     * @return this
     */
    public Vector2l add(Vector2lc v) {
        this.x = this.x + v.x();
        this.y = this.y + v.y();
        return this;
    }

    public Vector2l add(Vector2lc v, Vector2l dest) {
        dest.x = x + v.x();
        dest.y = y + v.y();
        return dest;
    }

    /**
     * Increment the components of this vector by the given values.
     *
     * @param x
     *          the x component to add
     * @param y
     *          the y component to add
     * @return this
     */
    public Vector2l add(long x, long y) {
        this.x = this.x + x;
        this.y = this.y + y;
        return this;
    }

    public Vector2l add(long x, long y, Vector2l dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        return dest;
    }

    /**
     * Multiply all components of this {@link Vector2l} by the given scalar
     * value.
     *
     * @param scalar
     *          the scalar to multiply this vector by
     * @return this
     */
    public Vector2l mul(long scalar) {
        this.x = x * scalar;
        this.y = y * scalar;
        return this;
    }

    public Vector2l mul(long scalar, Vector2l dest) {
        dest.x = x * scalar;
        dest.y = y * scalar;
        return dest;
    }

    /**
     * Multiply all components of this {@link Vector2l} by the given vector.
     *
     * @param v
     *          the vector to multiply
     * @return this
     */
    public Vector2l mul(Vector2lc v) {
        this.x = this.x * v.x();
        this.y = this.y * v.y();
        return this;
    }

    public Vector2l mul(Vector2lc v, Vector2l dest) {
        dest.x = x * v.x();
        dest.y = y * v.y();
        return dest;
    }

    /**
     * Multiply the components of this vector by the given values.
     *
     * @param x
     *          the x component to multiply
     * @param y
     *          the y component to multiply
     * @return this
     */
    public Vector2l mul(long x, long y) {
        this.x = this.x * x;
        this.y = this.y * y;
        return this;
    }

    public Vector2l mul(long x, long y, Vector2l dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;
        return dest;
    }

    /**
     * Divide all components of this {@link Vector2l} by the given scalar value.
     *
     * @param scalar
     *          the scalar to divide by
     * @return this
     */
    public Vector2l div(float scalar) {
        float invscalar = 1.0f / scalar;
        this.x = (int) (x * invscalar);
        this.y = (int) (y * invscalar);
        return this;
    }

    public Vector2l div(float scalar, Vector2l dest) {
        float invscalar = 1.0f / scalar;
        dest.x = (int) (x * invscalar);
        dest.y = (int) (y * invscalar);
        return dest;
    }

    /**
     * Divide all components of this {@link Vector2l} by the given scalar value.
     *
     * @param scalar
     *          the scalar to divide by
     * @return this
     */
    public Vector2l div(long scalar) {
        this.x = x / scalar;
        this.y = y / scalar;
        return this;
    }

    public Vector2l div(long scalar, Vector2l dest) {
        dest.x = x / scalar;
        dest.y = y / scalar;
        return dest;
    }

    public long lengthSquared() {
        return x * x + y * y;
    }

    /**
     * Get the length squared of a 2-dimensional single-precision vector.
     *
     * @param x The vector's x component
     * @param y The vector's y component
     *
     * @return the length squared of the given vector
     */
    public static long lengthSquared(long x, long y) {
        return x * x + y * y;
    }

    public double length() {
        return org.joml.Math.sqrt(x * x + y * y);
    }

    /**
     * Get the length of a 2-dimensional single-precision vector.
     *
     * @param x The vector's x component
     * @param y The vector's y component
     *
     * @return the length squared of the given vector
     */
    public static double length(long x, long y) {
        return org.joml.Math.sqrt(x * x + y * y);
    }

    public double distance(Vector2lc v) {
        long dx = this.x - v.x();
        long dy = this.y - v.y();
        return org.joml.Math.sqrt(dx * dx + dy * dy);
    }

    public double distance(long x, long y) {
        long dx = this.x - x;
        long dy = this.y - y;
        return org.joml.Math.sqrt(dx * dx + dy * dy);
    }

    public long gridDistance(Vector2lc v) {
        return Math.abs(v.x() - x()) + Math.abs(v.y() - y());
    }

    public long gridDistance(long x, long y) {
        return Math.abs(x - x()) + Math.abs(y - y());
    }

    public long distanceSquared(Vector2lc v) {
        long dx = this.x - v.x();
        long dy = this.y - v.y();
        return dx * dx + dy * dy;
    }

    public long distanceSquared(long x, long y) {
        long dx = this.x - x;
        long dy = this.y - y;
        return dx * dx + dy * dy;
    }

    /**
     * Return the distance between <code>(x1, y1)</code> and <code>(x2, y2)</code>.
     *
     * @param x1
     *          the x component of the first vector
     * @param y1
     *          the y component of the first vector
     * @param x2
     *          the x component of the second vector
     * @param y2
     *          the y component of the second vector
     * @return the euclidean distance
     */
    public static double distance(long x1, long y1, long x2, long y2) {
        return org.joml.Math.sqrt(distanceSquared(x1, y1, x2, y2));
    }

    /**
     * Return the squared distance between <code>(x1, y1)</code> and <code>(x2, y2)</code>.
     *
     * @param x1
     *          the x component of the first vector
     * @param y1
     *          the y component of the first vector
     * @param x2
     *          the x component of the second vector
     * @param y2
     *          the y component of the second vector
     * @return the euclidean distance squared
     */
    public static long distanceSquared(long x1, long y1, long x2, long y2) {
        long dx = x1 - x2;
        long dy = y1 - y2;
        return dx * dx + dy * dy;
    }

    /**
     * Set all components to zero.
     *
     * @return this
     */
    public Vector2l zero() {
        this.x = 0;
        this.y = 0;
        return this;
    }

    /**
     * Return a string representation of this vector.
     * <p>
     * This method creates a new {@link DecimalFormat} on every invocation with the format string "<code>0.000E0;-</code>".
     *
     * @return the string representation
     */
    public String toString() {
        return Runtime.formatNumbers(toString(Options.NUMBER_FORMAT));
    }

    /**
     * Return a string representation of this vector by formatting the vector components with the given {@link NumberFormat}.
     *
     * @param formatter
     *          the {@link NumberFormat} used to format the vector components with
     * @return the string representation
     */
    public String toString(NumberFormat formatter) {
        return "(" + formatter.format(x) + " " + formatter.format(y) + ")";
    }

    /**
     * Negate this vector.
     *
     * @return this
     */
    public Vector2l negate() {
        this.x = -x;
        this.y = -y;
        return this;
    }

    public Vector2l negate(Vector2l dest) {
        dest.x = -x;
        dest.y = -y;
        return dest;
    }

    /**
     * Set the components of this vector to be the component-wise minimum of this and the other vector.
     *
     * @param v
     *          the other vector
     * @return this
     */
    public Vector2l min(Vector2lc v) {
        this.x = x < v.x() ? x : v.x();
        this.y = y < v.y() ? y : v.y();
        return this;
    }

    public Vector2l min(Vector2lc v, Vector2l dest) {
        dest.x = x < v.x() ? x : v.x();
        dest.y = y < v.y() ? y : v.y();
        return dest;
    }

    /**
     * Set the components of this vector to be the component-wise maximum of this and the other vector.
     *
     * @param v
     *          the other vector
     * @return this
     */
    public Vector2l max(Vector2lc v) {
        this.x = x > v.x() ? x : v.x();
        this.y = y > v.y() ? y : v.y();
        return this;
    }

    public Vector2l max(Vector2lc v, Vector2l dest) {
        dest.x = x > v.x() ? x : v.x();
        dest.y = y > v.y() ? y : v.y();
        return dest;
    }

    public long maxComponent() {
        float absX = org.joml.Math.abs(x);
        float absY = org.joml.Math.abs(y);
        if (absX >= absY) {
            return 0;
        } else {
            return 1;
        }
    }

    public long minComponent() {
        float absX = org.joml.Math.abs(x);
        float absY = org.joml.Math.abs(y);
        if (absX < absY) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     * Set <code>this</code> vector's components to their respective absolute values.
     *
     * @return this
     */
    public Vector2l absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        return this;
    }

    public Vector2l absolute(Vector2l dest) {
        dest.x = Math.abs(this.x);
        dest.y = Math.abs(this.y);
        return dest;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Vector2l other = (Vector2l) obj;
        if (x != other.x) {
            return false;
        }
        if (y != other.y) {
            return false;
        }
        return true;
    }

    public boolean equals(long x, long y) {
        if (this.x != x)
            return false;
        if (this.y != y)
            return false;
        return true;
    }

}