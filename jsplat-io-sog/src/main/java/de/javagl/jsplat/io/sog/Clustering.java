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
package de.javagl.jsplat.io.sog;

/**
 * Utilities for computing a clustering, for SOG.
 * 
 * Some comments here are a bit shallow, I know. Maybe I'll add details later...
 */
class Clustering
{
    /**
     * Default iterations for K-Means clustering
     */
    private static final int DEFAULT_ITERATIONS = 10;
    
    /**
     * Package-private class representing the result of a clustering
     */
    static class ClusteringResult
    {
        /**
         * The centroids
         */
        float centroids[][];
        
        /**
         * The labels
         */
        int labels[];
    }
    
    /**
     * Compute a clustering result from the given data
     * @param data The data
     * @param k The desired number of clusters
     * @return The result
     */
    static ClusteringResult compute(double data[][], int k) 
    {
        return ClusteringElki.compute(data, k, DEFAULT_ITERATIONS);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Clustering()
    {
        // Private constructor to prevent instantiation
    }

}
