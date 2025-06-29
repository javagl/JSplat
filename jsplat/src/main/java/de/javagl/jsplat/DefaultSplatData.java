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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Default implementation of a {@link SplatData}
 */
class DefaultSplatData implements SplatData
{
    /**
     * The spherical harmonics degree
     */
    private final int shDegree;
    
    /**
     * The size
     */
    private final int size;

    /**
     * The positions
     */
    private final FloatBuffer positions;
    
    /**
     * The scales
     */
    private final FloatBuffer scales;
    
    /**
     * The rotations
     */
    private final FloatBuffer rotations;
    
    /**
     * The opacities
     */
    private final FloatBuffer opacities;
    
    /**
     * The spherical harmonics
     */
    private final FloatBuffer shs;
    
    
    /**
     * Creates a new instance
     * 
     * @param shDegree The spherical harmonics degree
     * @param size The number of splats
     * @throws IllegalArgumentException if the given degree or size is negative
     */
    DefaultSplatData(int shDegree, int size)
    {
        if (shDegree < 0)
        {
            throw new IllegalArgumentException(
                "The spherical harmonics degree may not be negative, but is "
                    + shDegree);
        }
        if (size < 0)
        {
            throw new IllegalArgumentException(
                "The size may not be negative, but is " + size);
        }
        this.shDegree = shDegree;
        this.size = size;
        
        this.positions = createFloatBuffer(size * 3);
        this.scales = createFloatBuffer(size * 3);
        this.rotations = createFloatBuffer(size * 4);
        this.opacities = createFloatBuffer(size);
        
        int dimensions = Splats.dimensionsForDegree(shDegree);
        this.shs = createFloatBuffer(dimensions * size * 3);
    }

    /**
     * Creates a new direct float buffer with the given size and native
     * byte order
     * 
     * @param size The size
     * @return The buffer
     */
    private static FloatBuffer createFloatBuffer(int size)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(size * Float.BYTES);
        bb.order(ByteOrder.nativeOrder());
        return bb.asFloatBuffer();
    }
    
    @Override
    public int getShDegree()
    {
        return shDegree;
    }

    @Override
    public int getShDimensions()
    {
        return Splats.dimensionsForDegree(shDegree);
    }
    
    @Override
    public int getSize()
    {
        return size;
    }

    @Override
    public FloatBuffer getPositions()
    {
        return positions.slice();
    }

    @Override
    public FloatBuffer getScales()
    {
        return scales.slice();
    }

    @Override
    public FloatBuffer getRotations()
    {
        return rotations.slice();
    }

    @Override
    public FloatBuffer getOpacities()
    {
        return opacities.slice();
    }

    @Override
    public FloatBuffer getShs()
    {
        return shs.slice();
    }

}
