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
    private double data[];

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
        this.data = new double[3 + 3 + 4 + 1 + shDimensions * 3];
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
    public double getPositionX()
    {
        return data[0];
    }

    @Override
    public double getPositionY()
    {
        return data[1];
    }

    @Override
    public double getPositionZ()
    {
        return data[2];
    }

    @Override
    public double getScaleX()
    {
        return data[3];
    }

    @Override
    public double getScaleY()
    {
        return data[4];
    }

    @Override
    public double getScaleZ()
    {
        return data[5];
    }

    @Override
    public double getRotationX()
    {
        return data[6];
    }

    @Override
    public double getRotationY()
    {
        return data[7];
    }

    @Override
    public double getRotationZ()
    {
        return data[8];
    }

    @Override
    public double getRotationW()
    {
        return data[9];
    }

    @Override
    public double getOpacity()
    {
        return data[10];
    }

    @Override
    public double getShX(int dimension)
    {
        return data[11 + dimension * 3 + 0];
    }

    @Override
    public double getShY(int dimension)
    {
        return data[11 + dimension * 3 + 1];
    }

    @Override
    public double getShZ(int dimension)
    {
        return data[11 + dimension * 3 + 2];
    }

    @Override
    public void setPositionX(double v)
    {
        data[0] = v;
    }

    @Override
    public void setPositionY(double v)
    {
        data[1] = v;
    }

    @Override
    public void setPositionZ(double v)
    {
        data[2] = v;
    }

    @Override
    public void setScaleX(double v)
    {
        data[3] = v;
    }

    @Override
    public void setScaleY(double v)
    {
        data[4] = v;
    }

    @Override
    public void setScaleZ(double v)
    {
        data[5] = v;
    }

    @Override
    public void setRotationX(double v)
    {
        data[6] = v;
    }

    @Override
    public void setRotationY(double v)
    {
        data[7] = v;
    }

    @Override
    public void setRotationZ(double v)
    {
        data[8] = v;
    }

    @Override
    public void setRotationW(double v)
    {
        data[9] = v;
    }

    @Override
    public void setOpacity(double v)
    {
        data[10] = v;
    }

    @Override
    public void setShX(int dimension, double v)
    {
        data[11 + dimension * 3 + 0] = v;
    }

    @Override
    public void setShY(int dimension, double v)
    {
        data[11 + dimension * 3 + 1] = v;
    }

    @Override
    public void setShZ(int dimension, double v)
    {
        data[11 + dimension * 3 + 2] = v;
    }

}