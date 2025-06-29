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

/**
 * Default implementation of a {@link MutableSplat}
 */
class DefaultSplat implements MutableSplat
{
    /**
     * The spherical harmonics degree
     */
    private final int shDegree;

    /**
     * The spherical harmonics dimensions
     */
    private final int shDimensions;

    /**
     * The actual data
     */
    private float data[];

    /**
     * Creates a new instance
     * 
     * @param shDegree The spherical harmonics degree
     * @throws IllegalArgumentException if the given degree is negative
     */
    DefaultSplat(int shDegree)
    {
        if (shDegree < 0)
        {
            throw new IllegalArgumentException(
                "The spherical harmonics degree may not be negative, but is "
                    + shDegree);
        }
        this.shDegree = shDegree;
        this.shDimensions = Splats.dimensionsForDegree(shDegree);
        this.data = new float[3 + 3 + 4 + 1 + shDimensions * 3];
    }

    @Override
    public int getShDegree()
    {
        return shDegree;
    }

    @Override
    public int getShDimensions()
    {
        return shDimensions;
    }

    @Override
    public float getPositionX()
    {
        return data[0];
    }

    @Override
    public float getPositionY()
    {
        return data[1];
    }

    @Override
    public float getPositionZ()
    {
        return data[2];
    }

    @Override
    public float getScaleX()
    {
        return data[3];
    }

    @Override
    public float getScaleY()
    {
        return data[4];
    }

    @Override
    public float getScaleZ()
    {
        return data[5];
    }

    @Override
    public float getRotationX()
    {
        return data[6];
    }

    @Override
    public float getRotationY()
    {
        return data[7];
    }

    @Override
    public float getRotationZ()
    {
        return data[8];
    }

    @Override
    public float getRotationW()
    {
        return data[9];
    }

    @Override
    public float getOpacity()
    {
        return data[10];
    }

    @Override
    public float getShX(int dimension)
    {
        return data[11 + dimension * 3 + 0];
    }

    @Override
    public float getShY(int dimension)
    {
        return data[11 + dimension * 3 + 1];
    }

    @Override
    public float getShZ(int dimension)
    {
        return data[11 + dimension * 3 + 2];
    }

    @Override
    public void setPositionX(float v)
    {
        data[0] = v;
    }

    @Override
    public void setPositionY(float v)
    {
        data[1] = v;
    }

    @Override
    public void setPositionZ(float v)
    {
        data[2] = v;
    }

    @Override
    public void setScaleX(float v)
    {
        data[3] = v;
    }

    @Override
    public void setScaleY(float v)
    {
        data[4] = v;
    }

    @Override
    public void setScaleZ(float v)
    {
        data[5] = v;
    }

    @Override
    public void setRotationX(float v)
    {
        data[6] = v;
    }

    @Override
    public void setRotationY(float v)
    {
        data[7] = v;
    }

    @Override
    public void setRotationZ(float v)
    {
        data[8] = v;
    }

    @Override
    public void setRotationW(float v)
    {
        data[9] = v;
    }

    @Override
    public void setOpacity(float v)
    {
        data[10] = v;
    }

    @Override
    public void setShX(int dimension, float v)
    {
        data[11 + dimension * 3 + 0] = v;
    }

    @Override
    public void setShY(int dimension, float v)
    {
        data[11 + dimension * 3 + 1] = v;
    }

    @Override
    public void setShZ(int dimension, float v)
    {
        data[11 + dimension * 3 + 2] = v;
    }

}