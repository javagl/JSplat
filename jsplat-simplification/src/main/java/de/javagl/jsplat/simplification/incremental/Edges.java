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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.javagl.jsplat.Splat;

/**
 * Computation of edges between splats and their nearest neighbors
 */
public class Edges
{

    /**
     * Computes edges between each of the given splats and its k nearest
     * neighbors
     * 
     * @param splats The splats
     * @param k The k
     * @return The edges
     */
    public static List<Edge> compute(List<? extends Splat> splats, int k)
    {
        double[][] positions = extractPositions(splats);
        KNN knn = KNNs.create(positions);
        int n = positions.length;
        List<Edge> edges = IntStream.range(0, n).parallel().mapToObj(i0 ->
        {
            List<Edge> innerEdges = new ArrayList<Edge>(k);
            double[] query = positions[i0];
            List<Integer> knnIndices = knn.compute(query, k + 1);
            for (int j = 1; j < knnIndices.size(); j++)
            {
                int i1 = knnIndices.get(j);
                Edge edge = new Edge(i0, i1);
                innerEdges.add(edge);
            }
            return innerEdges;
        }).flatMap(e -> e.stream()).collect(Collectors.toList());
        return edges;
    }

    /**
     * Extract the positions from the given splats, as an array of 3-element
     * arrays
     * 
     * @param splats The splats
     * @return The positions
     */
    private static double[][] extractPositions(List<? extends Splat> splats)
    {
        int n = splats.size();
        double result[][] = new double[n][];
        IntStream.range(0, n).parallel().forEach(i ->
        {
            Splat s = splats.get(i);
            double x = s.getPositionX();
            double y = s.getPositionY();
            double z = s.getPositionZ();
            double p[] =
            { x, y, z };
            result[i] = p;
        });
        return result;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Edges()
    {
        // Private constructor to prevent instantiation
    }

}
