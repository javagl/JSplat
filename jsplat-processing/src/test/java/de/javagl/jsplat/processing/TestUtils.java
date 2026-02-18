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

import java.util.Random;

/**
 * Utilities for the tests
 */
public class TestUtils
{
    /**
     * Create a 4x4 matrix with a random rotation and translation
     * 
     * @param random The random number generator
     * @return The matrix
     */
    static float[] createRandomMatrix4(Random random)
    {
        float[] q = createRandomScalarLastQuaternion(random);
        float[] m3 = VecMath.scalarLastQuaternionToRotationMatrix(q, null);
        float[] m4 = VecMath.createMatrix4FromMatrix3(m3, null);
        float tx = -10.0f + random.nextFloat() * 20.0f;
        float ty = -10.0f + random.nextFloat() * 20.0f;
        float tz = -10.0f + random.nextFloat() * 20.0f;
        VecMath.translate4x4(m4, tx, ty, tz, m4);
        return m4;
    }

    /**
     * Create a random (unit) quaternion
     * 
     * @param random The random number generator
     * @return The quaternion
     */
    static float[] createRandomScalarLastQuaternion(Random random)
    {
        float axis[] = new float[3];
        axis[0] = -1.0f + random.nextFloat() * 2.0f;
        axis[1] = -1.0f + random.nextFloat() * 2.0f;
        axis[2] = -1.0f + random.nextFloat() * 2.0f;
        float angleRad = random.nextFloat() * (float) (Math.PI * 2.0);
        float q[] = VecMath.createScalarLastQuaternionFromAxisAngleRad(axis,
            angleRad, null);
        return q;
    }

    /**
     * Returns whether the given scalar-last quaternions describe the same
     * rotation.
     * 
     * Two scalar-last quaternions describe the same rotation when their dot
     * product is +1.0 or -1.0. The quaternions should be rotation quaternions
     * (i.e. normalized), but this is not ensured in general, so they are
     * normalized here.
     * 
     * @param ax The first x-component
     * @param ay The first y-component
     * @param az The first z-component
     * @param aw The first w-component (scalar)
     * @param bx The second x-component
     * @param by The second y-component
     * @param bz The second z-component
     * @param bw The second w-component (scalar)
     * @param epsilon The epsilon
     * @return The result
     */
    static boolean rotationEqual(float ax, float ay, float az, float aw,
        float bx, float by, float bz, float bw, float epsilon)
    {
        float aLenSquared = ax * ax + ay * ay + az * az + aw * aw;
        float aInvLen = 1.0f / (float) Math.sqrt(aLenSquared);
        float anx = ax * aInvLen;
        float any = ay * aInvLen;
        float anz = az * aInvLen;
        float anw = aw * aInvLen;

        float bLenSquared = bx * bx + by * by + bz * bz + bw * bw;
        float bInvLen = 1.0f / (float) Math.sqrt(bLenSquared);
        float bnx = bx * bInvLen;
        float bny = by * bInvLen;
        float bnz = bz * bInvLen;
        float bnw = bw * bInvLen;

        float dot = anx * bnx + any * bny + anz * bnz + anw * bnw;
        boolean rotationEqual = Math.abs(Math.abs(dot) - 1.0f) <= epsilon;
        return rotationEqual;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private TestUtils()
    {
        // Private constructor to prevent instantiation
    }
}
