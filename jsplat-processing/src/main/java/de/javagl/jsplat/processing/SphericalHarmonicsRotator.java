/*
 * This file is based on https://github.com/andrewwillmott/sh-lib
 * It is a port of the "RotateSH" function from /SHLib.cpp, line 1090
 * Commit 8821cba4acc2273ab20417388df16bd0012f0760
 * 
 * Original sh-lib license:
 * 
 * ============================================================================
 * 
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute 
 * this software, either in source code form or as a compiled binary, for any 
 * purpose, commercial or non-commercial, and by any means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors of 
 * this software dedicate any and all copyright interest in the software to the 
 * public domain. We make this dedication for the benefit of the public at 
 * large and to the detriment of our heirs and successors. We intend this 
 * dedication to be an overt act of relinquishment in perpetuity of all present
 * and future rights to this software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * For more information, please refer to http://unlicense.org
 */
package de.javagl.jsplat.processing;

import java.nio.FloatBuffer;

/**
 * Internal class for rotating the spherical harmonics coefficients of splats.
 * 
 * Based on https://github.com/andrewwillmott/sh-lib
 * 
 * In contrast to the original implementation from "sh-lib", this is storing the
 * rotation matrices as fields, and offers a dedicated function to actually
 * {@link #rotate(FloatBuffer, FloatBuffer)} the coefficients for a single
 * splat.
 * 
 * Internal note: See inlined comments of {@link SplatShRotator#rotateSh} 
 * for some details about the buffer layout.
 */
@SuppressWarnings("javadoc")
class SphericalHarmonicsRotator
{
    private static final float kSqrt03_02 = (float) Math.sqrt(3.0 / 2.0);
    private static final float kSqrt01_03 = (float) Math.sqrt(1.0 / 3.0);
    private static final float kSqrt02_03 = (float) Math.sqrt(2.0 / 3.0);
    private static final float kSqrt04_03 = (float) Math.sqrt(4.0 / 3.0);
    private static final float kSqrt01_04 = (float) Math.sqrt(1.0 / 4.0);
    private static final float kSqrt03_04 = (float) Math.sqrt(3.0 / 4.0);
    private static final float kSqrt01_05 = (float) Math.sqrt(1.0 / 5.0);
    private static final float kSqrt03_05 = (float) Math.sqrt(3.0 / 5.0);
    private static final float kSqrt06_05 = (float) Math.sqrt(6.0 / 5.0);
    private static final float kSqrt08_05 = (float) Math.sqrt(8.0 / 5.0);
    private static final float kSqrt09_05 = (float) Math.sqrt(9.0 / 5.0);
    private static final float kSqrt01_06 = (float) Math.sqrt(1.0 / 6.0);
    private static final float kSqrt05_06 = (float) Math.sqrt(5.0 / 6.0);
    private static final float kSqrt03_08 = (float) Math.sqrt(3.0 / 8.0);
    private static final float kSqrt05_08 = (float) Math.sqrt(5.0 / 8.0);
    private static final float kSqrt09_08 = (float) Math.sqrt(9.0 / 8.0);
    private static final float kSqrt05_09 = (float) Math.sqrt(5.0 / 9.0);
    private static final float kSqrt08_09 = (float) Math.sqrt(8.0 / 9.0);
    private static final float kSqrt01_10 = (float) Math.sqrt(1.0 / 10.0);
    private static final float kSqrt03_10 = (float) Math.sqrt(3.0 / 10.0);
    private static final float kSqrt01_12 = (float) Math.sqrt(1.0 / 12.0);
    private static final float kSqrt04_15 = (float) Math.sqrt(4.0 / 15.0);
    private static final float kSqrt01_16 = (float) Math.sqrt(1.0 / 16.0);
    private static final float kSqrt15_16 = (float) Math.sqrt(15.0 / 16.0);
    private static final float kSqrt01_18 = (float) Math.sqrt(1.0 / 18.0);
    private static final float kSqrt01_60 = (float) Math.sqrt(1.0 / 60.0);

    /**
     * The 3x3 matrix for rotating the dimensions for the first degree
     */
    private final float sh1[][];

    /**
     * The 5x5 matrix for rotating the dimensions of the second degree
     */
    private final float sh2[][];

    /**
     * The 7x7 matrix for rotating the dimensions of the third degree
     */
    private final float sh3[][];

    /**
     * Creates a new instance for the given matrix.
     * 
     * The given matrix is a 3x3 matrix, stored in a 9-element array, in
     * column-major order
     * 
     * @param rot The rotation matrix
     */
    SphericalHarmonicsRotator(float rot[])
    {
        this.sh1 = new float[][]
        {
            { rot[4], -rot[7], rot[1] },
            { -rot[5], rot[8], -rot[2] },
            { rot[3], -rot[6], rot[0] } };
        
        this.sh2 = new float[][]
        {
            { kSqrt01_04 * ((sh1[2][2] * sh1[0][0] + sh1[2][0] * sh1[0][2])
                + (sh1[0][2] * sh1[2][0] + sh1[0][0] * sh1[2][2])),
                (sh1[2][1] * sh1[0][0] + sh1[0][1] * sh1[2][0]),
                kSqrt03_04 * (sh1[2][1] * sh1[0][1] + sh1[0][1] * sh1[2][1]),
                (sh1[2][1] * sh1[0][2] + sh1[0][1] * sh1[2][2]),
                kSqrt01_04 * ((sh1[2][2] * sh1[0][2] - sh1[2][0] * sh1[0][0])
                    + (sh1[0][2] * sh1[2][2] - sh1[0][0] * sh1[2][0])) },
            { kSqrt01_04 * ((sh1[1][2] * sh1[0][0] + sh1[1][0] * sh1[0][2])
                + (sh1[0][2] * sh1[1][0] + sh1[0][0] * sh1[1][2])),
                sh1[1][1] * sh1[0][0] + sh1[0][1] * sh1[1][0],
                kSqrt03_04 * (sh1[1][1] * sh1[0][1] + sh1[0][1] * sh1[1][1]),
                sh1[1][1] * sh1[0][2] + sh1[0][1] * sh1[1][2],
                kSqrt01_04 * ((sh1[1][2] * sh1[0][2] - sh1[1][0] * sh1[0][0])
                    + (sh1[0][2] * sh1[1][2] - sh1[0][0] * sh1[1][0])) },
            { kSqrt01_03 * (sh1[1][2] * sh1[1][0] + sh1[1][0] * sh1[1][2])
                - kSqrt01_12 * ((sh1[2][2] * sh1[2][0] + sh1[2][0] * sh1[2][2])
                    + (sh1[0][2] * sh1[0][0] + sh1[0][0] * sh1[0][2])),
                kSqrt04_03 * sh1[1][1] * sh1[1][0] - kSqrt01_03
                    * (sh1[2][1] * sh1[2][0] + sh1[0][1] * sh1[0][0]),
                sh1[1][1] * sh1[1][1] - kSqrt01_04
                    * (sh1[2][1] * sh1[2][1] + sh1[0][1] * sh1[0][1]),
                kSqrt04_03 * sh1[1][1] * sh1[1][2] - kSqrt01_03
                    * (sh1[2][1] * sh1[2][2] + sh1[0][1] * sh1[0][2]),
                kSqrt01_03 * (sh1[1][2] * sh1[1][2] - sh1[1][0] * sh1[1][0])
                    - kSqrt01_12 * ((sh1[2][2] * sh1[2][2]
                        - sh1[2][0] * sh1[2][0])
                        + (sh1[0][2] * sh1[0][2] - sh1[0][0] * sh1[0][0])) },
            { kSqrt01_04 * ((sh1[1][2] * sh1[2][0] + sh1[1][0] * sh1[2][2])
                + (sh1[2][2] * sh1[1][0] + sh1[2][0] * sh1[1][2])),
                sh1[1][1] * sh1[2][0] + sh1[2][1] * sh1[1][0],
                kSqrt03_04 * (sh1[1][1] * sh1[2][1] + sh1[2][1] * sh1[1][1]),
                sh1[1][1] * sh1[2][2] + sh1[2][1] * sh1[1][2],
                kSqrt01_04 * ((sh1[1][2] * sh1[2][2] - sh1[1][0] * sh1[2][0])
                    + (sh1[2][2] * sh1[1][2] - sh1[2][0] * sh1[1][0])) },
            { kSqrt01_04 * ((sh1[2][2] * sh1[2][0] + sh1[2][0] * sh1[2][2])
                - (sh1[0][2] * sh1[0][0] + sh1[0][0] * sh1[0][2])),
                (sh1[2][1] * sh1[2][0] - sh1[0][1] * sh1[0][0]),
                kSqrt03_04 * (sh1[2][1] * sh1[2][1] - sh1[0][1] * sh1[0][1]),
                (sh1[2][1] * sh1[2][2] - sh1[0][1] * sh1[0][2]),
                kSqrt01_04 * ((sh1[2][2] * sh1[2][2] - sh1[2][0] * sh1[2][0])
                    - (sh1[0][2] * sh1[0][2] - sh1[0][0] * sh1[0][0])) } };

        this.sh3 = new float[][]
        {
            { kSqrt01_04 * ((sh1[2][2] * sh2[0][0] + sh1[2][0] * sh2[0][4])
                + (sh1[0][2] * sh2[4][0] + sh1[0][0] * sh2[4][4])),
                kSqrt03_02 * (sh1[2][1] * sh2[0][0] + sh1[0][1] * sh2[4][0]),
                kSqrt15_16 * (sh1[2][1] * sh2[0][1] + sh1[0][1] * sh2[4][1]),
                kSqrt05_06 * (sh1[2][1] * sh2[0][2] + sh1[0][1] * sh2[4][2]),
                kSqrt15_16 * (sh1[2][1] * sh2[0][3] + sh1[0][1] * sh2[4][3]),
                kSqrt03_02 * (sh1[2][1] * sh2[0][4] + sh1[0][1] * sh2[4][4]),
                kSqrt01_04 * ((sh1[2][2] * sh2[0][4] - sh1[2][0] * sh2[0][0])
                    + (sh1[0][2] * sh2[4][4] - sh1[0][0] * sh2[4][0])) },
            { kSqrt01_06 * (sh1[1][2] * sh2[0][0] + sh1[1][0] * sh2[0][4])
                + kSqrt01_06 * ((sh1[2][2] * sh2[1][0] + sh1[2][0] * sh2[1][4])
                    + (sh1[0][2] * sh2[3][0] + sh1[0][0] * sh2[3][4])),
                sh1[1][1] * sh2[0][0]
                    + (sh1[2][1] * sh2[1][0] + sh1[0][1] * sh2[3][0]),
                kSqrt05_08 * sh1[1][1] * sh2[0][1] + kSqrt05_08
                    * (sh1[2][1] * sh2[1][1] + sh1[0][1] * sh2[3][1]),
                kSqrt05_09 * sh1[1][1] * sh2[0][2] + kSqrt05_09
                    * (sh1[2][1] * sh2[1][2] + sh1[0][1] * sh2[3][2]),
                kSqrt05_08 * sh1[1][1] * sh2[0][3] + kSqrt05_08
                    * (sh1[2][1] * sh2[1][3] + sh1[0][1] * sh2[3][3]),
                sh1[1][1] * sh2[0][4]
                    + (sh1[2][1] * sh2[1][4] + sh1[0][1] * sh2[3][4]),
                kSqrt01_06 * (sh1[1][2] * sh2[0][4] - sh1[1][0] * sh2[0][0])
                    + kSqrt01_06 * ((sh1[2][2] * sh2[1][4]
                        - sh1[2][0] * sh2[1][0])
                        + (sh1[0][2] * sh2[3][4] - sh1[0][0] * sh2[3][0])) },
            { kSqrt04_15 * (sh1[1][2] * sh2[1][0] + sh1[1][0] * sh2[1][4])
                + kSqrt01_05 * (sh1[0][2] * sh2[2][0] + sh1[0][0] * sh2[2][4])
                - kSqrt01_60 * ((sh1[2][2] * sh2[0][0] + sh1[2][0] * sh2[0][4])
                    - (sh1[0][2] * sh2[4][0] + sh1[0][0] * sh2[4][4])),
                kSqrt08_05 * sh1[1][1] * sh2[1][0]
                    + kSqrt06_05 * sh1[0][1] * sh2[2][0]
                    - kSqrt01_10
                        * (sh1[2][1] * sh2[0][0] - sh1[0][1] * sh2[4][0]),
                sh1[1][1] * sh2[1][1] + kSqrt03_04 * sh1[0][1] * sh2[2][1]
                    - kSqrt01_16
                        * (sh1[2][1] * sh2[0][1] - sh1[0][1] * sh2[4][1]),
                kSqrt08_09 * sh1[1][1] * sh2[1][2]
                    + kSqrt02_03 * sh1[0][1] * sh2[2][2]
                    - kSqrt01_18
                        * (sh1[2][1] * sh2[0][2] - sh1[0][1] * sh2[4][2]),
                sh1[1][1] * sh2[1][3] + kSqrt03_04 * sh1[0][1] * sh2[2][3]
                    - kSqrt01_16
                        * (sh1[2][1] * sh2[0][3] - sh1[0][1] * sh2[4][3]),
                kSqrt08_05 * sh1[1][1] * sh2[1][4]
                    + kSqrt06_05 * sh1[0][1] * sh2[2][4]
                    - kSqrt01_10
                        * (sh1[2][1] * sh2[0][4] - sh1[0][1] * sh2[4][4]),
                kSqrt04_15 * (sh1[1][2] * sh2[1][4] - sh1[1][0] * sh2[1][0])
                    + kSqrt01_05
                        * (sh1[0][2] * sh2[2][4] - sh1[0][0] * sh2[2][0])
                    - kSqrt01_60 * ((sh1[2][2] * sh2[0][4]
                        - sh1[2][0] * sh2[0][0])
                        - (sh1[0][2] * sh2[4][4] - sh1[0][0] * sh2[4][0])) },
            { kSqrt03_10 * (sh1[1][2] * sh2[2][0] + sh1[1][0] * sh2[2][4])
                - kSqrt01_10 * ((sh1[2][2] * sh2[3][0] + sh1[2][0] * sh2[3][4])
                    + (sh1[0][2] * sh2[1][0] + sh1[0][0] * sh2[1][4])),
                kSqrt09_05 * sh1[1][1] * sh2[2][0] - kSqrt03_05
                    * (sh1[2][1] * sh2[3][0] + sh1[0][1] * sh2[1][0]),
                kSqrt09_08 * sh1[1][1] * sh2[2][1] - kSqrt03_08
                    * (sh1[2][1] * sh2[3][1] + sh1[0][1] * sh2[1][1]),
                sh1[1][1] * sh2[2][2] - kSqrt01_03
                    * (sh1[2][1] * sh2[3][2] + sh1[0][1] * sh2[1][2]),
                kSqrt09_08 * sh1[1][1] * sh2[2][3] - kSqrt03_08
                    * (sh1[2][1] * sh2[3][3] + sh1[0][1] * sh2[1][3]),
                kSqrt09_05 * sh1[1][1] * sh2[2][4] - kSqrt03_05
                    * (sh1[2][1] * sh2[3][4] + sh1[0][1] * sh2[1][4]),
                kSqrt03_10 * (sh1[1][2] * sh2[2][4] - sh1[1][0] * sh2[2][0])
                    - kSqrt01_10 * ((sh1[2][2] * sh2[3][4]
                        - sh1[2][0] * sh2[3][0])
                        + (sh1[0][2] * sh2[1][4] - sh1[0][0] * sh2[1][0])) },
            { kSqrt04_15 * (sh1[1][2] * sh2[3][0] + sh1[1][0] * sh2[3][4])
                + kSqrt01_05 * (sh1[2][2] * sh2[2][0] + sh1[2][0] * sh2[2][4])
                - kSqrt01_60 * ((sh1[2][2] * sh2[4][0] + sh1[2][0] * sh2[4][4])
                    + (sh1[0][2] * sh2[0][0] + sh1[0][0] * sh2[0][4])),
                kSqrt08_05 * sh1[1][1] * sh2[3][0]
                    + kSqrt06_05 * sh1[2][1] * sh2[2][0]
                    - kSqrt01_10
                        * (sh1[2][1] * sh2[4][0] + sh1[0][1] * sh2[0][0]),
                sh1[1][1] * sh2[3][1] + kSqrt03_04 * sh1[2][1] * sh2[2][1]
                    - kSqrt01_16
                        * (sh1[2][1] * sh2[4][1] + sh1[0][1] * sh2[0][1]),
                kSqrt08_09 * sh1[1][1] * sh2[3][2]
                    + kSqrt02_03 * sh1[2][1] * sh2[2][2]
                    - kSqrt01_18
                        * (sh1[2][1] * sh2[4][2] + sh1[0][1] * sh2[0][2]),
                sh1[1][1] * sh2[3][3] + kSqrt03_04 * sh1[2][1] * sh2[2][3]
                    - kSqrt01_16
                        * (sh1[2][1] * sh2[4][3] + sh1[0][1] * sh2[0][3]),
                kSqrt08_05 * sh1[1][1] * sh2[3][4]
                    + kSqrt06_05 * sh1[2][1] * sh2[2][4]
                    - kSqrt01_10
                        * (sh1[2][1] * sh2[4][4] + sh1[0][1] * sh2[0][4]),
                kSqrt04_15 * (sh1[1][2] * sh2[3][4] - sh1[1][0] * sh2[3][0])
                    + kSqrt01_05
                        * (sh1[2][2] * sh2[2][4] - sh1[2][0] * sh2[2][0])
                    - kSqrt01_60 * ((sh1[2][2] * sh2[4][4]
                        - sh1[2][0] * sh2[4][0])
                        + (sh1[0][2] * sh2[0][4] - sh1[0][0] * sh2[0][0])) },
            { kSqrt01_06 * (sh1[1][2] * sh2[4][0] + sh1[1][0] * sh2[4][4])
                + kSqrt01_06 * ((sh1[2][2] * sh2[3][0] + sh1[2][0] * sh2[3][4])
                    - (sh1[0][2] * sh2[1][0] + sh1[0][0] * sh2[1][4])),
                sh1[1][1] * sh2[4][0]
                    + (sh1[2][1] * sh2[3][0] - sh1[0][1] * sh2[1][0]),
                kSqrt05_08 * sh1[1][1] * sh2[4][1] + kSqrt05_08
                    * (sh1[2][1] * sh2[3][1] - sh1[0][1] * sh2[1][1]),
                kSqrt05_09 * sh1[1][1] * sh2[4][2] + kSqrt05_09
                    * (sh1[2][1] * sh2[3][2] - sh1[0][1] * sh2[1][2]),
                kSqrt05_08 * sh1[1][1] * sh2[4][3] + kSqrt05_08
                    * (sh1[2][1] * sh2[3][3] - sh1[0][1] * sh2[1][3]),
                sh1[1][1] * sh2[4][4]
                    + (sh1[2][1] * sh2[3][4] - sh1[0][1] * sh2[1][4]),
                kSqrt01_06 * (sh1[1][2] * sh2[4][4] - sh1[1][0] * sh2[4][0])
                    + kSqrt01_06 * ((sh1[2][2] * sh2[3][4]
                        - sh1[2][0] * sh2[3][0])
                        - (sh1[0][2] * sh2[1][4] - sh1[0][0] * sh2[1][0])) },
            { kSqrt01_04 * ((sh1[2][2] * sh2[4][0] + sh1[2][0] * sh2[4][4])
                - (sh1[0][2] * sh2[0][0] + sh1[0][0] * sh2[0][4])),
                kSqrt03_02 * (sh1[2][1] * sh2[4][0] - sh1[0][1] * sh2[0][0]),
                kSqrt15_16 * (sh1[2][1] * sh2[4][1] - sh1[0][1] * sh2[0][1]),
                kSqrt05_06 * (sh1[2][1] * sh2[4][2] - sh1[0][1] * sh2[0][2]),
                kSqrt15_16 * (sh1[2][1] * sh2[4][3] - sh1[0][1] * sh2[0][3]),
                kSqrt03_02 * (sh1[2][1] * sh2[4][4] - sh1[0][1] * sh2[0][4]),
                kSqrt01_04 * ((sh1[2][2] * sh2[4][4] - sh1[2][0] * sh2[4][0])
                    - (sh1[0][2] * sh2[0][4] - sh1[0][0] * sh2[0][0])) } };
    }

    /**
     * Rotate the spherical harmonics coefficients in the given input buffer,
     * and write the result to the given output.
     * 
     * @param coeffsIn The input buffer
     * @param coeffs The output
     */
    void rotate(FloatBuffer coeffsIn, FloatBuffer coeffs)
    {
        
        int i = 0;
        
        if (coeffs.capacity() < 3)
        {
            return;
        }
        coeffs.put(i++, dp(3, coeffsIn, 0, sh1[0]));
        coeffs.put(i++, dp(3, coeffsIn, 0, sh1[1]));
        coeffs.put(i++, dp(3, coeffsIn, 0, sh1[2]));

        if (coeffs.capacity() < 8)
        {
            return;
        }
        coeffs.put(i++, dp(5, coeffsIn, 3, sh2[0]));
        coeffs.put(i++, dp(5, coeffsIn, 3, sh2[1]));
        coeffs.put(i++, dp(5, coeffsIn, 3, sh2[2]));
        coeffs.put(i++, dp(5, coeffsIn, 3, sh2[3]));
        coeffs.put(i++, dp(5, coeffsIn, 3, sh2[4]));

        if (coeffs.capacity() < 15)
        {
            return;
        }
        coeffs.put(i++, dp(7, coeffsIn, 8, sh3[0]));
        coeffs.put(i++, dp(7, coeffsIn, 8, sh3[1]));
        coeffs.put(i++, dp(7, coeffsIn, 8, sh3[2]));
        coeffs.put(i++, dp(7, coeffsIn, 8, sh3[3]));
        coeffs.put(i++, dp(7, coeffsIn, 8, sh3[4]));
        coeffs.put(i++, dp(7, coeffsIn, 8, sh3[5]));
        coeffs.put(i++, dp(7, coeffsIn, 8, sh3[6]));
    }

    /**
     * Computes a "dot product" between the specified portion of the given
     * buffer, and the given array.
     * 
     * @param n The number of dimensions
     * @param a The float buffer
     * @param offset The offset inside the float buffer
     * @param b The array
     * @return The dot product
     */
    private static float dp(int n, FloatBuffer a, int offset, float b[])
    {
        float result = 0.0f;
        for (int i = 0; i < n; i++)
        {
            result += a.get(offset + i) * b[i];
        }
        return result;
    }

}