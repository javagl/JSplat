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
package de.javagl.jsplat;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Methods related to {@link SplatData} instances.
 * 
 * The methods in this class that read and write from and to buffers should not
 * be considered to be part of the public API. Some of them are underspecified.
 * They might be modified or moved to a different class in future releases.
 */
public class SplatDatas
{
    /**
     * Creates a new {@link SplatData} instance.
     * 
     * @param shDegree The spherical harmonics degree
     * @param size The number of splats
     * @return The {@link SplatData}
     * @throws IllegalArgumentException if the given degree or size is negative
     */
    public static SplatData create(int shDegree, int size)
    {
        return new DefaultSplatData(shDegree, size);
    }

    /**
     * Create a new {@link SplatData} instance from the given {@link Splat}
     * objects.
     * 
     * @param splats The {@link Splat} objects
     * @return The {@link SplatData}
     */
    public static SplatData fromSplats(Collection<? extends Splat> splats)
    {
        Splat splat0 = splats.iterator().next();
        int shDegree = splat0.getShDegree();
        int size = splats.size();
        SplatData splatData = SplatDatas.create(shDegree, size);

        FloatBuffer positions = splatData.getPositions();
        FloatBuffer scales = splatData.getScales();
        FloatBuffer rotations = splatData.getRotations();
        FloatBuffer opacities = splatData.getOpacities();
        FloatBuffer shs = splatData.getShs();

        readPositions(splats, positions);
        readScales(splats, scales);
        readRotations(splats, rotations);
        readOpacities(splats, opacities);
        readShs(splats, shDegree, shs);

        return splatData;
    }

    /**
     * Create a new list of splats from the given {@link SplatData}
     * 
     * @param splatData The {@link SplatData}
     * @return The splats
     */
    public static List<MutableSplat> toList(SplatData splatData)
    {
        int shDegree = splatData.getShDegree();
        int size = splatData.getSize();
        FloatBuffer positions = splatData.getPositions();
        FloatBuffer scales = splatData.getScales();
        FloatBuffer rotations = splatData.getRotations();
        FloatBuffer opacities = splatData.getOpacities();
        FloatBuffer shs = splatData.getShs();

        List<MutableSplat> splats = new ArrayList<MutableSplat>(size);
        for (int i = 0; i < size; i++)
        {
            MutableSplat splat = Splats.create(shDegree);
            splats.add(splat);
        }

        writePositions(positions, splats);
        writeScales(scales, splats);
        writeRotations(rotations, splats);
        writeOpacities(opacities, splats);
        writeShs(shs, splats, shDegree);

        return splats;
    }

    /**
     * Read the positions from the given splats and write them into the given
     * target buffer.
     * 
     * The position of the given buffer will not be modified.
     * 
     * The caller is responsible for the buffer having a size of at least
     * <code>splats.size() * 3</code>.
     * 
     * If the given buffer is <code>null</code>, then a new direct buffer with
     * native byte order will be created and returned.
     * 
     * @param splats The splats
     * @param result The buffer for the result
     * @return The result
     */
    public static FloatBuffer readPositions(Collection<? extends Splat> splats,
        FloatBuffer result)
    {
        FloatBuffer b = result;
        if (b == null)
        {
            b = Buffers.createFloatBuffer(splats.size() * 3);
        }
        int i = 0;
        for (Splat s : splats)
        {
            b.put(i * 3 + 0, s.getPositionX());
            b.put(i * 3 + 1, s.getPositionY());
            b.put(i * 3 + 2, s.getPositionZ());
            i++;
        }
        return b;
    }

    /**
     * Read the scales from the given splats and write them into the given
     * target buffer.
     * 
     * The position of the given buffer will not be modified.
     * 
     * The caller is responsible for the buffer having a size of at least
     * <code>splats.size() * 3</code>.
     * 
     * If the given buffer is <code>null</code>, then a new direct buffer with
     * native byte order will be created and returned.
     * 
     * @param splats The splats
     * @param result The buffer for the result
     * @return The result
     */
    public static FloatBuffer readScales(Collection<? extends Splat> splats,
        FloatBuffer result)
    {
        FloatBuffer b = result;
        if (b == null)
        {
            b = Buffers.createFloatBuffer(splats.size() * 3);
        }
        int i = 0;
        for (Splat s : splats)
        {
            b.put(i * 3 + 0, s.getScaleX());
            b.put(i * 3 + 1, s.getScaleY());
            b.put(i * 3 + 2, s.getScaleZ());
            i++;
        }
        return b;
    }

    /**
     * Read the rotations from the given splats and write them into the given
     * buffer, using scalar-last order.
     * 
     * The position of the given buffer will not be modified.
     * 
     * The caller is responsible for the buffer having a size of at least
     * <code>splats.size() * 4</code>.
     * 
     * If the given buffer is <code>null</code>, then a new direct buffer with
     * native byte order will be created and returned.
     * 
     * @param splats The splats
     * @param result The buffer for the result
     * @return The result
     */
    public static FloatBuffer readRotations(Collection<? extends Splat> splats,
        FloatBuffer result)
    {
        FloatBuffer b = result;
        if (b == null)
        {
            b = Buffers.createFloatBuffer(splats.size() * 4);
        }
        int i = 0;
        for (Splat s : splats)
        {
            b.put(i * 4 + 0, s.getRotationX());
            b.put(i * 4 + 1, s.getRotationY());
            b.put(i * 4 + 2, s.getRotationZ());
            b.put(i * 4 + 3, s.getRotationW());
            i++;
        }
        return b;
    }

    /**
     * Read the opacities from the given splats and write them into the given
     * target buffer.
     * 
     * The position of the given buffer will not be modified.
     * 
     * The caller is responsible for the buffer having a size of at least
     * <code>splats.size()</code>.
     * 
     * If the given buffer is <code>null</code>, then a new direct buffer with
     * native byte order will be created and returned.
     * 
     * @param splats The splats
     * @param result The buffer for the result
     * @return The result
     */
    public static FloatBuffer readOpacities(Collection<? extends Splat> splats,
        FloatBuffer result)
    {
        FloatBuffer b = result;
        if (b == null)
        {
            b = Buffers.createFloatBuffer(splats.size());
        }
        int i = 0;
        for (Splat s : splats)
        {
            b.put(i, s.getOpacity());
            i++;
        }
        return b;
    }

    /**
     * Read the specified spherical harmonics from the given splats and write
     * them into the given target buffer.
     * 
     * The position of the given buffer will not be modified.
     * 
     * The caller is responsible for the buffer having a size of at least
     * <code>splats.size()</code>.
     * 
     * If the given buffer is <code>null</code>, then a new direct buffer with
     * native byte order will be created and returned.
     * 
     * @param splats The splats
     * @param degree The degree
     * @param coefficient The coefficient
     * @param result The buffer for the result
     * @return The result
     */
    public static FloatBuffer readSh(Collection<? extends Splat> splats,
        int degree, int coefficient, FloatBuffer result)
    {
        int index = Splats.dimensionForCoefficient(degree, coefficient);
        FloatBuffer b = result;
        if (b == null)
        {
            b = Buffers.createFloatBuffer(splats.size() * 3);
        }
        int i = 0;
        for (Splat s : splats)
        {
            b.put(i * 3 + 0, s.getShX(index));
            b.put(i * 3 + 1, s.getShY(index));
            b.put(i * 3 + 2, s.getShZ(index));
            i++;
        }
        return b;
    }

    /**
     * Read the spherical harmonics from the given splats and write them into
     * the given target buffer.
     * 
     * The position of the given buffer will not be modified.
     * 
     * The caller is responsible for the buffer having a size of at least
     * <code>splats.size() * shDimensions * 3</code>.
     * 
     * If the given buffer is <code>null</code>, then a new direct buffer with
     * native byte order will be created and returned.
     * 
     * @param splats The splats
     * @param shDegree The spherical harmonics degree
     * @param result The result
     * @return The result
     */
    public static FloatBuffer readShs(Collection<? extends Splat> splats,
        int shDegree, FloatBuffer result)
    {
        int shDimensions = Splats.dimensionsForDegree(shDegree);
        FloatBuffer b = result;
        if (b == null)
        {
            b = Buffers.createFloatBuffer(splats.size() * shDimensions * 3);
        }
        int i = 0;
        for (Splat s : splats)
        {
            for (int d = 0; d < shDimensions; d++)
            {
                b.put(((i * shDimensions) + d) * 3 + 0, s.getShX(d));
                b.put(((i * shDimensions) + d) * 3 + 1, s.getShY(d));
                b.put(((i * shDimensions) + d) * 3 + 2, s.getShZ(d));
            }
            i++;
        }
        return b;
    }

    /**
     * Write the positions from the given buffer with
     * <code>splats.size() * 3</code> elements into the given splats
     * 
     * @param b The buffer
     * @param splats The splats
     */
    public static void writePositions(FloatBuffer b,
        Collection<? extends MutableSplat> splats)
    {
        int i = 0;
        for (MutableSplat s : splats)
        {
            s.setPositionX(b.get(i * 3 + 0));
            s.setPositionY(b.get(i * 3 + 1));
            s.setPositionZ(b.get(i * 3 + 2));
            i++;
        }
    }

    /**
     * Write the scales from the given buffer with
     * <code>splats.size() * 3</code> elements into the given splats
     * 
     * @param b The buffer
     * @param splats The splats
     */
    public static void writeScales(FloatBuffer b,
        Collection<? extends MutableSplat> splats)
    {
        int i = 0;
        for (MutableSplat s : splats)
        {
            s.setScaleX(b.get(i * 3 + 0));
            s.setScaleY(b.get(i * 3 + 1));
            s.setScaleZ(b.get(i * 3 + 2));
            i++;
        }
    }

    /**
     * Write the rotations from the given buffer with
     * <code>splats.size() * 4</code> elements, storing the rotations in
     * scalar-last order, into the given splats
     * 
     * @param b The buffer
     * @param splats The splats
     */
    public static void writeRotations(FloatBuffer b,
        Collection<? extends MutableSplat> splats)
    {
        int i = 0;
        for (MutableSplat s : splats)
        {
            s.setRotationX(b.get(i * 4 + 0));
            s.setRotationY(b.get(i * 4 + 1));
            s.setRotationZ(b.get(i * 4 + 2));
            s.setRotationW(b.get(i * 4 + 3));
            i++;
        }
    }

    /**
     * Write the opacities from the given buffer with <code>splats.size()</code>
     * elements into the given splats
     * 
     * @param b The buffer
     * @param splats The splats
     */
    public static void writeOpacities(FloatBuffer b,
        Collection<? extends MutableSplat> splats)
    {
        int i = 0;
        for (MutableSplat s : splats)
        {
            s.setOpacity(b.get(i));
            i++;
        }
    }

    /**
     * Write the specified spherical harmonics from the given buffer with
     * <code>splats.size() * 3</code> elements into the given splats
     * 
     * @param b The buffer
     * @param splats The splats
     * @param degree The degree
     * @param coefficient The coefficient
     */
    public static void writeSh(FloatBuffer b,
        Collection<? extends MutableSplat> splats, int degree, int coefficient)
    {
        int index = Splats.dimensionForCoefficient(degree, coefficient);
        int i = 0;
        for (MutableSplat s : splats)
        {
            s.setShX(index, b.get(i * 3 + 0));
            s.setShY(index, b.get(i * 3 + 1));
            s.setShZ(index, b.get(i * 3 + 2));
            i++;
        }
    }

    /**
     * Write the specified spherical harmonics from the given buffer with
     * <code>splats.size() * shDimensions * 3</code> elements into the given
     * splats
     * 
     * @param b The buffer
     * @param splats The splats
     * @param shDegree The degree
     */
    public static void writeShs(FloatBuffer b,
        Collection<? extends MutableSplat> splats, int shDegree)
    {
        int shDimensions = Splats.dimensionsForDegree(shDegree);
        int i = 0;
        for (MutableSplat s : splats)
        {
            for (int d = 0; d < shDimensions; d++)
            {
                s.setShX(d, b.get(((i * shDimensions) + d) * 3 + 0));
                s.setShY(d, b.get(((i * shDimensions) + d) * 3 + 1));
                s.setShZ(d, b.get(((i * shDimensions) + d) * 3 + 2));
            }
            i++;
        }
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatDatas()
    {
        // Private constructor to prevent instantiation
    }

}