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

import java.util.Collection;
import java.util.function.ToDoubleFunction;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;

/**
 * Utility methods related to averaging splats.
 * 
 * This is highly experimental. It may not even make sense. It probably doesn't.
 */
public class SplatAveraging
{
    /**
     * Compute an unspecified "average" of the given splats.
     * 
     * @param splats The splats
     * @return The result
     */
    public static MutableSplat computeAverage(Collection<? extends Splat> splats)
    {
        Splat inputSplat = splats.iterator().next();
        MutableSplat splat = Splats.create(inputSplat.getShDegree());
        
        // Average position
        float px = average(splats, Splat::getPositionX);
        float py = average(splats, Splat::getPositionY);
        float pz = average(splats, Splat::getPositionZ);
        splat.setPositionX(px);
        splat.setPositionY(py);
        splat.setPositionZ(pz);

        // Average scale
        float sizeX = average(splats, s -> Math.exp(s.getScaleX()));
        float sizeY = average(splats, s -> Math.exp(s.getScaleY()));
        float sizeZ = average(splats, s -> Math.exp(s.getScaleZ()));
        splat.setScaleX((float)Math.log(sizeX));
        splat.setScaleY((float)Math.log(sizeY));
        splat.setScaleZ((float)Math.log(sizeZ));
        
        // Tricky: Average rotation
        float r[] = SplatJomlUtils.computeAverageRotation(splats);
        splat.setRotationX(r[0]);
        splat.setRotationY(r[1]);
        splat.setRotationZ(r[2]);
        splat.setRotationW(r[3]);
        
        // Average opacity
        float a = average(splats, s -> Splats.opacityToAlpha(s.getOpacity()));
        splat.setOpacity(Splats.alphaToOpacity(a));
        
        // Now this is wild: Averaging spherical harmonics...?
        // There's some research out there.
        // I'm just "vibe coding" here.
        int shDimensions = splat.getShDimensions();
        for (int d=0; d<shDimensions; d++)
        {
            int dim = d;
            float shx = average(splats, s -> s.getShX(dim));
            float shy = average(splats, s -> s.getShY(dim));
            float shz = average(splats, s -> s.getShZ(dim));
            splat.setShX(dim, shx);
            splat.setShY(dim, shy);
            splat.setShZ(dim, shz);
        }
        
        return splat;
    }
    
    /**
     * Returns the average of the values obtained with the given function
     * 
     * @param <T> The element type
     * @param ts The elements
     * @param f The function
     * @return The result
     */
    private static <T> float average(Collection<T> ts, ToDoubleFunction<T> f)
    {
        double d = ts.stream().mapToDouble(f).average().getAsDouble();
        return (float)d;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatAveraging()
    {
        // Private constructor to prevent instantiation
    }
}
