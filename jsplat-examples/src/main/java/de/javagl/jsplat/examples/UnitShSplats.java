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
    private static final double size = 100.0;

    /**
     * The base scale factor for all splats
     */
    private static final double baseScale = 1.0;

    /**
     * Create a list of splats, representing a cube.
     * 
     * Many details are intentionally not specified.
     * 
     * @param degree The degree
     * @return The splats
     */
    private static List<MutableSplat> createCorners(int degree)
    {
        List<MutableSplat> splats = new ArrayList<MutableSplat>();
        for (int c = 0; c < 8; c++)
        {
            double x = -0.5 + ((c & 1) == 0 ? 0.0f : 1.0f);
            double y = -0.5 + ((c & 2) == 0 ? 0.0f : 1.0f);
            double z = -0.5 + ((c & 4) == 0 ? 0.0f : 1.0f);
            add(splats, degree, x, y, z);
        }
        return splats;
    }

    /**
     * Create a list of splats, representing a cube with a "unit spherical
     * harmonics splat" at its center.
     * 
     * Many details are intentionally not specified.
     * 
     * @return The splats
     */
    public static List<MutableSplat> createDeg2()
    {
        List<MutableSplat> splats = createCorners(2);
        splats.add(createUnitShSplatDeg2());
        return splats;
    }

    /**
     * Create a "unit spherical harmonics splat" with degree 2. Details are not
     * specified.
     * 
     * @return The splat
     */
    private static MutableSplat createUnitShSplatDeg2()
    {
        MutableSplat splat = Splats.create(2);

        splat.setPositionX(0.0);
        splat.setPositionY(0.0);
        splat.setPositionZ(0.0);

        splat.setScaleX(3.0 * baseScale);
        splat.setScaleY(3.0 * baseScale);
        splat.setScaleZ(3.0 * baseScale);

        splat.setRotationX(0.0);
        splat.setRotationY(0.0);
        splat.setRotationZ(0.0);
        splat.setRotationW(1.0);

        splat.setOpacity(Splats.alphaToOpacity(1.0));

        // One day, someone will read this, and wonder where
        // these values are coming from.
        
        int d = 0;

        splat.setShX(d, 0.1476983315919267);
        splat.setShY(d, 0.14769703694704606);
        splat.setShZ(d, 0.14771121930450226);
        d++;

        // Dim 1
        splat.setShX(d, -0.7830568718641374);
        splat.setShY(d, 1.054372367029317);
        splat.setShZ(d, -0.7830538122533596);
        d++;

        // Dim 2
        splat.setShX(d, 0.7830484620033045);
        splat.setShY(d, 0.7830548598697407);
        splat.setShZ(d, -1.054407314935455);
        d++;

        // Dim 3
        splat.setShX(d, 1.05434329450671);
        splat.setShY(d, -0.7829838731344525);
        splat.setShZ(d, -0.7830519155090939);
        d++;

        // Dim 4
        splat.setShX(d, 0.22883454327537334);
        splat.setShY(d, 0.22881694536762498);
        splat.setShZ(d, -7.2464152314211105E-6);
        d++;

        // Dim 5
        splat.setShX(d, -1.1875671158900758E-5);
        splat.setShY(d, -0.22880031503889064);
        splat.setShZ(d, -0.22880974771043117);
        d++;

        // Dim 6
        splat.setShX(d, -0.026421538828617974);
        splat.setShY(d, -0.02640910542972641);
        splat.setShZ(d, 0.05283968482224943);
        d++;

        // Dim 7
        splat.setShX(d, -0.2289855776512113);
        splat.setShY(d, -1.917440275245319E-5);
        splat.setShZ(d, -0.2288323230516771);
        d++;

        // Dim 8
        splat.setShX(d, 0.04575373638021474);
        splat.setShY(d, -0.045751875002436826);
        splat.setShZ(d, -8.16414088511408E-6);

        return splat;
    }

    /**
     * Create a list of splats, representing a cube with a "unit spherical
     * harmonics splat" at its center.
     * 
     * Many details are intentionally not specified.
     * 
     * @return The splats
     */
    public static List<MutableSplat> createDeg3()
    {
        List<MutableSplat> splats = createCorners(3);
        splats.add(createUnitShSplatDeg3());
        return splats;
    }

    /**
     * Create a "unit spherical harmonics splat" with degree 3. Details are not
     * specified.
     * 
     * @return The splat
     */
    private static MutableSplat createUnitShSplatDeg3()
    {
        MutableSplat splat = Splats.create(3);

        splat.setPositionX(0.0);
        splat.setPositionY(0.0);
        splat.setPositionZ(0.0);

        splat.setScaleX(3.0 * baseScale);
        splat.setScaleY(3.0 * baseScale);
        splat.setScaleZ(3.0 * baseScale);

        splat.setRotationX(0.0);
        splat.setRotationY(0.0);
        splat.setRotationZ(0.0);
        splat.setRotationW(1.0);

        splat.setOpacity(Splats.alphaToOpacity(1.0));

        // One day, someone will read this, and wonder where
        // these values are coming from.

        int d = 0;

        splat.setShX(d, 0.19689117349855656);
        splat.setShY(d, 0.19701742462465832);
        splat.setShZ(d, 0.1968082144814114);
        d++;

        // Dim 1
        splat.setShX(d, -0.6390529124621291);
        splat.setShY(d, 1.0730615144907147);
        splat.setShZ(d, -0.6388168750684198);
        d++;

        // Dim 2
        splat.setShX(d, 0.6387493239628561);
        splat.setShY(d, 0.6386066087881057);
        splat.setShZ(d, -1.0728855189454842);
        d++;

        // Dim 3
        splat.setShX(d, 1.0728635469386794);
        splat.setShY(d, -0.6389343617744989);
        splat.setShZ(d, -0.6387102939070701);
        d++;

        // Dim 4
        splat.setShX(d, 0.2288865409514229);
        splat.setShY(d, 0.22883301398721945);
        splat.setShZ(d, -5.788200187595294E-6);
        d++;

        // Dim 5
        splat.setShX(d, -2.5718827998844063E-5);
        splat.setShY(d, -0.22887600094296534);
        splat.setShZ(d, -0.22883277467319174);
        d++;

        // Dim 6
        splat.setShX(d, -0.04403047602516996);
        splat.setShY(d, -0.04397372941428612);
        splat.setShZ(d, 0.08799531361390533);
        d++;

        // Dim 7
        splat.setShX(d, -0.22888164895998098);
        splat.setShY(d, -5.907535404192643E-6);
        splat.setShZ(d, -0.22906249045635607);
        d++;

        // Dim 8
        splat.setShX(d, 0.07618985703501813);
        splat.setShY(d, -0.07637156357642105);
        splat.setShZ(d, -2.1415442069683266E-5);
        d++;

        // Dim 9
        splat.setShX(d, 0.2739307455019897);
        splat.setShY(d, 0.02589419616012356);
        splat.setShZ(d, 0.12407953674212568);
        d++;

        // Dim 10
        splat.setShX(d, -1.285382433181138);
        splat.setShY(d, -1.3224151408611615);
        splat.setShZ(d, -1.3605540604623292);
        d++;

        // Dim 11
        splat.setShX(d, 0.05738138052643116);
        splat.setShY(d, 0.019969600263549214);
        splat.setShZ(d, 0.25077591195710025);
        d++;

        // Dim 12
        splat.setShX(d, 0.2520360451989494);
        splat.setShY(d, 0.2517234445249994);
        splat.setShZ(d, 0.03241969076161055);
        d++;

        // Dim 13
        splat.setShX(d, 0.019920206825017717);
        splat.setShY(d, 0.057338277160965845);
        splat.setShZ(d, 0.25081388794113546);
        d++;

        // Dim 14
        splat.setShX(d, -0.12233861767199117);
        splat.setShY(d, 0.12225649047975251);
        splat.setShZ(d, 8.455848014232714E-6);
        d++;

        // Dim 15
        splat.setShX(d, -0.02563393584434004);
        splat.setShY(d, -0.2737727007701105);
        splat.setShZ(d, -0.12410133512136956);

        return splat;
    }

    /**
     * Add a splat to the given list, with properties derived from the given
     * parameters and some constants. Details are not specified.
     * 
     * @param splats The splats
     * @param degree The degree
     * @param npx The normalized x-coordinate
     * @param npy The normalized y-coordinate
     * @param npz The normalized z-coordinate
     */
    private static void add(List<MutableSplat> splats, int degree, double npx,
        double npy, double npz)
    {
        MutableSplat splat = Splats.create(degree);

        splat.setPositionX(npx * size);
        splat.setPositionY(npy * size);
        splat.setPositionZ(npz * size);

        splat.setScaleX(baseScale);
        splat.setScaleY(baseScale);
        splat.setScaleZ(baseScale);

        splat.setRotationX(0.0);
        splat.setRotationY(0.0);
        splat.setRotationZ(0.0);
        splat.setRotationW(1.0);

        splat.setOpacity(Splats.alphaToOpacity(1.0));

        if (npx == -0.5 && npy == -0.5 && npz == -0.5)
        {
            splat.setShX(0, Splats.colorToDirectCurrent(0.1));
            splat.setShY(0, Splats.colorToDirectCurrent(0.1));
            splat.setShZ(0, Splats.colorToDirectCurrent(0.1));
        }
        else
        {
            splat.setShX(0, Splats.colorToDirectCurrent(npx + 0.5));
            splat.setShY(0, Splats.colorToDirectCurrent(npy + 0.5));
            splat.setShZ(0, Splats.colorToDirectCurrent(npz + 0.5));
        }

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
