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
     * Creates a new instance
     *  
     * @param sx The scaling along x
     * @param sy The scaling along y
     * @param sz The scaling along z
     */
    SplatScaleScaler(float sx, float sy, float sz)
    {
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
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