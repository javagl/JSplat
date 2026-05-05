/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splats;

/**
 * Convenience methods for setting properties of {@link MutableSplat} instances.
 * 
 * Note: Some of these functions might eventually be offered as part of the
 * MutableSplat interface.
 */
public class SplatSetters
{
    /**
     * Epsilon for quaternion computations
     */
    private static final double EPSILON = 1e-6f;
    
    /**
     * Set some default values for the given splat
     * 
     * @param s The splat
     */
    public static void setDefaults(MutableSplat s)
    {
        s.setOpacity(20.0f);
        setColor(s, 1.0f, 1.0f, 1.0f);
        setRotationAxisAngleRad(s, 0.0f, 0.0f, 0.0f, 0.0f);
        setScale(s, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Set the position of the given splat
     * 
     * @param s The splat
     * @param x The x-component
     * @param y The y-component
     * @param z The z-component
     */
    public static void setPosition(MutableSplat s, double x, double y, double z)
    {
        s.setPositionX(x);
        s.setPositionY(y);
        s.setPositionZ(z);
    }
    
    /**
     * Set the color of the given splat, with components in [0,1], being
     * translated to spherical harmonics coefficients internally
     * 
     * @param s The splat
     * @param r The red component
     * @param g The green component
     * @param b The blue component
     */
    public static void setColor(MutableSplat s, double r, double g, double b)
    {
        s.setShX(0, Splats.colorToDirectCurrent(r));
        s.setShY(0, Splats.colorToDirectCurrent(g));
        s.setShZ(0, Splats.colorToDirectCurrent(b));
    }

    /**
     * Set the rotation of the given splat to be a rotation around the specified
     * axis, about the given angle
     * 
     * @param s The splat
     * @param x The x-component of the axis
     * @param y The y-component of the axis
     * @param z The z-component of the axis
     * @param angleRad The angle, in radians
     */
    public static void setRotationAxisAngleRad(
        MutableSplat s, double x, double y, double z, double angleRad)
    {
        double q[] = createScalarLastQuaternionFromAxisAngleRad(x, y, z, angleRad);
        s.setRotationX(q[0]);
        s.setRotationY(q[1]);
        s.setRotationZ(q[2]);
        s.setRotationW(q[3]);
    }

    /**
     * Set the scale of the given splat
     * 
     * @param s The splat
     * @param x The scale in x-direction
     * @param y The scale in y-direction
     * @param z The scale in z-direction
     */
    public static void setScale(MutableSplat s, double x, double y, double z)
    {
        s.setScaleX(x);
        s.setScaleY(y);
        s.setScaleZ(z);
    }

    /**
     * Set the scale of the given splat, from values that are given in linear
     * space (i.e. setting the scale to be the logarithm of the given values)
     * 
     * @param s The splat
     * @param x The linear scale in x-direction
     * @param y The linear scale in y-direction
     * @param z The linear scale in z-direction
     */
    public static void setScaleLinear(MutableSplat s, double x, double y, double z)
    {
        s.setScaleX(Math.log(x));
        s.setScaleY(Math.log(y));
        s.setScaleZ(Math.log(z));
    }
    
    /**
     * Create the components of a quaternion that describes a rotation around
     * the given axis, about the given angle
     * 
     * @param x The x-component of the axis
     * @param y The y-component of the axis
     * @param z The z-component of the axis
     * @param angleRad The angle, in radians
     * @return The quaternion, in scalar-last representation
     */
    private static double[] createScalarLastQuaternionFromAxisAngleRad(double x,
        double y, double z, double angleRad)
    {
        double halfAngleRad = angleRad * 0.5f;
        double s = Math.sin(halfAngleRad);

        double lenSquared = x * x + y * y + z * z;
        if (lenSquared < EPSILON)
        {
            return new double[]
            { 0.0, 0.0, 0.0, 1.0 };
        }

        double invLen = 1.0 / Math.sqrt(lenSquared);
        double qx = x * invLen * s;
        double qy = y * invLen * s;
        double qz = z * invLen * s;
        double qw = Math.cos(halfAngleRad);

        double q[] = new double[]
        { qx, qy, qz, qw };
        return q;
    }

    /**
     * Create an array containing the axis and the angle in radians that is
     * described by the given quaternion.
     * 
     * @param qx The x-element
     * @param qy The y-element
     * @param qz The z-element
     * @param qw The w-element (scalar)
     * @return The array
     */
    static double[] createAxisAngleRadFromScalarLastQuaternion(double qx,
        double qy, double qz, double qw)
    {
        double x = 1.0;
        double y = 0.0;
        double z = 0.0;
        double angleRad = 0.0;
        if (Math.abs(qw - 1.0) >= EPSILON && Math.abs(qw + 1.0) >= EPSILON)
        {
            double f = 1.0 / Math.sqrt(1.0 - qw * qw);
            x = qx * f;
            y = qy * f;
            z = qz * f;

        }
        if (Math.abs(qw - 1.0) >= EPSILON)
        {
            angleRad = 2.0 * Math.acos(qw);
        }
        double a[] =
        { x, y, z, angleRad };
        return a;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatSetters()
    {
        // Private constructor to prevent instantiation
    }

}
