/*
 * This file is based on https://github.com/andrewwillmott/sh-lib
 * It is a port of the "RotateSH" function from /SHLib.cpp, line 1090
 * Commit 8821cba4acc2273ab20417388df16bd0012f0760
 * 
 * Original sh-lib license:
 * 
 * ============================================================================
 * 
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute 
 * this software, either in source code form or as a compiled binary, for any 
 * purpose, commercial or non-commercial, and by any means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors of 
 * this software dedicate any and all copyright interest in the software to the 
 * public domain. We make this dedication for the benefit of the public at 
 * large and to the detriment of our heirs and successors. We intend this 
 * dedication to be an overt act of relinquishment in perpetuity of all present
 * and future rights to this software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * For more information, please refer to http://unlicense.org
 */
package de.javagl.jsplat.processing;

import de.javagl.jsplat.MutableSplat;

/**
 * Internal class for (linearly) scaling the scale of splats
 */
class SplatScaleScaler
{
    /**
     * The scale factor along x
     */
    private final float sx;

    /**
     * The scale factor along y
     */
    private final float sy;

    /**
     * The scale factor along z
     */
    private final float sz;

    /**
     * Creates a new instance for the scaling of the given matrix
     * 
     * The given matrix is a 3x3 matrix, stored in a 9-element array, in
     * column-major order
     * 
     * @param matrix The matrix
     */
    SplatScaleScaler(float matrix[])
    {
        float m00 = matrix[0];
        float m01 = matrix[1];
        float m02 = matrix[2];

        float m10 = matrix[3];
        float m11 = matrix[4];
        float m12 = matrix[5];

        float m20 = matrix[6];
        float m21 = matrix[7];
        float m22 = matrix[8];
        
        this.sx = (float)Math.sqrt(m00 * m00 + m01 * m01 + m02 * m02);
        this.sy = (float)Math.sqrt(m10 * m10 + m11 * m11 + m12 * m12);
        this.sz = (float)Math.sqrt(m20 * m20 + m21 * m21 + m22 * m22);
    }

    /**
     * Scale the scaling of the given splat (linearly)
     * 
     * @param s The splat
     */
    void scale(MutableSplat s)
    {
        s.setScaleX((float)Math.log(Math.exp(s.getScaleX()) * sx));
        s.setScaleY((float)Math.log(Math.exp(s.getScaleY()) * sy));
        s.setScaleZ((float)Math.log(Math.exp(s.getScaleZ()) * sz));
    }

}