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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * Utility methods related to images
 */
class Images
{
    /**
     * Read the pixels of the image from the given input stream and return
     * them as an array of RGBA byte values
     * 
     * @param inputStream The input stream
     * @return The data
     * @throws IOException If an IO error occurs
     */
    static byte[] readPixelsRgba(InputStream inputStream) throws IOException
    {
        BufferedImage image = ImageIO.read(inputStream);
        int pixelsIntArgb[] = getPixelsIntArgb(image);
        byte pixelsByteRgba[] = convertIntArgbToByteRgba(pixelsIntArgb);
        return pixelsByteRgba;
    }

    /**
     * Returns the pixels of the given image as an array of ARGB int values.
     * 
     * If necessary, this will convert the given image to an ARGB image
     * internally.
     * 
     * @param inputImage The input image
     * @return The result
     */
    private static int[] getPixelsIntArgb(BufferedImage inputImage)
    {
        BufferedImage image = inputImage;
        if (image.getType() != BufferedImage.TYPE_INT_ARGB)
        {
            image = convertToArgb(image);
        }
        WritableRaster raster = image.getRaster();
        DataBuffer dataBuffer = raster.getDataBuffer();
        DataBufferInt dataBufferInt = (DataBufferInt) dataBuffer;
        int[] data = dataBufferInt.getData();
        return data;
    }

    /**
     * Convert the given array of ARGB int values into an array of RGBA byte
     * values
     * 
     * @param pixelsIntArgb The input
     * @return The result
     */
    private static byte[] convertIntArgbToByteRgba(int pixelsIntArgb[])
    {
        byte pixelsByteRgba[] = new byte[pixelsIntArgb.length * 4];
        for (int i = 0; i < pixelsIntArgb.length; i++)
        {
            int argb = pixelsIntArgb[i];
            int a = (argb >>> 24) & 0xFF;
            int r = (argb >>> 16) & 0xFF;
            int g = (argb >>> 8) & 0xFF;
            int b = (argb >>> 0) & 0xFF;
            pixelsByteRgba[i * 4 + 0] = (byte) r;
            pixelsByteRgba[i * 4 + 1] = (byte) g;
            pixelsByteRgba[i * 4 + 2] = (byte) b;
            pixelsByteRgba[i * 4 + 3] = (byte) a;
        }
        return pixelsByteRgba;

    }

    /**
     * Convert the given image into one that uses ARGB integer pixels
     * 
     * @param image The image
     * @return The converted image
     */
    private static BufferedImage convertToArgb(BufferedImage image)
    {
        BufferedImage newImage = new BufferedImage(image.getWidth(),
            image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Images()
    {
        // Private constructor to prevent instantiation
    }

}
