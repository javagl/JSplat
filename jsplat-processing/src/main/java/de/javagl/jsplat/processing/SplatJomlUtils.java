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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joml.Quaternionf;

import de.javagl.jsplat.Splat;

/**
 * Utility methods for calling the {@link JomlUtils}
 */
class SplatJomlUtils
{
    // That use of Unsafe in JOML was a mistake.
    static
    {
        System.setProperty("joml.nounsafe", "true");
    }

    /**
     * The thread-local {@link JomlUtils} instance
     */
    private static final ThreadLocal<JomlUtils> threadLocalJomlUtils =
        ThreadLocal.withInitial(() -> new JomlUtils());

    /**
     * A thread-local list of quaternions.
     * 
     * These instances may be extended with additional elements in
     * {@link #computeAverageRotation}
     */
    private static final ThreadLocal<List<Quaternionf>> threadLocalQuaternions =
        ThreadLocal.withInitial(() -> new ArrayList<Quaternionf>());

    /**
     * A thread-local list of weights.
     * 
     * These instances may be extended with additional elements in
     * {@link #computeAverageRotation}
     */
    private static final ThreadLocal<List<Float>> threadLocalWeights =
        ThreadLocal.withInitial(() -> new ArrayList<Float>());

    /**
     * Compute the average rotation of the given splats, as a 4-element array
     * containing the average quaternion in scalar-last representation.
     * 
     * @param splats The splats
     * @return The average
     */
    static float[] computeAverageRotation(Collection<? extends Splat> splats)
    {
        JomlUtils jomlUtils = threadLocalJomlUtils.get();
        List<Quaternionf> quaternions = threadLocalQuaternions.get();
        List<Float> weights = threadLocalWeights.get();

        while (quaternions.size() < splats.size())
        {
            quaternions.add(new Quaternionf());
            weights.add(Float.valueOf(1.0f));
        }
        int index = 0;
        for (Splat s : splats)
        {
            Quaternionf q = quaternions.get(index);
            q.x = s.getRotationX();
            q.y = s.getRotationY();
            q.z = s.getRotationZ();
            q.w = s.getRotationW();

            // Pretty random guess: Use the "volume" as the weight.
            // Small splats should not contribute as much as large ones.
            float sx = s.getScaleX();
            float sy = s.getScaleY();
            float sz = s.getScaleZ();
            float weight = sx * sy * sz;
            weights.set(index, weight);

            index++;
        }
        List<Quaternionf> qs = quaternions.subList(0, splats.size());
        List<Float> ws = weights.subList(0, splats.size());
        int maxSvdIterations = 10;
        Quaternionf dest = new Quaternionf();
        jomlUtils.computeWeightedAverage(qs, ws, maxSvdIterations, dest);
        float result[] = new float[]
        { dest.x, dest.y, dest.z, dest.w };
        return result;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatJomlUtils()
    {
        // Private constructor to prevent instantiation
    }

}
