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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

/**
 * Tests for the {@link SplatTransforms} class
 */
public class VecMathTest
{
    /**
     * The epsilon for comparisons
     */
    private static final float EPSILON = 1e-5f;

    /**
     * Basic test for matrix-quaternion conversions
     */
    @Test
    public void testMatrixQuaternion()
    {
        Random random = new Random(0);
        for (int i = 0; i < 100; i++)
        {
            float[] e = TestUtils.createRandomScalarLastQuaternion(random);
            float[] m = VecMath.scalarLastQuaternionToRotationMatrix(e, null);
            float[] a = VecMath.rotationMatrixToScalarLastQuaternion(m, null);

            String message =
                "\n a=" + Arrays.toString(a) + ",\n e=" + Arrays.toString(e);
            boolean equal = TestUtils.rotationEqual(a[0], a[1], a[2], a[3],
                e[0], e[1], e[2], e[3], EPSILON);
            assertTrue(message, equal);
        }
    }

}
