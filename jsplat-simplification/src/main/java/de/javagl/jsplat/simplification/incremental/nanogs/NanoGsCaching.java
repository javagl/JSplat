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

import java.util.List;
import java.util.stream.IntStream;

import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;

/**
 * Implementation of the caching functionality in NanoGS
 * 
 * Ported from https://github.com/saliteta/NanoGS 
 * Commit: 62ddc34e230a01c061b762103ef69113f6259e48
 */
class NanoGsCaching
{
    /**
     * Constant for (2*PI)^1.5
     */
    private static final float TWO_PI_POW_1P5 =
        (float) Math.pow(2.0 * Math.PI, 1.5);

    /**
     * The cache that is passed to {@link NanoGsCost#fullCostPairCached}
     */
    static class Cache
    {
        /**
         * The 3x3 rotation matrices
         */
        float[] R;

        /**
         * The 3x3 rotation matrices, transposed
         */
        float[] Rt;

        /**
         * The 3 variances
         */
        float[] v;

        /**
         * The 3 inverse diagonals
         */
        float[] invdiag;

        /**
         * The 1 logarithm of the determinant
         */
        float[] logdet;

        /**
         * The 3x3 covariance matrix
         */
        float[] sigma;

        /**
         * The 1 "mass" (alpha times volume)
         */
        float[] mass;

        /**
         * The 3 linear scale values (Not present in the original NanoGS
         * implementation)
         */
        float[] linearScale;

        /**
         * The 1 alpha value (Not present in the original NanoGS implementation)
         */
        float[] alpha;

        /**
         * Creates a new cache with the given size
         * 
         * @param N The size
         */
        Cache(int N)
        {
            R = new float[N * 9];
            Rt = new float[N * 9];
            v = new float[N * 3];
            invdiag = new float[N * 3];
            logdet = new float[N];
            sigma = new float[N * 9];
            mass = new float[N];
            linearScale = new float[N * 3];
            alpha = new float[N];
        }
    }

    /**
     * Create the cache for the given splats
     * 
     * @param state The splats
     * @param epsCov The epsilon to add for covariance computations
     * @return The cache
     */
    static Cache buildPerSplatCache(List<? extends Splat> state, float epsCov)
    {
        int N = state.size();

        Cache c = new Cache(N);
        IntStream.range(0, N).parallel().forEach(i ->
        {
            int i3 = i * 3;
            int i9 = i * 9;

            Splat s = state.get(i);

            float sx = (float) Math.exp(s.getScaleX());
            float sy = (float) Math.exp(s.getScaleY());
            float sz = (float) Math.exp(s.getScaleZ());

            c.linearScale[i3 + 0] = sx;
            c.linearScale[i3 + 1] = sy;
            c.linearScale[i3 + 2] = sz;

            float vx = sx * sx + epsCov;
            float vy = sy * sy + epsCov;
            float vz = sz * sz + epsCov;

            c.v[i3 + 0] = vx;
            c.v[i3 + 1] = vy;
            c.v[i3 + 2] = vz;

            double cvx = Math.max(vx, 1e-30);
            double cvy = Math.max(vy, 1e-30);
            double cvz = Math.max(vz, 1e-30);

            c.invdiag[i3 + 0] = (float) (1.0 / cvx);
            c.invdiag[i3 + 1] = (float) (1.0 / cvy);
            c.invdiag[i3 + 2] = (float) (1.0 / cvz);
            c.logdet[i] = (float) Math.log(cvx) + (float) Math.log(cvy)
                + (float) Math.log(cvz);

            float qw = s.getRotationW();
            float qx = s.getRotationX();
            float qy = s.getRotationY();
            float qz = s.getRotationZ();
            NanoGsMath.quatToRotmatInto(qw, qx, qy, qz, c.R, i9);
            NanoGsMath.transpose3Into(c.R, i9, c.Rt, i9);
            NanoGsMath.sigmaFromRotVarInto(c.R, i9, vx, vy, vz, c.sigma, i9);

            float op = s.getOpacity();
            float alpha = Splats.opacityToAlpha(op);
            c.alpha[i] = Splats.opacityToAlpha(s.getOpacity());

            c.mass[i] = (float) (TWO_PI_POW_1P5 * alpha * sx * sy * sz + 1e-12);
        });

        return c;
    }

}
