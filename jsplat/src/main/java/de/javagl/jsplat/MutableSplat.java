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
 * Interface for a {@link Splat} where the property values can be modified.
 */
public interface MutableSplat extends Splat
{
    /**
     * Set the x-coordinate of the position
     * 
     * @param v The value
     */
    void setPositionX(float v);

    /**
     * Set the y-coordinate of the position
     * 
     * @param v The value
     */
    void setPositionY(float v);

    /**
     * Set the z-coordinate of the position
     * 
     * @param v The value
     */
    void setPositionZ(float v);

    /**
     * Set scale factor in x-direction
     * 
     * @param v The value
     */
    void setScaleX(float v);

    /**
     * Set scale factor in y-direction
     * 
     * @param v The value
     */
    void setScaleY(float v);

    /**
     * Set scale factor in y-direction
     * 
     * @param v The value
     */
    void setScaleZ(float v);

    /**
     * Set the x-component of the rotation quaternion
     * 
     * @param v The value
     */
    void setRotationX(float v);

    /**
     * Set the y-component of the rotation quaternion
     * 
     * @param v The value
     */
    void setRotationY(float v);

    /**
     * Set the z-component of the rotation quaternion
     * 
     * @param v The value
     */
    void setRotationZ(float v);

    /**
     * Set the w-component of the rotation quaternion
     * 
     * @param v The value
     */
    void setRotationW(float v);

    /**
     * Set the opacity value
     * 
     * @param v The value
     */
    void setOpacity(float v);

    /**
     * Set the x-component of the spherical harmonic for the given dimension
     * 
     * @param dimension The dimension
     * @param v The value
     */
    void setShX(int dimension, float v);

    /**
     * Set the y-component of the spherical harmonic for the given dimension
     * 
     * @param dimension The dimension
     * @param v The value
     */
    void setShY(int dimension, float v);

    /**
     * Set the z-component of the spherical harmonic for the given dimension
     * 
     * @param dimension The dimension
     * @param v The value
     */
    void setShZ(int dimension, float v);

}
