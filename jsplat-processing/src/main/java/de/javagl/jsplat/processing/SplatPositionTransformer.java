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

import de.javagl.jsplat.MutableSplat;

/**
 * Internal class for transforming the position of splats
 */
class SplatPositionTransformer
{
    /**
     * The transform matrix
     */
    private final float matrix4[];
    
    /**
     * A 3D point
     */
    private final float p[];
    
    /**
     * The result
     */
    private final float result[];

    /**
     * Creates a new instance for the given matrix.
     * 
     * The given matrix is a 4x4 matrix, stored in a 16-element array, in
     * column-major order.
     * 
     * This will store a reference to the given array. The array may not be
     * modified after this instance was created.
     * 
     * @param matrix4 The matrix
     */
    SplatPositionTransformer(float matrix4[])
    {
        this.matrix4 = matrix4;
        this.p = new float[3];
        this.result = new float[3];
    }

    /**
     * Transform the position of the given splat
     * 
     * @param s The splat
     */
    void transform(MutableSplat s)
    {
        p[0] = s.getPositionX();
        p[1] = s.getPositionY();
        p[2] = s.getPositionZ();
        VecMath.multiplyMatrix4WithPoint(matrix4, p, result);
        s.setPositionX(result[0]);
        s.setPositionY(result[1]);
        s.setPositionZ(result[2]);
    }

}