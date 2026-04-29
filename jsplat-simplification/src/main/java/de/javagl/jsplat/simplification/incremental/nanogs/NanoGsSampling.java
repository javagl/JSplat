/*
 * www.javagl.de - JSplat
 *
 * Copyright 2026 Marco Hutter - http://www.javagl.de
 * 
 * Ported from https://github.com/saliteta/NanoGS
 * Commit: 62ddc34e230a01c061b762103ef69113f6259e48
 * 
 * Published under "Attribution-NonCommercial 4.0 International" license.
 * See the "NanoGS-LICENSE.txt" in the root directory of this project.
 */
package de.javagl.jsplat.simplification.incremental.nanogs;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * Utility methods for the sampling in NanoGS.
 * 
 * Ported from https://github.com/saliteta/NanoGS
 * Commit: 62ddc34e230a01c061b762103ef69113f6259e48
 */
class NanoGsSampling
{
    /**
     * Computes the specified number of 3D sample points, following a Gaussian
     * distribution.
     * 
     * Location: scripts/simplify.js#L1199
     * 
     * @param n The number of sample points
     * @param seed The random seet
     * @return The points, as an array of 3-element arrays
     */
    static float[][] makeGaussianSamples(int n, int seed)
    {
        Random rand = new Random(seed);
        float out[][] = new float[n][];
        IntStream.range(0, n).parallel().forEach(i ->
        {
            float u1 = Math.max(rand.nextFloat(), 1e-12f);
            float u2 = rand.nextFloat();
            float u3 = Math.max(rand.nextFloat(), 1e-12f);
            float u4 = rand.nextFloat();

            float r1 = (float) (Math.sqrt(-2.0 * Math.log(u1)));
            float t1 = (float) (2.0 * Math.PI * u2);
            float r2 = (float) (Math.sqrt(-2.0 * Math.log(u3)));
            float t2 = (float) (2.0 * Math.PI * u4);

            float x = (float) (r1 * Math.cos(t1));
            float y = (float) (r1 * Math.sin(t1));
            float z = (float) (r2 * Math.cos(t2));
            out[i] = new float[]
            { x, y, z, };
        });
        return out;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private NanoGsSampling()
    {
        // Private constructor to prevent instantiation
    }

}
