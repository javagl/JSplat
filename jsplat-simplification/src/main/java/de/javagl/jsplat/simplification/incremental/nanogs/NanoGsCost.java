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
import de.javagl.jsplat.simplification.incremental.Edge;
import de.javagl.jsplat.simplification.incremental.nanogs.NanoGsCaching.Cache;

/**
 * Implementation of the merge cost computation from NanoGS
 * 
 * Ported from https://github.com/saliteta/NanoGS 
 * Commit: 62ddc34e230a01c061b762103ef69113f6259e48
 */
class NanoGsCost
{
    /**
     * Constant for (2*PI)^1.5
     */
    private static final double TWO_PI_POW_1P5 =
        Math.pow(2.0 * Math.PI, 1.5);

    /**
     * Constant for log(2*PI)
     */
    private static double LOG2PI = Math.log(2.0 * Math.PI);

    /**
     * A thread-local 3x3 matrix for "Sigm"
     */
    private static final ThreadLocal<double[]> THREAD_LOCAL_Sigm =
        ThreadLocal.withInitial(() -> new double[9]);        
    
    /**
     * Compute the weights for the given edges
     * 
     * @param cur The current splats
     * @param edges The edges
     * @param Z The gaussian samples
     * @param epsCov The epsilon to add to the covariance computation
     * @param lamGeo The weight for the geometry part
     * @param lamSh The weight for the SH part
     */
    static void computeWeights(List<? extends Splat> cur, List<Edge> edges,
        double Z[][], double epsCov, double lamGeo, double lamSh)
    {
        Cache cache = NanoGsCaching.buildPerSplatCache(cur, epsCov);
        int n = edges.size();
        IntStream.range(0, n).parallel().forEach(i ->
        {
            Edge edge = edges.get(i);
            Splat s0 = cur.get(edge.i0);
            Splat s1 = cur.get(edge.i1);
            double distance = NanoGsCost.fullCostPairCached(edge.i0, s0, edge.i1,
                s1, cache, Z, lamGeo, lamSh);
            edge.weight = distance;
        });
    }

    /**
     * Computes the cost for the specified edge.
     * 
     * Location: scripts/simplify.js#L445
     * 
     * @param i The index of the first splat
     * @param splatI The first splat
     * @param j The index of the second splat
     * @param splatJ The second splat
     * @param cache The {@link Cache}
     * @param Z The gaussian samples
     * @param lamGeo The weight for the geometry part
     * @param lamSh The weight for the SH part
     * @return The cost
     */
    private static double fullCostPairCached(int i, Splat splatI, int j,
        Splat splatJ, Cache cache, double[][] Z, double lamGeo, double lamSh)
    {
        int i3 = i * 3;
        int j3 = j * 3;
        int i9 = i * 9;
        int j9 = j * 9;

        double cpEpsCov = 1e-8;

        double mux = splatI.getPositionX();
        double muy = splatI.getPositionY();
        double muz = splatI.getPositionZ();

        double mvx = splatJ.getPositionX();
        double mvy = splatJ.getPositionY();
        double mvz = splatJ.getPositionZ();

        double scaleIx = cache.linearScale[i3 + 0];
        double scaleIy = cache.linearScale[i3 + 1];
        double scaleIz = cache.linearScale[i3 + 2];

        double scaleJx = cache.linearScale[j3 + 0];
        double scaleJy = cache.linearScale[j3 + 1];
        double scaleJz = cache.linearScale[j3 + 2];

        double alphaI = cache.alpha[i];
        double alphaJ = cache.alpha[j];
        double scaleI = scaleIx * scaleIy * scaleIz;
        double scaleJ = scaleJx * scaleJy * scaleJz;
        double wi = TWO_PI_POW_1P5 * alphaI * scaleI + 1e-12;
        double wj = TWO_PI_POW_1P5 * alphaJ * scaleJ + 1e-12;
        double W = wi + wj;
        double Wsafe = W > 0 ? W : 1.0;

        double pi = wi / Wsafe;
        pi = Math.min(1 - 1e-12, Math.max(1e-12, pi));
        double pj = 1.0 - pi;
        double logPi = Math.log(pi);
        double logPj = Math.log(pj);

        double mmx = pi * mux + pj * mvx;
        double mmy = pi * muy + pj * mvy;
        double mmz = pi * muz + pj * mvz;

        double dix = mux - mmx;
        double diy = muy - mmy;
        double diz = muz - mmz;
        double djx = mvx - mmx;
        double djy = mvy - mmy;
        double djz = mvz - mmz;

        double Sigm[] = THREAD_LOCAL_Sigm.get();
        for (int a = 0; a < 9; a++)
        {
            Sigm[a] = pi * cache.sigma[i9 + a] + pj * cache.sigma[j9 + a];
        }

        Sigm[0] += pi * dix * dix + pj * djx * djx;
        Sigm[1] += pi * dix * diy + pj * djx * djy;
        Sigm[2] += pi * dix * diz + pj * djx * djz;
        Sigm[3] += pi * diy * dix + pj * djy * djx;
        Sigm[4] += pi * diy * diy + pj * djy * djy;
        Sigm[5] += pi * diy * diz + pj * djy * djz;
        Sigm[6] += pi * diz * dix + pj * djz * djx;
        Sigm[7] += pi * diz * diy + pj * djz * djy;
        Sigm[8] += pi * diz * diz + pj * djz * djz;

        double s01 = 0.5 * (Sigm[1] + Sigm[3]);
        double s02 = 0.5 * (Sigm[2] + Sigm[6]);
        double s12 = 0.5 * (Sigm[5] + Sigm[7]);
        Sigm[1] = Sigm[3] = s01;
        Sigm[2] = Sigm[6] = s02;
        Sigm[5] = Sigm[7] = s12;
        Sigm[0] += cpEpsCov;
        Sigm[4] += cpEpsCov;
        Sigm[8] += cpEpsCov;

        double detm = Math.max(NanoGsMath.det3Flat(Sigm), 1e-30);
        double logdetm = Math.log(detm);

        double EpNegLogQ = 0.5 * (3.0 * LOG2PI + logdetm + 3.0);

        double stdix = Math.sqrt(Math.max(cache.v[i3 + 0], 0));
        double stdiy = Math.sqrt(Math.max(cache.v[i3 + 1], 0));
        double stdiz = Math.sqrt(Math.max(cache.v[i3 + 2], 0));

        double stdjx = Math.sqrt(Math.max(cache.v[j3 + 0], 0));
        double stdjy = Math.sqrt(Math.max(cache.v[j3 + 1], 0));
        double stdjz = Math.sqrt(Math.max(cache.v[j3 + 2], 0));

        double sumLogpOnI = 0.0;
        double sumLogpOnJ = 0.0;

        for (int s = 0; s < Z.length; s++)
        {
            double z0 = Z[s][0];
            double z1 = Z[s][1];
            double z2 = Z[s][2];

            double xix = mux + z0 * stdix * cache.Rt[i9]
                + z1 * stdiy * cache.Rt[i9 + 3] + z2 * stdiz * cache.Rt[i9 + 6];
            double xiy = muy + z0 * stdix * cache.Rt[i9 + 1]
                + z1 * stdiy * cache.Rt[i9 + 4] + z2 * stdiz * cache.Rt[i9 + 7];
            double xiz = muz + z0 * stdix * cache.Rt[i9 + 2]
                + z1 * stdiy * cache.Rt[i9 + 5] + z2 * stdiz * cache.Rt[i9 + 8];

            double xjx = mvx + z0 * stdjx * cache.Rt[j9]
                + z1 * stdjy * cache.Rt[j9 + 3] + z2 * stdjz * cache.Rt[j9 + 6];
            double xjy = mvy + z0 * stdjx * cache.Rt[j9 + 1]
                + z1 * stdjy * cache.Rt[j9 + 4] + z2 * stdjz * cache.Rt[j9 + 7];
            double xjz = mvz + z0 * stdjx * cache.Rt[j9 + 2]
                + z1 * stdjy * cache.Rt[j9 + 5] + z2 * stdjz * cache.Rt[j9 + 8];

            double logNiOnI = gaussLogpdfDiagrotFlat(xix, xiy, xiz, mux, muy,
                muz, cache.R, i9, cache.invdiag[i3], cache.invdiag[i3 + 1],
                cache.invdiag[i3 + 2], cache.logdet[i]);
            double logNjOnI = gaussLogpdfDiagrotFlat(xix, xiy, xiz, mvx, mvy,
                mvz, cache.R, j9, cache.invdiag[j3], cache.invdiag[j3 + 1],
                cache.invdiag[j3 + 2], cache.logdet[j]);
            sumLogpOnI += logAddExp(logPi + logNiOnI, logPj + logNjOnI);

            double logNiOnJ = gaussLogpdfDiagrotFlat(xjx, xjy, xjz, mux, muy,
                muz, cache.R, i9, cache.invdiag[i3], cache.invdiag[i3 + 1],
                cache.invdiag[i3 + 2], cache.logdet[i]);
            double logNjOnJ = gaussLogpdfDiagrotFlat(xjx, xjy, xjz, mvx, mvy,
                mvz, cache.R, j9, cache.invdiag[j3], cache.invdiag[j3 + 1],
                cache.invdiag[j3 + 2], cache.logdet[j]);
            sumLogpOnJ += logAddExp(logPi + logNiOnJ, logPj + logNjOnJ);
        }

        double Ei = sumLogpOnI / Z.length;
        double Ej = sumLogpOnJ / Z.length;
        double EpLogp = pi * Ei + pj * Ej;
        double geo = EpLogp + EpNegLogQ;

        double cSh = 0.0;
        int shDim = splatI.getShDimensions();
        for (int k = 0; k < shDim; k++)
        {
            double dX = splatI.getShX(k) - splatJ.getShX(k);
            double dY = splatI.getShY(k) - splatJ.getShY(k);
            double dZ = splatI.getShZ(k) - splatJ.getShZ(k);
            cSh += dX * dX;
            cSh += dY * dY;
            cSh += dZ * dZ;
        }
        return lamGeo * geo + lamSh * cSh;
    }

    /**
     * Computes the logarithm of the sum of the exponents of the given values.
     * 
     * Ported from https://github.com/RongLiu-Leo/NanoGS Commit:
     * 9e49497b3f16674aed6ab9204584e14794f82f84 Location:
     * scripts/simplify.js#L1241
     * 
     * @param a The first value
     * @param b The second value
     * @return The result
     */
    private static double logAddExp(double a, double b)
    {
        double m = Math.max(a, b);
        return m + Math.log(Math.exp(a - m) + Math.exp(b - m));
    }

    /**
     * Computes the logarithmic probability density function of a 3D point under
     * a Gaussian distribution with a diagonal covariance matrix that has been
     * rotated.
     * 
     * Ported from https://github.com/RongLiu-Leo/NanoGS/blob Commit: Location:
     * scripts/simplify.js#L1069
     * 
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @param z The z-coordinate
     * @param mx The mean x
     * @param my The mean y
     * @param mz The mean z
     * @param R The array that contains the rotation matrix
     * @param rOffset The offset in the array
     * @param invx The inverse of the x-diagonal element of the matrix
     * @param invy The inverse of the y-diagonal element of the matrix
     * @param invz The inverse of the z-diagonal element of the matrix
     * @param logdet The logarithm of the determinant
     * @return The result
     */
    private static double gaussLogpdfDiagrotFlat(double x, double y, double z,
        double mx, double my, double mz, double R[], int rOffset, double invx,
        double invy, double invz, double logdet)
    {
        double dx = x - mx;
        double dy = y - my;
        double dz = z - mz;

        double y0 =
            dx * R[rOffset + 0] + dy * R[rOffset + 3] + dz * R[rOffset + 6];
        double y1 =
            dx * R[rOffset + 1] + dy * R[rOffset + 4] + dz * R[rOffset + 7];
        double y2 =
            dx * R[rOffset + 2] + dy * R[rOffset + 5] + dz * R[rOffset + 8];

        double quad = y0 * y0 * invx + y1 * y1 * invy + y2 * y2 * invz;
        return -0.5 * (3.0 * LOG2PI + logdet + quad);
    }

}
