/*
 * www.javagl.de - JSplat
 *
 * Copyright 2026 Marco Hutter - http://www.javagl.de
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
package de.javagl.jsplat.simplification.incremental;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Methods for computing pairs of elements from edges
 */
public class Pairs
{
    /**
     * Selects up to the specified number of edges from the given list, ordered
     * by their weight, in incresing order
     * 
     * @param edges The edges
     * @param numElements The number of elements that the edge indices refer to
     * @param numPairsToSelect The maximum number of pairs to select
     * @return The pairs
     */
    public static List<Edge> select(List<Edge> edges, int numElements,
        int numPairsToSelect)
    {
        if (edges.isEmpty())
        {
            return Collections.emptyList();
        }
        Edge valid[] = new Edge[edges.size()];
        int counter = 0;
        for (Edge edge : edges)
        {
            if (Float.isFinite(edge.weight))
            {
                valid[counter] = edge;
                counter++;
            }
        }
        if (counter == 0)
        {
            return Collections.emptyList();
        }
        Arrays.parallelSort(valid, 0, counter, (e0, e1) ->
        {
            if (e0.weight < e1.weight)
            {
                return -1;
            }
            if (e0.weight > e1.weight)
            {
                return 1;
            }
            return 0;
        });

        boolean used[] = new boolean[numElements];
        List<Edge> pairs = new ArrayList<Edge>(numPairsToSelect);
        for (int i = 0; i < counter; i++)
        {
            Edge e = valid[i];
            if (used[e.i0] || used[e.i1])
            {
                continue;
            }
            used[e.i0] = true;
            used[e.i1] = true;
            pairs.add(e);
            if (pairs.size() >= numPairsToSelect)
            {
                break;
            }
        }
        return pairs;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Pairs()
    {
        // Private constructor to prevent instantiation
    }
}
