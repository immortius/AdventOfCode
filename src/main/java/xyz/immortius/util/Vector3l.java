package xyz.immortius.util;

import com.google.common.base.Objects;
import org.joml.Runtime;
import org.joml.*;

import java.lang.Math;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Vector3l implements Vector3lc {

    /**
     * The x component of the vector.
     */
    public long x;
    /**
     * The y component of the vector.
     */
    public long y;
    /**
     * The z component of the vector.
     */
    public long z;

    /**
     * Create a new {@link Vector3l} of <code>(0, 0, 0)</code>.
     */
    public Vector3l() {
    }

    /**
     * Create a new {@link Vector3l} and initialize all three components with
     * the given value.
     *
     * @param d
     *          the value of all three components
     */
    public Vector3l(long d) {
        this.x = d;
        this.y = d;
        this.z = d;
    }

    /**
     * Create a new {@link Vector3l} with the given component values.
     *
     * @param x
     *          the value of x
     * @param y
     *          the value of y
     * @param z
     *          the value of z
     */
    public Vector3l(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Create a new {@link Vector3l} with the same values as <code>v</code>.
     *
     * @param v
     *          the {@link Vector3lc} to copy the values from
     */
    public Vector3l(Vector3lc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
    }

    /**
     * Create a new {@link Vector3l} with the first two components from the
     * given <code>v</code> and the given <code>z</code>
     *
     * @param v
     *          the {@link Vector2ic} to copy the values from
     * @param z
     *          the z component
     */
    public Vector3l(Vector2ic v, long z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
    }

    /**
     * Create a new {@link Vector3l} and initialize its three components from the first
     * three elements of the given array.
     *
     * @param xyz
     *          the array containing at least three elements
     */
    public Vector3l(long[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
    }

    public long x() {
        return this.x;
    }

    public long y() {
        return this.y;
    }

    public long z() {
        return this.z;
    }

    /**
     * Set the x, y and z components to match the supplied vector.
     *
     * @param v
     *          contains the values of x, y and z to set
     * @return this
     */
    public Vector3l set(Vector3lc v) {
        x = v.x();
        y = v.y();
        z = v.z();
        return this;
    }

    /**
     * Set this {@link Vector3l} to the values of v using {@link RoundingMode#TRUNCATE} rounding.
     * <p>
     * Note that due to the given vector <code>v</code> storing the components
     * in double-precision, there is the possibility to lose precision.
     *
     * @param v
     *          the vector to copy from
     * @return this
     */
    public Vector3l set(Vector3dc v) {
        this.x = (int) v.x();
        this.y = (int) v.y();
        this.z = (int) v.z();
        return this;
    }

    /**
     * Set the first two components from the given <code>v</code> and the z
     * component from the given <code>z</code>
     *
     * @param v
     *          the {@link Vector2ic} to copy the values from
     * @param z
     *          the z component
     * @return this
     */
    public Vector3l set(Vector2ic v, long z) {
        this.x = v.x();
        this.y = v.y();
        this.z = z;
        return this;
    }

    /**
     * Set the x, y, and z components to the supplied value.
     *
     * @param d
     *          the value of all three components
     * @return this
     */
    public Vector3l set(long d) {
        this.x = d;
        this.y = d;
        this.z = d;
        return this;
    }

    /**
     * Set the x, y and z components to the supplied values.
     *
     * @param x
     *          the x component
     * @param y
     *          the y component
     * @param z
     *          the z component
     * @return this
     */
    public Vector3l set(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Set the three components of this vector to the first three elements of the given array.
     *
     * @param xyz
     *          the array containing at least three elements
     * @return this
     */
    public Vector3l set(int[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
        return this;
    }

    public long get(int component) throws IllegalArgumentException {
        switch (component) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
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
    public Vector3l setComponent(int component, long value) throws IllegalArgumentException {
        switch (component) {
            case 0:
                x = value;
                break;
            case 1:
                y = value;
                break;
            case 2:
                z = value;
                break;
            default:
                throw new IllegalArgumentException();
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
    public Vector3l sub(Vector3lc v) {
        this.x = this.x - v.x();
        this.y = this.y - v.y();
        this.z = this.z - v.z();
        return this;
    }

    public Vector3l sub(Vector3lc v, Vector3l dest) {
        dest.x = x - v.x();
        dest.y = y - v.y();
        dest.z = z - v.z();
        return dest;
    }

    /**
     * Decrement the components of this vector by the given values.
     *
     * @param x
     *          the x component to subtract
     * @param y
     *          the y component to subtract
     * @param z
     *          the z component to subtract
     * @return this
     */
    public Vector3l sub(long x, long y, long z) {
        this.x = this.x - x;
        this.y = this.y - y;
        this.z = this.z - z;
        return this;
    }

    public Vector3l sub(long x, long y, long z, Vector3l dest) {
        dest.x = this.x - x;
        dest.y = this.y - y;
        dest.z = this.z - z;
        return dest;
    }

    /**
     * Add the supplied vector to this one.
     *
     * @param v
     *          the vector to add
     * @return this
     */
    public Vector3l add(Vector3lc v) {
        this.x = this.x + v.x();
        this.y = this.y + v.y();
        this.z = this.z + v.z();
        return this;
    }

    public Vector3l add(Vector3lc v, Vector3l dest) {
        dest.x = x + v.x();
        dest.y = y + v.y();
        dest.z = z + v.z();
        return dest;
    }

    /**
     * Increment the components of this vector by the given values.
     *
     * @param x
     *          the x component to add
     * @param y
     *          the y component to add
     * @param z
     *          the z component to add
     * @return this
     */
    public Vector3l add(long x, long y, long z) {
        this.x = this.x + x;
        this.y = this.y + y;
        this.z = this.z + z;
        return this;
    }

    public Vector3l add(long x, long y, long z, Vector3l dest) {
        dest.x = this.x + x;
        dest.y = this.y + y;
        dest.z = this.z + z;
        return dest;
    }

    /**
     * Multiply all components of this {@link Vector3l} by the given scalar
     * value.
     *
     * @param scalar
     *          the scalar to multiply this vector by
     * @return this
     */
    public Vector3l mul(long scalar) {
        this.x = x * scalar;
        this.y = y * scalar;
        this.z = z * scalar;
        return this;
    }

    public Vector3l mul(long scalar, Vector3l dest) {
        dest.x = x * scalar;
        dest.y = y * scalar;
        dest.z = z * scalar;
        return dest;
    }

    /**
     * Multiply all components of this {@link Vector3l} by the given vector.
     *
     * @param v
     *          the vector to multiply
     * @return this
     */
    public Vector3l mul(Vector3lc v) {
        this.x = this.x * v.x();
        this.y = this.y * v.y();
        this.z = this.z * v.z();
        return this;
    }

    public Vector3l mul(Vector3lc v, Vector3l dest) {
        dest.x = x * v.x();
        dest.y = y * v.y();
        dest.z = z * v.z();
        return dest;
    }

    /**
     * Multiply the components of this vector by the given values.
     *
     * @param x
     *          the x component to multiply
     * @param y
     *          the y component to multiply
     * @param z
     *          the z component to multiply
     * @return this
     */
    public Vector3l mul(long x, long y, long z) {
        this.x = this.x * x;
        this.y = this.y * y;
        this.z = this.z * z;
        return this;
    }

    public Vector3l mul(long x, long y, long z, Vector3l dest) {
        dest.x = this.x * x;
        dest.y = this.y * y;
        dest.z = this.z * z;
        return dest;
    }

    /**
     * Divide all components of this {@link Vector3l} by the given scalar value.
     *
     * @param scalar
     *          the scalar to divide by
     * @return this
     */
    public Vector3l div(float scalar) {
        float invscalar = 1.0f / scalar;
        this.x = (int) (x * invscalar);
        this.y = (int) (y * invscalar);
        this.z = (int) (z * invscalar);
        return this;
    }

    public Vector3l div(float scalar, Vector3l dest) {
        float invscalar = 1.0f / scalar;
        dest.x = (int) (x * invscalar);
        dest.y = (int) (y * invscalar);
        dest.z = (int) (z * invscalar);
        return dest;
    }

    /**
     * Divide all components of this {@link Vector3l} by the given scalar value.
     *
     * @param scalar
     *          the scalar to divide by
     * @return this
     */
    public Vector3l div(long scalar) {
        this.x = x / scalar;
        this.y = y / scalar;
        this.z = z / scalar;
        return this;
    }

    public Vector3l div(long scalar, Vector3l dest) {
        dest.x = x / scalar;
        dest.y = y / scalar;
        dest.z = z / scalar;
        return dest;
    }

    public long lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * Get the length squared of a 3-dimensional single-precision vector.
     *
     * @param x The vector's x component
     * @param y The vector's y component
     * @param z The vector's z component
     *
     * @return the length squared of the given vector
     */
    public static long lengthSquared(long x, long y, long z) {
        return x * x + y * y + z * z;
    }

    public double length() {
        return org.joml.Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Get the length of a 3-dimensional single-precision vector.
     *
     * @param x The vector's x component
     * @param y The vector's y component
     * @param z The vector's z component
     *
     * @return the length squared of the given vector
     */
    public static double length(long x, long y, long z) {
        return org.joml.Math.sqrt(x * x + y * y + z * z);
    }

    public double distance(Vector3lc v) {
        long dx = this.x - v.x();
        long dy = this.y - v.y();
        long dz = this.z - v.z();
        return org.joml.Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double distance(long x, long y, long z) {
        long dx = this.x - x;
        long dy = this.y - y;
        long dz = this.z - z;
        return org.joml.Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public long gridDistance(Vector3lc v) {
        return Math.abs(v.x() - x()) + Math.abs(v.y() - y())  + Math.abs(v.z() - z());
    }

    public long gridDistance(long x, long y, long z) {
        return Math.abs(x - x()) + Math.abs(y - y()) + Math.abs(z - z());
    }

    public long distanceSquared(Vector3lc v) {
        long dx = this.x - v.x();
        long dy = this.y - v.y();
        long dz = this.z - v.z();
        return dx * dx + dy * dy + dz * dz;
    }

    public long distanceSquared(long x, long y, long z) {
        long dx = this.x - x;
        long dy = this.y - y;
        long dz = this.z - z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Return the distance between <code>(x1, y1, z1)</code> and <code>(x2, y2, z2)</code>.
     *
     * @param x1
     *          the x component of the first vector
     * @param y1
     *          the y component of the first vector
     * @param z1
     *          the z component of the first vector
     * @param x2
     *          the x component of the second vector
     * @param y2
     *          the y component of the second vector
     * @param z2
     *          the z component of the second vector
     * @return the euclidean distance
     */
    public static double distance(long x1, long y1, long z1, long x2, long y2, long z2) {
        return org.joml.Math.sqrt(distanceSquared(x1, y1, z1, x2, y2, z2));
    }

    /**
     * Return the squared distance between <code>(x1, y1, z1)</code> and <code>(x2, y2, z2)</code>.
     *
     * @param x1
     *          the x component of the first vector
     * @param y1
     *          the y component of the first vector
     * @param z1
     *          the z component of the first vector
     * @param x2
     *          the x component of the second vector
     * @param y2
     *          the y component of the second vector
     * @param z2
     *          the z component of the second vector
     * @return the euclidean distance squared
     */
    public static long distanceSquared(long x1, long y1, long z1, long x2, long y2, long z2) {
        long dx = x1 - x2;
        long dy = y1 - y2;
        long dz = z1 - z2;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * Set all components to zero.
     *
     * @return this
     */
    public Vector3l zero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
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
        return "(" + formatter.format(x) + " " + formatter.format(y) + " " + formatter.format(z) + ")";
    }

    /**
     * Negate this vector.
     *
     * @return this
     */
    public Vector3l negate() {
        this.x = -x;
        this.y = -y;
        this.z = -z;
        return this;
    }

    public Vector3l negate(Vector3l dest) {
        dest.x = -x;
        dest.y = -y;
        dest.z = -z;
        return dest;
    }

    /**
     * Set the components of this vector to be the component-wise minimum of this and the other vector.
     *
     * @param v
     *          the other vector
     * @return this
     */
    public Vector3l min(Vector3lc v) {
        this.x = x < v.x() ? x : v.x();
        this.y = y < v.y() ? y : v.y();
        this.z = z < v.z() ? z : v.z();
        return this;
    }

    public Vector3l min(Vector3lc v, Vector3l dest) {
        dest.x = x < v.x() ? x : v.x();
        dest.y = y < v.y() ? y : v.y();
        dest.z = z < v.z() ? z : v.z();
        return dest;
    }

    /**
     * Set the components of this vector to be the component-wise maximum of this and the other vector.
     *
     * @param v
     *          the other vector
     * @return this
     */
    public Vector3l max(Vector3lc v) {
        this.x = x > v.x() ? x : v.x();
        this.y = y > v.y() ? y : v.y();
        this.z = z > v.z() ? z : v.z();
        return this;
    }

    public Vector3l max(Vector3lc v, Vector3l dest) {
        dest.x = x > v.x() ? x : v.x();
        dest.y = y > v.y() ? y : v.y();
        dest.z = z > v.z() ? z : v.z();
        return dest;
    }

    public long maxComponent() {
        float absX = org.joml.Math.abs(x);
        float absY = org.joml.Math.abs(y);
        float absZ = org.joml.Math.abs(z);
        if (absX >= absY && absX >= absZ) {
            return 0;
        } else if (absY >= absZ) {
            return 1;
        }
        return 2;
    }

    public long minComponent() {
        float absX = org.joml.Math.abs(x);
        float absY = org.joml.Math.abs(y);
        float absZ = org.joml.Math.abs(z);
        if (absX < absY && absX < absZ) {
            return 0;
        } else if (absY < absZ) {
            return 1;
        }
        return 2;
    }

    /**
     * Set <code>this</code> vector's components to their respective absolute values.
     *
     * @return this
     */
    public Vector3l absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        return this;
    }

    public Vector3l absolute(Vector3l dest) {
        dest.x = Math.abs(this.x);
        dest.y = Math.abs(this.y);
        dest.z = Math.abs(this.z);
        return dest;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y, z);
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
        Vector3l other = (Vector3l) obj;
        if (x != other.x) {
            return false;
        }
        if (y != other.y) {
            return false;
        }
        if (z != other.z) {
            return false;
        }
        return true;
    }

    public boolean equals(long x, long y, long z) {
        if (this.x != x)
            return false;
        if (this.y != y)
            return false;
        if (this.z != z)
            return false;
        return true;
    }

}