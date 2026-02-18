/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jsplat.examples.gltf;

/**
 * Utility methods to create matrices.
 * 
 * All matrices will be 16-element arrays representing 4x4 matrices in
 * column-major order.
 */
class Matrices
{
    /**
     * Create a matrix describing a rotation around the x-axis, and the given
     * translation
     * 
     * @param angleDeg The angle, in degrees
     * @param tx The translation along x
     * @param ty The translation along y
     * @param tz The translation along z
     * @return The matrix
     */
    static float[] createMatrixX(float angleDeg, float tx, float ty,
        float tz)
    {
        float matrix[] = new float[16];

        double angleRad = Math.toRadians(angleDeg);
        float c = (float) Math.cos(angleRad);
        float s = (float) Math.sin(angleRad);

        // Column 0
        matrix[0] = 1.0f;
        matrix[1] = 0.0f;
        matrix[2] = 0.0f;
        matrix[3] = 0.0f;

        // Column 1
        matrix[4] = 0.0f;
        matrix[5] = c;
        matrix[6] = s;
        matrix[7] = 0.0f;

        // Column 2
        matrix[8] = 0.0f;
        matrix[9] = -s;
        matrix[10] = c;
        matrix[11] = 0.0f;

        // Column 3 (Translation)
        matrix[12] = tx;
        matrix[13] = ty;
        matrix[14] = tz;
        matrix[15] = 1.0f;

        return matrix;
    }

    /**
     * Create a matrix describing a rotation around the y-axis, and the given
     * translation
     * 
     * @param angleDeg The angle, in degrees
     * @param tx The translation along x
     * @param ty The translation along y
     * @param tz The translation along z
     * @return The matrix
     */
    static float[] createMatrixY(float angleDeg, float tx, float ty,
        float tz)
    {
        float matrix[] = new float[16];

        double angleRad = Math.toRadians(angleDeg);
        float c = (float) Math.cos(angleRad);
        float s = (float) Math.sin(angleRad);

        // Column 0
        matrix[0] = c;
        matrix[1] = 0.0f;
        matrix[2] = -s;
        matrix[3] = 0.0f;

        // Column 1
        matrix[4] = 0.0f;
        matrix[5] = 1.0f;
        matrix[6] = 0.0f;
        matrix[7] = 0.0f;

        // Column 2
        matrix[8] = s;
        matrix[9] = 0.0f;
        matrix[10] = c;
        matrix[11] = 0.0f;

        // Column 3 (Translation)
        matrix[12] = tx;
        matrix[13] = ty;
        matrix[14] = tz;
        matrix[15] = 1.0f;

        return matrix;
    }

    /**
     * Create a matrix describing uniform scaling
     * 
     * @param s The scaling factor
     * @return The matrix
     */
    static float[] createMatrixScale(float s)
    {
        float[] m = new float[16];
        m[0] = s;
        m[5] = s;
        m[10] = s;
        m[15] = 1.0f;
        return m;
    }

    /**
     * Create a matrix describing a translation
     * 
     * @param x The translation in x-direction
     * @param y The translation in y-direction
     * @param z The translation in z-direction
     * @return The matrix
     */
    static float[] createMatrixTranslation(float x, float y, float z)
    {
        float[] m = new float[16];
        m[0] = 1.0f;
        m[5] = 1.0f;
        m[10] = 1.0f;
        m[15] = 1.0f;
        m[12] = x;
        m[13] = y;
        m[14] = z;
        return m;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Matrices()
    {
        // Private constructor to prevent instantiation
    }
    
}
