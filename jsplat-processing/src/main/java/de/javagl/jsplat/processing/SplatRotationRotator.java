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
 * Internal class for rotating the rotation of splats
 */
class SplatRotationRotator
{
    /**
     * The quaternion for the rotation
     */
    private final float rotationQuaternion[];

    /**
     * A temporary quaternion
     */
    private final float q[];

    /**
     * A temporary quaternion for the rotation
     */
    private final float result[];

    /**
     * Creates a new instance for the given scalar-last quaternion.
     * 
     * This will store a reference to the given array. The array may not be
     * modified after this instance was created.
     * 
     * @param rotationQuaternion The rotation quaternion
     */
    SplatRotationRotator(float rotationQuaternion[])
    {
        this.rotationQuaternion = rotationQuaternion;
        this.q = new float[4];
        this.result = new float[4];
    }

    /**
     * Rotate the rotation of the given splat
     * 
     * @param s The splat
     */
    void rotate(MutableSplat s)
    {
        q[0] = s.getRotationX();
        q[1] = s.getRotationY();
        q[2] = s.getRotationZ();
        q[3] = s.getRotationW();
        VecMath.multiplyScalarLastQuaternions(q, rotationQuaternion, result);
        s.setRotationX(result[0]);
        s.setRotationY(result[1]);
        s.setRotationZ(result[2]);
        s.setRotationW(result[3]);
    }

}