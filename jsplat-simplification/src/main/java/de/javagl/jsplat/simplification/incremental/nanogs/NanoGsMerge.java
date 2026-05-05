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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.simplification.incremental.Edge;
import de.javagl.jsplat.simplification.incremental.nanogs.NanoGsMath.Eigendecomposition3x3;

/**
 * The merge functionality from NanoGS
 * 
 * Ported from https://github.com/saliteta/NanoGS
 * Commit: 62ddc34e230a01c061b762103ef69113f6259e48
 */
class NanoGsMerge
{
    /**
     * Constant for (2*PI)^1.5
     */
    private static final double TWO_PI_POW_1P5 =
        Math.pow(2.0 * Math.PI, 1.5);

    /**
     * A thread-local 3x3 matrix for "SigI"
     */
    private static final ThreadLocal<double[]> THREAD_LOCAL_SigI =
        ThreadLocal.withInitial(() -> new double[9]);        
    
    /**
     * A thread-local 3x3 matrix for "SigJ"
     */
    private static final ThreadLocal<double[]> THREAD_LOCAL_SigJ =
        ThreadLocal.withInitial(() -> new double[9]);        
    
    /**
     * A thread-local 3x3 matrix for "Sig"
     */
    private static final ThreadLocal<double[]> THREAD_LOCAL_Sig =
        ThreadLocal.withInitial(() -> new double[9]);        
    
    /**
     * A thread-local 3x3 matrix for "R"
     */
    private static final ThreadLocal<double[]> THREAD_LOCAL_R =
        ThreadLocal.withInitial(() -> new double[9]);        
    
    /**
     * A thread-local quaternion "q"
     */
    private static final ThreadLocal<double[]> THREAD_LOCAL_q =
        ThreadLocal.withInitial(() -> new double[4]);        
    
    /**
     * Thread-local indexed values
     */
    private static final ThreadLocal<IndexedValue[]> THREAD_LOCAL_indexedValues =
        ThreadLocal.withInitial(() -> new IndexedValue[] {
            new IndexedValue(),
            new IndexedValue(),
            new IndexedValue(),
        });        

    /**
     * Thread-local eigendecomposition
     */
    private static final ThreadLocal<Eigendecomposition3x3> THREAD_LOCAL_eigendecomposition =
        ThreadLocal.withInitial(() -> new Eigendecomposition3x3());        
    
    /**
     * Helper class for sorting eigenvalues
     */
    private static class IndexedValue
    {
        /**
         * The value
         */
        private double value;

        /**
         * The index
         */
        private int index;
    }

    /**
     * Merge the specified pairs of splats, and return a new list containing the
     * merged ones and the ones that had not been merged from the given list
     * 
     * @param cur The input splats
     * @param pairs THe pairs to merge
     * @return The result
     */
    static List<Splat> mergePairs(List<? extends Splat> cur, List<Edge> pairs)
    {
        int N = cur.size();
        boolean used[] = new boolean[N];
        int n = pairs.size();
        List<Splat> list = IntStream.range(0, n).parallel().mapToObj(i ->
        {
            Edge pair = pairs.get(i);
            Splat s0 = cur.get(pair.i0);
            Splat s1 = cur.get(pair.i1);
            used[pair.i0] = true;
            used[pair.i1] = true;
            MutableSplat m = Splats.copy(s0);
            NanoGsMerge.merge(s0, s1, m);
            return m;
        }).collect(Collectors.toList());

        List<Splat> merged = new ArrayList<Splat>(list);
        for (int i = 0; i < N; i++)
        {
            if (!used[i])
            {
                Splat s = cur.get(i);
                merged.add(s);
            }
        }
        return merged;
    }

    /**
     * Merges two Gaussian splats into a third.
     * 
     * Ported from https://github.com/RongLiu-Leo/NanoGS/ Commit:
     * 9e49497b3f16674aed6ab9204584e14794f82f84 Location:
     * scripts/simplify.js#L646
     *
     * @param splatI The first splat.
     * @param splatJ The second splat.
     * @param result The splat that will store the result
     */
    private static void merge(Splat splatI, Splat splatJ, MutableSplat result)
    {
        double scaleIx = Math.exp(splatI.getScaleX());
        double scaleIy = Math.exp(splatI.getScaleY());
        double scaleIz = Math.exp(splatI.getScaleZ());

        double scaleJx = Math.exp(splatJ.getScaleX());
        double scaleJy = Math.exp(splatJ.getScaleY());
        double scaleJz = Math.exp(splatJ.getScaleZ());

        double rotIw = splatI.getRotationW();
        double rotIx = splatI.getRotationX();
        double rotIy = splatI.getRotationY();
        double rotIz = splatI.getRotationZ();

        double rotJw = splatJ.getRotationW();
        double rotJx = splatJ.getRotationX();
        double rotJy = splatJ.getRotationY();
        double rotJz = splatJ.getRotationZ();

        double alphaI = Splats.opacityToAlpha(splatI.getOpacity());
        double alphaJ = Splats.opacityToAlpha(splatJ.getOpacity());
        double scaleI = scaleIx * scaleIy * scaleIz;
        double scaleJ = scaleJx * scaleJy * scaleJz;
        double wi = TWO_PI_POW_1P5 * alphaI * scaleI + 1e-12;
        double wj = TWO_PI_POW_1P5 * alphaJ * scaleJ + 1e-12;
        double W = wi + wj;

        // Compute the weighted average of the positions
        double muxi = splatI.getPositionX();
        double muxj = splatJ.getPositionX();
        double muyi = splatI.getPositionY();
        double muyj = splatJ.getPositionY();
        double muzi = splatI.getPositionZ();
        double muzj = splatJ.getPositionZ();
        double mux = (wi * muxi + wj * muxj) / W;
        double muy = (wi * muyi + wj * muyj) / W;
        double muz = (wi * muzi + wj * muzj) / W;

        // Compute the resulting opacity
        double alphaBase = alphaI + alphaJ - alphaI * alphaJ;
        double n_alpha = Math.min(1.0f, Math.max(0.0f, alphaBase));
        double n_opacity = Splats.alphaToOpacity(n_alpha);

        // Compute the resulting covariance matrix
        double SigI[] = THREAD_LOCAL_SigI.get();
        double SigJ[] = THREAD_LOCAL_SigJ.get();
        NanoGsMath.sigmaFromQuatScaleFlatInto(rotIw, rotIx, rotIy, rotIz,
            scaleIx, scaleIy, scaleIz, SigI);
        NanoGsMath.sigmaFromQuatScaleFlatInto(rotJw, rotJx, rotJy, rotJz,
            scaleJx, scaleIy, scaleJz, SigJ);

        double dix = muxi - mux;
        double diy = muyi - muy;
        double diz = muzi - muz;

        double djx = muxj - mux;
        double djy = muyj - muy;
        double djz = muzj - muz;

        double Sig[] = THREAD_LOCAL_Sig.get();
        for (int a = 0; a < 9; a++)
        {
            Sig[a] = (wi * SigI[a] + wj * SigJ[a]) / W;
        }

        Sig[0] += (wi * dix * dix + wj * djx * djx) / W;
        Sig[1] += (wi * dix * diy + wj * djx * djy) / W;
        Sig[2] += (wi * dix * diz + wj * djx * djz) / W;
        Sig[3] += (wi * diy * dix + wj * djy * djx) / W;
        Sig[4] += (wi * diy * diy + wj * djy * djy) / W;
        Sig[5] += (wi * diy * diz + wj * djy * djz) / W;
        Sig[6] += (wi * diz * dix + wj * djz * djx) / W;
        Sig[7] += (wi * diz * diy + wj * djz * djy) / W;
        Sig[8] += (wi * diz * diz + wj * djz * djz) / W;

        double s01 = 0.5 * (Sig[1] + Sig[3]);
        double s02 = 0.5 * (Sig[2] + Sig[6]);
        double s12 = 0.5 * (Sig[5] + Sig[7]);
        Sig[1] = Sig[3] = s01;
        Sig[2] = Sig[6] = s02;
        Sig[5] = Sig[7] = s12;
        Sig[0] += 1e-8;
        Sig[4] += 1e-8;
        Sig[8] += 1e-8;

        // Extract the scale (eigenvalues) and rotation from the eigenvectors
        Eigendecomposition3x3 ev = THREAD_LOCAL_eigendecomposition.get();
        NanoGsMath.eigenSymmetric3x3Flat(Sig, ev);
        double[] vals = ev.eigenvalues;
        double[] vecs = ev.eigenvectors;

        IndexedValue[] indexedEigenvalues = THREAD_LOCAL_indexedValues.get();
        indexedEigenvalues[0].index = 0;
        indexedEigenvalues[0].value = vals[0];
        indexedEigenvalues[1].index = 1;
        indexedEigenvalues[1].value = vals[1];
        indexedEigenvalues[2].index = 2;
        indexedEigenvalues[2].value = vals[2];
        Arrays.sort(indexedEigenvalues,
            Comparator.comparingDouble(v -> v.value));
        vals[0] = indexedEigenvalues[0].value;
        vals[1] = indexedEigenvalues[1].value;
        vals[2] = indexedEigenvalues[2].value;

        double R[] = THREAD_LOCAL_R.get();
        for (int c = 0; c < 3; c++)
        {
            int src = indexedEigenvalues[c].index;
            R[0 + c] = vecs[0 + src];
            R[3 + c] = vecs[3 + src];
            R[6 + c] = vecs[6 + src];
        }

        if (NanoGsMath.det3Flat(R) < 0)
        {
            R[2] = -R[2];
            R[5] = -R[5];
            R[8] = -R[8];
        }

        double q[] = THREAD_LOCAL_q.get();
        NanoGsMath.rotmatToQuatFlat(R, q);

        // Assign the computed properties to the result splat
        result.setPositionX(mux);
        result.setPositionY(muy);
        result.setPositionZ(muz);
        result.setScaleX(Math.log(Math.sqrt(vals[0])));
        result.setScaleY(Math.log(Math.sqrt(vals[1])));
        result.setScaleZ(Math.log(Math.sqrt(vals[2])));
        result.setRotationW(q[0]);
        result.setRotationX(q[1]);
        result.setRotationY(q[2]);
        result.setRotationZ(q[3]);
        result.setOpacity(n_opacity);
        
        // Compute the weighted average of the spherical harmonics coefficients
        int dim = splatI.getShDimensions();
        for (int i = 0; i < dim; i++)
        {
            double shXi = splatI.getShX(i);
            double shXj = splatJ.getShX(i);
            double shYi = splatI.getShY(i);
            double shYj = splatJ.getShY(i);
            double shZi = splatI.getShZ(i);
            double shZj = splatJ.getShZ(i);
            double shX = (wi * shXi + wj * shXj) / W;
            double shY = (wi * shYi + wj * shYj) / W;
            double shZ = (wi * shZi + wj * shZj) / W;
            result.setShX(i, shX);
            result.setShY(i, shY);
            result.setShZ(i, shZ);
        }
    }

}
