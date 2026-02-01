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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;

/**
 * Tests for the {@link SplatTransforms} class
 */
public class SplatTransformsTest
{

    /**
     * The epsilon for comparisons
     */
    private static final float EPSILON = 1e-5f;

    /**
     * Test whether a random splat remains unmodified under identity transform
     */
    @Test
    public void testIdentityTransform()
    {
        Random random = new Random(0);
        MutableSplat splat = createRandomSplat(3, random);
        MutableSplat expectedSplat = copy(splat);

        float[] identityMatrix = VecMath.identity4x4(null);

        SplatTransforms.transformList(Arrays.asList(splat), identityMatrix);

        assertSplatsEqual(expectedSplat, splat, "Identity transform");
    }

    /**
     * Test transforms for splats of degree 1
     */
    @Test
    public void testDegree1()
    {
        runTest(1);
    }

    /**
     * Test transforms for splats of degree 1
     */
    @Test
    public void testDegree2()
    {
        runTest(2);
    }

    /**
     * Test transforms for splats of degree 1
     */
    @Test
    public void testDegree3()
    {
        runTest(3);
    }

    /**
     * Run the test for random splats with the given degree
     * 
     * @param shDegree The degree
     */
    private void runTest(int shDegree)
    {
        Random random = new Random(0);
        for (int i = 0; i < 100; i++)
        {
            MutableSplat splat = createRandomSplat(shDegree, random);
            MutableSplat expectedSplat = copy(splat);

            float[] m = createRandomMatrix(random);
            float[] inv = VecMath.invert4x4(m, null);

            SplatTransforms.transformList(Arrays.asList(splat), m);
            SplatTransforms.transformList(Arrays.asList(splat), inv);

            assertSplatsEqual(expectedSplat, splat,
                "Test " + i + " for degree " + shDegree);
        }
    }

    /**
     * Create a random splat with the given degree
     * 
     * @param shDegree The degree
     * @param random The random number generator
     * @return The splat
     */
    private static MutableSplat createRandomSplat(int shDegree, Random random)
    {
        MutableSplat splat = Splats.create(shDegree);

        splat.setPositionX(random.nextFloat() * 20.0f - 10.0f);
        splat.setPositionY(random.nextFloat() * 20.0f - 10.0f);
        splat.setPositionZ(random.nextFloat() * 20.0f - 10.0f);

        float[] q = createRandomQuaternion(random);
        splat.setRotationX(q[0]);
        splat.setRotationY(q[1]);
        splat.setRotationZ(q[2]);
        splat.setRotationW(q[3]);

        splat.setScaleX(-1.0f + random.nextFloat() * 2.0f);
        splat.setScaleY(-1.0f + random.nextFloat() * 2.0f);
        splat.setScaleZ(-1.0f + random.nextFloat() * 2.0f);

        for (int i = 0; i < splat.getShDimensions(); i++)
        {
            splat.setShX(i, -1.0f + random.nextFloat() * 2.0f);
            splat.setShY(i, -1.0f + random.nextFloat() * 2.0f);
            splat.setShZ(i, -1.0f + random.nextFloat() * 2.0f);
        }
        return splat;
    }

    /**
     * Create a 4x4 matrix with a random rotation and translation
     * 
     * @param random The random number generator
     * @return The matrix
     */
    private float[] createRandomMatrix(Random random)
    {
        float[] q = createRandomQuaternion(random);
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
    private static float[] createRandomQuaternion(Random random)
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
     * Create a copy of the given splat
     * 
     * @param s The splat
     * @return The copy
     */
    private static MutableSplat copy(Splat s)
    {
        MutableSplat copy = Splats.create(s.getShDegree());
        copy.setPositionX(s.getPositionX());
        copy.setPositionY(s.getPositionY());
        copy.setPositionZ(s.getPositionZ());
        copy.setScaleX(s.getScaleX());
        copy.setScaleY(s.getScaleY());
        copy.setScaleZ(s.getScaleZ());
        copy.setRotationX(s.getRotationX());
        copy.setRotationY(s.getRotationY());
        copy.setRotationZ(s.getRotationZ());
        copy.setRotationW(s.getRotationW());
        for (int i = 0; i < s.getShDimensions(); i++)
        {
            copy.setShX(i, s.getShX(i));
            copy.setShY(i, s.getShY(i));
            copy.setShZ(i, s.getShZ(i));
        }
        return copy;
    }

    /**
     * Assert that the given spalts are epsilon-equal
     * 
     * @param expected The expected splat
     * @param actual The actual splat
     * @param message A message for the tests
     */
    private static void assertSplatsEqual(Splat expected, Splat actual,
        String message)
    {

        // System.out.println("Compare");
        // System.out.println("Actual:\n"+Splats.createString(actual));
        // System.out.println("Expected:\n"+Splats.createString(expected));

        assertEquals(message + ": PositionX", expected.getPositionX(),
            actual.getPositionX(), EPSILON);
        assertEquals(message + ": PositionY", expected.getPositionY(),
            actual.getPositionY(), EPSILON);
        assertEquals(message + ": PositionZ", expected.getPositionZ(),
            actual.getPositionZ(), EPSILON);

        assertEquals(message + ": ScaleX", expected.getScaleX(),
            actual.getScaleX(), EPSILON);
        assertEquals(message + ": ScaleY", expected.getScaleY(),
            actual.getScaleY(), EPSILON);
        assertEquals(message + ": ScaleZ", expected.getScaleZ(),
            actual.getScaleZ(), EPSILON);

        // assertEquals(message + ": RotationX", expected.getRotationX(),
        // actual.getRotationX(), EPSILON);
        // assertEquals(message + ": RotationY", expected.getRotationY(),
        // actual.getRotationY(), EPSILON);
        // assertEquals(message + ": RotationZ", expected.getRotationZ(),
        // actual.getRotationZ(), EPSILON);
        // assertEquals(message + ": RotationW", expected.getRotationW(),
        // actual.getRotationW(), EPSILON);

        // Special treatment for rotation:
        // Two scalar-last quaternions describe the same rotation when 
        // their dot product is +1.0 or -1.0. The quaternions should
        // be rotation quaternions (i.e. normalized), but this is not
        // ensured in general, so they are normalized here.
        float ax = actual.getRotationX();
        float ay = actual.getRotationY();
        float az = actual.getRotationZ();
        float aw = actual.getRotationW();
        float aLenSquared = ax * ax + ay * ay + az * az + aw * aw;
        float aInvLen = 1.0f / (float)Math.sqrt(aLenSquared);
        float anx = ax * aInvLen; 
        float any = ay * aInvLen; 
        float anz = az * aInvLen; 
        float anw = aw * aInvLen; 
        
        float bx = expected.getRotationX();
        float by = expected.getRotationY();
        float bz = expected.getRotationZ();
        float bw = expected.getRotationW();
        float bLenSquared = bx * bx + by * by + bz * bz + bw * bw;
        float bInvLen = 1.0f / (float)Math.sqrt(bLenSquared);
        float bnx = bx * bInvLen; 
        float bny = by * bInvLen; 
        float bnz = bz * bInvLen; 
        float bnw = bw * bInvLen; 
        
        float dot = anx * bnx + any * bny + anz * bnz + anw * bnw;
        boolean rotationEqual = Math.abs(Math.abs(dot) - 1.0f) <= EPSILON;

        assertTrue(message + ": Rotation", rotationEqual);

        for (int i = 0; i < expected.getShDimensions(); i++)
        {
            assertEquals(message + ": SH_X[" + i + "]", expected.getShX(i),
                actual.getShX(i), EPSILON);
            assertEquals(message + ": SH_Y[" + i + "]", expected.getShY(i),
                actual.getShY(i), EPSILON);
            assertEquals(message + ": SH_Z[" + i + "]", expected.getShZ(i),
                actual.getShZ(i), EPSILON);
        }
    }
}
