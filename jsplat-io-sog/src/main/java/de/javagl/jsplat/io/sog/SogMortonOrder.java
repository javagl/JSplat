/*
 * www.javagl.de - JSplat
 * 
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 * 
 * This file contains code that was ported from different files of
 * https://github.com/playcanvas/splat-transform 
 * commit 5ee7baa7b3a77c221d8522d0ffc2497b45f087f0
 * published under the MIT/X11 license.
 * 
 * Original license header:
 * 
 * Copyright (c) 2011-2025 PlayCanvas Ltd.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.javagl.jsplat.io.sog;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Functions for computing the SOG representation of splats.
 * 
 * Mostly ported from https://github.com/playcanvas/splat-transform
 * (morton-order.ts). Refer to that file for comments. LOL.
 */
@SuppressWarnings("javadoc")
class SogMortonOrder
{
    static void generate(IntBuffer indices, IntFloatFunction cx,
        IntFloatFunction cy, IntFloatFunction cz)
    {
        float mx = 0.0f;
        float my = 0.0f;
        float mz = 0.0f;
        float Mx = 0.0f;
        float My = 0.0f;
        float Mz = 0.0f;

        // calculate scene extents across all splats (using sort
        // centers, because they're in world space)
        for (int i = 0; i < indices.capacity(); ++i)
        {
            int ri = indices.get(i);
            float x = cx.apply(ri);
            float y = cy.apply(ri);
            float z = cz.apply(ri);

            if (i == 0)
            {
                mx = Mx = x;
                my = My = y;
                mz = Mz = z;
            }
            else
            {
                if (x < mx)
                {
                    mx = x;
                }
                else if (x > Mx)
                {
                    Mx = x;
                }
                if (y < my)
                {
                    my = y;
                }
                else if (y > My)
                {
                    My = y;
                }
                if (z < mz)
                {
                    mz = z;
                }
                else if (z > Mz)
                {
                    Mz = z;
                }
            }
        }

        float xlen = Mx - mx;
        float ylen = My - my;
        float zlen = Mz - mz;

        if (!Float.isFinite(xlen) || !Float.isFinite(ylen)
            || !Float.isFinite(zlen))
        {
            System.err
                .println("invalid extents: " + xlen + " " + ylen + " " + zlen);
            return;
        }

        // all points are identical
        if (xlen == 0.0f && ylen == 0.0f && zlen == 0.0f)
        {
            return;
        }

        float xmul = (xlen == 0.0f) ? 0.0f : 1024.0f / xlen;
        float ymul = (ylen == 0.0f) ? 0.0f : 1024.0f / ylen;
        float zmul = (zlen == 0.0f) ? 0.0f : 1024.0f / zlen;

        int morton[] = new int[indices.capacity()];
        for (int i = 0; i < indices.capacity(); ++i)
        {
            int ri = indices.get(i);
            float x = cx.apply(ri);
            float y = cy.apply(ri);
            float z = cz.apply(ri);

            int ix = (int) Math.min(1023, (x - mx) * xmul);
            int iy = (int) Math.min(1023, (y - my) * ymul);
            int iz = (int) Math.min(1023, (z - mz) * zmul);

            morton[i] = encodeMorton3(ix, iy, iz);
        }

        // sort indices by morton code
        List<Integer> order = new ArrayList<Integer>();
        for (int i = 0; i < indices.capacity(); i++)
        {
            order.add(indices.get(i));
        }
        Collections.sort(order, (i0, i1) ->
        {
            return morton[i0] - morton[i1];
        });

        IntBuffer tmpIndices = IntBuffer.allocate(indices.capacity());
        tmpIndices.slice().put(indices.slice());
        for (int i = 0; i < indices.capacity(); ++i)
        {
            indices.put(i, tmpIndices.get(order.get(i)));
        }

        // sort the largest buckets recursively
        int start = 0;
        int end = 1;
        while (start < indices.capacity())
        {
            while (end < indices.capacity()
                && morton[order.get(end)] == morton[order.get(start)])
            {
                ++end;
            }

            if (end - start > 256)
            {
                // logger.debug('sorting', end - start);
                IntBuffer s = indices.slice();
                s.position(start);
                s.limit(start + end);
                IntBuffer subarray = s.slice();
                generate(subarray, cx, cy, cz);
            }

            start = end;
        }
    };

    private static int Part1By2(int x)
    {
        int r = x;
        r &= 0x000003ff;
        r = (r ^ (r << 16)) & 0xff0000ff;
        r = (r ^ (r << 8)) & 0x0300f00f;
        r = (r ^ (r << 4)) & 0x030c30c3;
        r = (r ^ (r << 2)) & 0x09249249;
        return r;
    };

    // https://fgiesen.wordpress.com/2009/12/13/decoding-morton-codes/
    private static int encodeMorton3(int x, int y, int z)
    {
        return (Part1By2(z) << 2) + (Part1By2(y) << 1) + Part1By2(x);
    };
    
    /**
     * Private constructor to prevent instantiation
     */
    private SogMortonOrder()
    {
        // Private constructor to prevent instantiation
    }
}
