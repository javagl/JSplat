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
 * Methods related to {@link SplatData}
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
        int shDimensions = Splats.dimensionsForDegree(shDegree);
        int size = splats.size();
        SplatData splatData = SplatDatas.create(shDegree, size);

        FloatBuffer positions = splatData.getPositions();
        FloatBuffer scales = splatData.getScales();
        FloatBuffer rotations = splatData.getRotations();
        FloatBuffer opacities = splatData.getOpacities();
        FloatBuffer shs = splatData.getShs();

        int i = 0;
        for (Splat splat : splats)
        {
            positions.put(i * 3 + 0, splat.getPositionX());
            positions.put(i * 3 + 1, splat.getPositionY());
            positions.put(i * 3 + 2, splat.getPositionZ());

            scales.put(i * 3 + 0, splat.getScaleX());
            scales.put(i * 3 + 1, splat.getScaleY());
            scales.put(i * 3 + 2, splat.getScaleZ());

            rotations.put(i * 4 + 0, splat.getRotationX());
            rotations.put(i * 4 + 1, splat.getRotationY());
            rotations.put(i * 4 + 2, splat.getRotationZ());
            rotations.put(i * 4 + 3, splat.getRotationW());

            opacities.put(i, splat.getOpacity());

            for (int d = 0; d < shDimensions; d++)
            {
                shs.put(((i * shDimensions) + d) * 3 + 0, splat.getShX(d));
                shs.put(((i * shDimensions) + d) * 3 + 1, splat.getShY(d));
                shs.put(((i * shDimensions) + d) * 3 + 2, splat.getShZ(d));
            }
            i++;
        }
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
        int shDimensions = Splats.dimensionsForDegree(shDegree);
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

            splat.setPositionX(positions.get(i * 3 + 0));
            splat.setPositionY(positions.get(i * 3 + 1));
            splat.setPositionZ(positions.get(i * 3 + 2));

            splat.setScaleX(scales.get(i * 3 + 0));
            splat.setScaleY(scales.get(i * 3 + 1));
            splat.setScaleZ(scales.get(i * 3 + 2));

            splat.setRotationX(rotations.get(i * 4 + 0));
            splat.setRotationY(rotations.get(i * 4 + 1));
            splat.setRotationZ(rotations.get(i * 4 + 2));
            splat.setRotationW(rotations.get(i * 4 + 3));

            splat.setOpacity(opacities.get(i));

            for (int d = 0; d < shDimensions; d++)
            {
                splat.setShX(d, shs.get(((i * shDimensions) + d) * 3 + 0));
                splat.setShY(d, shs.get(((i * shDimensions) + d) * 3 + 1));
                splat.setShZ(d, shs.get(((i * shDimensions) + d) * 3 + 2));
            }
            splats.add(splat);
        }
        return splats;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatDatas()
    {
        // Private constructor to prevent instantiation
    }

}