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
            SplatSetters.setDefaults(s);
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
     * @param pointsX The number of points in x-direction
     * @param pointsY The number of points in y-direction
     * @param pointsZ The number of points in z-direction
     * @param minX The minimum x-position
     * @param minY The minimum y-position
     * @param minZ The minimum z-position
     * @param maxX The maximum x-position
     * @param maxY The maximum y-position
     * @param maxZ The maximum z-position
     * 
     * @return The data set
     */
    public static List<MutableSplat> createBox(
        int pointsX, int pointsY, int pointsZ,
        float minX, float minY, float minZ, 
        float maxX, float maxY, float maxZ)
    {
        int shDegree = 0;
        Supplier<MutableSplat> supplier = () ->
        {
            MutableSplat s = Splats.create(shDegree);
            SplatSetters.setDefaults(s);
            return s;
        };
        SplatGridBuilder g = new SplatGridBuilder(
            pointsX, pointsY, pointsZ, supplier);

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
            SplatSetters.setDefaults(s);
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
            SplatSetters.setDefaults(s);
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
            SplatSetters.setDefaults(s);
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
            SplatSetters.setDefaults(s);
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
            SplatSetters.setRotationAxisAngleRad(s, x, y, z, angleRad);
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
            SplatSetters.setDefaults(s);
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
            SplatSetters.setDefaults(s);
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
            SplatSetters.setDefaults(s);
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
            SplatSetters.setDefaults(s);
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
                SplatSetters.setScale(s, 3.0f, 1.0f, 1.0f);
                SplatSetters.setColor(s, 1.0f, 0.0f, 0.0f);
            }
            if (y == 0.5f)
            {
                SplatSetters.setScale(s, 1.0f, 3.0f, 1.0f);
                SplatSetters.setColor(s, 0.0f, 1.0f, 0.0f);
            }
            if (y == 1.0f)
            {
                SplatSetters.setScale(s, 1.0f, 1.0f, 3.0f);
                SplatSetters.setColor(s, 0.0f, 0.0f, 1.0f);
            }
        });
        g.register((s, x, y, z) ->
        {
            float aRad = (float) (x * Math.PI * 0.5);
            if (y == 0.0f)
            {
                SplatSetters.setRotationAxisAngleRad(s, 0.0f, 1.0f, 0.0f, aRad);
            }
            if (y == 0.5f)
            {
                SplatSetters.setRotationAxisAngleRad(s, 0.0f, 0.0f, 1.0f, aRad);
            }
            if (y == 1.0f)
            {
                SplatSetters.setRotationAxisAngleRad(s, 1.0f, 0.0f, 0.0f, aRad);
            }
        });

        return g.generate();
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatGrids()
    {
        // Private constructor to prevent instantiation
    }
}
