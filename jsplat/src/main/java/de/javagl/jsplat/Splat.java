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
 * Interface for a generic, basic, read-only Gaussian splat.
 */
public interface Splat
{
    /**
     * Returns the x-coordinate of the position
     * 
     * @return The value
     */
    float getPositionX();

    /**
     * Returns the y-coordinate of the position
     * 
     * @return The value
     */
    float getPositionY();

    /**
     * Returns the z-coordinate of the position
     * 
     * @return The value
     */
    float getPositionZ();

    /**
     * Returns the scale factor in x-direction
     * 
     * @return The value
     */
    float getScaleX();

    /**
     * Returns the scale factor in y-direction
     * 
     * @return The value
     */
    float getScaleY();

    /**
     * Returns the scale factor in z-direction
     * 
     * @return The value
     */
    float getScaleZ();

    /**
     * Returns the x-component of the rotation quaternion
     * 
     * @return The value
     */
    float getRotationX();

    /**
     * Returns the y-component of the rotation quaternion
     * 
     * @return The value
     */
    float getRotationY();

    /**
     * Returns the z-component of the rotation quaternion
     * 
     * @return The value
     */
    float getRotationZ();

    /**
     * Returns the w-component of the rotation quaternion
     * 
     * @return The value
     */
    float getRotationW();

    /**
     * Returns the opacity value
     * 
     * @return The value
     */
    float getOpacity();

    /**
     * Returns the spherical harmonics degree
     * 
     * @return The value
     */
    int getShDegree();

    /**
     * Returns the number of dimensions for the sperical harmonics degree
     * 
     * @return The number of dimensions
     */
    int getShDimensions();

    /**
     * Returns the x-component of the spherical harmonic for the given dimension
     * 
     * @param dimension The dimension
     * @return The value
     */
    float getShX(int dimension);

    /**
     * Returns the y-component of the spherical harmonic for the given dimension
     * 
     * @param dimension The dimension
     * @return The value
     */
    float getShY(int dimension);

    /**
     * Returns the z-component of the spherical harmonic for the given dimension
     * 
     * @param dimension The dimension
     * @return The value
     */
    float getShZ(int dimension);
}