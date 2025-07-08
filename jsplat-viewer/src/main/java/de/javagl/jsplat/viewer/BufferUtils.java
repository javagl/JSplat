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
package de.javagl.jsplat.viewer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Utility methods for buffers.
 * 
 * This class is not really part of the public API, but only contains those
 * functions that are found in every class that is called "BufferUtils", so ...
 * just use it, or copy-and-paste the functions, whatever...
 */
public class BufferUtils
{
    /**
     * Create a direct byte buffer with the given size
     * 
     * @param size The size of the buffer
     * @return The buffer
     */
    private static ByteBuffer createByteBuffer(int size)
    {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    /**
     * Create a direct float buffer with the given size
     * 
     * @param size The size of the buffer
     * @return The buffer
     */
    public static FloatBuffer createFloatBuffer(int size)
    {
        return createByteBuffer(size * 4).asFloatBuffer();
    }

    /**
     * Create a direct int buffer with the given size
     * 
     * @param size The size of the buffer
     * @return The buffer
     */
    public static IntBuffer createIntBuffer(int size)
    {
        return createByteBuffer(size * 4).asIntBuffer();
    }

    /**
     * Create a direct int buffer which has the contents of the given array.
     * 
     * @param a The array
     * @return The buffer
     */
    public static IntBuffer createDirectBuffer(int a[])
    {
        IntBuffer b = createIntBuffer(a.length);
        b.put(a);
        b.rewind();
        return b;
    }

    /**
     * Create a direct float buffer which has the contents of the given array.
     * 
     * @param a The array
     * @return The buffer
     */
    public static FloatBuffer createDirectBuffer(float a[])
    {
        FloatBuffer b = createFloatBuffer(a.length);
        b.put(a);
        b.rewind();
        return b;
    }


    /**
     * Private constructor to prevent instantiation
     */
    private BufferUtils()
    {
        // Private constructor to prevent instantiation
    }

}
