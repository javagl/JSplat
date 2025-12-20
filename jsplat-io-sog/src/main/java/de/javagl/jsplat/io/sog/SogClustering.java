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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.javagl.jsplat.io.sog.Clustering.ClusteringResult;

/**
 * Functions for computing the SOG representation of splats.
 * 
 * Mostly ported from https://github.com/playcanvas/splat-transform
 */
class SogClustering
{
    /**
     * Package-private class representing the 1D clustering result for SOG
     */
    static class ClusteringResult1D
    {
        /**
         * The centroids
         */
        float centroids[];

        /**
         * The labels
         */
        byte labels[][];
    }

    /**
     * Compute a clustering of the given data.
     * 
     * The result will describe a clustering consisting of 256 centroids and the
     * cluster labels of the elements.
     * 
     * @param numRows The number of rows of the input data
     * @param columns The columns of the input data
     * @return The clustering result
     */
    static ClusteringResult1D cluster1d(int numRows, IntFloatFunction columns[])
    {
        // Convert the given data into a 1D table
        int numColumns = columns.length;
        double data[][] = new double[numRows * numColumns][1];
        for (int i = 0; i < numColumns; ++i)
        {
            for (int r = 0; r < numRows; r++)
            {
                data[i * numRows + r][0] = columns[i].apply(r);
            }
        }

        // Compute the ND-clustering
        ClusteringResult clusteringResult = Clustering.compute(data, 256);
        float[][] centroids = clusteringResult.centroids;
        int[] labels = clusteringResult.labels;

        // Ported from the SOG implementation:

        // order centroids smallest to largest
        List<float[]> centroidsData = Arrays.asList(centroids);
        List<Integer> order = new ArrayList<Integer>();
        for (int i = 0; i < centroidsData.size(); i++)
        {
            order.add(i);
        }
        Collections.sort(order, (i0, i1) ->
        {
            float[] c0 = centroidsData.get(i0);
            float[] c1 = centroidsData.get(i1);
            return Float.compare(c0[0], c1[0]);
        });

        // reorder centroids
        List<float[]> tmp = new ArrayList<float[]>(centroidsData);
        for (int i = 0; i < order.size(); ++i)
        {
            centroidsData.set(i, tmp.get(order.get(i)));
        }
        int invOrder[] = new int[order.size()];
        for (int i = 0; i < order.size(); ++i)
        {
            invOrder[order.get(i)] = i;
        }

        // reorder labels
        for (int i = 0; i < labels.length; i++)
        {
            labels[i] = invOrder[labels[i]];
        }

        // Convert the result into the 1D clustering result
        byte labels1D[][] = new byte[numRows][];
        for (int i = 0; i < numRows; ++i)
        {
            labels1D[i] = new byte[numColumns];
        }
        for (int i = 0; i < numColumns; ++i)
        {
            for (int r = 0; r < numRows; r++)
            {
                labels1D[r][i] = (byte) labels[i * numRows + r];
            }
        }
        ClusteringResult1D clusteringResult1D = new ClusteringResult1D();
        clusteringResult1D.centroids = new float[256];
        for (int i = 0; i < centroids.length; i++)
        {
            clusteringResult1D.centroids[i] = centroids[i][0];
        }
        clusteringResult1D.labels = labels1D;
        return clusteringResult1D;
    };

    /**
     * Private constructor to prevent instantiation
     */
    private SogClustering()
    {
        // Private constructor to prevent instantiation
    }

}
