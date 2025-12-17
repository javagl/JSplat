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
package de.javagl.jsplat.io.sog;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.io.sog.meta.Means;
import de.javagl.jsplat.io.sog.meta.Meta;
import de.javagl.jsplat.io.sog.meta.Scales;
import de.javagl.jsplat.io.sog.meta.Sh0;
import de.javagl.jsplat.io.sog.meta.ShN;

/**
 * Implementation of a {@link SplatListReader} that reads SOG data
 * 
 * TODO The image access functions might do some color conversion. Everything
 * ~"looks right", but maybe the data should be accessed in its raw form (using
 * the DataBuffer structures). This could also allow more efficient access.
 */
public final class SogSplatReader implements SplatListReader
{
    /**
     * Creates a new instance
     */
    public SogSplatReader()
    {
        // Default constructor
    }

    @Override
    public List<MutableSplat> readList(InputStream inputStream)
        throws IOException
    {
        List<MutableSplat> result = null;
        File tempFile = createTempFile(inputStream);
        try
        {
            result = readListFromFile(tempFile);
        }
        finally
        {
            tempFile.delete();
        }
        return result;
    }

    /**
     * Create a temporary file with the same contents as the given input stream,
     * and return it. The caller is responsible for deleting the file.
     * 
     * @param inputStream The input stream
     * @return The file
     * @throws IOException If an IO error occurs
     */
    private static File createTempFile(InputStream inputStream)
        throws IOException
    {
        // Work around "only DEFLATED entries can have EXT descriptor"
        Path tempFilePath = Files.createTempFile("SogSplatReader", "sog");
        File tempFile = tempFilePath.toFile();
        FileOutputStream fos = new FileOutputStream(tempFile);
        byte buffer[] = new byte[1024];
        while (true)
        {
            int read = inputStream.read(buffer);
            if (read == -1)
            {
                break;
            }
            fos.write(buffer, 0, read);
        }
        fos.close();
        return tempFile;
    }

    /**
     * Read the list of splats from the given file
     * 
     * @param file The file
     * @return The list of splats
     * @throws IOException If an IO error occurs
     */
    private static List<MutableSplat> readListFromFile(File file)
        throws IOException
    {
        Map<String, ZipEntry> entryMap = new LinkedHashMap<String, ZipEntry>();
        try (ZipFile zipFile = new ZipFile(file))
        {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory())
                {
                    continue;
                }
                entryMap.put(entry.getName(), entry);
            }
            SogData sogData = readSogData(zipFile, entryMap);
            return convertSogData(sogData);
        }
    }

    /**
     * Read the {@link SogData} from the given data
     * 
     * @param zipFile The ZIP file
     * @param entryMap The mapping from file names to ZIP entries
     * @return The {@link SogData}
     * @throws IOException If an IO error occurs
     */
    private static SogData readSogData(ZipFile zipFile,
        Map<String, ZipEntry> entryMap) throws IOException
    {
        if (!entryMap.containsKey("meta.json"))
        {
            throw new IOException(
                "The meta.json was not found in the SOG data");
        }
        ZipEntry metaJsonEntry = entryMap.get("meta.json");
        try (InputStream is = zipFile.getInputStream(metaJsonEntry))
        {
            ObjectMapper om = JacksonUtils.createObjectMapper();
            Meta meta = om.readValue(is, Meta.class);
            if (meta.version != 2)
            {
                throw new IOException("Only SOG version 2 is supported. "
                    + "Found version " + meta.version);
            }
            return readSogData(zipFile, entryMap, meta);
        }
    }

    /**
     * Read the {@link SogData} from the given input data
     * 
     * @param zipFile The ZIP file
     * @param entryMap The mapping from file names to ZIP entries
     * @param meta The {@link Meta} object
     * @return The {@link SogData}
     * @throws IOException If an IO error occurs
     */
    private static SogData readSogData(ZipFile zipFile,
        Map<String, ZipEntry> entryMap, Meta meta) throws IOException
    {
        BufferedImage[] means =
            readImages(zipFile, entryMap, "means", meta.means.files, 2);
        BufferedImage[] scales =
            readImages(zipFile, entryMap, "scales", meta.scales.files, 1);
        BufferedImage[] quats =
            readImages(zipFile, entryMap, "quats", meta.quats.files, 1);
        BufferedImage[] sh0 =
            readImages(zipFile, entryMap, "sh0", meta.sh0.files, 1);
        BufferedImage[] shN = null;
        if (meta.shN != null)
        {
            shN = readImages(zipFile, entryMap, "shN", meta.shN.files, 2);
        }

        SogData sogData = new SogData();
        sogData.meta = meta;

        List<String> meansFiles = Arrays.asList(meta.means.files);
        int indexL = meansFiles.indexOf("means_l.webp");
        if (indexL == -1)
        {
            throw new IOException(
                "The means_l.webp was not found in the SOG data");
        }
        int indexU = meansFiles.indexOf("means_u.webp");
        if (indexU == -1)
        {
            throw new IOException(
                "The means_u.webp was not found in the SOG data");
        }
        sogData.meansL = means[indexL];
        sogData.meansU = means[indexU];

        sogData.scales = scales[0];
        sogData.quats = quats[0];
        sogData.sh0 = sh0[0];

        if (shN != null)
        {
            List<String> shNFiles = Arrays.asList(meta.shN.files);
            int indexSL = shNFiles.indexOf("shN_labels.webp");
            if (indexSL == -1)
            {
                throw new IOException(
                    "The shN_labels.webp was not found in the SOG data");
            }
            int indexSC = shNFiles.indexOf("shN_centroids.webp");
            if (indexSC == -1)
            {
                throw new IOException(
                    "The shN_centroids.webp was not found in the SOG data");
            }
            sogData.shNLabels = shN[indexSL];
            sogData.shNCentroids = shN[indexSC];
        }
        return sogData;
    }

    /**
     * Read WEBP images with the given file names from the given input data
     * 
     * @param zipFile The ZIP file
     * @param entryMap The mapping from file names to ZIP entries
     * @param name The name of the image category (e.g. "means")
     * @param files The file names
     * @param expected The expected number of file names
     * @return The images
     * @throws IOException If an IO error occurs
     */
    private static BufferedImage[] readImages(ZipFile zipFile,
        Map<String, ZipEntry> entryMap, String name, String files[],
        int expected) throws IOException
    {
        if (files == null)
        {
            throw new IOException("No files found for " + name);
        }
        if (files.length != expected)
        {
            throw new IOException("Expected " + name
                + ".files to have length 2, but has " + files.length);
        }
        BufferedImage images[] = new BufferedImage[expected];
        for (int i = 0; i < expected; i++)
        {
            String file = files[i];
            BufferedImage image = readImage(zipFile, entryMap, file);
            images[i] = image;
        }
        return images;
    }

    /**
     * Read a WEBP image with the given name from the given input data
     * 
     * @param zipFile The ZIP file
     * @param entryMap The mapping from file names to ZIP entries
     * @param fileName The file name
     * @return The image
     * @throws IOException If an IO error occurs
     */
    private static BufferedImage readImage(ZipFile zipFile,
        Map<String, ZipEntry> entryMap, String fileName) throws IOException
    {
        ZipEntry zipEntry = entryMap.get(fileName);
        if (zipEntry == null)
        {
            throw new IOException("No entry found for " + fileName);
        }
        try (InputStream is = zipFile.getInputStream(zipEntry))
        {
            BufferedImage image = Images.readWebP(is);
            return image;
        }
    }

    /**
     * Convert the given {@link SogData} into splats
     * 
     * @param sogData The {@link SogData}
     * @return The splats
     * @throws IOException If an IO error occurs
     */
    private static List<MutableSplat> convertSogData(SogData sogData)
        throws IOException
    {
        Meta meta = sogData.meta;
        int count = meta.count;

        int shDegree = 0;
        if (meta.shN != null)
        {
            shDegree = meta.shN.bands;
        }
        List<MutableSplat> result = new ArrayList<MutableSplat>();
        for (int i = 0; i < count; i++)
        {
            MutableSplat s = Splats.create(shDegree);
            convertPosition(s, i, meta.means, sogData.meansU, sogData.meansL);
            convertQuaternions(s, i, sogData.quats);
            convertScales(s, i, meta.scales.codebook, sogData.scales);
            convertSh0(s, i, meta.sh0.codebook, sogData.sh0);
            if (meta.shN != null)
            {
                convertShN(s, i, meta.shN, sogData.shNLabels,
                    sogData.shNCentroids);
            }
            result.add(s);
        }
        return result;
    }

    /**
     * Convert the position of the specified splat from SOG into its standard
     * form.
     * 
     * @param s The splat
     * @param index The index
     * @param means The {@link Means}
     * @param meansU The means_u image
     * @param meansL The means_l image
     */
    private static void convertPosition(MutableSplat s, int index, Means means,
        BufferedImage meansU, BufferedImage meansL)
    {
        int ix = index % meansU.getWidth();
        int iy = index / meansU.getWidth();

        int meansUrgb = meansU.getRGB(ix, iy);
        int meansUr = (meansUrgb >> 16) & 0xFF;
        int meansUg = (meansUrgb >> 8) & 0xFF;
        int meansUb = (meansUrgb >> 0) & 0xFF;

        int meansLrgb = meansL.getRGB(ix, iy);
        int meansLr = (meansLrgb >> 16) & 0xFF;
        int meansLg = (meansLrgb >> 8) & 0xFF;
        int meansLb = (meansLrgb >> 0) & 0xFF;

        // 16-bit normalized value per axis (0..65535)
        int qx = (meansUr << 8) | meansLr;
        int qy = (meansUg << 8) | meansLg;
        int qz = (meansUb << 8) | meansLb;

        // Dequantize into log-domain nx,ny,nz using per-axis ranges from meta:
        float nx = lerp(means.mins[0], means.maxs[0], qx / 65535.0f);
        float ny = lerp(means.mins[1], means.maxs[1], qy / 65535.0f);
        float nz = lerp(means.mins[2], means.maxs[2], qz / 65535.0f);

        // Undo the symmetric log transform used at encode time:
        float x = unlog(nx);
        float y = unlog(ny);
        float z = unlog(nz);

        s.setPositionX(x);
        s.setPositionY(y);
        s.setPositionZ(z);
    }

    /**
     * Convert the rotation of the specified splat from SOG into its standard
     * form.
     * 
     * @param s The splat
     * @param index The index
     * @param quats The quats image
     * @throws IOException If an IO error occurs
     */
    private static void convertQuaternions(MutableSplat s, int index,
        BufferedImage quats) throws IOException
    {
        int ix = index % quats.getWidth();
        int iy = index / quats.getWidth();

        int quatsrgb = quats.getRGB(ix, iy);
        int quatsa = (quatsrgb >> 24) & 0xFF;
        int quatsr = (quatsrgb >> 16) & 0xFF;
        int quatsg = (quatsrgb >> 8) & 0xFF;
        int quatsb = (quatsrgb >> 0) & 0xFF;

        // Dequantize the stored three components:
        float a = toComp(quatsr);
        float b = toComp(quatsg);
        float c = toComp(quatsb);

        // 0..3 (R,G,B,A is one of the four components)
        int mode = (int) (quatsa - 252);

        // Reconstruct the omitted component so that ||q|| = 1
        // and w.l.o.g. the omitted one is non-negative
        float t = a * a + b * b + c * c;
        float d = (float) Math.sqrt(Math.max(0, 1 - t));

        // Place components according to mode
        float q[];
        switch (mode)
        {
            case 0:
                q = new float[]
                { d, a, b, c };
                break; // omitted = x
            case 1:
                q = new float[]
                { a, d, b, c };
                break; // omitted = y
            case 2:
                q = new float[]
                { a, b, d, c };
                break; // omitted = z
            case 3:
                q = new float[]
                { a, b, c, d };
                break; // omitted = w
            default:
                throw new IOException("Invalid quaternion mode");
        }
        s.setRotationW(q[0]);
        s.setRotationX(q[1]);
        s.setRotationY(q[2]);
        s.setRotationZ(q[3]);
    }

    /**
     * Convert the scales of the specified splat from SOG into its standard
     * form.
     * 
     * @param s The splat
     * @param index The index
     * @param codebook The {@link Scales#codebook}
     * @param scales The quats image
     * @throws IOException If an IO error occurs
     */
    private static void convertScales(MutableSplat s, int index,
        float codebook[], BufferedImage scales) throws IOException
    {
        int ix = index % scales.getWidth();
        int iy = index / scales.getWidth();

        int scalesrgb = scales.getRGB(ix, iy);
        int scalesr = (scalesrgb >> 16) & 0xFF;
        int scalesg = (scalesrgb >> 8) & 0xFF;
        int scalesb = (scalesrgb >> 0) & 0xFF;

        float sx = codebook[scalesr];
        float sy = codebook[scalesg];
        float sz = codebook[scalesb];

        s.setScaleX(sx);
        s.setScaleY(sy);
        s.setScaleZ(sz);
    }

    /**
     * Convert the SH0 of the specified splat from SOG into its standard form.
     * 
     * @param s The splat
     * @param index The index
     * @param codebook The {@link Sh0#codebook}
     * @param sh0 The sh0 image
     * @throws IOException If an IO error occurs
     */
    private static void convertSh0(MutableSplat s, int index, float codebook[],
        BufferedImage sh0) throws IOException
    {
        int ix = index % sh0.getWidth();
        int iy = index / sh0.getWidth();

        int sh0rgb = sh0.getRGB(ix, iy);
        int sh0a = (sh0rgb >> 24) & 0xFF;
        int sh0r = (sh0rgb >> 16) & 0xFF;
        int sh0g = (sh0rgb >> 8) & 0xFF;
        int sh0b = (sh0rgb >> 0) & 0xFF;

        // Not converting to "color" here
        float r = codebook[sh0r];
        float g = codebook[sh0g];
        float b = codebook[sh0b];
        float a = sh0a / 255.0f;

        s.setShX(0, r);
        s.setShY(0, g);
        s.setShZ(0, b);
        s.setOpacity(Splats.alphaToOpacity(a));
    }

    /**
     * Convert the SH0 of the specified splat from SOG into its standard form.
     * 
     * @param s The splat
     * @param splatIndex The splat index
     * @param shN The {@link ShN}
     * @param shNLabels The labels image
     * @param shNCentroids The centroids image
     * @throws IOException If an IO error occurs
     */
    private static void convertShN(MutableSplat s, int splatIndex, ShN shN,
        BufferedImage shNLabels, BufferedImage shNCentroids) throws IOException
    {
        int bands = shN.bands;
        int ix = splatIndex % shNLabels.getWidth();
        int iy = splatIndex / shNLabels.getWidth();

        int labelrgb = shNLabels.getRGB(ix, iy);
        int labelr = (labelrgb >> 16) & 0xFF;
        int labelg = (labelrgb >> 8) & 0xFF;

        int index = labelr + (labelg << 8);
        int coeffs[] =
        { 3, 8, 15 };
        int u = (index % 64) * coeffs[bands - 1];
        int v = index / 64;

        for (int k = 0; k < coeffs[bands - 1]; k++)
        {
            int centroidrgb = shNCentroids.getRGB(u + k, v);
            int centroidr = (centroidrgb >> 16) & 0xFF;
            int centroidg = (centroidrgb >> 8) & 0xFF;
            int centroidb = (centroidrgb >> 0) & 0xFF;
            float x = shN.codebook[centroidr];
            float y = shN.codebook[centroidg];
            float z = shN.codebook[centroidb];
            s.setShX(k + 1, x);
            s.setShY(k + 1, y);
            s.setShZ(k + 1, z);
        }
    }

    /**
     * Linear interpolation
     * 
     * @param a The first value
     * @param b The second value
     * @param t The interpolation value
     * @return The result
     */
    private static float lerp(float a, float b, float t)
    {
        return a + (b - a) * t;
    }

    /**
     * Undo a log-conversion
     * 
     * @param n The input
     * @return The result
     */
    private static float unlog(float n)
    {
        return (float) (Math.signum(n) * (Math.exp(Math.abs(n)) - 1));
    }

    /**
     * Dequantization of quaternion component
     * 
     * @param c The input
     * @return The result
     */
    private static float toComp(float c)
    {
        return (float) ((c / 255.0f - 0.5) * 2.0f / Math.sqrt(2.0));
    }

}
