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

import java.util.ArrayList;
import java.util.List;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splats;

/**
 * Methods to create test data set for splat depth sorting
 */
class SplatDepthTests
{
    /**
     * The cube size
     */
    private static final float size = 100.0f;

    /**
     * Create a test data set for splat depth sorting tests.
     * 
     * Some details are unspecified here.
     * 
     * @return The test data
     */
    static List<MutableSplat> createDepthTest()
    {
        List<MutableSplat> splats = new ArrayList<MutableSplat>();

        int n = 8;
        float minX = 0.25f;
        float minY = 0.25f;
        float minZ = 0.25f;
        float maxX = 0.75f;
        float maxY = 0.75f;
        float maxZ = 0.75f;
        for (int i = 1; i < n; i++)
        {
            float rel = (float) (i - 1) / (n - 2);
            MutableSplat s = Splats.create(0);

            float x = minX + rel * (maxX - minX);
            float y = minY + rel * (maxY - minY);
            float z = minZ + rel * (maxZ - minZ);
            s.setPositionX(-0.5f * size + x * size);
            s.setPositionY(-0.5f * size + y * size);
            s.setPositionZ(-0.5f * size + z * size);

            s.setScaleX(2.5f);
            s.setScaleY(2.5f);
            s.setScaleZ(0.01f);
            s.setRotationX(0.0f);
            s.setRotationY(0.0f);
            s.setRotationZ(0.0f);
            s.setRotationW(1.0f);
            s.setOpacity(Splats.alphaToOpacity(1.0f));

            float r = ((i & 1) == 0 ? 0.0f : 1.0f);
            float g = ((i & 2) == 0 ? 0.0f : 1.0f);
            float b = ((i & 4) == 0 ? 0.0f : 1.0f);

            s.setShX(0, Splats.colorToDirectCurrent(r));
            s.setShY(0, Splats.colorToDirectCurrent(g));
            s.setShZ(0, Splats.colorToDirectCurrent(b));
            splats.add(s);
        }

        splats.addAll(SplatTests.createCorners(0, size));
        return splats;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatDepthTests()
    {
        // Private constructor to prevent instantiation
    }

}
