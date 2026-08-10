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
import static de.javagl.jsplat.examples.SplatSetters.setScaleLinear;

import java.util.ArrayList;
import java.util.List;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splats;

/**
 * A class creating different test data sets
 */
public class SplatScaleTests
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
     * Create a list of splats for scaling tests.
     * 
     * The list will contain 10 scaling test splats for each axis. The scaling
     * factors will be interpolated between 1.0 and 25.0. There will be a 
     * small, white splat at the "end" of each scaling test splat.
     * 
     * Note: This does not make sense. Splats are infinitely large.
     * 
     * @return The splats
     */
    static List<MutableSplat> createScales()
    {
        int shDegree = 0;
        List<MutableSplat> splats = new ArrayList<MutableSplat>();

        int n = 10;
        float dotScale = 0.5f;
        float minScale = 1.0f;
        float maxScale = 25.0f;
        for (int i = 0; i < n; i++)
        {
            float a = (float) i / (n - 1);
            float scale = minScale + a * (maxScale - minScale);
            float y = minY + a * (maxY - minY);
            {
                MutableSplat s = Splats.create(shDegree);
                setDefaults(s);
                setPosition(s, -scale * 2.0f, y, minZ);
                setScaleLinear(s, dotScale, dotScale, dotScale);
                splats.add(s);
            }
            {
                MutableSplat s = Splats.create(shDegree);
                setDefaults(s);
                setColor(s, 1.0f, 0.0f, 0.0f);
                setPosition(s, 0.0f, y, minZ);
                setScaleLinear(s, scale, dotScale, dotScale);
                splats.add(s);
            }
            {
                MutableSplat s = Splats.create(shDegree);
                setDefaults(s);
                setPosition(s, scale * 2.0f, y, minZ);
                setScaleLinear(s, dotScale, dotScale, dotScale);
                splats.add(s);
            }
        }
        for (int i = 0; i < n; i++)
        {
            float a = (float) i / (n - 1);
            float scale = minScale + a * (maxScale - minScale);
            float x = minX + a * (maxX - minX);
            {
                MutableSplat s = Splats.create(shDegree);
                setDefaults(s);
                setPosition(s, x, minY, -scale * 2.0f);
                setScaleLinear(s, dotScale, dotScale, dotScale);
                splats.add(s);
            }
            {
                MutableSplat s = Splats.create(shDegree);
                setDefaults(s);
                setColor(s, 0.0f, 0.0f, 1.0f);
                setPosition(s, x, minY, 0.0f);
                setScaleLinear(s, dotScale, dotScale, scale);
                splats.add(s);
            }
            {
                MutableSplat s = Splats.create(shDegree);
                setDefaults(s);
                setPosition(s, x, minY, scale * 2.0f);
                setScaleLinear(s, dotScale, dotScale, dotScale);
                splats.add(s);
            }
        }
        for (int i = 0; i < n; i++)
        {
            float a = (float) i / (n - 1);
            float scale = minScale + a * (maxScale - minScale);
            float z = minZ + a * (maxZ - minZ);
            {
                MutableSplat s = Splats.create(shDegree);
                setDefaults(s);
                setPosition(s, minX, -scale * 2.0f, z);
                setScaleLinear(s, dotScale, dotScale, dotScale);
                splats.add(s);
            }
            {
                MutableSplat s = Splats.create(shDegree);
                setDefaults(s);
                setColor(s, 0.0f, 1.0f, 0.0f);
                setPosition(s, minX, 0.0f, z);
                setScaleLinear(s, dotScale, scale, dotScale);
                splats.add(s);
            }
            {
                MutableSplat s = Splats.create(shDegree);
                setDefaults(s);
                setPosition(s, minX, scale * 2.0f, z);
                setScaleLinear(s, dotScale, dotScale, dotScale);
                splats.add(s);
            }
        }
        return splats;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatScaleTests()
    {
        // Private constructor to prevent instantiation
    }
}
