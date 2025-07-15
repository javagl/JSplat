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
import java.util.Arrays;
import java.util.List;

import de.javagl.jsplat.Splat;

/**
 * A basic implementation of a {@link SplatSorter}
 */
class BasicSplatSorter implements SplatSorter
{
    /**
     * An entry used for sorting the splats by their distance
     */
    private static class DepthEntry
    {
        /**
         * The index of the splat
         */
        int index;

        /**
         * The depth of the splat
         */
        float depth;

        @Override
        public String toString()
        {
            return "(" + index + ", " + depth + ")";
        }
    }

    /**
     * The splats that are supposed to be sorted
     */
    private List<? extends Splat> splats;

    /**
     * The entries for the splat sorting computations
     */
    private DepthEntry depthEntries[];

    /**
     * The indices as they have been computed in the last sorting pass
     */
    private int indices[];

    /**
     * The third row of the view matrix that was used for the previous sorting
     * pass
     */
    private final float previousViewMatrixRow[] = new float[]
    { Float.NaN, Float.NaN, Float.NaN, Float.NaN };

    /**
     * Creates a new instance
     */
    BasicSplatSorter()
    {
        // Default constructor
    }

    @Override
    public void init(List<? extends Splat> splats)
    {
        this.splats = splats;
        depthEntries = new DepthEntry[splats.size()];
        for (int i = 0; i < splats.size(); i++)
        {
            DepthEntry depthEntry = new DepthEntry();
            depthEntry.index = i;
            depthEntry.depth = 0.0f;
            depthEntries[i] = depthEntry;
        }
        indices = new int[splats.size()];
        Arrays.fill(previousViewMatrixRow, Float.NaN);
    }

    @Override
    public void sort(FloatBuffer viewMatrix)
    {
        float mx = viewMatrix.get(0 * 4 + 2);
        float my = viewMatrix.get(1 * 4 + 2);
        float mz = viewMatrix.get(2 * 4 + 2);
        float mw = viewMatrix.get(3 * 4 + 2);
        if (!viewMatrixChanged(mx, my, mz, mw))
        {
            return;
        }
        performSort(mx, my, mz, mw);
        finishSort();
    }

    /**
     * Returns whether the view matrix elements have changed since the previous
     * call
     * 
     * @param mx The x-element of row 2 of the view matrix
     * @param my The y-element of row 2 of the view matrix
     * @param mz The z-element of row 2 of the view matrix
     * @param mw The w-element of row 2 of the view matrix
     * @return Whether the elements have changed
     */
    protected boolean viewMatrixChanged(float mx, float my, float mz, float mw)
    {
        if (mx != previousViewMatrixRow[0] || my != previousViewMatrixRow[1]
            || mz != previousViewMatrixRow[2] || mw != previousViewMatrixRow[3])
        {
            previousViewMatrixRow[0] = mx;
            previousViewMatrixRow[1] = my;
            previousViewMatrixRow[2] = mz;
            previousViewMatrixRow[3] = mw;
            return true;
        }
        return false;
    }

    /**
     * Perform the actual sorting
     * 
     * @param mx The x-element of row 2 of the view matrix
     * @param my The y-element of row 2 of the view matrix
     * @param mz The z-element of row 2 of the view matrix
     * @param mw The w-element of row 2 of the view matrix
     */
    protected void performSort(float mx, float my, float mz, float mw)
    {
        int numSplats = splats.size();
        for (int i = 0; i < numSplats; i++)
        {
            Splat s = splats.get(i);
            float px = s.getPositionX();
            float py = s.getPositionY();
            float pz = s.getPositionZ();
            float depth = mx * px + my * py + mz * pz + mw;
            DepthEntry depthEntry = depthEntries[i];
            depthEntry.index = i;
            depthEntry.depth = depth;
        }
        Arrays.parallelSort(depthEntries, (e0, e1) ->
        {
            if (e0.depth < e1.depth)
            {
                return -1;
            }
            if (e0.depth > e1.depth)
            {
                return 1;
            }
            return 0;
        });
    }

    /**
     * Finish the sorting operation by writing the indices of the sorting
     * entries into the indices array in their current order.
     * 
     * NOTE: This is an own method (and synchronized) in anticipation of the
     * ThreadedSplatSorter. The goal is to prevent concurrent read- and write
     * operations to the indices array. This should be considered as a
     * package-private implementation detail, and could be solved differently.
     */
    protected synchronized void finishSort()
    {
        int numSplats = splats.size();
        for (int i = 0; i < numSplats; i++)
        {
            DepthEntry depthEntry = depthEntries[i];
            indices[i] = depthEntry.index;
        }
    }

    // See NOTE about synchronization in finishSort
    @Override
    public synchronized void apply(IntBuffer buffer)
    {
        buffer.slice().put(indices);
    }

}
