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

import org.joml.Matrix3f;
import org.joml.Quaternionf;

import de.javagl.jsplat.MutableSplat;

/**
 * Internal class for rotating the rotation of splats
 */
class SplatRotationRotator
{
    // That use of Unsafe in JOML was a mistake.
    static
    {
        System.setProperty("joml.nounsafe", "true");
    }

    /**
     * The quaternion for the rotation
     */
    private final Quaternionf rotationQuaternion;

    /**
     * A temporary quaternion for the rotation
     */
    private final Quaternionf q;

    /**
     * Creates a new instance for the given matrix.
     * 
     * The given matrix is a 3x3 matrix, stored in a 9-element array, in
     * column-major order
     * 
     * @param matrix The rotation matrix
     */
    SplatRotationRotator(float matrix[])
    {
        Quaternionf q = new Quaternionf();
        Matrix3f m = new Matrix3f(FloatBuffer.wrap(matrix));
        q.setFromUnnormalized(m);
        this.rotationQuaternion = q;
        this.q = new Quaternionf();
    }

    /**
     * Rotate the rotation of the given splat
     * 
     * @param s The splat
     */
    void rotate(MutableSplat s)
    {
        q.x = s.getRotationX();
        q.y = s.getRotationY();
        q.z = s.getRotationZ();
        q.w = s.getRotationW();
        q.mul(rotationQuaternion);
        s.setRotationX(q.x);
        s.setRotationY(q.y);
        s.setRotationZ(q.z);
        s.setRotationW(q.w);
    }

}