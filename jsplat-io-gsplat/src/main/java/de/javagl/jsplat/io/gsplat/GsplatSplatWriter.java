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
package de.javagl.jsplat.io.gsplat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.stream.Stream;

import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListWriter;
import de.javagl.jsplat.SplatStreamWriter;
import de.javagl.jsplat.Splats;

/**
 * A {@link SplatStreamWriter} and {@link SplatListWriter} that writes
 * <code>gsplat</code> encoded data
 */
public final class GsplatSplatWriter
    implements SplatStreamWriter, SplatListWriter
{
    /**
     * Creates a new instance
     */
    public GsplatSplatWriter()
    {
        // Default constructor
    }

    @Override
    public void writeList(List<? extends Splat> splats,
        OutputStream outputStream) throws IOException
    {
        writeStream(splats.stream(), outputStream);
    }

    @Override
    public void writeStream(Stream<? extends Splat> splats,
        OutputStream outputStream) throws IOException
    {
        byte array[] = new byte[32];
        ByteBuffer bb = ByteBuffer.wrap(array).order(ByteOrder.LITTLE_ENDIAN);
        try
        {
            splats.forEach(splat ->
            {
                writeToBuffer(splat, bb);
                try
                {
                    outputStream.write(array);
                }
                catch (IOException e)
                {
                    throw new UncheckedIOException(e);
                }
            });
        }
        catch (UncheckedIOException e)
        {
            throw e.getCause();
        }
    }

    /**
     * Write the given splat into the given buffer in <code>gsplat</code>
     * format.
     * 
     * @param splat The splat
     * @param buffer The buffer
     */
    static void writeToBuffer(Splat splat, ByteBuffer buffer)
    {
        buffer.putFloat(0, splat.getPositionX());
        buffer.putFloat(4, -splat.getPositionY());
        buffer.putFloat(8, -splat.getPositionZ());
        buffer.putFloat(12, (float) Math.exp(splat.getScaleX()));
        buffer.putFloat(16, (float) Math.exp(splat.getScaleY()));
        buffer.putFloat(20, (float) Math.exp(splat.getScaleZ()));

        float sr = splat.getShX(0);
        float sg = splat.getShY(0);
        float sb = splat.getShZ(0);
        float sa = splat.getOpacity();

        float fr = Splats.directCurrentToColor(sr);
        float fg = Splats.directCurrentToColor(sg);
        float fb = Splats.directCurrentToColor(sb);
        float fa = Splats.opacityToAlpha(sa);

        byte r = (byte) (fr * 255.0);
        byte g = (byte) (fg * 255.0);
        byte b = (byte) (fb * 255.0);
        byte a = (byte) (fa * 255.0);

        buffer.put(24, r);
        buffer.put(25, g);
        buffer.put(26, b);
        buffer.put(27, a);

        float srx = splat.getRotationX();
        float sry = splat.getRotationY();
        float srz = splat.getRotationZ();
        float srw = splat.getRotationW();
        
        float lenSquared = srx * srx + sry * sry + srz * srz + srw * srw;
        float invLen = (float) (1.0 / Math.sqrt(lenSquared));
        
        byte rx = (byte) ((srx * invLen) * 128.0 + 128.0);
        byte ry = (byte) ((sry * invLen) * 128.0 + 128.0);
        byte rz = (byte) ((srz * invLen) * 128.0 + 128.0);
        byte rw = (byte) ((srw * invLen) * 128.0 + 128.0);
        buffer.put(28, rx);
        buffer.put(29, ry);
        buffer.put(30, rz);
        buffer.put(31, rw);

    }

}
