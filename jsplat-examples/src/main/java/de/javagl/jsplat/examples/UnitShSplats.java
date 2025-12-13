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
 * Internal utility methods to create "unit spherical harmonics" splat data
 */
public class UnitShSplats
{
    /**
     * The size of the cube
     */
    private static final float size = 100.0f;

    /**
     * The base scale factor for all splats
     */
    private static final float baseScale = 1.0f;

    /**
     * Create a list of splats, representing a cube with a "unit spherical
     * harmonics splat" at its center.
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
            float x = -0.5f + ((c & 1) == 0 ? 0.0f : 1.0f);
            float y = -0.5f + ((c & 2) == 0 ? 0.0f : 1.0f);
            float z = -0.5f + ((c & 4) == 0 ? 0.0f : 1.0f);
            add(splats, x, y, z);
        }
        addUnitSh(splats);
        return splats;
    }

    /**
     * Add a "unit spherical harmonics splat" to the given list. Details are not
     * specified.
     * 
     * @param splats The splats
     */
    private static void addUnitSh(List<MutableSplat> splats)
    {
        MutableSplat splat = Splats.create(3);

        splat.setPositionX(0.0f);
        splat.setPositionY(0.0f);
        splat.setPositionZ(0.0f);

        splat.setScaleX(3.0f * baseScale);
        splat.setScaleY(3.0f * baseScale);
        splat.setScaleZ(3.0f * baseScale);

        splat.setRotationX(0.0f);
        splat.setRotationY(0.0f);
        splat.setRotationZ(0.0f);
        splat.setRotationW(1.0f);

        splat.setOpacity(Splats.alphaToOpacity(1.0f));

        // One day, someone will read this, and wonder where 
        // these values are coming from.
        
        int d = 0;

        splat.setShX(d, 0.19689117349855656f);
        splat.setShY(d, 0.19701742462465832f);
        splat.setShZ(d, 0.1968082144814114f);
        d++;

        // Dim 1
        splat.setShX(d, -0.6390529124621291f);
        splat.setShY(d, 1.0730615144907147f);
        splat.setShZ(d, -0.6388168750684198f);
        d++;

        // Dim 2
        splat.setShX(d, 0.6387493239628561f);
        splat.setShY(d, 0.6386066087881057f);
        splat.setShZ(d, -1.0728855189454842f);
        d++;

        // Dim 3
        splat.setShX(d, 1.0728635469386794f);
        splat.setShY(d, -0.6389343617744989f);
        splat.setShZ(d, -0.6387102939070701f);
        d++;

        // Dim 4
        splat.setShX(d, 0.2288865409514229f);
        splat.setShY(d, 0.22883301398721945f);
        splat.setShZ(d, -5.788200187595294E-6f);
        d++;

        // Dim 5
        splat.setShX(d, -2.5718827998844063E-5f);
        splat.setShY(d, -0.22887600094296534f);
        splat.setShZ(d, -0.22883277467319174f);
        d++;

        // Dim 6
        splat.setShX(d, -0.04403047602516996f);
        splat.setShY(d, -0.04397372941428612f);
        splat.setShZ(d, 0.08799531361390533f);
        d++;

        // Dim 7
        splat.setShX(d, -0.22888164895998098f);
        splat.setShY(d, -5.907535404192643E-6f);
        splat.setShZ(d, -0.22906249045635607f);
        d++;

        // Dim 8
        splat.setShX(d, 0.07618985703501813f);
        splat.setShY(d, -0.07637156357642105f);
        splat.setShZ(d, -2.1415442069683266E-5f);
        d++;

        // Dim 9
        splat.setShX(d, 0.2739307455019897f);
        splat.setShY(d, 0.02589419616012356f);
        splat.setShZ(d, 0.12407953674212568f);
        d++;

        // Dim 10
        splat.setShX(d, -1.285382433181138f);
        splat.setShY(d, -1.3224151408611615f);
        splat.setShZ(d, -1.3605540604623292f);
        d++;

        // Dim 11
        splat.setShX(d, 0.05738138052643116f);
        splat.setShY(d, 0.019969600263549214f);
        splat.setShZ(d, 0.25077591195710025f);
        d++;

        // Dim 12
        splat.setShX(d, 0.2520360451989494f);
        splat.setShY(d, 0.2517234445249994f);
        splat.setShZ(d, 0.03241969076161055f);
        d++;

        // Dim 13
        splat.setShX(d, 0.019920206825017717f);
        splat.setShY(d, 0.057338277160965845f);
        splat.setShZ(d, 0.25081388794113546f);
        d++;

        // Dim 14
        splat.setShX(d, -0.12233861767199117f);
        splat.setShY(d, 0.12225649047975251f);
        splat.setShZ(d, 8.455848014232714E-6f);
        d++;

        // Dim 15
        splat.setShX(d, -0.02563393584434004f);
        splat.setShY(d, -0.2737727007701105f);
        splat.setShZ(d, -0.12410133512136956f);

        splats.add(splat);
    }

    /**
     * Add a splat to the given list, with properties derived from the given
     * parameters and some constants. Details are not specified.
     * 
     * @param splats The splats
     * @param npx The normalized x-coordinate
     * @param npy The normalized y-coordinate
     * @param npz The normalized z-coordinate
     */
    private static void add(List<MutableSplat> splats, float npx, float npy,
        float npz)
    {
        MutableSplat splat = Splats.create(3);

        splat.setPositionX(npx * size);
        splat.setPositionY(npy * size);
        splat.setPositionZ(npz * size);

        splat.setScaleX(baseScale);
        splat.setScaleY(baseScale);
        splat.setScaleZ(baseScale);

        splat.setRotationX(0.0f);
        splat.setRotationY(0.0f);
        splat.setRotationZ(0.0f);
        splat.setRotationW(1.0f);

        splat.setOpacity(Splats.alphaToOpacity(1.0f));

        splat.setShX(0, Splats.colorToDirectCurrent(1.0f));
        splat.setShY(0, Splats.colorToDirectCurrent(1.0f));
        splat.setShZ(0, Splats.colorToDirectCurrent(1.0f));

        splats.add(splat);
    }

    /**
     * Private constructor to prevent instantiation
     */
    private UnitShSplats()
    {
        // Private constructor to prevent instantiation
    }
}
