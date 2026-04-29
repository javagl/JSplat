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

import java.util.Arrays;

/**
 * Low-level math utility functions for NanoGS.
 * 
 * Ported from https://github.com/saliteta/NanoGS
 * Commit: 62ddc34e230a01c061b762103ef69113f6259e48
 */
class NanoGsMath
{
    /**
     * A thread-local 3x3 matrix for "R"
     */
    private static final ThreadLocal<float[]> THREAD_LOCAL_R =
        ThreadLocal.withInitial(() -> new float[9]);        

    /**
     * A thread-local 3x3 matrix for "matrix"
     */
    private static final ThreadLocal<float[]> THREAD_LOCAL_matrix =
        ThreadLocal.withInitial(() -> new float[9]);        

    /**
     * A container for the results of an eigendecomposition
     */
    static class Eigendecomposition3x3
    {
        /**
         * The eigenvalues
         */
        final float[] eigenvalues = new float[3];

        /**
         * The eigenvectors, as a flat 9-element array
         */
        final float[] eigenvectors = new float[9];
    }

    /**
     * Computes the determinant of the 3x3 matrix 
     * 
     * @param A The input array
     * @return The determinant
     */
    static float det3Flat(float A[])
    {
        float a00 = A[0];
        float a01 = A[1];
        float a02 = A[2];
        float a10 = A[3];
        float a11 = A[4];
        float a12 = A[5];
        float a20 = A[6];
        float a21 = A[7];
        float a22 = A[8];
        return (a00 * (a11 * a22 - a12 * a21) - a01 * (a10 * a22 - a12 * a20)
            + a02 * (a10 * a21 - a11 * a20));
    }

    /**
     * Computes a scalar-FIRST quaternion from the 3x3 matrix and stores it
     * in the given array.
     * 
     * @param R The array
     * @param q The output
     */
    static void rotmatToQuatFlat(float R[], float q[])
    {
        float m00 = R[0];
        float m11 = R[4];
        float m22 = R[8];
        float tr = m00 + m11 + m22;
        float qw, qx, qy, qz;

        if (tr > 0)
        {
            float S = (float) Math.sqrt(tr + 1.0) * 2.0f;
            qw = 0.25f * S;
            qx = (R[7] - R[5]) / S;
            qy = (R[2] - R[6]) / S;
            qz = (R[3] - R[1]) / S;
        }
        else if (m00 > m11 && m00 > m22)
        {
            float S = (float) Math.sqrt(1.0 + m00 - m11 - m22) * 2.0f;
            qw = (R[7] - R[5]) / S;
            qx = 0.25f * S;
            qy = (R[1] + R[3]) / S;
            qz = (R[2] + R[6]) / S;
        }
        else if (m11 > m22)
        {
            float S = (float) Math.sqrt(1.0 + m11 - m00 - m22) * 2.0f;
            qw = (R[2] - R[6]) / S;
            qx = (R[1] + R[3]) / S;
            qy = 0.25f * S;
            qz = (R[5] + R[7]) / S;
        }
        else
        {
            float S = (float) Math.sqrt(1.0 + m22 - m00 - m11) * 2.0f;
            qw = (R[3] - R[1]) / S;
            qx = (R[2] + R[6]) / S;
            qy = (R[5] + R[7]) / S;
            qz = 0.25f * S;
        }
        q[0] = qw;
        q[1] = qx;
        q[2] = qy;
        q[3] = qz;
        normalize(q);
    }

    /**
     * Normalize the given quaternion in place
     * 
     * @param q The quaternion
     */
    private static void normalize(float[] q)
    {
        float lenSq = q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3];
        if (lenSq > 0)
        {
            float len = (float) Math.sqrt(lenSq);
            for (int i = 0; i < 4; i++)
            {
                q[i] /= len;
            }
        }
    }

    /**
     * Read the 3x3 matrix from the given source array at the given offset, and
     * write it in transposed form into the given target array at the given
     * offset
     * 
     * @param src The source array
     * @param sOffset The sourceOffset
     * @param dst The target array
     * @param dOffset The target offset
     */
    static void transpose3Into(float src[], int sOffset, float dst[],
        int dOffset)
    {
        dst[dOffset + 0] = src[sOffset + 0];
        dst[dOffset + 1] = src[sOffset + 3];
        dst[dOffset + 2] = src[sOffset + 6];
        dst[dOffset + 3] = src[sOffset + 1];
        dst[dOffset + 4] = src[sOffset + 4];
        dst[dOffset + 5] = src[sOffset + 7];
        dst[dOffset + 6] = src[sOffset + 2];
        dst[dOffset + 7] = src[sOffset + 5];
        dst[dOffset + 8] = src[sOffset + 8];
    }

    /**
     * Computes the 3x3 covariance matrix (sigma) from the given quaternion and
     * the given linear scale values
     * 
     * @param qw The w-component of the quaternion
     * @param qx The x-component of the quaternion
     * @param qy The y-component of the quaternion
     * @param qz The z-component of the quaternion
     * @param sx The scale value along x
     * @param sy The scale value along y
     * @param sz The scale value along z
     * @param out The output array
     */
    static void sigmaFromQuatScaleFlatInto(float qw, float qx, float qy,
        float qz, float sx, float sy, float sz, float[] out)
    {
        float[] R = THREAD_LOCAL_R.get();
        quatToRotmatInto(qw, qx, qy, qz, R, 0);
        sigmaFromRotVarInto(R, 0, sx * sx, sy * sy, sz * sz, out, 0);
    }

    /**
     * Write a 3x3 rotation matrix that was computed from the specified
     * quaternion into the given output array at the given offset
     * 
     * @param w The w-component
     * @param x The x-component
     * @param y The y-component
     * @param z The z-component
     * @param out The output array
     * @param offset The offset
     */
    static void quatToRotmatInto(float w, float x, float y, float z,
        float out[], int offset)
    {
        float xx = x * x;
        float yy = y * y;
        float zz = z * z;
        float wx = w * x;
        float wy = w * y;
        float wz = w * z;
        float xy = x * y;
        float xz = x * z;
        float yz = y * z;

        out[offset + 0] = 1 - 2 * (yy + zz);
        out[offset + 1] = 2 * (xy - wz);
        out[offset + 2] = 2 * (xz + wy);

        out[offset + 3] = 2 * (xy + wz);
        out[offset + 4] = 1 - 2 * (xx + zz);
        out[offset + 5] = 2 * (yz - wx);

        out[offset + 6] = 2 * (xz - wy);
        out[offset + 7] = 2 * (yz + wx);
        out[offset + 8] = 1 - 2 * (xx + yy);
    }

    /**
     * Computes the 3x3 covariance matrix (sigma) from the given rotation matrix
     * and the given variances.
     * 
     * @param R The array with the 3x3 rotation matrix
     * @param rOffset The offset in the array where the rotation matrix starts
     * @param vx The variance along x
     * @param vy The variance along y
     * @param vz THe variance along z
     * @param out The output array
     * @param oOffset The offset where the output should be written
     */
    static void sigmaFromRotVarInto(float R[], int rOffset, float vx, float vy,
        float vz, float out[], int oOffset)
    {
        float r00 = R[rOffset + 0];
        float r01 = R[rOffset + 1];
        float r02 = R[rOffset + 2];
        float r10 = R[rOffset + 3];
        float r11 = R[rOffset + 4];
        float r12 = R[rOffset + 5];
        float r20 = R[rOffset + 6];
        float r21 = R[rOffset + 7];
        float r22 = R[rOffset + 8];

        out[oOffset + 0] = r00 * r00 * vx + r01 * r01 * vy + r02 * r02 * vz;
        out[oOffset + 1] = r00 * r10 * vx + r01 * r11 * vy + r02 * r12 * vz;
        out[oOffset + 2] = r00 * r20 * vx + r01 * r21 * vy + r02 * r22 * vz;

        out[oOffset + 3] = out[oOffset + 1];
        out[oOffset + 4] = r10 * r10 * vx + r11 * r11 * vy + r12 * r12 * vz;
        out[oOffset + 5] = r10 * r20 * vx + r11 * r21 * vy + r12 * r22 * vz;

        out[oOffset + 6] = out[oOffset + 2];
        out[oOffset + 7] = out[oOffset + 5];
        out[oOffset + 8] = r20 * r20 * vx + r21 * r21 * vy + r22 * r22 * vz;
    }

    /**
     * Compute an eigendecomposition of the given 3x3 matrix
     * 
     * @param matrix The matrix
     * @param result The result
     */
    static void eigenSymmetric3x3Flat(float[] matrix, Eigendecomposition3x3 result)
    {
        float[] mat = THREAD_LOCAL_matrix.get();
        System.arraycopy(matrix, 0, mat, 0, 9);
        float[] V = result.eigenvectors;
        V[0] = 1.0f;
        V[1] = 0.0f;
        V[2] = 0.0f;
        V[3] = 0.0f;
        V[4] = 1.0f;
        V[5] = 0.0f;
        V[6] = 0.0f;
        V[7] = 0.0f;
        V[8] = 1.0f;
        for (int i = 0; i < 24; i++)
        {
            int p = 0;
            int q = 1;
            float maxAbs = Math.abs(mat[1]);

            if (Math.abs(mat[2]) > maxAbs)
            {
                p = 0;
                q = 2;
                maxAbs = Math.abs(mat[2]);
            }
            if (Math.abs(mat[5]) > maxAbs)
            {
                p = 1;
                q = 2;
                maxAbs = Math.abs(mat[5]);
            }

            if (maxAbs < 1e-12)
            {
                break;
            }

            int pp = 3 * p + p;
            int qq = 3 * q + q;
            int pq = 3 * p + q;
            int qp = 3 * q + p;

            float app = mat[pp];
            float aqq = mat[qq];
            float apq = mat[pq];

            float tau = (aqq - app) / (2 * apq);
            float t = (float) (Math.signum(tau)
                / (Math.abs(tau) + Math.sqrt(1 + tau * tau)));
            float c = 1 / (float) Math.sqrt(1 + t * t);
            float s = t * c;

            for (int k = 0; k < 3; k++)
            {
                if (k == p || k == q)
                {
                    continue;
                }
                int kp = 3 * k + p;
                int kq = 3 * k + q;
                int pk = 3 * p + k;
                int qk = 3 * q + k;
                float akp = mat[kp];
                float akq = mat[kq];
                mat[kp] = c * akp - s * akq;
                mat[pk] = mat[kp];
                mat[kq] = s * akp + c * akq;
                mat[qk] = mat[kq];
            }

            mat[pp] = c * c * app - 2 * s * c * apq + s * s * aqq;
            mat[qq] = s * s * app + 2 * s * c * apq + c * c * aqq;
            mat[pq] = 0;
            mat[qp] = 0;

            for (int k = 0; k < 3; k++)
            {
                int kp = 3 * k + p;
                int kq = 3 * k + q;
                float vkp = V[kp];
                float vkq = V[kq];
                V[kp] = c * vkp - s * vkq;
                V[kq] = s * vkp + c * vkq;
            }
        }
        result.eigenvalues[0] = mat[0];
        result.eigenvalues[1] = mat[4];
        result.eigenvalues[2] = mat[8];
    }

    /**
     * Compute the specified percentile of the given array, IN-PLACE (meaning
     * that it will sort the given array!)
     * 
     * @param xs The array
     * @param p The percentile
     * @return The result
     */
    static float percentileInPlace(float xs[], float p)
    {
        Arrays.parallelSort(xs);
        if (xs.length == 0)
        {
            return 0;
        }
        float t = (xs.length - 1) * p;
        int i = (int) Math.floor(t);
        int j = Math.min(i + 1, xs.length - 1);
        float w = t - i;
        return xs[i] * (1 - w) + xs[j] * w;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private NanoGsMath()
    {
        // Private constructor to prevent instantiation
    }
}
