/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.util.List;
import java.util.function.Supplier;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splats;

/**
 * Internal utility methods to create splat data.
 * 
 * Most details of these methods are not specified.
 */
public class SplatGrids
{
    /**
     * Epsilon for quaternion computations
     */
    private static final float EPSILON = 1e-6f;

    /**
     * Create a basic splat data set
     * 
     * @return The data set
     */
    public static List<MutableSplat> createBasic()
    {
        int shDegree = 0;
        Supplier<MutableSplat> supplier = () ->
        {
            MutableSplat s = Splats.create(shDegree);
            setDefaults(s);
            return s;
        };
        SplatGridBuilder g = new SplatGridBuilder(2, 2, 2, supplier);

        float maxPosition = 100.0f;
        g.registerX(0.0f, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0f, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0f, maxPosition, MutableSplat::setPositionZ);

        return g.generate();
    }

    /**
     * Create a basic splat data set
     * 
     * @param minX The minimum x-position
     * @param minY The minimum y-position
     * @param minZ The minimum z-position
     * @param maxX The maximum x-position
     * @param maxY The maximum y-position
     * @param maxZ The maximum z-position
     * 
     * @return The data set
     */
    public static List<MutableSplat> createBox(float minX, float minY,
        float minZ, float maxX, float maxY, float maxZ)
    {
        int shDegree = 0;
        Supplier<MutableSplat> supplier = () ->
        {
            MutableSplat s = Splats.create(shDegree);
            setDefaults(s);
            setScale(s, 3.0f, 3.0f, 3.0f);
            return s;
        };
        SplatGridBuilder g = new SplatGridBuilder(3, 3, 3, supplier);

        g.registerX(minX, maxX, MutableSplat::setPositionX);
        g.registerY(minY, maxY, MutableSplat::setPositionY);
        g.registerZ(minZ, maxZ, MutableSplat::setPositionZ);

        g.registerX((s, x) -> s.setShX(0, Splats.colorToDirectCurrent(x)));
        g.registerY((s, y) -> s.setShY(0, Splats.colorToDirectCurrent(y)));
        g.registerZ((s, z) -> s.setShZ(0, Splats.colorToDirectCurrent(z)));

        return g.generate();
    }

    /**
     * Create a splat data set with different scales
     * 
     * @return The data set
     */
    public static List<MutableSplat> createScales()
    {
        int shDegree = 0;
        Supplier<MutableSplat> supplier = () ->
        {
            MutableSplat s = Splats.create(shDegree);
            setDefaults(s);
            return s;
        };
        SplatGridBuilder g = new SplatGridBuilder(3, 3, 3, supplier);

        float maxPosition = 100.0f;
        g.registerX(0.0f, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0f, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0f, maxPosition, MutableSplat::setPositionZ);

        float maxScale = 2.0f;
        g.registerX(0.0f, maxScale, MutableSplat::setScaleX);
        g.registerY(0.0f, maxScale, MutableSplat::setScaleY);
        g.registerZ(0.0f, maxScale, MutableSplat::setScaleZ);

        return g.generate();
    }

    /**
     * Create a splat data set with different opacity values
     * 
     * @return The data set
     */
    public static List<MutableSplat> createOpacities()
    {
        int shDegree = 0;
        Supplier<MutableSplat> supplier = () ->
        {
            MutableSplat s = Splats.create(shDegree);
            setDefaults(s);
            return s;
        };
        SplatGridBuilder g = new SplatGridBuilder(9, 9, 9, supplier);

        float maxPosition = 100.0f;
        g.registerX(0.0f, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0f, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0f, maxPosition, MutableSplat::setPositionZ);

        g.registerX((s, x) -> s.setShX(0, Splats.colorToDirectCurrent(x)));
        g.registerY((s, y) -> s.setShY(0, Splats.colorToDirectCurrent(y)));
        g.registerZ((s, z) -> s.setShZ(0, Splats.colorToDirectCurrent(z)));

        float minOpacity = -20.f;
        float maxOpacity = 20.0f;
        g.register((s, x, y, z) ->
        {
            float dx = 0.5f - x;
            float dy = 0.5f - y;
            float dz = 0.5f - z;
            float d = 1.0f - (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            float opacity = minOpacity + d * (maxOpacity - minOpacity);
            s.setOpacity(opacity);
        });

        return g.generate();
    }

    /**
     * Create a splat data set with different colors
     * 
     * @return The data set
     */
    public static List<MutableSplat> createColors()
    {
        int shDegree = 0;
        Supplier<MutableSplat> supplier = () ->
        {
            MutableSplat s = Splats.create(shDegree);
            setDefaults(s);
            return s;
        };
        SplatGridBuilder g = new SplatGridBuilder(9, 9, 9, supplier);

        float maxPosition = 100.0f;
        g.registerX(0.0f, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0f, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0f, maxPosition, MutableSplat::setPositionZ);

        g.registerX((s, x) -> s.setShX(0, Splats.colorToDirectCurrent(x)));
        g.registerY((s, y) -> s.setShY(0, Splats.colorToDirectCurrent(y)));
        g.registerZ((s, z) -> s.setShZ(0, Splats.colorToDirectCurrent(z)));

        return g.generate();
    }

    /**
     * Create a splat data set with different rotations (and scales)
     * 
     * @return The data set
     */
    public static List<MutableSplat> createRotations()
    {
        int shDegree = 0;
        Supplier<MutableSplat> supplier = () ->
        {
            MutableSplat s = Splats.create(shDegree);
            setDefaults(s);
            return s;
        };
        SplatGridBuilder g = new SplatGridBuilder(3, 3, 3, supplier);

        float maxPosition = 100.0f;
        g.registerX(0.0f, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0f, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0f, maxPosition, MutableSplat::setPositionZ);

        float maxScale = 2.0f;
        g.registerX(0.0f, maxScale, MutableSplat::setScaleX);
        g.registerY(0.0f, maxScale, MutableSplat::setScaleY);
        g.registerZ(0.0f, maxScale, MutableSplat::setScaleZ);

        g.registerX((s, x) -> s.setShX(0, Splats.colorToDirectCurrent(x)));
        g.registerY((s, y) -> s.setShY(0, Splats.colorToDirectCurrent(y)));
        g.registerZ((s, z) -> s.setShZ(0, Splats.colorToDirectCurrent(z)));

        g.register((s, x, y, z) ->
        {
            float max = Math.max(x, Math.max(y, z));
            float angleRad = (float) (max * Math.PI * 0.5);
            setRotation(s, x, y, z, angleRad);
        });

        return g.generate();
    }

    /**
     * Create a splat data set with different spherical harmonics, degree 1
     * 
     * @return The data set
     */
    public static List<MutableSplat> createShs1()
    {
        int shDegree = 1;
        Supplier<MutableSplat> supplier = () ->
        {
            MutableSplat s = Splats.create(shDegree);
            setDefaults(s);
            return s;
        };
        SplatGridBuilder g = new SplatGridBuilder(3, 3, 3, supplier);

        float maxPosition = 100.0f;
        g.registerX(0.0f, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0f, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0f, maxPosition, MutableSplat::setPositionZ);

        g.register((s, x, y, z) ->
        {
            for (int d = 0; d < 4; d++)
            {
                int r = d % 3;
                float fx = r == 0 ? 1.0f : 0.0f;
                float fy = r == 1 ? 1.0f : 0.0f;
                float fz = r == 2 ? 1.0f : 0.0f;
                s.setShX(d, x * fx);
                s.setShY(d, y * fy);
                s.setShZ(d, z * fz);
            }
        });

        return g.generate();
    }

    /**
     * Create a splat data set with different spherical harmonics, degree 2
     * 
     * @return The data set
     */
    public static List<MutableSplat> createShs2()
    {
        int shDegree = 2;
        Supplier<MutableSplat> supplier = () ->
        {
            MutableSplat s = Splats.create(shDegree);
            setDefaults(s);
            return s;
        };
        SplatGridBuilder g = new SplatGridBuilder(3, 3, 3, supplier);

        float maxPosition = 100.0f;
        g.registerX(0.0f, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0f, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0f, maxPosition, MutableSplat::setPositionZ);

        g.register((s, x, y, z) ->
        {
            for (int d = 0; d < 9; d++)
            {
                int r = d % 3;
                float fx = r == 0 ? 1.0f : 0.0f;
                float fy = r == 1 ? 1.0f : 0.0f;
                float fz = r == 2 ? 1.0f : 0.0f;
                s.setShX(d, x * fx);
                s.setShY(d, y * fy);
                s.setShZ(d, z * fz);
            }
        });

        return g.generate();
    }

    /**
     * Create a splat data set with different spherical harmonics, degree 3
     * 
     * @return The data set
     */
    public static List<MutableSplat> createShs3()
    {
        int shDegree = 3;
        Supplier<MutableSplat> supplier = () ->
        {
            MutableSplat s = Splats.create(shDegree);
            setDefaults(s);
            return s;
        };
        SplatGridBuilder g = new SplatGridBuilder(3, 3, 3, supplier);

        float maxPosition = 100.0f;
        g.registerX(0.0f, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0f, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0f, maxPosition, MutableSplat::setPositionZ);

        g.register((s, x, y, z) ->
        {
            for (int d = 0; d < 16; d++)
            {
                int r = d % 3;
                float fx = r == 0 ? 1.0f : 0.0f;
                float fy = r == 1 ? 1.0f : 0.0f;
                float fz = r == 2 ? 1.0f : 0.0f;
                s.setShX(d, x * fx);
                s.setShY(d, y * fy);
                s.setShZ(d, z * fz);
            }
        });

        return g.generate();
    }

    /**
     * Create a splat data set with different rotations
     * 
     * @return The data set
     */
    public static List<MutableSplat> createRotations2D()
    {
        int shDegree = 0;
        Supplier<MutableSplat> supplier = () ->
        {
            MutableSplat s = Splats.create(shDegree);
            setDefaults(s);
            return s;
        };
        SplatGridBuilder g = new SplatGridBuilder(7, 3, 1, supplier);

        float maxPosition = 250.0f;
        g.registerX(0.0f, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0f, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0f, maxPosition, MutableSplat::setPositionZ);

        g.registerY((s, y) ->
        {
            if (y == 0.0f)
            {
                setScale(s, 3.0f, 1.0f, 1.0f);
                setColor(s, 1.0f, 0.0f, 0.0f);
            }
            if (y == 0.5f)
            {
                setScale(s, 1.0f, 3.0f, 1.0f);
                setColor(s, 0.0f, 1.0f, 0.0f);
            }
            if (y == 1.0f)
            {
                setScale(s, 1.0f, 1.0f, 3.0f);
                setColor(s, 0.0f, 0.0f, 1.0f);
            }
        });
        g.register((s, x, y, z) ->
        {
            float angleRad = (float) (x * Math.PI * 0.5);
            if (y == 0.0f)
            {
                setRotation(s, 0.0f, 1.0f, 0.0f, angleRad);
            }
            if (y == 0.5f)
            {
                setRotation(s, 0.0f, 0.0f, 1.0f, angleRad);
            }
            if (y == 1.0f)
            {
                setRotation(s, 1.0f, 0.0f, 0.0f, angleRad);
            }
        });

        return g.generate();
    }

    /**
     * Set some default values for the given splat
     * 
     * @param s The splat
     */
    private static void setDefaults(MutableSplat s)
    {
        s.setOpacity(20.0f);
        setColor(s, 1.0f, 1.0f, 1.0f);
        setRotation(s, 0.0f, 0.0f, 0.0f, 0.0f);
        setScale(s, 1.0f, 1.0f, 1.0f);
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
    private static void setColor(MutableSplat s, float r, float g, float b)
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
    private static void setRotation(MutableSplat s, float x, float y, float z,
        float angleRad)
    {
        float q[] = createScalarLastQuaternionFromAxisAngle(x, y, z, angleRad);
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
    private static void setScale(MutableSplat s, float x, float y, float z)
    {
        s.setScaleX(x);
        s.setScaleY(y);
        s.setScaleZ(z);
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
    private static float[] createScalarLastQuaternionFromAxisAngle(float x,
        float y, float z, float angleRad)
    {
        float halfAngleRad = angleRad * 0.5f;
        float s = (float) Math.sin(halfAngleRad);

        float lenSquared = x * x + y * y + z * z;
        if (lenSquared < EPSILON)
        {
            return new float[]
            { 0.0f, 0.0f, 0.0f, 1.0f };
        }

        float invLen = (float) (1.0 / Math.sqrt(lenSquared));
        float qx = x * invLen * s;
        float qy = y * invLen * s;
        float qz = z * invLen * s;
        float qw = (float) Math.cos(halfAngleRad);

        float q[] = new float[]
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
    static float[] createAxisAngleRadFromScalarLastQuaternion(float qx,
        float qy, float qz, float qw)
    {
        float x = 1.0f;
        float y = 0.0f;
        float z = 0.0f;
        float angleRad = 0.0f;
        if (Math.abs(qw - 1.0) >= EPSILON && Math.abs(qw + 1.0) >= EPSILON)
        {
            float f = (float) (1.0 / Math.sqrt(1.0 - qw * qw));
            x = qx * f;
            y = qy * f;
            z = qz * f;

        }
        if (Math.abs(qw - 1.0) >= EPSILON)
        {
            angleRad = (float) (2.0 * Math.acos(qw));
        }
        float a[] =
        { x, y, z, angleRad };
        return a;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatGrids()
    {
        // Private constructor to prevent instantiation
    }
}
