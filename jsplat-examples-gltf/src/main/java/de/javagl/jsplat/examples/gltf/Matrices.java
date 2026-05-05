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
    static double[] createMatrixX(double angleDeg, double tx, double ty,
        double tz)
    {
        double matrix[] = new double[16];

        double angleRad = Math.toRadians(angleDeg);
        double c = Math.cos(angleRad);
        double s = Math.sin(angleRad);

        // Column 0
        matrix[0] = 1.0;
        matrix[1] = 0.0;
        matrix[2] = 0.0;
        matrix[3] = 0.0;

        // Column 1
        matrix[4] = 0.0;
        matrix[5] = c;
        matrix[6] = s;
        matrix[7] = 0.0;

        // Column 2
        matrix[8] = 0.0;
        matrix[9] = -s;
        matrix[10] = c;
        matrix[11] = 0.0;

        // Column 3 (Translation)
        matrix[12] = tx;
        matrix[13] = ty;
        matrix[14] = tz;
        matrix[15] = 1.0;

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
    static double[] createMatrixY(double angleDeg, double tx, double ty,
        double tz)
    {
        double matrix[] = new double[16];

        double angleRad = Math.toRadians(angleDeg);
        double c = Math.cos(angleRad);
        double s = Math.sin(angleRad);

        // Column 0
        matrix[0] = c;
        matrix[1] = 0.0;
        matrix[2] = -s;
        matrix[3] = 0.0;

        // Column 1
        matrix[4] = 0.0;
        matrix[5] = 1.0;
        matrix[6] = 0.0;
        matrix[7] = 0.0;

        // Column 2
        matrix[8] = s;
        matrix[9] = 0.0;
        matrix[10] = c;
        matrix[11] = 0.0;

        // Column 3 (Translation)
        matrix[12] = tx;
        matrix[13] = ty;
        matrix[14] = tz;
        matrix[15] = 1.0;

        return matrix;
    }

    /**
     * Create a matrix describing uniform scaling
     * 
     * @param s The scaling factor
     * @return The matrix
     */
    static double[] createMatrixScale(double s)
    {
        double[] m = new double[16];
        m[0] = s;
        m[5] = s;
        m[10] = s;
        m[15] = 1.0;
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
    static double[] createMatrixTranslation(double x, double y, double z)
    {
        double[] m = new double[16];
        m[0] = 1.0;
        m[5] = 1.0;
        m[10] = 1.0;
        m[15] = 1.0;
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
