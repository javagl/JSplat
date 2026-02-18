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
package de.javagl.jsplat.io.gltf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SceneModel;
import de.javagl.jgltf.model.io.GltfModelReader;
import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatDatas;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.processing.SplatTransforms;

/**
 * Implementation of a {@link SplatListReader} that reads glTF data with the
 * KHR_gaussian_splatting Gaussian splat extension.
 * 
 * NOTE: This class is preliminary
 */
public final class GltfSplatReader implements SplatListReader
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(GltfSplatReader.class.getName());

    /**
     * The extension name (and attribute prefix)
     */
    private static final String NAME = "KHR_gaussian_splatting";

    /**
     * Whether all splats from all mesh primitives should be read, transformed
     * according to the global transform matrix of the respective node, and
     * added the the resulting list.
     * 
     * If this is <code>false</code>, then only the splats from the first mesh
     * primitive that contains splats will be considered, and returned
     * untransformed.
     */
    private final boolean readAll;

    /**
     * Creates a new instance
     */
    public GltfSplatReader()
    {
        this(true);
    }

    /**
     * Creates a new instance
     * 
     * @param readAll Whether all splats from all mesh primitives should be
     *        read, transformed according to the global transform matrix of the
     *        respective node, and added the the resulting list
     */
    public GltfSplatReader(boolean readAll)
    {
        this.readAll = readAll;
    }

    @Override
    public List<MutableSplat> readList(InputStream inputStream)
        throws IOException
    {
        if (readAll)
        {
            return readAllTransformed(inputStream);
        }
        return readFirstUntransformed(inputStream);
    }

    /**
     * Read all splats that are contained in the glTF that is read from the
     * given input stream, transforming them according to the global transform
     * of the node that they are attached to, and return them all in a single
     * list.
     * 
     * @param inputStream The input stream
     * @return The result
     * @throws IOException If an IO error occurs
     */
    private static List<MutableSplat>
        readAllTransformed(InputStream inputStream) throws IOException
    {
        GltfModelReader r = new GltfModelReader();
        GltfModel gltfModel = r.readWithoutReferences(inputStream);

        List<MutableSplat> allSplats = new ArrayList<MutableSplat>();

        List<SceneModel> sceneModels = gltfModel.getSceneModels();
        for (SceneModel sceneModel : sceneModels)
        {
            List<NodeModel> nodeModels = sceneModel.getNodeModels();
            for (NodeModel nodeModel : nodeModels)
            {
                float[] globalTransform =
                    nodeModel.computeGlobalTransform(null);
                List<MeshModel> meshModels = nodeModel.getMeshModels();
                for (MeshModel meshModel : meshModels)
                {
                    List<MeshPrimitiveModel> meshPrimitiveModels =
                        meshModel.getMeshPrimitiveModels();
                    for (MeshPrimitiveModel meshPrimitiveModel : meshPrimitiveModels)
                    {
                        Map<String, Object> extensions =
                            meshPrimitiveModel.getExtensions();
                        if (extensions != null)
                        {
                            Object extension = extensions.get(NAME);
                            if (extension != null)
                            {
                                List<MutableSplat> splats =
                                    readListFrom(meshPrimitiveModel);
                                if (splats != null && !splats.isEmpty())
                                {
                                    SplatTransforms.transformList(splats,
                                        globalTransform);
                                    allSplats = merge(allSplats, splats);
                                }
                            }
                        }
                    }
                }
            }
        }
        return allSplats;
    }

    /**
     * Merge the given lists of splats and return the result.
     * 
     * If the existing splats and the new splats have different degrees, then a
     * new list will be created that contains splats with the larger of both
     * degrees, initialized based on the given splats.
     * 
     * Otherwise, the given added splats will be added to the list of all
     * splats, and the result will be returned.
     * 
     * @param all All existing splats
     * @param added The added splats
     * @return The result
     */
    private static List<MutableSplat> merge(List<MutableSplat> all,
        List<MutableSplat> added)
    {
        // Handle trivial cases
        if (all.isEmpty())
        {
            all.addAll(added);
            return all;
        }
        if (added.isEmpty())
        {
            return all;
        }

        // When they have the same degree, just add them
        Splat oldSplat = all.get(0);
        Splat newSplat = added.get(0);
        if (oldSplat.getShDegree() == newSplat.getShDegree())
        {
            all.addAll(added);
            return all;
        }

        // Create a new list, and fill them with copies of the given
        // splats, with the copies having the larger of both degrees
        int newDegree =
            Math.max(oldSplat.getShDegree(), newSplat.getShDegree());
        List<MutableSplat> merged = new ArrayList<MutableSplat>();
        for (int i = 0; i < all.size(); i++)
        {
            Splat s = all.get(i);
            MutableSplat t = Splats.create(newDegree);
            Splats.setAny(s, t);
            merged.add(t);
        }
        for (int i = 0; i < added.size(); i++)
        {
            Splat s = added.get(i);
            MutableSplat t = Splats.create(newDegree);
            Splats.setAny(s, t);
            merged.add(t);
        }
        return merged;
    }

    /**
     * Read the splats from the first mesh primitive that contains splats, and
     * return them as a list (ignoring the transform of the node that the mesh
     * may be attached to)
     * 
     * @param inputStream The input stream
     * @return The result
     * @throws IOException If an IO error occurs
     */
    private static List<MutableSplat>
        readFirstUntransformed(InputStream inputStream) throws IOException
    {
        GltfModelReader r = new GltfModelReader();
        GltfModel gltfModel = r.readWithoutReferences(inputStream);

        List<MeshModel> meshModels = gltfModel.getMeshModels();
        for (MeshModel meshModel : meshModels)
        {
            List<MeshPrimitiveModel> meshPrimitiveModels =
                meshModel.getMeshPrimitiveModels();
            for (MeshPrimitiveModel meshPrimitiveModel : meshPrimitiveModels)
            {
                Map<String, Object> extensions =
                    meshPrimitiveModel.getExtensions();
                if (extensions != null)
                {
                    Object extension = extensions.get(NAME);
                    if (extension != null)
                    {
                        return readListFrom(meshPrimitiveModel);
                    }
                }
            }
        }
        throw new IOException(
            "No mesh primitive with Gaussian splats found in input data");
    }

    /**
     * Read a list of splats from the given mesh primitive model, assuming that
     * it contains valid KHR_gaussian_splatting attributes.
     * 
     * @param meshPrimitiveModel The mesh primitive model
     * @return The splats
     */
    private static List<MutableSplat>
        readListFrom(MeshPrimitiveModel meshPrimitiveModel)
    {
        Map<String, AccessorModel> attributes =
            meshPrimitiveModel.getAttributes();

        String positionName = "POSITION";
        AccessorModel positionAccessor = attributes.get(positionName);
        if (positionAccessor == null)
        {
            logger.severe("No POSITION accessor found in mesh primitive");
            return Collections.emptyList();
        }

        String scaleName = NAME + ":" + "SCALE";
        AccessorModel scaleAccessor = attributes.get(scaleName);
        if (scaleAccessor == null)
        {
            logger.severe(
                "No " + scaleName + " accessor found in mesh primitive");
            return Collections.emptyList();
        }

        String rotationName = NAME + ":" + "ROTATION";
        AccessorModel rotationAccessor = attributes.get(rotationName);
        if (rotationAccessor == null)
        {
            logger.severe(
                "No " + rotationName + " accessor found in mesh primitive");
            return Collections.emptyList();
        }

        String opacityName = NAME + ":" + "OPACITY";
        AccessorModel opacityAccessor = attributes.get(opacityName);
        if (opacityAccessor == null)
        {
            logger.severe(
                "No " + opacityName + " accessor found in mesh primitive");
            return Collections.emptyList();
        }

        List<AccessorModel> shAccessors = new ArrayList<AccessorModel>();
        int maxDegrees = 4;
        int shDegree = 0;
        for (int d = 0; d < maxDegrees; d++)
        {
            int numCoefficients = Splats.coefficientsForDegree(d);
            for (int c = 0; c < numCoefficients; c++)
            {
                String name = NAME + ":" + "SH_DEGREE_" + d + "_COEF_" + c;
                AccessorModel shAccessor = attributes.get(name);
                if (shAccessor != null)
                {
                    shAccessors.add(shAccessor);
                    shDegree = d;
                }
            }
        }

        // There are no sanity checks here. It simply assumes that all the
        // accessors have the same counts. Leave that to the validator...
        int count = positionAccessor.getCount();
        List<MutableSplat> splats = new ArrayList<MutableSplat>();
        for (int i = 0; i < count; i++)
        {
            MutableSplat splat = Splats.create(shDegree);
            splats.add(splat);
        }

        FloatBuffer positionBuffer = readAsFloatBuffer(positionAccessor);
        SplatDatas.writePositions(positionBuffer, splats);

        FloatBuffer scaleBuffer = readAsFloatBuffer(scaleAccessor);
        SplatDatas.writeScales(scaleBuffer, splats);

        FloatBuffer rotationBuffer = readAsFloatBuffer(rotationAccessor);
        SplatDatas.writeRotations(rotationBuffer, splats);

        // The opacity values are in [0,1] in glTF, so they are written
        // as alpha values to the splats here (converting them to an
        // opacity value in [-Inf,+Inf]).
        FloatBuffer opacityBuffer = readAsFloatBuffer(opacityAccessor);
        SplatDatas.writeAlphas(opacityBuffer, splats);

        int shIndex = 0;
        for (int d = 0; d <= shDegree; d++)
        {
            int numCoefficients = Splats.coefficientsForDegree(d);
            for (int c = 0; c < numCoefficients; c++)
            {
                AccessorModel shAccessor = shAccessors.get(shIndex);
                FloatBuffer shBuffer = readAsFloatBuffer(shAccessor);
                SplatDatas.writeSh(shBuffer, splats, d, c);
                shIndex++;
            }
        }

        return splats;
    }

    /**
     * Returns the data from the given accessor model as a float buffer, tightly
     * packed, applying dequantization as necessary.
     * 
     * @param accessorModel The accessor model
     * @return The buffer
     * @throws IllegalArgumentException If the component type of the given
     *         accessor model is neither float, nor signed/unsigned byte/short.
     */
    private static FloatBuffer readAsFloatBuffer(AccessorModel accessorModel)
    {
        return Quantization.readAsFloatBuffer(accessorModel);
    }

}
