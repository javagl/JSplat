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

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListWriter;

/**
 * Implementation of a {@link SplatListWriter} that writes SOG data
 */
public final class SogSplatWriter implements SplatListWriter
{
    /**
     * Creates a new instance
     */
    public SogSplatWriter()
    {
        /// Default constructor
    }

    @Override
    public void writeList(List<? extends Splat> splats,
        OutputStream outputStream) throws IOException
    {
        EntryConsumer entryConsumer = new ZipEntryConsumer(outputStream);
        write(splats, entryConsumer);
        entryConsumer.close();
    }

    /**
     * Write the given splats in SOG representation into the given directory,
     * as individual files for the meta JSOn, means, scales, quaternions,
     * colors, and spherical harmonics.
     * 
     * @param splats The splats
     * @param directoryName The directory name
     * @throws IOException If an IO error occurs
     */
    public void writeListTo(List<? extends Splat> splats, String directoryName)
        throws IOException
    {
        EntryConsumer entryConsumer = new DirectoryEntryConsumer(directoryName);
        write(splats, entryConsumer);
        entryConsumer.close();
    }

    /**
     * Write the given splats to the given consumer
     * 
     * @param splats The splats
     * @param entryConsumer The consumer
     * @throws IOException If an IO error occurs
     */
    private static void write(List<? extends Splat> splats,
        EntryConsumer entryConsumer) throws IOException
    {
        SogDataGenerator g = new SogDataGenerator();
        SogData sogData = g.generate(splats);

        int width = g.getWidth();
        int height = g.getHeight();

        int shWidth = g.getShWidth();
        int shHeight = g.getShHeight();

        byte[] meta = generateJsonData(sogData.meta);
        entryConsumer.consume("meta.json", meta);

        byte[] meansL = generateWebpData(sogData.meansL, width, height);
        entryConsumer.consume(sogData.meta.means.files[0], meansL);

        byte[] meansU = generateWebpData(sogData.meansU, width, height);
        entryConsumer.consume(sogData.meta.means.files[1], meansU);

        byte[] quats = generateWebpData(sogData.quats, width, height);
        entryConsumer.consume(sogData.meta.quats.files[0], quats);

        byte[] scales = generateWebpData(sogData.scales, width, height);
        entryConsumer.consume(sogData.meta.scales.files[0], scales);

        byte[] sh0 = generateWebpData(sogData.sh0, width, height);
        entryConsumer.consume(sogData.meta.sh0.files[0], sh0);

        if (sogData.shNLabels != null)
        {
            byte[] shc =
                generateWebpData(sogData.shNCentroids, shWidth, shHeight);
            entryConsumer.consume(sogData.meta.shN.files[0], shc);

            byte[] shl = generateWebpData(sogData.shNLabels, width, height);
            entryConsumer.consume(sogData.meta.shN.files[1], shl);
        }
    }

    /**
     * Generate JSON data for the given object
     *  
     * @param object The object
     * @return The JSON data
     * @throws IOException If an IO error occurs
     */
    private static byte[] generateJsonData(Object object) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonUtils.writeValue(object, baos);
        baos.flush();
        baos.close();
        return baos.toByteArray();
    }

    /**
     * Generate WEBP image data from the given pixel data
     * 
     * @param data The pixel data
     * @param w The width
     * @param h The height
     * @return The image data
         * @throws IOException If an IO error occurs
     */
    private static byte[] generateWebpData(byte data[], int w, int h)
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Images.writePixelsByteRgba(w, h, data, baos);
        baos.flush();
        baos.close();
        return baos.toByteArray();
    };

    /**
     * Interface for classes that can receive a sequence of "entries" and
     * either write them as ZIP entries or as files
     */
    private static interface EntryConsumer
    {
        /**
         * Consume the given entry data
         * 
         * @param name The entry name
         * @param data The data
         * @throws IOException If an IO error occurs
         */
        void consume(String name, byte data[]) throws IOException;

        /**
         * Close this consumer
         *  
         * @throws IOException If an IO error occurs
         */
        void close() throws IOException;
    }

    /**
     * An {@link EntryConsumer} that writes to a ZIP output stream
     */
    private static class ZipEntryConsumer implements EntryConsumer
    {
        /**
         * The ZIP output stream
         */
        private final ZipOutputStream zipOutputStream;

        /**
         * Creates a new instance
         * 
         * @param outputStream The output stream
         */
        ZipEntryConsumer(OutputStream outputStream)
        {
            this.zipOutputStream = new ZipOutputStream(outputStream);
        }

        @Override
        public void consume(String name, byte[] data) throws IOException
        {
            ZipEntry zipEntry = new ZipEntry(name);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(data);
        }

        @Override
        public void close() throws IOException
        {
            zipOutputStream.close();
        }
    }

    /**
     * Implementation of an {@link EntryConsumer} that writes to a directory
     */
    private static class DirectoryEntryConsumer implements EntryConsumer
    {
        /**
         * The base directory
         */
        private final String baseDirectory;

        /**
         * Creates a new instance
         * 
         * @param baseDirectory The base directory
         */
        DirectoryEntryConsumer(String baseDirectory)
        {
            this.baseDirectory = baseDirectory;
        }

        @Override
        public void consume(String name, byte[] data) throws IOException
        {
            Path path = Paths.get(baseDirectory, name);
            FileOutputStream fos = new FileOutputStream(path.toFile());
            fos.write(data);
            fos.close();
        }

        @Override
        public void close() throws IOException
        {
            // Nothing to do here
        }

    }
}
