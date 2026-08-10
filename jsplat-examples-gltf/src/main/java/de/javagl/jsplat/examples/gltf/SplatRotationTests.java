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
package de.javagl.jsplat.examples.gltf;

import static de.javagl.jsplat.examples.SplatSetters.setColor;
import static de.javagl.jsplat.examples.SplatSetters.setDefaults;
import static de.javagl.jsplat.examples.SplatSetters.setPosition;
import static de.javagl.jsplat.examples.SplatSetters.setRotationAxisAngleRad;
import static de.javagl.jsplat.examples.SplatSetters.setScale;

import java.util.ArrayList;
import java.util.List;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splats;

/**
 * A class creating different test data sets
 */
public class SplatRotationTests
{
    /**
     * The size of the cube that contains the splats
     */
    private static final float cubeSize = 100.0f;

    /**
     * The minimum X-coordinate
     */
    private static final float minX = -0.5f * cubeSize;

    /**
     * The minimum Y-coordinate
     */
    private static final float minY = -0.5f * cubeSize;

    /**
     * The minimum Z-coordinate
     */
    private static final float minZ = -0.5f * cubeSize;

    /**
     * The maximum X-coordinate
     */
    private static final float maxX = 0.5f * cubeSize;

    /**
     * The maximum Y-coordinate
     */
    private static final float maxY = 0.5f * cubeSize;

    /**
     * The maximum Z-coordinate
     */
    private static final float maxZ = 0.5f * cubeSize;

    /**
     * Create a list of splats for rotation tests.
     * 
     * The list will contain 10 splats, at positions that are interpolated along
     * the x-axis. Each splat will have a scaling factor of 3.0 along the
     * z-axis. The rotation of each splat will be interpolated between 0.0
     * degrees and 90.0 degrees around x. The color will be interpolated from
     * white to red.
     * 
     * The list will include splats that indicate the corners of a containing
     * cube.
     * 
     * @return The splats
     */
    static List<MutableSplat> createRotationsX()
    {
        int shDegree = 0;
        List<MutableSplat> splats = new ArrayList<MutableSplat>();

        int n = 10;
        for (int i = 0; i < n; i++)
        {
            float a = (float) i / (n - 1);
            float x = minX + a * (maxX - minX);
            float angleRad = 0.0f + a * (float) (Math.PI / 2.0);

            MutableSplat s0 = Splats.create(shDegree);
            setDefaults(s0);
            setPosition(s0, x, 0.0f, 0.0f);
            setColor(s0, 1.0f, 1.0f - a, 1.0f - a);
            setScale(s0, 1.0f, 1.0f, 3.0f);
            setRotationAxisAngleRad(s0, 1.0f, 0.0f, 0.0f, angleRad);
            splats.add(s0);
        }
        splats.addAll(SplatTests.createCorners(shDegree, cubeSize));
        return splats;
    }

    /**
     * Create a list of splats for rotation tests.
     * 
     * The list will contain 10 splats, at positions that are interpolated along
     * the y-axis. Each splat will have a scaling factor of 3.0 along the
     * x-axis. The rotation of each splat will be interpolated between 0.0
     * degrees and 90.0 degrees around y. The color will be interpolated from
     * white to green.
     * 
     * The list will include splats that indicate the corners of a containing
     * cube.
     * 
     * @return The splats
     */
    static List<MutableSplat> createRotationsY()
    {
        int shDegree = 0;
        List<MutableSplat> splats = new ArrayList<MutableSplat>();

        int n = 10;
        for (int i = 0; i < n; i++)
        {
            float a = (float) i / (n - 1);
            float y = minY + a * (maxY - minY);
            float angleRad = 0.0f + a * (float) (Math.PI / 2.0);

            MutableSplat s0 = Splats.create(shDegree);
            setDefaults(s0);
            setPosition(s0, 0.0f, y, 0.0f);
            setColor(s0, 1.0f - a, 1.0f, 1.0f - a);
            setScale(s0, 3.0f, 1.0f, 1.0f);
            setRotationAxisAngleRad(s0, 0.0f, 1.0f, 0.0f, angleRad);
            splats.add(s0);
        }
        splats.addAll(SplatTests.createCorners(shDegree, cubeSize));
        return splats;
    }

    /**
     * Create a list of splats for rotation tests.
     * 
     * The list will contain 10 splats, at positions that are interpolated along
     * the z-axis. Each splat will have a scaling factor of 3.0 along the
     * y-axis. The rotation of each splat will be interpolated between 0.0
     * degrees and 90.0 degrees around z. The color will be interpolated from
     * white to blue.
     * 
     * The list will include splats that indicate the corners of a containing
     * cube.
     * 
     * @return The splats
     */
    static List<MutableSplat> createRotationsZ()
    {
        int shDegree = 0;
        List<MutableSplat> splats = new ArrayList<MutableSplat>();

        int n = 10;
        for (int i = 0; i < n; i++)
        {
            float a = (float) i / (n - 1);
            float z = minZ + a * (maxZ - minZ);
            float angleRad = 0.0f + a * (float) (Math.PI / 2.0);

            MutableSplat s0 = Splats.create(shDegree);
            setDefaults(s0);
            setPosition(s0, 0.0f, 0.0f, z);
            setColor(s0, 1.0f - a, 1.0f - a, 1.0f);
            setScale(s0, 1.0f, 3.0f, 1.0f);
            setRotationAxisAngleRad(s0, 0.0f, 0.0f, 1.0f, angleRad);
            splats.add(s0);
        }
        splats.addAll(SplatTests.createCorners(shDegree, cubeSize));
        return splats;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatRotationTests()
    {
        // Private constructor to prevent instantiation
    }
}
