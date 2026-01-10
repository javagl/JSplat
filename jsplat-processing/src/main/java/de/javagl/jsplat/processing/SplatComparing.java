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

import de.javagl.jsplat.Splat;

/**
 * Utility methods related to comparing splats.
 * 
 * This is highly experimental. It may not even make sense.
 */
public class SplatComparing
{
    /**
     * Compute the squared distance of the positions of the given splats
     * 
     * @param s0 The first splat
     * @param s1 The second splat
     * @return The result
     */
    public static float squaredDistanceByPosition(Splat s0, Splat s1)
    {
        float px0 = s0.getPositionX();
        float py0 = s0.getPositionY();
        float pz0 = s0.getPositionZ();

        float px1 = s1.getPositionX();
        float py1 = s1.getPositionY();
        float pz1 = s1.getPositionZ();

        float dpx = px0 - px1;
        float dpy = py0 - py1;
        float dpz = pz0 - pz1;
        
        float squaredDistance = dpx * dpx + dpy * dpy + dpz * dpz;
        return squaredDistance;
    }

    /**
     * Compute the distance of the positions of the given splats
     * 
     * @param s0 The first splat
     * @param s1 The second splat
     * @return The result
     */
    static float distanceByPosition(Splat s0, Splat s1)
    {
        float dd = squaredDistanceByPosition(s0, s1);
        float d = (float) Math.sqrt(dd);
        return d;
    }

    /**
     * Compare the given splats by their "importance", in ascending order
     * 
     * What this means is totally unspecified for now.
     * 
     * @param s0 The first splat
     * @param s1 The second splat
     * @return The comparison result
     */
    static int compareByImportance(Splat s0, Splat s1)
    {
        float sx0 = (float) Math.exp(s0.getScaleX());
        float sy0 = (float) Math.exp(s0.getScaleY());
        float sz0 = (float) Math.exp(s0.getScaleZ());

        float sx1 = (float) Math.exp(s1.getScaleX());
        float sy1 = (float) Math.exp(s1.getScaleY());
        float sz1 = (float) Math.exp(s1.getScaleZ());

        float volume0 = sx0 * sy0 * sz0;
        float volume1 = sx1 * sy1 * sz1;

        float o0 = s0.getOpacity();
        float o1 = s1.getOpacity();

        float importance0 = volume0 * o0;
        float importance1 = volume1 * o1;

        return Float.compare(importance0, importance1);
    }

    /**
     * Compare the given splats by their "volume", i.e. the product of all their
     * linearized scale factors, in ascending order.
     * 
     * ("Linearized" means that the scale values are assumed to be in
     * logarithmic space, and the actual scale will be computed as exp(scale))
     * 
     * @param s0 The first splat
     * @param s1 The second splat
     * @return The comparison result
     */
    static int compareByVolumeLinear(Splat s0, Splat s1)
    {
        float sx0 = (float) Math.exp(s0.getScaleX());
        float sy0 = (float) Math.exp(s0.getScaleY());
        float sz0 = (float) Math.exp(s0.getScaleZ());

        float sx1 = (float) Math.exp(s1.getScaleX());
        float sy1 = (float) Math.exp(s1.getScaleY());
        float sz1 = (float) Math.exp(s1.getScaleZ());

        float volume0 = sx0 * sy0 * sz0;
        float volume1 = sx1 * sy1 * sz1;
        return Float.compare(volume0, volume1);
    }

    /**
     * Compare the given splats by the maximum of their scale factors, in
     * ascending order.
     * 
     * @param s0 The first splat
     * @param s1 The second splat
     * @return The comparison result
     */
    static int compareByMaxScale(Splat s0, Splat s1)
    {
        float sx0 = s0.getScaleX();
        float sy0 = s0.getScaleY();
        float sz0 = s0.getScaleZ();

        float sx1 = s1.getScaleX();
        float sy1 = s1.getScaleY();
        float sz1 = s1.getScaleZ();

        float maxScale0 = Math.max(sx0, Math.max(sy0, sz0));
        float maxScale1 = Math.max(sx1, Math.max(sy1, sz1));
        return Float.compare(maxScale0, maxScale1);
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatComparing()
    {
        // Private constructor to prevent instantiation
    }
}
