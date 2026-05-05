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

        double maxPosition = 100.0;
        g.registerX(0.0, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0, maxPosition, MutableSplat::setPositionZ);

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
        double minX, double minY, double minZ, 
        double maxX, double maxY, double maxZ)
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

        double maxPosition = 100.0;
        g.registerX(0.0, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0, maxPosition, MutableSplat::setPositionZ);

        double maxScale = 2.0;
        g.registerX(0.0, maxScale, MutableSplat::setScaleX);
        g.registerY(0.0, maxScale, MutableSplat::setScaleY);
        g.registerZ(0.0, maxScale, MutableSplat::setScaleZ);

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

        double maxPosition = 100.0;
        g.registerX(0.0f, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0f, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0f, maxPosition, MutableSplat::setPositionZ);

        g.registerX((s, x) -> s.setShX(0, Splats.colorToDirectCurrent(x)));
        g.registerY((s, y) -> s.setShY(0, Splats.colorToDirectCurrent(y)));
        g.registerZ((s, z) -> s.setShZ(0, Splats.colorToDirectCurrent(z)));

        double minOpacity = -20.0;
        double maxOpacity = 20.0;
        g.register((s, x, y, z) ->
        {
            double dx = 0.5 - x;
            double dy = 0.5 - y;
            double dz = 0.5 - z;
            double d = 1.0 - Math.sqrt(dx * dx + dy * dy + dz * dz);
            double opacity = minOpacity + d * (maxOpacity - minOpacity);
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

        double maxPosition = 100.0;
        g.registerX(0.0, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0, maxPosition, MutableSplat::setPositionZ);

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

        double maxPosition = 100.0f;
        g.registerX(0.0, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0, maxPosition, MutableSplat::setPositionZ);

        double maxScale = 2.0;
        g.registerX(0.0, maxScale, MutableSplat::setScaleX);
        g.registerY(0.0, maxScale, MutableSplat::setScaleY);
        g.registerZ(0.0, maxScale, MutableSplat::setScaleZ);

        g.registerX((s, x) -> s.setShX(0, Splats.colorToDirectCurrent(x)));
        g.registerY((s, y) -> s.setShY(0, Splats.colorToDirectCurrent(y)));
        g.registerZ((s, z) -> s.setShZ(0, Splats.colorToDirectCurrent(z)));

        g.register((s, x, y, z) ->
        {
            double max = Math.max(x, Math.max(y, z));
            double angleRad = max * Math.PI * 0.5;
            SplatSetters.setRotationAxisAngleRad(s, x, y, z, angleRad);
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

        double maxPosition = 250.0;
        g.registerX(0.0, maxPosition, MutableSplat::setPositionX);
        g.registerY(0.0, maxPosition, MutableSplat::setPositionY);
        g.registerZ(0.0, maxPosition, MutableSplat::setPositionZ);

        g.registerY((s, y) ->
        {
            if (y == 0.0)
            {
                SplatSetters.setScale(s, 3.0, 1.0, 1.0);
                SplatSetters.setColor(s, 1.0, 0.0, 0.0);
            }
            if (y == 0.5)
            {
                SplatSetters.setScale(s, 1.0, 3.0, 1.0);
                SplatSetters.setColor(s, 0.0, 1.0, 0.0);
            }
            if (y == 1.0)
            {
                SplatSetters.setScale(s, 1.0, 1.0, 3.0);
                SplatSetters.setColor(s, 0.0, 0.0, 1.0);
            }
        });
        g.register((s, x, y, z) ->
        {
            double aRad = x * Math.PI * 0.5;
            if (y == 0.0)
            {
                SplatSetters.setRotationAxisAngleRad(s, 0.0, 1.0, 0.0, aRad);
            }
            if (y == 0.5)
            {
                SplatSetters.setRotationAxisAngleRad(s, 0.0, 0.0, 1.0, aRad);
            }
            if (y == 1.0)
            {
                SplatSetters.setRotationAxisAngleRad(s, 1.0, 0.0, 0.0, aRad);
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
