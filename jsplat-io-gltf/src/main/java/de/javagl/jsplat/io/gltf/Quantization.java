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
package de.javagl.jsplat.io.gltf;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.GltfConstants;

/**
 * Internal utility methods for quantization.
 * 
 * These methods will sooner or later become part of JglTF.
 */
class Quantization
{
    /**
     * Returns the data from the given accessor model as a float buffer, tightly
     * packed, applying dequantization as necessary.
     * 
     * @param accessorModel The accessor model
     * @return The buffer
     * @throws IllegalArgumentException If the component type of the given
     *         accessor model is neither float, nor signed/unsigned byte/short.
     */
    static FloatBuffer readAsFloatBuffer(AccessorModel accessorModel)
    {
        AccessorData accessorData = accessorModel.getAccessorData();
        int componentType = accessorModel.getComponentType();
        if (componentType == GltfConstants.GL_FLOAT)
        {
            ByteBuffer inputByteBuffer = accessorData.createByteBuffer();
            return inputByteBuffer.asFloatBuffer();
        }
        if (componentType == GltfConstants.GL_SHORT)
        {
            ByteBuffer inputByteBuffer = accessorData.createByteBuffer();
            ShortBuffer shortBuffer = inputByteBuffer.asShortBuffer();
            return dequantizeShortBuffer(shortBuffer);
        }
        if (componentType == GltfConstants.GL_UNSIGNED_SHORT)
        {
            ByteBuffer inputByteBuffer = accessorData.createByteBuffer();
            ShortBuffer shortBuffer = inputByteBuffer.asShortBuffer();
            return dequantizeUnsignedShortBuffer(shortBuffer);
        }
        if (componentType == GltfConstants.GL_BYTE)
        {
            ByteBuffer inputByteBuffer = accessorData.createByteBuffer();
            return dequantizeByteBuffer(inputByteBuffer);
        }
        if (componentType == GltfConstants.GL_UNSIGNED_BYTE)
        {
            ByteBuffer inputByteBuffer = accessorData.createByteBuffer();
            return dequantizeUnsignedByteBuffer(inputByteBuffer);
        }
        throw new IllegalArgumentException(
            "Component type " + GltfConstants.stringFor(componentType)
                + " cannot be converted to float");
    }

    /**
     * Dequantize the given buffer into a float buffer, treating each element of
     * the input as a signed byte.
     * 
     * @param byteBuffer The input buffer
     * @return The result
     */
    private static FloatBuffer dequantizeByteBuffer(ByteBuffer byteBuffer)
    {
        FloatBuffer floatBuffer = FloatBuffer.allocate(byteBuffer.capacity());
        for (int i = 0; i < byteBuffer.capacity(); i++)
        {
            byte c = byteBuffer.get(i);
            float f = dequantizeByte(c);
            floatBuffer.put(i, f);
        }
        return floatBuffer;
    }

    /**
     * Dequantize the given buffer into a float buffer, treating each element of
     * the input as an unsigned byte.
     * 
     * @param byteBuffer The input buffer
     * @return The result
     */
    private static FloatBuffer
        dequantizeUnsignedByteBuffer(ByteBuffer byteBuffer)
    {
        FloatBuffer floatBuffer = FloatBuffer.allocate(byteBuffer.capacity());
        for (int i = 0; i < byteBuffer.capacity(); i++)
        {
            byte c = byteBuffer.get(i);
            float f = dequantizeUnsignedByte(c);
            floatBuffer.put(i, f);
        }
        return floatBuffer;
    }

    /**
     * Dequantize the given buffer into a float buffer, treating each element of
     * the input as a signed short.
     * 
     * @param shortBuffer The input buffer
     * @return The result
     */
    private static FloatBuffer dequantizeShortBuffer(ShortBuffer shortBuffer)
    {
        FloatBuffer floatBuffer = FloatBuffer.allocate(shortBuffer.capacity());
        for (int i = 0; i < shortBuffer.capacity(); i++)
        {
            short c = shortBuffer.get(i);
            float f = dequantizeShort(c);
            floatBuffer.put(i, f);
        }
        return floatBuffer;
    }

    /**
     * Dequantize the given buffer into a float buffer, treating each element of
     * the input as an unsigned short.
     * 
     * @param shortBuffer The input buffer
     * @return The result
     */
    private static FloatBuffer
        dequantizeUnsignedShortBuffer(ShortBuffer shortBuffer)
    {
        FloatBuffer floatBuffer = FloatBuffer.allocate(shortBuffer.capacity());
        for (int i = 0; i < shortBuffer.capacity(); i++)
        {
            short c = shortBuffer.get(i);
            float f = dequantizeUnsignedShort(c);
            floatBuffer.put(i, f);
        }
        return floatBuffer;
    }

    /**
     * Dequantize the given signed byte into a floating point value
     * 
     * @param c The input
     * @return The result
     */
    private static float dequantizeByte(byte c)
    {
        float f = Math.max(c / 127.0f, -1.0f);
        return f;
    }

    /**
     * Dequantize the given unsigned byte into a floating point value
     * 
     * @param c The input
     * @return The result
     */
    private static float dequantizeUnsignedByte(byte c)
    {
        int i = Byte.toUnsignedInt(c);
        float f = i / 255.0f;
        return f;
    }

    /**
     * Dequantize the given signed short into a floating point value
     * 
     * @param c The input
     * @return The result
     */
    private static float dequantizeShort(short c)
    {
        float f = Math.max(c / 32767.0f, -1.0f);
        return f;
    }

    /**
     * 
     * Dequantize the given unsigned byte into a floating point value
     * 
     * @param c The input
     * @return The result
     */
    private static float dequantizeUnsignedShort(short c)
    {
        int i = Short.toUnsignedInt(c);
        float f = i / 65535.0f;
        return f;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Quantization()
    {
        // Private constructor to prevent instantiation
    }

}
