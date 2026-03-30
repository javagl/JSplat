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
 * Methods to create test data for splats
 */
class SplatTests
{
    /**
     * Create a list of splats, representing a cube.
     * 
     * Many details are intentionally not specified.
     * 
     * @param size The size of the cube
     * @param degree The degree
     * @return The splats
     */
    static List<MutableSplat> createCorners(int degree, float size)
    {
        List<MutableSplat> splats = new ArrayList<MutableSplat>();
        for (int c = 0; c < 8; c++)
        {
            float x = -0.5f + ((c & 1) == 0 ? 0.0f : 1.0f);
            float y = -0.5f + ((c & 2) == 0 ? 0.0f : 1.0f);
            float z = -0.5f + ((c & 4) == 0 ? 0.0f : 1.0f);
            add(splats, degree, size, x, y, z);
        }
        return splats;
    }

    /**
     * Add a splat to the given list, with properties derived from the given
     * parameters and some constants. Details are not specified.
     * 
     * @param splats The splats
     * @param degree The degree
     * @param size The size of the cube
     * @param npx The normalized x-coordinate
     * @param npy The normalized y-coordinate
     * @param npz The normalized z-coordinate
     */
    private static void add(List<MutableSplat> splats, int degree, float size,
        float npx, float npy, float npz)
    {
        MutableSplat splat = Splats.create(degree);

        splat.setPositionX(npx * size);
        splat.setPositionY(npy * size);
        splat.setPositionZ(npz * size);

        splat.setScaleX(1.0f);
        splat.setScaleY(1.0f);
        splat.setScaleZ(1.0f);

        splat.setRotationX(0.0f);
        splat.setRotationY(0.0f);
        splat.setRotationZ(0.0f);
        splat.setRotationW(1.0f);

        splat.setOpacity(Splats.alphaToOpacity(1.0f));

        if (npx == -0.5f && npy == -0.5f && npz == -0.5f)
        {
            splat.setShX(0, Splats.colorToDirectCurrent(0.1f));
            splat.setShY(0, Splats.colorToDirectCurrent(0.1f));
            splat.setShZ(0, Splats.colorToDirectCurrent(0.1f));
        }
        else
        {
            splat.setShX(0, Splats.colorToDirectCurrent(npx + 0.5f));
            splat.setShY(0, Splats.colorToDirectCurrent(npy + 0.5f));
            splat.setShZ(0, Splats.colorToDirectCurrent(npz + 0.5f));
        }

        splats.add(splat);
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatTests()
    {
        // Private constructor to prevent instantiation
    }
}
