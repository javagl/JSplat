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

import org.joml.Matrix4f;
import org.joml.Vector3f;

import de.javagl.jsplat.MutableSplat;

/**
 * Internal class for transforming the position of splats
 */
class SplatPositionTransformer
{
    // That use of Unsafe in JOML was a mistake.
    static
    {
        System.setProperty("joml.nounsafe", "true");
    }

    /**
     * The transform matrix
     */
    private final Matrix4f matrix;

    /**
     * A temporary vector for the transform
     */
    private final Vector3f v;


    /**
     * Creates a new instance for the given matrix.
     * 
     * The given matrix is a 4x4 matrix, stored in a 16-element array, in
     * column-major order
     * 
     * @param matrix The matrix
     */
    SplatPositionTransformer(float matrix[])
    {
        this.matrix = new Matrix4f(FloatBuffer.wrap(matrix));
        this.v = new Vector3f();
    }

    /**
     * Transform the position of the given splat
     * 
     * @param s The splat
     */
    void transform(MutableSplat s)
    {
        v.x = s.getPositionX();
        v.y = s.getPositionY();
        v.z = s.getPositionZ();
        matrix.transformPosition(v);
        s.setPositionX(v.x);
        s.setPositionY(v.y);
        s.setPositionZ(v.z);
    }

}