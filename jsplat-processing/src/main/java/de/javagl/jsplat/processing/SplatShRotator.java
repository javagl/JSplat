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
package de.javagl.jsplat.processing;

import java.nio.FloatBuffer;

import de.javagl.jsplat.MutableSplat;

/**
 * Internal class for rotating the spherical harmonics of splats
 */
class SplatShRotator
{
    /**
     * A thread-local buffer for the input coefficients
     */
    private final ThreadLocal<FloatBuffer> threadLocalCoeffsIn;

    /**
     * A thread-local buffer for the coefficients
     */
    private final ThreadLocal<FloatBuffer> threadLocalCoeffs;
    
    /**
     * The {@link SphericalHarmonicsRotator}
     */
    private final SphericalHarmonicsRotator sr;

    /**
     * Creates a new instance for the given matrix, to rotate the spherical
     * harmonics coefficients with the given number of dimensions.
     * 
     * The given matrix is a 3x3 matrix, stored in a 9-element array, in
     * column-major order
     * 
     * @param matrix The matrix
     * @param dims The dimensions
     */
    SplatShRotator(float matrix[], int dims)
    {
        this.threadLocalCoeffsIn =
            ThreadLocal.withInitial(() -> FloatBuffer.allocate(dims - 1));
        this.threadLocalCoeffs =
            ThreadLocal.withInitial(() -> FloatBuffer.allocate(dims - 1));
        this.sr = new SphericalHarmonicsRotator(matrix);
    }
    
    /**
     * Rotate the spherical harmonics of the given splat
     * 
     * @param s The splat
     */
    void rotateSh(MutableSplat s)
    {
        // The SphericalHarmonicsRotator (ported from sh-lib) uses a 
        // different indexing scheme.
        // 
        // In JSplat, there are
        // degree 0: 1 coefficient  (total:  1 dimension until now)
        // degree 1: 3 coefficients (total:  4 dimension until now)
        // degree 2: 5 coefficients (total:  9 dimension until now)
        // degree 3: 7 coefficients (total: 16 dimension until now)
        // 
        // The "coefficients" that are fed into the SphericalHarmonicsRotator 
        // are ONLY the coefficients for degree 1, or 2, or 3.
        //
        // (The coefficient for degree 0, the color, is handled
        // explicitly above)
        // 
        // So there are ...
        //
        // - 3  coefficients for the rotator for  4 dimensions in JSplat
        // - 8  coefficients for the rotator for  9 dimensions in JSplat
        // - 15 coefficients for the rotator for 16 dimensions in JSplat
        // 
        // This is handled with "dims - 1" in the following loops,
        // and the "d + 1" when accessing the JSplat structures. 
        // And... yeah, the coefficients are given separately for
        // the X, Y, and Z components...
        
        int dims = s.getShDimensions();
        
        FloatBuffer coeffsIn = threadLocalCoeffsIn.get();
        FloatBuffer coeffs = threadLocalCoeffs.get();

        // Coefficients for X
        for (int d = 0; d < dims - 1; d++)
        {
            coeffsIn.put(d, s.getShX(d + 1));
        }
        sr.rotate(coeffsIn, coeffs);
        for (int d = 0; d < dims - 1; d++)
        {
            s.setShX(d + 1, coeffs.get(d));
        }

        // Coefficients for Y
        for (int d = 0; d < dims - 1; d++)
        {
            coeffsIn.put(d, s.getShY(d + 1));
        }
        sr.rotate(coeffsIn, coeffs);
        for (int d = 0; d < dims - 1; d++)
        {
            s.setShY(d + 1, coeffs.get(d));
        }
        
        // Coefficients for Z
        for (int d = 0; d < dims - 1; d++)
        {
            coeffsIn.put(d, s.getShZ(d + 1));
        }
        sr.rotate(coeffsIn, coeffs);
        for (int d = 0; d < dims - 1; d++)
        {
            s.setShZ(d + 1, coeffs.get(d));
        }
    }
}
