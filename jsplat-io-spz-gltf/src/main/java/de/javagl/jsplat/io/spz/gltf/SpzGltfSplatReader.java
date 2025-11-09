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

import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.impl.v2.MeshPrimitive;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.GltfAssetReader;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.io.spz.GaussianCloudSplats;
import de.javagl.jspz.GaussianCloud;
import de.javagl.jspz.SpzReader;
import de.javagl.jspz.SpzReaders;

/**
 * Implementation of a {@link SplatListReader} that reads glTF data with the
 * Gaussian splat data using the KHR_gaussian_splatting_compression_spz_2
 * extension.
 */
public final class SpzGltfSplatReader implements SplatListReader
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(SpzGltfSplatReader.class.getName());

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
    public SpzGltfSplatReader()
    {
        // Default constructor
    }

    @Override
    public List<MutableSplat> readList(InputStream inputStream)
        throws IOException
    {
        GltfAssetReader r = new GltfAssetReader();
        GltfAsset gltfAsset = r.readWithoutReferences(inputStream);
        if (!(gltfAsset instanceof GltfAssetV2))
        {
            throw new IOException(
                "Expected glTF version 2, but found " + gltfAsset);
        }
        GltfAssetV2 gltfAssetV2 = (GltfAssetV2) gltfAsset;
        GlTF gltf = gltfAssetV2.getGltf();

        List<Mesh> meshes = gltf.getMeshes();
        for (Mesh mesh : meshes)
        {
            List<MeshPrimitive> primitives = mesh.getPrimitives();
            for (MeshPrimitive primitive : primitives)
            {
                Integer bufferViewIndex =
                    getExtensionBufferViewIndex(primitive);
                if (bufferViewIndex == null)
                {
                    continue;
                }
                return readSplats(gltfAssetV2, bufferViewIndex);
            }
        }
        throw new IOException(
            "No mesh primitive with Gaussian splats found in input data");
    }

    /**
     * Read splats that are stored in SPZ format in the specified buffer view of
     * the given glTF asset.
     * 
     * @param gltfAsset The glTF asset
     * @param bufferViewIndex The buffer view index
     * @return The splats
     * @throws IOException If an IO error occurs
     */
    private static List<MutableSplat> readSplats(GltfAssetV2 gltfAsset,
        int bufferViewIndex) throws IOException
    {
        ByteBuffer spzData = extractBufferViewData(gltfAsset, bufferViewIndex);
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
     * @param gltfAsset The glTF asset
     * @param bufferViewIndex The buffer view index
     * @return The resulting buffer data
     */
    private static ByteBuffer extractBufferViewData(GltfAssetV2 gltfAsset,
        int bufferViewIndex)
    {
        GlTF gltf = gltfAsset.getGltf();
        List<BufferView> bufferViews = gltf.getBufferViews();
        BufferView bufferView = bufferViews.get(bufferViewIndex);
        ByteBuffer binaryData = gltfAsset.getBinaryData();
        Integer byteOffset = bufferView.getByteOffset();
        if (byteOffset == null)
        {
            byteOffset = 0;
        }
        Integer byteLength = bufferView.getByteLength();

        // Workaround for NoSuchMethodError when compiling and
        // using with newer JDKs
        ((Buffer) binaryData).limit(byteOffset + byteLength);
        ((Buffer) binaryData).position(byteOffset);
        ByteBuffer spzData = binaryData.slice();
        return spzData;
    }

    /**
     * Returns the <code>bufferView</code> index for the SPZ data from the given
     * mesh primitive.
     * 
     * This will try to fall back to different legacy extension versions. Not
     * all of these legacy versions may be fully supported...
     * 
     * @param meshPrimitive The {@link MeshPrimitive}
     * @return The buffer view index
     */
    private Integer getExtensionBufferViewIndex(MeshPrimitive meshPrimitive)
    {
        Integer bufferViewIndex =
            getFinalExtensionBufferViewIndex(meshPrimitive);
        if (bufferViewIndex != null)
        {
            return bufferViewIndex;
        }
        return getLegacyExtensionBufferViewIndex(meshPrimitive);
    }

    /**
     * Returns the index of the buffer view that stores the SPZ data, using the
     * "final" form of the extension specification.
     * 
     * If it can not be found, then <code>null</code> is returned.
     * 
     * Details omitted here.
     * 
     * @param meshPrimitive The mesh primitive
     * @return The index
     */
    private static Integer
        getFinalExtensionBufferViewIndex(MeshPrimitive meshPrimitive)
    {
        Map<String, Object> extensions = meshPrimitive.getExtensions();
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
     * @param meshPrimitive The mesh primitive
     * @return The index
     */
    private static Integer
        getLegacyExtensionBufferViewIndex(MeshPrimitive meshPrimitive)
    {
        Map<String, Object> extensions = meshPrimitive.getExtensions();
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
