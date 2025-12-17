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
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.luciad.imageio.webp.WebPReadParam;

/**
 * Utility methods related to images
 */
class Images
{
    /**
     * Read a WEBP image from the given input stream
     * 
     * @param inputStream The input stream
     * @return The image
     * @throws IOException If an IO error occurs
     */
    static BufferedImage readWebP(InputStream inputStream) throws IOException
    {
        ImageReader imageReader =
            ImageIO.getImageReadersByMIMEType("image/webp").next();
        WebPReadParam readParam = new WebPReadParam();
        readParam.setBypassFiltering(true);
        ImageInputStream imageInputStream =
            ImageIO.createImageInputStream(inputStream);
        imageReader.setInput(imageInputStream);
        BufferedImage image = imageReader.read(0, readParam);
        return image;

    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Images()
    {
        // Private constructor to prevent instantiation
    }
    
}
