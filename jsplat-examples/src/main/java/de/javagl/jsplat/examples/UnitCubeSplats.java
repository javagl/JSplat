/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.util.ArrayList;
import java.util.List;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splats;

/**
 * Internal utility methods to create "unit cube" splat data
 */
public class UnitCubeSplats
{
    /**
     * The size of the cube
     */
    private static final double size = 10.0;

    /**
     * A scale factor so that the edges look nice
     */
    private static final double edgeScale = 1.0;

    /**
     * The base scale factor for all splats
     */
    private static final double baseScale = 0.1;

    /**
     * The alpha value for all splats
     */
    private static final double alpha = 1.0;
    
    /**
     * Create a list of splats, representing a "unit cube".
     * 
     * Many details are intentionally not specified.
     * 
     * @return The splats
     */
    public static List<MutableSplat> create()
    {
        List<MutableSplat> splats = new ArrayList<MutableSplat>();

        for (int c = 0; c < 8; c++)
        {
            double x = (c & 1) == 0 ? 0.0 : 1.0;
            double y = (c & 2) == 0 ? 0.0 : 1.0;
            double z = (c & 4) == 0 ? 0.0 : 1.0;

            add(splats, x, y, z, 1.0, 1.0, 1.0);
        }

        add(splats, 0.5, 0.0, 0.0, size, 1.0, 1.0);
        add(splats, 0.5, 1.0, 0.0, size, 1.0, 1.0);
        add(splats, 0.5, 0.0, 1.0, size, 1.0, 1.0);
        add(splats, 0.5, 1.0, 1.0, size, 1.0, 1.0);

        add(splats, 0.0, 0.5, 0.0, 1.0, size, 1.0);
        add(splats, 0.0, 0.5, 1.0, 1.0, size, 1.0);
        add(splats, 1.0, 0.5, 0.0, 1.0, size, 1.0);
        add(splats, 1.0, 0.5, 1.0, 1.0, size, 1.0);

        add(splats, 0.0, 0.0, 0.5, 1.0, 1.0, size);
        add(splats, 0.0, 1.0, 0.5, 1.0, 1.0, size);
        add(splats, 1.0, 0.0, 0.5, 1.0, 1.0, size);
        add(splats, 1.0, 1.0, 0.5, 1.0, 1.0, size);

        return splats;
    }

    /**
     * Add a splat to the given list, with properties derived from the given
     * parameters and some constants. Details are not specified.
     * 
     * @param splats The splats
     * @param npx The normalized x-coordinate
     * @param npy The normalized y-coordinate
     * @param npz The normalized z-coordinate
     * @param sx The scaling factor in x-direction
     * @param sy The scaling factor in y-direction
     * @param sz The scaling factor in z-direction
     */
    private static void add(List<MutableSplat> splats, double npx, double npy,
        double npz, double sx, double sy, double sz)
    {
        MutableSplat splat = Splats.create(0);
        
        splat.setPositionX(npx * size);
        splat.setPositionY(npy * size);
        splat.setPositionZ(npz * size);

        splat.setScaleX(sx * baseScale * edgeScale);
        splat.setScaleY(sy * baseScale * edgeScale);
        splat.setScaleZ(sz * baseScale * edgeScale);

        splat.setRotationX(0.0);
        splat.setRotationY(0.0);
        splat.setRotationZ(0.0);
        splat.setRotationW(1.0);

        splat.setOpacity(Splats.alphaToOpacity(alpha));

        splat.setShX(0, Splats.colorToDirectCurrent(npx));
        splat.setShY(0, Splats.colorToDirectCurrent(npy));
        splat.setShZ(0, Splats.colorToDirectCurrent(npz));

        splats.add(splat);
    }

    /**
     * Private constructor to prevent instantiation
     */
    private UnitCubeSplats()
    {
        // Private constructor to prevent instantiation
    }
}
