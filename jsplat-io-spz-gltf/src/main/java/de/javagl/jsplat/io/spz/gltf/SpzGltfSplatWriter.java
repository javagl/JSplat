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
package de.javagl.jsplat.io.spz.gltf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.javagl.jgltf.impl.v2.Accessor;
import de.javagl.jgltf.impl.v2.Asset;
import de.javagl.jgltf.impl.v2.Buffer;
import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.impl.v2.MeshPrimitive;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.impl.v2.Scene;
import de.javagl.jgltf.model.GltfConstants;
import de.javagl.jgltf.model.io.GltfAssetWriter;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListWriter;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.io.spz.GaussianCloudSplats;
import de.javagl.jspz.GaussianCloud;
import de.javagl.jspz.SpzWriter;
import de.javagl.jspz.SpzWriters;

/**
 * Implementation of a {@link SplatListWriter} that writes glTF data with
 * SPZ-compressed Gaussian splats.
 */
public final class SpzGltfSplatWriter implements SplatListWriter
{
    /**
     * The base extension name (and attribute prefix)
     */
    private static final String BASE_NAME = "KHR_gaussian_splatting";

    /**
     * The extension name
     */
    private static final String NAME =
        "KHR_gaussian_splatting_compression_spz_2";

    /**
     * Creates a new instance
     */
    public SpzGltfSplatWriter()
    {
        // Default constructor
    }

    @Override
    public void writeList(List<? extends Splat> splats,
        OutputStream outputStream) throws IOException
    {
        SpzWriter spzWriter = SpzWriters.createDefault();
        GaussianCloud gaussianCloud = GaussianCloudSplats.fromSplats(splats);
        ByteArrayOutputStream spzBaos = new ByteArrayOutputStream();
        spzWriter.write(gaussianCloud, spzBaos);

        int numPoints = gaussianCloud.getNumPoints();
        int shDegree = gaussianCloud.getShDegree();
        float[] box = computeBoundingBox(gaussianCloud);

        GltfAssetV2 gltfAsset =
            createGltfAsset(numPoints, shDegree, box, spzBaos.toByteArray());
        GltfAssetWriter w = new GltfAssetWriter();
        w.writeBinary(gltfAsset, outputStream);
    }

    /**
     * Create a binary glTF asset that uses the
     * <code>KHR_gaussian_splatting_compression_spz_2</code> extension to define
     * Gaussian Splats
     * 
     * @param numPoints The number of points
     * @param shDegree The spherical harmonics degree
     * @param boundingBox The bounding box
     * @param spzBytes The SPZ data
     * @return The asset
     */
    private GltfAssetV2 createGltfAsset(int numPoints, int shDegree,
        float boundingBox[], byte spzBytes[])
    {
        // Create the glTF
        GlTF gltf = new GlTF();

        // Add the asset
        Asset asset = new Asset();
        asset.setVersion("2.0");
        gltf.setAsset(asset);

        // Add the POSITION accessor
        Accessor position = new Accessor();
        position.setComponentType(GltfConstants.GL_FLOAT);
        position.setType("VEC3");
        position.setCount(numPoints);
        position.setMin(new Number[]
        { boundingBox[0], boundingBox[1], boundingBox[2] });
        position.setMax(new Number[]
        { boundingBox[3], boundingBox[4], boundingBox[5] });
        gltf.addAccessors(position);

        // Add the COLOR_0 accessor
        Accessor color = new Accessor();
        color.setComponentType(GltfConstants.GL_UNSIGNED_BYTE);
        color.setNormalized(true);
        color.setType("VEC4");
        color.setCount(numPoints);
        gltf.addAccessors(color);

        // Add the _ROTATION accessor
        Accessor rotation = new Accessor();
        rotation.setComponentType(GltfConstants.GL_FLOAT);
        rotation.setType("VEC4");
        rotation.setCount(numPoints);
        gltf.addAccessors(rotation);

        // Add the _SCALE accessor
        Accessor scale = new Accessor();
        scale.setComponentType(GltfConstants.GL_FLOAT);
        scale.setType("VEC3");
        scale.setCount(numPoints);
        gltf.addAccessors(scale);

        // Add the spherical harmonics accessors
        for (int d = 0; d < shDegree; d++)
        {
            int numCoeffs = Splats.coefficientsForDegree(d + 1);
            for (int n = 0; n < numCoeffs; n++)
            {
                Accessor sh = new Accessor();
                sh.setComponentType(GltfConstants.GL_FLOAT);
                sh.setType("VEC3");
                sh.setCount(numPoints);
                gltf.addAccessors(sh);
            }
        }

        // Add the buffer
        Buffer buffer = new Buffer();
        buffer.setByteLength(spzBytes.length);
        gltf.addBuffers(buffer);

        // Add the buffer view
        BufferView bufferView = new BufferView();
        bufferView.setBuffer(0);
        bufferView.setByteLength(spzBytes.length);
        gltf.addBufferViews(bufferView);

        // Create the mesh primitive
        MeshPrimitive primitive = new MeshPrimitive();
        primitive.setMode(GltfConstants.GL_POINTS);

        // Add all accessors to the mesh primitive
        int a = 0;
        primitive.addAttributes("POSITION", a++);
        primitive.addAttributes(BASE_NAME + ":" + "SCALE", a++);
        primitive.addAttributes(BASE_NAME + ":" + "ROTATION", a++);
        primitive.addAttributes(BASE_NAME + ":" + "OPACITY", a++);

        for (int d = 0; d <= shDegree; d++)
        {
            int numCoeffs = Splats.coefficientsForDegree(d);
            for (int n = 0; n < numCoeffs; n++)
            {
                String s = "SH_DEGREE_" + d + "_COEF_" + n;
                primitive.addAttributes(BASE_NAME + ":" + s, a++);
            }
        }

        // Add the extension object to the primitive
        Map<Object, Object> spzExtension = new LinkedHashMap<Object, Object>();
        spzExtension.put("bufferView", 0);

        Map<Object, Object> baseExtension = new LinkedHashMap<Object, Object>();
        Map<Object, Object> innerExtensions =
            new LinkedHashMap<Object, Object>();
        innerExtensions.put(NAME, spzExtension);
        baseExtension.put("extensions", innerExtensions);
        primitive.addExtensions(BASE_NAME, baseExtension);

        // Add the mesh
        Mesh mesh = new Mesh();
        mesh.addPrimitives(primitive);
        gltf.addMeshes(mesh);

        // Add the node
        Node node = new Node();
        node.setMesh(0);
        gltf.addNodes(node);

        // Add the scene
        Scene scene = new Scene();
        scene.addNodes(0);
        gltf.addScenes(scene);
        gltf.setScene(0);

        // Add information about the used/required extension
        gltf.addExtensionsUsed(BASE_NAME);
        gltf.addExtensionsUsed(NAME);

        // Build the actual asset
        ByteBuffer binaryData = ByteBuffer.wrap(spzBytes);
        GltfAssetV2 gltfAsset = new GltfAssetV2(gltf, binaryData);

        return gltfAsset;
    }

    /**
     * Compute the bounding box of the given {@link GaussianCloud}, as
     * <code>minX,minY,minZ,maxX,maxY,maxZ</code>
     * 
     * @param g The {@link GaussianCloud}
     * @return The bounding box
     */
    private static float[] computeBoundingBox(GaussianCloud g)
    {
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;

        int n = g.getNumPoints();
        FloatBuffer positions = g.getPositions();
        for (int i = 0; i < n; i++)
        {
            float x = positions.get(i * 3 + 0);
            float y = positions.get(i * 3 + 1);
            float z = positions.get(i * 3 + 2);
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }
        float[] box = new float[]
        { minX, minY, minZ, maxX, maxY, maxZ };
        return box;
    }

}
