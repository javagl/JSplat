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

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import de.javagl.jsplat.MutableSplat;

/**
 * Internal class for transforming the position of splats
 */
class SplatPositionTransformer
{
    // That use of Unsafe in JOML was a mistake.
    static
    {
        System.setProperty("joml.nounsafe", "true");
    }

    /**
     * The transform matrix
     */
    private final Matrix4f matrix;

    /**
     * A temporary vector for the transform
     */
    private final Vector3f v;


    /**
     * Creates a new instance for the given matrix.
     * 
     * The given matrix is a 4x4 matrix, stored in a 16-element array, in
     * column-major order
     * 
     * @param matrix The matrix
     */
    SplatPositionTransformer(float matrix[])
    {
        this.matrix = new Matrix4f(FloatBuffer.wrap(matrix));
        this.v = new Vector3f();
    }

    /**
     * Transform the position of the given splat
     * 
     * @param s The splat
     */
    void transform(MutableSplat s)
    {
        v.x = s.getPositionX();
        v.y = s.getPositionY();
        v.z = s.getPositionZ();
        matrix.transformPosition(v);
        s.setPositionX(v.x);
        s.setPositionY(v.y);
        s.setPositionZ(v.z);
    }

}