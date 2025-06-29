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
package de.javagl.jsplat.io.spz;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListWriter;
import de.javagl.jspz.GaussianCloud;
import de.javagl.jspz.SpzWriter;
import de.javagl.jspz.SpzWriters;

/**
 * Implementation of a {@link SplatListWriter} that writes SPZ data
 */
public final class SpzSplatWriter implements SplatListWriter
{
    /**
     * Creates a new instance
     */
    public SpzSplatWriter()
    {
        // Default constructor
    }

    @Override
    public void writeList(List<? extends Splat> splats,
        OutputStream outputStream) throws IOException
    {
        SpzWriter spzWriter = SpzWriters.createDefaultV2();
        GaussianCloud g = GaussianCloudSplats.fromSplats(splats);
        spzWriter.write(g, outputStream);
    }

}
