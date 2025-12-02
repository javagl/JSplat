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
import java.io.OutputStream;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.SceneModel;
import de.javagl.jgltf.model.creation.AccessorModels;
import de.javagl.jgltf.model.creation.GltfModelBuilder;
import de.javagl.jgltf.model.creation.MeshPrimitiveBuilder;
import de.javagl.jgltf.model.creation.SceneModels;
import de.javagl.jgltf.model.impl.DefaultAccessorModel;
import de.javagl.jgltf.model.impl.DefaultExtensionsModel;
import de.javagl.jgltf.model.impl.DefaultGltfModel;
import de.javagl.jgltf.model.impl.DefaultMeshPrimitiveModel;
import de.javagl.jgltf.model.io.GltfModelWriter;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatDatas;
import de.javagl.jsplat.SplatListWriter;
import de.javagl.jsplat.Splats;

/**
 * Implementation of a {@link SplatListWriter} that writes glTF data with
 * Gaussian splats using the KHR_gaussian_splatting extension
 * 
 * NOTE: This class is preliminary and limited in terms of its capabilities: It
 * assumes a single mesh primitive that contains splat attributes for now.
 */
public final class GltfSplatWriter implements SplatListWriter
{
    /**
     * The extension name (and attribute prefix)
     */
    private static final String NAME = "KHR_gaussian_splatting";

    /**
     * Creates a new instance
     */
    public GltfSplatWriter()
    {
        // Default constructor
    }

    @Override
    public void writeList(List<? extends Splat> splats,
        OutputStream outputStream) throws IOException
    {
        GltfModel gltfModel = createGltfModel(splats);
        GltfModelWriter w = new GltfModelWriter();
        w.writeBinary(gltfModel, outputStream);
    }

    /**
     * Create a binary glTF asset that uses the
     * <code>KHR_gaussian_splatting</code> extension to define Gaussian Splats
     * 
     * @param splats The splats
     * @return The asset
     */
    private DefaultGltfModel createGltfModel(List<? extends Splat> splats)
    {
        DefaultMeshPrimitiveModel meshPrimitiveModel =
            createMeshPrimitiveModel(splats);

        SceneModel sceneModel =
            SceneModels.createFromMeshPrimitive(meshPrimitiveModel);

        GltfModelBuilder b = GltfModelBuilder.create();
        b.addSceneModel(sceneModel);
        DefaultGltfModel gltfModel = b.build();

        DefaultExtensionsModel extensionsModel = gltfModel.getExtensionsModel();
        extensionsModel.addExtensionsUsed(Arrays.asList(NAME));

        return gltfModel;
    }

    /**
     * Create a mesh primitive model from the given splats, using the attribtes
     * that are defined in the KHR_gaussian_splatting extension
     * 
     * @param splats The splats
     * @return The mesh primitive model
     */
    private static DefaultMeshPrimitiveModel
        createMeshPrimitiveModel(List<? extends Splat> splats)
    {
        MeshPrimitiveBuilder mpb = MeshPrimitiveBuilder.create();

        FloatBuffer position = SplatDatas.readPositions(splats, null);
        DefaultAccessorModel positionAccessor =
            AccessorModels.createFloat3D(position);
        mpb.addAttribute("POSITION", positionAccessor);

        FloatBuffer scale = SplatDatas.readScales(splats, null);
        DefaultAccessorModel scaleAccessor =
            AccessorModels.createFloat3D(scale);
        mpb.addAttribute(NAME + ":" + "SCALE", scaleAccessor);

        FloatBuffer rotation = SplatDatas.readRotations(splats, null);
        DefaultAccessorModel rotationAccessor =
            AccessorModels.createFloat4D(rotation);
        mpb.addAttribute(NAME + ":" + "ROTATION", rotationAccessor);

        FloatBuffer opacity = SplatDatas.readOpacities(splats, null);
        DefaultAccessorModel opacityAccessor =
            AccessorModels.createFloatScalar(opacity);
        mpb.addAttribute(NAME + ":" + "OPACITY", opacityAccessor);

        int shDegree = splats.get(0).getShDegree();
        for (int d = 0; d <= shDegree; d++)
        {
            int numCoefficients = Splats.coefficientsForDegree(d);
            for (int c = 0; c < numCoefficients; c++)
            {
                FloatBuffer sh = SplatDatas.readSh(splats, d, c, null);
                DefaultAccessorModel shAccessor =
                    AccessorModels.createFloat3D(sh);

                String name = NAME + ":" + "SH_DEGREE_" + d + "_COEF_" + c;
                mpb.addAttribute(name, shAccessor);
            }
        }

        mpb.setPoints();

        // Create the mesh primitive
        DefaultMeshPrimitiveModel meshPrimitiveModel = mpb.build();

        // Manually add the extension (there is no model-level representation
        // of this extension yet)
        Map<String, Object> extension = createExtension();
        meshPrimitiveModel.addExtension(NAME, extension);
        return meshPrimitiveModel;
    }

    /**
     * Create a map that represents an unspecified default
     * KHR_gaussian_splatting extension object
     * 
     * @return The extension object
     */
    private static Map<String, Object> createExtension()
    {
        Map<String, Object> extension = new LinkedHashMap<String, Object>();
        extension.put("kernel", "ellipse");
        extension.put("colorSpace", "BT.709-sRGB");
        extension.put("sortingMethod", "cameraDistance");
        extension.put("projection", "perspective");
        return extension;
    }


}
