package xyz.immortius.util;

public interface Vector2lc {

    /**
     * @return the value of the x component
     */
    long x();

    /**
     * @return the value of the y component
     */
    long y();

    /**
     * Subtract the supplied vector from this one and store the result in
     * <code>dest</code>.
     *
     * @param v
     *          the vector to subtract
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2l sub(Vector2lc v, Vector2l dest);

    /**
     * Decrement the components of this vector by the given values and store the
     * result in <code>dest</code>.
     *
     * @param x
     *          the x component to subtract
     * @param y
     *          the y component to subtract
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2l sub(long x, long y, Vector2l dest);

    /**
     * Add the supplied vector to this one and store the result in
     * <code>dest</code>.
     *
     * @param v
     *          the vector to add
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2l add(Vector2lc v, Vector2l dest);

    /**
     * Increment the components of this vector by the given values and store the
     * result in <code>dest</code>.
     *
     * @param x
     *          the x component to add
     * @param y
     *          the y component to add
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2l add(long x, long y, Vector2l dest);

    /**
     * Multiply the components of this vector by the given scalar and store the result in <code>dest</code>.
     *
     * @param scalar
     *        the value to multiply this vector's components by
     * @param dest
     *        will hold the result
     * @return dest
     */
    Vector2l mul(long scalar, Vector2l dest);

    /**
     * Multiply the supplied vector by this one and store the result in
     * <code>dest</code>.
     *
     * @param v
     *          the vector to multiply
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2l mul(Vector2lc v, Vector2l dest);

    /**
     * Multiply the components of this vector by the given values and store the
     * result in <code>dest</code>.
     *
     * @param x
     *          the x component to multiply
     * @param y
     *          the y component to multiply
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2l mul(long x, long y, Vector2l dest);

    /**
     * Divide all components of this {@link Vector2l} by the given scalar value
     * and store the result in <code>dest</code>.
     *
     * @param scalar
     *          the scalar to divide by
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2l div(float scalar, Vector2l dest);

    /**
     * Divide all components of this {@link Vector2l} by the given scalar value
     * and store the result in <code>dest</code>.
     *
     * @param scalar
     *          the scalar to divide by
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2l div(long scalar, Vector2l dest);

    /**
     * Return the length squared of this vector.
     *
     * @return the length squared
     */
    long lengthSquared();

    /**
     * Return the length of this vector.
     *
     * @return the length
     */
    double length();

    /**
     * Return the distance between this Vector and <code>v</code>.
     *
     * @param v
     *          the other vector
     * @return the distance
     */
    double distance(Vector2lc v);

    /**
     * Return the distance between <code>this</code> vector and <code>(x, y)</code>.
     *
     * @param x
     *          the x component of the other vector
     * @param y
     *          the y component of the other vector
     * @return the euclidean distance
     */
    double distance(long x, long y);


    /**
     * Return the grid distance in between (aka 1-Norm, Minkowski or Manhattan distance)
     * <code>(x, y)</code>.
     *
     * @param v
     *          the other vector
     * @return the grid distance
     */
    long gridDistance(Vector2lc v);

    /**
     * Return the grid distance in between (aka 1-Norm, Minkowski or Manhattan distance)
     * <code>(x, y)</code>.
     *
     * @param x
     *          the x component of the other vector
     * @param y
     *          the y component of the other vector
     * @return the grid distance
     */
    long gridDistance(long x, long y);

    /**
     * Return the square of the distance between this vector and <code>v</code>.
     *
     * @param v
     *          the other vector
     * @return the squared of the distance
     */
    long distanceSquared(Vector2lc v);

    /**
     * Return the square of the distance between <code>this</code> vector and <code>(x, y)</code>.
     *
     * @param x
     *          the x component of the other vector
     * @param y
     *          the y component of the other vector
     * @return the square of the distance
     */
    long distanceSquared(long x, long y);

    /**
     * Negate this vector and store the result in <code>dest</code>.
     *
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2l negate(Vector2l dest);

    /**
     * Set the components of <code>dest</code> to be the component-wise minimum of this and the other vector.
     *
     * @param v
     *          the other vector
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2l min(Vector2lc v, Vector2l dest);

    /**
     * Set the components of <code>dest</code> to be the component-wise maximum of this and the other vector.
     *
     * @param v
     *          the other vector
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2l max(Vector2lc v, Vector2l dest);

    /**
     * Get the value of the specified component of this vector.
     *
     * @param component
     *          the component, within <code>[0..2]</code>
     * @return the value
     * @throws IllegalArgumentException if <code>component</code> is not within <code>[0..2]</code>
     */
    long get(int component) throws IllegalArgumentException;

    /**
     * Determine the component with the biggest absolute value.
     *
     * @return the component index, within <code>[0..2]</code>
     */
    long maxComponent();

    /**
     * Determine the component with the smallest (towards zero) absolute value.
     *
     * @return the component index, within <code>[0..2]</code>
     */
    long minComponent();

    /**
     * Compute the absolute of each of this vector's components
     * and store the result long <code>dest</code>.
     *
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector2l absolute(Vector2l dest);

    /**
     * Compare the vector components of <code>this</code> vector with the given <code>(x, y)</code>
     * and return whether all of them are equal.
     *
     * @param x
     *          the x component to compare to
     * @param y
     *          the y component to compare to
     * @return <code>true</code> if all the vector components are equal
     */
    boolean equals(long x, long y);
}
