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
    private static final float size = 10.0f;

    /**
     * A scale factor so that the edges look nice
     */
    private static final float edgeScale = 1.0f;

    /**
     * The base scale factor for all splats
     */
    private static final float baseScale = 0.1f;

    /**
     * The alpha value for all splats
     */
    private static final float alpha = 1.0f;
    
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
            float x = (c & 1) == 0 ? 0.0f : 1.0f;
            float y = (c & 2) == 0 ? 0.0f : 1.0f;
            float z = (c & 4) == 0 ? 0.0f : 1.0f;

            add(splats, x, y, z, 1.0f, 1.0f, 1.0f);
        }

        add(splats, 0.5f, 0.0f, 0.0f, size, 1.0f, 1.0f);
        add(splats, 0.5f, 1.0f, 0.0f, size, 1.0f, 1.0f);
        add(splats, 0.5f, 0.0f, 1.0f, size, 1.0f, 1.0f);
        add(splats, 0.5f, 1.0f, 1.0f, size, 1.0f, 1.0f);

        add(splats, 0.0f, 0.5f, 0.0f, 1.0f, size, 1.0f);
        add(splats, 0.0f, 0.5f, 1.0f, 1.0f, size, 1.0f);
        add(splats, 1.0f, 0.5f, 0.0f, 1.0f, size, 1.0f);
        add(splats, 1.0f, 0.5f, 1.0f, 1.0f, size, 1.0f);

        add(splats, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f, size);
        add(splats, 0.0f, 1.0f, 0.5f, 1.0f, 1.0f, size);
        add(splats, 1.0f, 0.0f, 0.5f, 1.0f, 1.0f, size);
        add(splats, 1.0f, 1.0f, 0.5f, 1.0f, 1.0f, size);

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
    private static void add(List<MutableSplat> splats, float npx, float npy,
        float npz, float sx, float sy, float sz)
    {
        MutableSplat splat = Splats.create(0);
        
        splat.setPositionX(npx * size);
        splat.setPositionY(npy * size);
        splat.setPositionZ(npz * size);

        splat.setScaleX(sx * baseScale * edgeScale);
        splat.setScaleY(sy * baseScale * edgeScale);
        splat.setScaleZ(sz * baseScale * edgeScale);

        splat.setRotationX(0.0f);
        splat.setRotationY(0.0f);
        splat.setRotationZ(0.0f);
        splat.setRotationW(1.0f);

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
