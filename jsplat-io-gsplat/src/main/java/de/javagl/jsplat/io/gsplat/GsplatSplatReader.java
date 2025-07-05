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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.SplatStreamReader;
import de.javagl.jsplat.Splats;

/**
 * A {@link SplatStreamReader} and {@link SplatListReader} that reads from
 * <code>gsplat</code> encoded data
 */
public final class GsplatSplatReader
    implements SplatStreamReader, SplatListReader
{
    /**
     * Creates a new instance
     */
    public GsplatSplatReader()
    {
        // Default constructor
    }

    @Override
    public Stream<MutableSplat> readStream(InputStream inputStream)
    {
        InputStream bis = new BufferedInputStream(inputStream);
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
            createIterator(bis), Spliterator.ORDERED), false);
    }

    @Override
    public List<MutableSplat> readList(InputStream inputStream) throws IOException
    {
        return readStream(inputStream).collect(Collectors.toList());
    }

    /**
     * Create an iterator over the splat instances
     * 
     * @param inputStream The stream to read from
     * @return The iterator
     */
    private Iterator<MutableSplat> createIterator(InputStream inputStream)
    {
        byte data[] = new byte[32];
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        return new Iterator<MutableSplat>()
        {
            /**
             * The next splat to be returned
             */
            private MutableSplat next = prepareNext();

            /**
             * Prepare the next splat to be returned
             * 
             * @return The next splat, or <code>null</code> if there are none
             */
            private MutableSplat prepareNext()
            {
                try
                {
                    int read = read(inputStream, data);
                    if (read < data.length)
                    {
                        return null;
                    }
                    MutableSplat splat = Splats.create(0);
                    readFromBuffer(bb, splat);
                    return splat;
                }
                catch (IOException e)
                {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public MutableSplat next()
            {
                if (next == null)
                {
                    throw new NoSuchElementException("No more elements");
                }
                MutableSplat result = next;
                next = prepareNext();
                return result;
            }

            @Override
            public boolean hasNext()
            {
                return next != null;
            }
        };
    }

    /**
     * Read the data from the given <code>gsplat</code> buffer and write it into
     * the given target
     * 
     * @param bb The source
     * @param splat The target
     */
    private static void readFromBuffer(ByteBuffer bb, MutableSplat splat)
    {
        splat.setPositionX(bb.getFloat(0));
        splat.setPositionY(-bb.getFloat(4));
        splat.setPositionZ(-bb.getFloat(8));

        splat.setScaleX((float) Math.log(bb.getFloat(12)));
        splat.setScaleY((float) Math.log(bb.getFloat(16)));
        splat.setScaleZ((float) Math.log(bb.getFloat(20)));

        float fr = Byte.toUnsignedInt(bb.get(24)) / 255.0f;
        float fg = Byte.toUnsignedInt(bb.get(25)) / 255.0f;
        float fb = Byte.toUnsignedInt(bb.get(26)) / 255.0f;
        float fa = Byte.toUnsignedInt(bb.get(27)) / 255.0f;

        splat.setShX(0, Splats.colorToDirectCurrent(fr));
        splat.setShY(0, Splats.colorToDirectCurrent(fg));
        splat.setShZ(0, Splats.colorToDirectCurrent(fb));

        splat.setOpacity(Splats.alphaToOpacity(fa));

        float rx = (Byte.toUnsignedInt(bb.get(28)) - 128.0f) / 128.0f;
        float ry = (Byte.toUnsignedInt(bb.get(29)) - 128.0f) / 128.0f;
        float rz = (Byte.toUnsignedInt(bb.get(30)) - 128.0f) / 128.0f;
        float rw = (Byte.toUnsignedInt(bb.get(31)) - 128.0f) / 128.0f;
        
        float lenSquared = rx * rx + ry * ry + rz * rz + rw * rw;
        float len = (float) Math.sqrt(lenSquared);
        
        splat.setRotationX(rx * len);
        splat.setRotationY(ry * len);
        splat.setRotationZ(rz * len);
        splat.setRotationW(rw * len);
    }

    /**
     * Read bytes into the given byte array
     * 
     * @param inputStream The input stream
     * @param target The target array
     * @return The number of bytes read (before the end of the stream was
     *         encountered)
     * @throws IOException If an IO error occurs
     */
    private static int read(InputStream inputStream, byte target[])
        throws IOException
    {
        int bytesRead = 0;
        while (bytesRead < target.length)
        {
            int read = inputStream.read(target, bytesRead, target.length);
            if (read == -1)
            {
                break;
            }
            bytesRead += read;
        }
        return bytesRead;
    }

}