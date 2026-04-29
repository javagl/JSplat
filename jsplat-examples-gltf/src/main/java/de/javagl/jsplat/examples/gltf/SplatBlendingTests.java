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
import de.javagl.jsplat.examples.SplatSetters;

/**
 * Methods to create test data set for splat blending
 */
class SplatBlendingTests
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
    static List<MutableSplat> createBlendingTest()
    {
        List<MutableSplat> splats = new ArrayList<MutableSplat>();

        // A with SH sum 5.0 and opacity 1.0
        {
            MutableSplat s = Splats.create(0);
            SplatSetters.setDefaults(s);

            s.setPositionX(-0.25f * size);
            s.setPositionY(0.0f);
            s.setPositionZ(0.0f);

            s.setScaleX(2.0f);
            s.setScaleY(2.0f);
            s.setScaleZ(0.01f);
            
            s.setOpacity(Splats.alphaToOpacity(1.0f));

            s.setShX(0, Splats.colorToDirectCurrent(5.0f));
            s.setShY(0, Splats.colorToDirectCurrent(5.0f));
            s.setShZ(0, Splats.colorToDirectCurrent(5.0f));
            splats.add(s);
        }

        // B with SH sum 0.0 and opacity 0.9
        {
            MutableSplat s = Splats.create(0);
            SplatSetters.setDefaults(s);

            s.setPositionX(-0.25f * size);
            s.setPositionY(0.0f);
            s.setPositionZ(0.05f * size);

            s.setScaleX(2.0f);
            s.setScaleY(2.0f);
            s.setScaleZ(0.01f);
            
            s.setOpacity(Splats.alphaToOpacity(0.9f));

            s.setShX(0, Splats.colorToDirectCurrent(0.0f));
            s.setShY(0, Splats.colorToDirectCurrent(0.0f));
            s.setShZ(0, Splats.colorToDirectCurrent(0.0f));
            splats.add(s);
        }

        
        
        
        // A with SH sum 5.0 and opacity 0.1
        {
            MutableSplat s = Splats.create(0);
            SplatSetters.setDefaults(s);

            s.setPositionX(0.25f * size);
            s.setPositionY(0.0f);
            s.setPositionZ(0.0f);

            s.setScaleX(2.0f);
            s.setScaleY(2.0f);
            s.setScaleZ(0.01f);
            
            s.setOpacity(Splats.alphaToOpacity(0.1f));

            s.setShX(0, Splats.colorToDirectCurrent(5.0f));
            s.setShY(0, Splats.colorToDirectCurrent(5.0f));
            s.setShZ(0, Splats.colorToDirectCurrent(5.0f));
            splats.add(s);
        }

        // B with SH sum 0.0 and opacity 0.9
        {
            MutableSplat s = Splats.create(0);
            SplatSetters.setDefaults(s);

            s.setPositionX(0.25f * size);
            s.setPositionY(0.0f);
            s.setPositionZ(0.05f * size);

            s.setScaleX(2.0f);
            s.setScaleY(2.0f);
            s.setScaleZ(0.01f);
            
            s.setOpacity(Splats.alphaToOpacity(0.9f));

            s.setShX(0, Splats.colorToDirectCurrent(0.0f));
            s.setShY(0, Splats.colorToDirectCurrent(0.0f));
            s.setShZ(0, Splats.colorToDirectCurrent(0.0f));
            splats.add(s);
        }
        
        
        
        splats.addAll(SplatTests.createCorners(0, size));
        return splats;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatBlendingTests()
    {
        // Private constructor to prevent instantiation
    }

}
