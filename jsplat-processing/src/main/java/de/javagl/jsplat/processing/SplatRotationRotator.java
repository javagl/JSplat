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
    }

    /**
     * Rotate the rotation of the given splat
     * 
     * @param s The splat
     */
    void rotate(MutableSplat s)
    {
        float q0x = rotationQuaternion[0];
        float q0y = rotationQuaternion[1];
        float q0z = rotationQuaternion[2];
        float q0w = rotationQuaternion[3];

        float q1x = s.getRotationX();
        float q1y = s.getRotationY();
        float q1z = s.getRotationZ();
        float q1w = s.getRotationW();

        float rx = q0w * q1x + q0x * q1w + q0y * q1z - q0z * q1y;
        float ry = q0w * q1y - q0x * q1z + q0y * q1w + q0z * q1x;
        float rz = q0w * q1z + q0x * q1y - q0y * q1x + q0z * q1w;
        float rw = q0w * q1w - q0x * q1x - q0y * q1y - q0z * q1z;

        s.setRotationX(rx);
        s.setRotationY(ry);
        s.setRotationZ(rz);
        s.setRotationW(rw);
    }

}