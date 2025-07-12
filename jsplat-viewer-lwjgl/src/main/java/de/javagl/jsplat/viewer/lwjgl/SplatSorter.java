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
package de.javagl.jsplat.viewer.lwjgl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import de.javagl.jsplat.Splat;

/**
 * A thin, internal abstraction layer for classes that can sort splats for a
 * GL-based viewer.
 */
interface SplatSorter
{
    /**
     * Initialize this sorter with the given splats
     * 
     * @param splats The splats
     */
    void init(List<? extends Splat> splats);

    /**
     * Perform the sort operation for the splats, based on the given view matrix.
     * 
     * @param viewMatrix The view matrix, as a 16-element buffer in column-major
     *        order
     */
    void sort(FloatBuffer viewMatrix);

    /**
     * Apply the current sort order to the given buffer, filling it with the
     * indices of the splats in their sorted order
     * 
     * @param buffer The buffer
     */
    void apply(IntBuffer buffer);
}
