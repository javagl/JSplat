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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.io.GltfModelReader;
import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.Splats;

/**
 * Implementation of a {@link SplatListReader} that reads glTF data with the
 * KHR_gaussian_splatting Gaussian splat extension.
 * 
 * NOTE: This class is preliminary and limited in terms of its capabilities: It
 * assumes a single mesh primitive that contains splat attributes for now, and
 * simply returns the splats from the first primitive.
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
     * Creates a new instance
     */
    public GltfSplatReader()
    {
        // Default constructor
    }

    @Override
    public List<MutableSplat> readList(InputStream inputStream)
        throws IOException
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
                Object extension = extensions.get(NAME);
                if (extension != null)
                {
                    return readListFrom(meshPrimitiveModel);
                }
            }
        }
        throw new IOException(
            "No mesh primitive with Gaussian splats found in input data");
    }

    /**
     * Read a list of splats from the given mesh primitive model, assuming
     * that it contains valid KHR_gaussian_splatting attributes. 
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
        writePositions(positionBuffer, splats);

        FloatBuffer scaleBuffer = readAsFloatBuffer(scaleAccessor);
        writeScales(scaleBuffer, splats);

        FloatBuffer rotationBuffer = readAsFloatBuffer(rotationAccessor);
        writeRotations(rotationBuffer, splats);

        FloatBuffer opacityBuffer = readAsFloatBuffer(opacityAccessor);
        writeOpacities(opacityBuffer, splats);

        int shIndex = 0;
        for (int d = 0; d <= shDegree; d++)
        {
            int numCoefficients = Splats.coefficientsForDegree(d);
            for (int c = 0; c < numCoefficients; c++)
            {
                AccessorModel shAccessor = shAccessors.get(shIndex);
                FloatBuffer shBuffer = readAsFloatBuffer(shAccessor);
                writeSh(shBuffer, splats, d, c);
                shIndex++;
            }
        }

        return splats;
    }

    /**
     * Returns the data from the given accessor model as a float buffer, tightly
     * packed.
     * 
     * @param accessorModel The accessor model
     * @return The buffer
     */
    private static FloatBuffer readAsFloatBuffer(AccessorModel accessorModel)
    {
        AccessorData accessorData = accessorModel.getAccessorData();
        ByteBuffer inputByteBuffer = accessorData.createByteBuffer();
        return inputByteBuffer.asFloatBuffer();
    }

    /**
     * Write the positions from the given buffer with splats.size() * 3 elements
     * into the given splats
     * 
     * @param b The buffer
     * @param splats The splats
     */
    private static void writePositions(FloatBuffer b,
        List<? extends MutableSplat> splats)
    {
        for (int i = 0; i < splats.size(); i++)
        {
            MutableSplat s = splats.get(i);
            s.setPositionX(b.get(i * 3 + 0));
            s.setPositionY(b.get(i * 3 + 1));
            s.setPositionZ(b.get(i * 3 + 2));
        }
    }

    /**
     * Write the scales from the given buffer with splats.size() * 3 elements
     * into the given splats
     * 
     * @param b The buffer
     * @param splats The splats
     */
    private static void writeScales(FloatBuffer b,
        List<? extends MutableSplat> splats)
    {
        for (int i = 0; i < splats.size(); i++)
        {
            MutableSplat s = splats.get(i);
            s.setScaleX(b.get(i * 3 + 0));
            s.setScaleY(b.get(i * 3 + 1));
            s.setScaleZ(b.get(i * 3 + 2));
        }
    }

    /**
     * Write the rotations from the given buffer with splats.size() * 4 elements
     * into the given splats
     * 
     * @param b The buffer
     * @param splats The splats
     */
    private static void writeRotations(FloatBuffer b,
        List<? extends MutableSplat> splats)
    {
        for (int i = 0; i < splats.size(); i++)
        {
            MutableSplat s = splats.get(i);
            s.setRotationX(b.get(i * 4 + 0));
            s.setRotationY(b.get(i * 4 + 1));
            s.setRotationZ(b.get(i * 4 + 2));
            s.setRotationW(b.get(i * 4 + 3));
        }
    }

    /**
     * Write the opacities from the given buffer with splats.size() elements
     * into the given splats
     * 
     * @param b The buffer
     * @param splats The splats
     */
    private static void writeOpacities(FloatBuffer b,
        List<? extends MutableSplat> splats)
    {
        for (int i = 0; i < splats.size(); i++)
        {
            MutableSplat s = splats.get(i);
            s.setOpacity(b.get(i));
        }
    }

    /**
     * Write the specified spherical harmonics from the given buffer with
     * splats.size() * 3 elements into the given splats
     * 
     * @param b The buffer
     * @param splats The splats
     * @param degree The degree
     * @param coefficient The coefficient
     */
    private static void writeSh(FloatBuffer b,
        List<? extends MutableSplat> splats, int degree, int coefficient)
    {
        int index = Splats.dimensionForCoefficient(degree, coefficient);
        for (int i = 0; i < splats.size(); i++)
        {
            MutableSplat s = splats.get(i);
            s.setShX(index, b.get(i * 3 + 0));
            s.setShY(index, b.get(i * 3 + 1));
            s.setShZ(index, b.get(i * 3 + 2));
        }
    }
}
