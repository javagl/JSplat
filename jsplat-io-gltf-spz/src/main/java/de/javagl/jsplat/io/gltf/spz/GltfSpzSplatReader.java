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
package de.javagl.jsplat.io.gltf.spz;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SceneModel;
import de.javagl.jgltf.model.io.GltfModelReader;
import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.io.spz.GaussianCloudSplats;
import de.javagl.jsplat.processing.SplatTransforms;
import de.javagl.jspz.GaussianCloud;
import de.javagl.jspz.SpzReader;
import de.javagl.jspz.SpzReaders;

/**
 * Implementation of a {@link SplatListReader} that reads glTF data with the
 * Gaussian splat data using the KHR_gaussian_splatting_compression_spz_2
 * extension.
 */
public final class GltfSpzSplatReader implements SplatListReader
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(GltfSpzSplatReader.class.getName());

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
    public GltfSpzSplatReader()
    {
        // Default constructor
    }

    @Override
    public List<MutableSplat> readList(InputStream inputStream)
        throws IOException
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
                        Integer bufferViewIndex =
                            getExtensionBufferViewIndex(meshPrimitiveModel);
                        if (bufferViewIndex == null)
                        {
                            continue;
                        }
                        List<MutableSplat> splats =
                            readSplats(gltfModel, bufferViewIndex);
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
     * Read splats that are stored in SPZ format in the specified buffer view of
     * the given glTF model.
     * 
     * @param gltfModel The glTF model
     * @param bufferViewIndex The buffer view index
     * @return The splats
     * @throws IOException If an IO error occurs
     */
    private static List<MutableSplat> readSplats(GltfModel gltfModel,
        int bufferViewIndex) throws IOException
    {
        ByteBuffer spzData = extractBufferViewData(gltfModel, bufferViewIndex);
        ByteBufferInputStream spzInputStream =
            new ByteBufferInputStream(spzData);
        SpzReader spzReader = SpzReaders.createDefault();
        GaussianCloud gaussianGloud = spzReader.read(spzInputStream);
        return GaussianCloudSplats.toSplats(gaussianGloud);

    }

    /**
     * Returns a buffer containing the part of the binary data of the given glTF
     * asset that represents the specified buffer view
     * 
     * @param gltfModel The glTF model
     * @param bufferViewIndex The buffer view index
     * @return The resulting buffer data
     */
    private static ByteBuffer extractBufferViewData(GltfModel gltfModel,
        int bufferViewIndex)
    {
        List<BufferViewModel> bufferViewModels =
            gltfModel.getBufferViewModels();
        BufferViewModel bufferViewModel = bufferViewModels.get(bufferViewIndex);
        return bufferViewModel.getBufferViewData();
    }

    /**
     * Returns the <code>bufferView</code> index for the SPZ data from the given
     * mesh primitive.
     * 
     * This will try to fall back to different legacy extension versions. Not
     * all of these legacy versions may be fully supported...
     * 
     * @param meshPrimitiveModel The {@link MeshPrimitiveModel}
     * @return The buffer view index
     */
    private Integer
        getExtensionBufferViewIndex(MeshPrimitiveModel meshPrimitiveModel)
    {
        Integer bufferViewIndex =
            getFinalExtensionBufferViewIndex(meshPrimitiveModel);
        if (bufferViewIndex != null)
        {
            return bufferViewIndex;
        }
        return getLegacyExtensionBufferViewIndex(meshPrimitiveModel);
    }

    /**
     * Returns the index of the buffer view that stores the SPZ data, using the
     * "final" form of the extension specification.
     * 
     * If it can not be found, then <code>null</code> is returned.
     * 
     * Details omitted here.
     * 
     * @param meshPrimitiveModel The mesh primitive
     * @return The index
     */
    private static Integer
        getFinalExtensionBufferViewIndex(MeshPrimitiveModel meshPrimitiveModel)
    {
        Map<String, Object> extensions = meshPrimitiveModel.getExtensions();
        if (extensions == null)
        {
            return null;
        }
        Map<?, ?> baseExtension = getMapOptional(extensions, BASE_NAME);
        Map<?, ?> baseExtensionExtensions =
            getMapOptional(baseExtension, "extensions");
        Map<?, ?> extension = getMapOptional(baseExtensionExtensions, NAME);
        Integer bufferViewIndex = getIntegerOptional(extension, "bufferView");
        return bufferViewIndex;
    }

    /**
     * Returns the index of the buffer view that stores the SPZ data, using
     * various "legacy" forms of the extension.
     * 
     * If it can not be found, then <code>null</code> is returned.
     * 
     * Details omitted here.
     * 
     * @param meshPrimitiveModel The mesh primitive
     * @return The index
     */
    private static Integer
        getLegacyExtensionBufferViewIndex(MeshPrimitiveModel meshPrimitiveModel)
    {
        Map<String, Object> extensions = meshPrimitiveModel.getExtensions();
        if (extensions == null)
        {
            return null;
        }
        List<String> legacyNames = Arrays.asList("KHR_spz_compression",
            "KHR_spz_gaussian_splats_compression",
            "KHR_gaussian_splatting_spz_compression");
        for (String legacyName : legacyNames)
        {
            Map<?, ?> extension = getMapOptional(extensions, legacyName);
            Integer bufferViewIndex =
                getIntegerOptional(extension, "bufferView");
            if (bufferViewIndex != null)
            {
                logger.warning(
                    "Fetching SPZ data from legacy extension with name "
                        + legacyName + " - this extension version may "
                        + "not be fully supported");
                return bufferViewIndex;
            }
        }
        return null;
    }

    /**
     * Returns the value for the given key from the given map, if that value is
     * a map. If the given map is <code>null</code> or the value is not a map,
     * then <code>null</code> is returned.
     * 
     * It could be worse. It could be JavaScript.
     * 
     * @param map The map
     * @param key The key
     * @return The result
     */
    private static Map<?, ?> getMapOptional(Map<?, ?> map, String key)
    {
        if (map == null)
        {
            return null;
        }
        Object object = map.get(key);
        if (!(object instanceof Map<?, ?>))
        {
            return null;
        }
        Map<?, ?> result = (Map<?, ?>) object;
        return result;
    }

    /**
     * Returns the integer value for the given key from the given map, if that
     * value is a number. If the given map is <code>null</code> or the value is
     * not a number, then <code>null</code> is returned.
     * 
     * It could be worse. It could be JavaScript.
     * 
     * @param map The map
     * @param key The key
     * @return The result
     */
    private static Integer getIntegerOptional(Map<?, ?> map, String key)
    {
        if (map == null)
        {
            return null;
        }
        Object object = map.get(key);
        if (!(object instanceof Number))
        {
            return null;
        }
        Number result = (Number) object;
        return result.intValue();
    }

    /**
     * Implementation of an input stream that reads from a byte buffer
     */
    private static class ByteBufferInputStream extends InputStream
    {
        /**
         * The byte buffer from which this stream is reading
         */
        private final ByteBuffer byteBuffer;

        /**
         * Creates a new instance that read from the given byte buffer. Reading
         * from the stream will increase the position of the given buffer. If
         * this is not desired, a slice of the actual buffer may be passed to
         * this constructor.
         * 
         * @param byteBuffer The byte buffer from which this stream is reading
         */
        ByteBufferInputStream(ByteBuffer byteBuffer)
        {
            this.byteBuffer = Objects.requireNonNull(byteBuffer,
                "The byteBuffer may not be null");
        }

        @Override
        public int read() throws IOException
        {
            if (!byteBuffer.hasRemaining())
            {
                return -1;
            }
            return byteBuffer.get() & 0xFF;
        }

        @Override
        public int read(byte[] bytes, int off, int len) throws IOException
        {
            if (!byteBuffer.hasRemaining())
            {
                return -1;
            }
            int readLength = Math.min(len, byteBuffer.remaining());
            byteBuffer.get(bytes, off, readLength);
            return readLength;
        }
    }

}
