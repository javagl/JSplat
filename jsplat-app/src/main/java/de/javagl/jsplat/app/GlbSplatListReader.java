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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.GltfAssetReader;
import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.io.gltf.GltfSplatReader;
import de.javagl.jsplat.io.gltf.spz.GltfSpzSplatReader;

/**
 * Internal implementation of a SplatListReader for GLB data.
 * 
 * Yeah, it's a bit quirky: It reads the GLB data and creates a glTF asset,
 * checks whether this asset uses the SPZ compression extension, and either
 * dispatches to a {@link GltfSplatReader} or {@link GltfSpzSplatReader}.
 */
class GlbSplatListReader implements SplatListReader
{
    @Override
    public List<MutableSplat> readList(InputStream inputStream)
        throws IOException
    {
        byte data[] = readFully(inputStream);
        boolean usesSpz = usesSpz(data);
        if (usesSpz)
        {
            GltfSpzSplatReader sr = new GltfSpzSplatReader();
            return sr.readList(new ByteArrayInputStream(data));
        }
        GltfSplatReader sr = new GltfSplatReader();
        return sr.readList(new ByteArrayInputStream(data));
    }

    /**
     * Returns whether the given GLB data uses the
     * KHR_gaussian_splatting_compression_spz_2 extension
     * 
     * @param glbData The GLB data
     * @return The result
     */
    private static boolean usesSpz(byte glbData[])
    {
        GltfAssetReader ar = new GltfAssetReader();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(glbData))
        {
            GltfAsset gltfAsset = ar.readWithoutReferences(bais);
            GlTF gltf = (GlTF) gltfAsset.getGltf();
            List<String> extensionsUsed = gltf.getExtensionsUsed();
            if (extensionsUsed == null)
            {
                return false;
            }
            if (extensionsUsed
                .contains("KHR_gaussian_splatting_compression_spz_2"))
            {
                return true;
            }
        }
        catch (IOException e)
        {
            return false;
        }
        return false;
    }

    /**
     * Read the given input stream into a byte array
     * 
     * @param inputStream The input stream
     * @return The byte array
     * @throws IOException If an IO error occurs
     */
    private static byte[] readFully(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = new byte[16384];
        while (true)
        {
            int read = inputStream.read(data, 0, data.length);
            if (read == -1)
            {
                break;
            }
            baos.write(data, 0, read);
        }
        return baos.toByteArray();
    }
}
