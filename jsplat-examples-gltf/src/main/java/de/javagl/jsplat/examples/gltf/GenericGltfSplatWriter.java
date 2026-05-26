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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.FloatBuffer;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.AnimationModel.Interpolation;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.creation.AccessorModels;
import de.javagl.jgltf.model.creation.GltfModelBuilder;
import de.javagl.jgltf.model.impl.DefaultAccessorModel;
import de.javagl.jgltf.model.impl.DefaultAnimationModel;
import de.javagl.jgltf.model.impl.DefaultExtensionsModel;
import de.javagl.jgltf.model.impl.DefaultGltfModel;
import de.javagl.jgltf.model.impl.DefaultMeshModel;
import de.javagl.jgltf.model.impl.DefaultMeshPrimitiveModel;
import de.javagl.jgltf.model.impl.DefaultNodeModel;
import de.javagl.jgltf.model.impl.DefaultSceneModel;
import de.javagl.jgltf.model.io.GltfModelWriter;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.io.gltf.GltfSplatWriter;

/**
 * A class that can write glTF assets with one or more mesh primitives that use
 * the KHR_gaussian_splatting extension.
 * 
 * NOTE: This class is preliminary
 */
class GenericGltfSplatWriter
{
    /**
     * The extension name (and attribute prefix)
     */
    private static final String NAME = "KHR_gaussian_splatting";

    /**
     * The default color space for the extension objects
     */
    private static final String DEFAULT_COLOR_SPACE = "srgb_rec709_display";

    /**
     * Key for mesh lookups, consisting of a list of splats and the color space
     */
    class MeshKey extends SimpleEntry<List<? extends Splat>, String>
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = 1L;

        /**
         * Creates a new instance
         * 
         * @param key The key
         * @param value The value
         */
        public MeshKey(List<? extends Splat> key, String value)
        {
            super(key, value);
        }
    }

    /**
     * An instantiation of a splat primitive mesh
     */
    private static class Instantiation
    {
        /**
         * The matrix for the node
         */
        private double[] matrix;

        /**
         * The animation for the node
         */
        private SplatAnimation splatAnimation;
    }

    /**
     * The mapping of keys to the instantiations
     */
    private final Map<MeshKey, List<Instantiation>> instantiations;

    /**
     * The list of mesh primitives that have been added
     */
    private final List<MeshPrimitiveModel> meshPrimitiveModels;

    /**
     * The list of matrices that are associated with the mesh primitives
     */
    private final List<double[]> meshPrimitiveMatrices;

    /**
     * Creates a new instance, using an unspecified default color space
     */
    public GenericGltfSplatWriter()
    {
        instantiations = new LinkedHashMap<MeshKey, List<Instantiation>>();
        meshPrimitiveModels = new ArrayList<MeshPrimitiveModel>();
        meshPrimitiveMatrices = new ArrayList<double[]>();
    }

    /**
     * Experimental
     * 
     * @param meshPrimitiveModel Do not use
     * @param matrix Do not use
     */
    public void addMeshPrimitive(MeshPrimitiveModel meshPrimitiveModel,
        double matrix[])
    {
        Objects.requireNonNull(meshPrimitiveModel,
            "The meshPrimitiveModel may not be null");
        meshPrimitiveModels.add(meshPrimitiveModel);
        if (matrix != null)
        {
            if (matrix.length != 16)
            {
                throw new IllegalArgumentException(
                    "Expected matrix to have a length of 16, "
                        + "but it has a length of " + matrix.length);
            }
        }
        meshPrimitiveMatrices.add(matrix);
    }

    /**
     * Add the given splats to this instance, using an unspecified default color
     * space.
     * 
     * The given splats will be contained in one mesh primitive that is part of
     * a mesh that is attached to a node that has the given matrix.
     * 
     * @param splats The splats
     * @param matrix An optional matrix
     * @throws IllegalArgumentException If the given matrix is not
     *         <code>null</code> and has a length that is not 16
     */
    public void addSplats(List<? extends Splat> splats, double matrix[])
    {
        addSplats(splats, matrix, DEFAULT_COLOR_SPACE);
    }

    /**
     * Add the given splats to this instance.
     * 
     * The given splats will be contained in one mesh primitive that is part of
     * a mesh that is attached to a node that has the given matrix.
     * 
     * @param splats The splats
     * @param matrix An optional matrix
     * @param colorSpace The color space for the splat extension
     * @throws IllegalArgumentException If the given matrix is not
     *         <code>null</code> and has a length that is not 16
     */
    public void addSplats(List<? extends Splat> splats, double matrix[],
        String colorSpace)
    {
        Objects.requireNonNull(splats, "The splats may not be null");
        if (matrix != null)
        {
            if (matrix.length != 16)
            {
                throw new IllegalArgumentException(
                    "Expected matrix to have a length of 16, "
                        + "but it has a length of " + matrix.length);
            }
        }

        Instantiation instantiation = new Instantiation();
        instantiation.matrix = matrix == null ? null : matrix.clone();

        MeshKey meshKey = new MeshKey(splats, colorSpace);
        List<Instantiation> list = instantiations.computeIfAbsent(meshKey,
            (k) -> new ArrayList<Instantiation>());
        list.add(instantiation);
    }

    /**
     * Add the given splats to this instance, using an unspecified default color
     * space.
     * 
     * The given splats will be contained in one mesh primitive that is part of
     * a mesh that is attached to a node that has the given animation.
     * 
     * @param splats The splats
     * @param splatAnimation The animation
     * @throws IllegalArgumentException If the given matrix is not
     *         <code>null</code> and has a length that is not 16
     */
    public void addAnimatedSplats(List<? extends Splat> splats,
        SplatAnimation splatAnimation)
    {
        addAnimatedSplats(splats, splatAnimation, DEFAULT_COLOR_SPACE);
    }

    /**
     * Add the given splats to this instance.
     * 
     * The given splats will be contained in one mesh primitive that is part of
     * a mesh that is attached to a node that has the given animation.
     * 
     * @param splats The splats
     * @param splatAnimation The animation
     * @param colorSpace The color space for the splat extension
     * @throws IllegalArgumentException If the given matrix is not
     *         <code>null</code> and has a length that is not 16
     */
    public void addAnimatedSplats(List<? extends Splat> splats,
        SplatAnimation splatAnimation, String colorSpace)
    {
        Objects.requireNonNull(splats, "The splats may not be null");

        Instantiation instantiation = new Instantiation();
        instantiation.splatAnimation = splatAnimation;

        MeshKey meshKey = new MeshKey(splats, colorSpace);
        List<Instantiation> list = instantiations.computeIfAbsent(meshKey,
            (k) -> new ArrayList<Instantiation>());
        list.add(instantiation);
    }

    /**
     * Write the glTF asset, as a binary glTF (GLB) file to the given output
     * stream.
     * 
     * @param outputStream The output stream
     * @throws IOException If an IO error occurs
     */
    public void write(OutputStream outputStream) throws IOException
    {
        DefaultSceneModel sceneModel = new DefaultSceneModel();

        // Create a mapping from the mesh keys to the respective mesh
        // models, each containing one mesh primitive with the
        // splat extension
        Map<MeshKey, DefaultMeshModel> splatMeshModels =
            new LinkedHashMap<MeshKey, DefaultMeshModel>();
        for (MeshKey meshKey : instantiations.keySet())
        {
            List<? extends Splat> splats = meshKey.getKey();
            String colorSpace = meshKey.getValue();

            DefaultMeshPrimitiveModel meshPrimitiveModel =
                GltfSplatWriter.createMeshPrimitiveModel(splats);

            // Manually add the extension (there is no model-level
            // representation of this extension yet)
            Map<String, Object> extension =
                GltfSplatWriter.createExtension(colorSpace);
            meshPrimitiveModel.addExtension(NAME, extension);

            DefaultMeshModel meshModel = new DefaultMeshModel();
            meshModel.addMeshPrimitiveModel(meshPrimitiveModel);

            splatMeshModels.put(meshKey, meshModel);
        }

        // For each "instantiation", create a node with the corresponding
        // matrix or animation, and attach the corresponding splat mesh
        // to that node
        List<DefaultAnimationModel> animationModels =
            new ArrayList<DefaultAnimationModel>();
        for (Entry<MeshKey, List<Instantiation>> entry : instantiations
            .entrySet())
        {
            MeshKey meshKey = entry.getKey();
            DefaultMeshModel meshModel = splatMeshModels.get(meshKey);

            List<Instantiation> instantiationsList = entry.getValue();
            for (Instantiation instantiation : instantiationsList)
            {
                SplatAnimation splatAnimation = instantiation.splatAnimation;
                if (splatAnimation != null)
                {
                    DefaultNodeModel nodeModel = new DefaultNodeModel();
                    nodeModel.addMeshModel(meshModel);
                    sceneModel.addNode(nodeModel);
                    DefaultAnimationModel animationModel =
                        createAnimationModel(nodeModel, splatAnimation);
                    if (animationModel != null)
                    {
                        animationModels.add(animationModel);
                    }
                }
                else
                {
                    double matrix[] = instantiation.matrix;
                    DefaultNodeModel nodeModel = new DefaultNodeModel();
                    nodeModel.setMatrix(toFloat(matrix));
                    nodeModel.addMeshModel(meshModel);
                    sceneModel.addNode(nodeModel);
                }
            }
        }

        // Add the mesh primitives that have been added manually
        for (int i = 0; i < meshPrimitiveModels.size(); i++)
        {
            MeshPrimitiveModel meshPrimitiveModel = meshPrimitiveModels.get(i);
            double matrix[] = meshPrimitiveMatrices.get(i);

            DefaultMeshModel meshModel = new DefaultMeshModel();
            meshModel.addMeshPrimitiveModel(meshPrimitiveModel);

            DefaultNodeModel nodeModel = new DefaultNodeModel();
            nodeModel.setMatrix(toFloat(matrix));
            nodeModel.addMeshModel(meshModel);
            sceneModel.addNode(nodeModel);
        }

        GltfModelBuilder b = GltfModelBuilder.create();
        b.addSceneModel(sceneModel);
        for (AnimationModel animationModel : animationModels)
        {
            b.addAnimationModel(animationModel);
        }
        DefaultGltfModel gltfModel = b.build();

        DefaultExtensionsModel extensionsModel = gltfModel.getExtensionsModel();
        extensionsModel.addExtensionsUsed(Arrays.asList(NAME));

        GltfModelWriter w = new GltfModelWriter();
        w.writeBinary(gltfModel, outputStream);
    }

    /**
     * Create an animation model from the given data
     * @param nodeModel The node model
     * @param splatAnimation The splat animation
     * @return The animation
     */
    private static DefaultAnimationModel
        createAnimationModel(NodeModel nodeModel, SplatAnimation splatAnimation)
    {
        float times[] = toFloat(splatAnimation.times);
        if (splatAnimation.translations != null)
        {
            float translations[] = toFloat1D(splatAnimation.translations);
            return createTranslationAnimationModel(nodeModel, times,
                translations);
        }
        if (splatAnimation.rotations != null)
        {
            float rotations[] = toFloat1D(splatAnimation.rotations);
            return createRotationAnimationModel(nodeModel, times,
                rotations);
        }
        if (splatAnimation.scales != null)
        {
            float scales[] = toFloat1D(splatAnimation.scales);
            return createScaleAnimationModel(nodeModel, times,
                scales);
        }
        return null;
    }

    /**
     * Create an animation model from the given data
     * 
     * @param nodeModel The node
     * @param times The times
     * @param translations The translations
     * @return The animation
     */
    private static DefaultAnimationModel createTranslationAnimationModel(
        NodeModel nodeModel, float times[], float translations[])
    {
        DefaultAccessorModel timesAccessorModel =
            AccessorModels.createFloatScalar(FloatBuffer.wrap(times));
        DefaultAccessorModel valuesAccessorModel =
            AccessorModels.createFloat3D(FloatBuffer.wrap(translations));

        DefaultAnimationModel animationModel = new DefaultAnimationModel();
        DefaultAnimationModel.DefaultSampler sampler =
            new DefaultAnimationModel.DefaultSampler(timesAccessorModel,
                Interpolation.LINEAR, valuesAccessorModel);
        DefaultAnimationModel.DefaultChannel channel =
            new DefaultAnimationModel.DefaultChannel(sampler, nodeModel,
                "translation");
        animationModel.addChannel(channel);

        return animationModel;
    }
    
    /**
     * Create an animation model from the given data
     * 
     * @param nodeModel The node
     * @param times The times
     * @param rotations The rotations
     * @return The animation
     */
    private static DefaultAnimationModel createRotationAnimationModel(
        NodeModel nodeModel, float times[], float rotations[])
    {
        DefaultAccessorModel timesAccessorModel =
            AccessorModels.createFloatScalar(FloatBuffer.wrap(times));
        DefaultAccessorModel valuesAccessorModel =
            AccessorModels.createFloat4D(FloatBuffer.wrap(rotations));

        DefaultAnimationModel animationModel = new DefaultAnimationModel();
        DefaultAnimationModel.DefaultSampler sampler =
            new DefaultAnimationModel.DefaultSampler(timesAccessorModel,
                Interpolation.LINEAR, valuesAccessorModel);
        DefaultAnimationModel.DefaultChannel channel =
            new DefaultAnimationModel.DefaultChannel(sampler, nodeModel,
                "rotation");
        animationModel.addChannel(channel);

        return animationModel;
    }
    
    /**
     * Create an animation model from the given data
     * 
     * @param nodeModel The node
     * @param times The times
     * @param scales The scales
     * @return The animation
     */
    private static DefaultAnimationModel createScaleAnimationModel(
        NodeModel nodeModel, float times[], float scales[])
    {
        DefaultAccessorModel timesAccessorModel =
            AccessorModels.createFloatScalar(FloatBuffer.wrap(times));
        DefaultAccessorModel valuesAccessorModel =
            AccessorModels.createFloat3D(FloatBuffer.wrap(scales));

        DefaultAnimationModel animationModel = new DefaultAnimationModel();
        DefaultAnimationModel.DefaultSampler sampler =
            new DefaultAnimationModel.DefaultSampler(timesAccessorModel,
                Interpolation.LINEAR, valuesAccessorModel);
        DefaultAnimationModel.DefaultChannel channel =
            new DefaultAnimationModel.DefaultChannel(sampler, nodeModel,
                "scale");
        animationModel.addChannel(channel);

        return animationModel;
    }

    /**
     * Convert the given array into a float array
     * 
     * @param array The array
     * @return The result
     */
    private static float[] toFloat(double array[])
    {
        if (array == null)
        {
            return null;
        }
        float result[] = new float[array.length];
        for (int i = 0; i < array.length; i++)
        {
            result[i] = (float) array[i];
        }
        return result;
    }

    /**
     * Convert the given array into a 1D float array
     * 
     * @param array The array
     * @return The result
     */
    private static float[] toFloat1D(double array[][])
    {
        if (array == null)
        {
            return null;
        }
        int length = 0;
        for (int i = 0; i < array.length; i++)
        {
            length += array[i].length;
        }
        float result[] = new float[length];
        int index = 0;
        for (int i = 0; i < array.length; i++)
        {
            double e[] = array[i];
            for (int j = 0; j < e.length; j++)
            {
                result[index++] = (float) e[j];
            }
        }
        return result;
    }

}
