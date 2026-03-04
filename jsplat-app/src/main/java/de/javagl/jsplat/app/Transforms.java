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
package de.javagl.jsplat.app;

import de.javagl.jsplat.processing.VecMath;

/**
 * Utility methods related to transforms
 */
class Transforms
{
    /**
     * Creates a 4x4 matrix (in column major order) for the given transform.
     * 
     * The order of operations is not specified for now.
     * 
     * @param t The transform
     * @return The matrix
     */
    static float[] toMatrix(Transform t)
    {
        float matrixX[] = VecMath.rotationX(t.rotationRadX, null);
        float matrixY[] = VecMath.rotationY(t.rotationRadY, null);
        float matrixZ[] = VecMath.rotationZ(t.rotationRadZ, null);

        float scaleMatrix[] =
            VecMath.scale4x4(t.scaleX, t.scaleY, t.scaleZ, null);

        float matrix[] = VecMath.identity4x4(null);
        VecMath.mul4x4(matrix, matrixX, matrix);
        VecMath.mul4x4(matrix, matrixY, matrix);
        VecMath.mul4x4(matrix, matrixZ, matrix);
        VecMath.translate4x4(matrix, t.translationX, t.translationY,
            t.translationZ, matrix);
        VecMath.mul4x4(matrix, scaleMatrix, matrix);
        return matrix;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Transforms()
    {
        // Private constructor to prevent instantiation
    }
}
