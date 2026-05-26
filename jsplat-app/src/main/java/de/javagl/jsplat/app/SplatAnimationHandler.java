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
package de.javagl.jsplat.app;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import de.javagl.jgltf.model.GltfAnimations;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.animation.Animation;
import de.javagl.jgltf.model.animation.AnimationManager;
import de.javagl.jgltf.model.animation.AnimationManager.AnimationPolicy;
import de.javagl.jgltf.model.animation.AnimationRunner;

/**
 * HIGHLY preliminary, internal class for handling splats that are attached to
 * animated glTF nodes.
 */
class SplatAnimationHandler
{
    /**
     * EXPERIMENTAL: Handle the animations in the given model
     * 
     * @param gltfModel The model
     * @param nodeDataSets Mapping from nodes to splat data sets
     * @param callback A callback for updates
     */
    public static void handleAnimation(GltfModel gltfModel,
        Map<NodeModel, DataSet> nodeDataSets, Runnable callback)
    {
        List<Animation> animations = GltfAnimations
            .createModelAnimations(gltfModel.getAnimationModels());
        if (animations.isEmpty())
        {
            return;
        }

        long updateNanos = 50 * 1000 * 1000;
        AtomicLong previousUpdateNs = new AtomicLong();
        AnimationManager animationManager =
            GltfAnimations.createAnimationManager(AnimationPolicy.LOOP);
        animationManager.addAnimationManagerListener(a ->
        {
            long p = previousUpdateNs.get();
            long c = System.nanoTime();
            if (c < p + updateNanos)
            {
                return;
            }
            previousUpdateNs.set(c);
            for (Entry<NodeModel, DataSet> entry : nodeDataSets.entrySet())
            {
                NodeModel nodeModel = entry.getKey();
                DataSet dataSet = entry.getValue();
                float[] transformMatrix = new float[16];
                nodeModel.computeGlobalTransform(transformMatrix);
                dataSet.setTransformMatrixInternal(toDouble(transformMatrix));
            }
            callback.run();
        });
        AnimationRunner animationRunner = new AnimationRunner(animationManager);
        animationManager.addAnimations(animations);
        animationRunner.start();
    }

    /**
     * Convert the given array into a double array
     * 
     * @param array The array
     * @return The result
     */
    private static double[] toDouble(float array[])
    {
        if (array == null)
        {
            return null;
        }
        double result[] = new double[array.length];
        for (int i = 0; i < array.length; i++)
        {
            result[i] = array[i];
        }
        return result;
    }

}
