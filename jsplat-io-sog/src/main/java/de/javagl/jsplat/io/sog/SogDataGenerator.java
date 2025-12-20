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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.io.sog.Clustering.ClusteringResult;
import de.javagl.jsplat.io.sog.SogClustering.ClusteringResult1D;
import de.javagl.jsplat.io.sog.meta.Asset;
import de.javagl.jsplat.io.sog.meta.Means;
import de.javagl.jsplat.io.sog.meta.Meta;
import de.javagl.jsplat.io.sog.meta.Quats;
import de.javagl.jsplat.io.sog.meta.Scales;
import de.javagl.jsplat.io.sog.meta.Sh0;
import de.javagl.jsplat.io.sog.meta.ShN;

/**
 * Package-private class to generate {@link SogData} from a list of splats.
 * 
 * The "core" of most of the "generate..." methods has been ported from
 * https://github.com/playcanvas/splat-transform
 */
class SogDataGenerator
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(SogDataGenerator.class.getName());

    /**
     * The square root of 2.0
     */
    private static final double SQRT2 = Math.sqrt(2);;

    /**
     * The input splats
     */
    private List<? extends Splat> splats;

    /**
     * The {@link SogData} that is filled with life here
     */
    private SogData sogData;

    /**
     * The indices for the SOG order
     */
    private IntBuffer indices;

    /**
     * The width of the images to generate
     */
    private int width;

    /**
     * The height of the images to generate
     */
    private int height;

    /**
     * The width of the SHN image to generate
     */
    private int shWidth;

    /**
     * The height of the SHN image to generate
     */
    private int shHeight;

    /**
     * The number of channels of the images
     */
    private int channels;

    /**
     * Creates a new instance
     */
    SogDataGenerator()
    {
        // Default constructor
    }

    /**
     * Create the {@link SogData} for the given splats
     * 
     * @param splats The splats
     * @return The {@link SogData}
     */
    SogData generate(List<? extends Splat> splats)
    {
        logger.fine("Generating SOG data for " + splats.size() + " splats");

        this.splats = splats;

        // Initialization of properties
        this.indices = generateIndices();
        int numRows = splats.size();
        this.width = (int) Math.ceil(Math.sqrt(numRows) / 4) * 4;
        this.height = (int) Math.ceil((float) numRows / width / 4) * 4;
        this.channels = 4;

        // Create the SogData object, and initialize the top-level metadata
        this.sogData = new SogData();

        // The meta object
        Meta meta = new Meta();
        meta.version = 2;
        meta.count = splats.size();
        sogData.meta = meta;

        // The meta.asset object
        Asset asset = new Asset();
        asset.generator = "JSplat - https://github.com/javagl/JSplat";
        meta.asset = asset;

        generateMeans();
        generateQuaternions();
        generateScales();
        generateColors();
        generateSh();

        logger.fine("Generating SOG data DONE");

        return sogData;
    };

    /**
     * Returns the image width
     * 
     * @return The image width
     */
    int getWidth()
    {
        return width;
    }

    /**
     * Returns the image height
     * 
     * @return The image height
     */
    int getHeight()
    {
        return height;
    }

    /**
     * Returns the SHN image width
     * 
     * @return The SHN image width
     */
    int getShWidth()
    {
        return shWidth;
    }

    /**
     * Returns the SHN image height
     * 
     * @return The SHN image height
     */
    int getShHeight()
    {
        return shHeight;
    }

    /**
     * Generate the indices that are required for the SOG-representation of the
     * splats, by encoding the positions in Morton order.
     * 
     * @return The indices
     */
    private IntBuffer generateIndices()
    {
        int n = splats.size();
        IntBuffer result = IntBuffer.allocate(n);
        for (int i = 0; i < n; i++)
        {
            result.put(i, i);
        }
        IntFloatFunction cx = i -> splats.get(i).getPositionX();
        IntFloatFunction cy = i -> splats.get(i).getPositionY();
        IntFloatFunction cz = i -> splats.get(i).getPositionZ();
        SogMortonOrder.generate(result, cx, cy, cz);
        return result;
    };

    /**
     * Some obscure layout function that was called in the SOG writer reference
     * implementation, but that was hard-wired to be the identity. Is it
     * important? Nobody knows. Let's just keep it, just to keep track of where
     * it was called...
     * 
     * @param i The input
     * @param width Ignored
     * @return The input
     */
    private static int layout(int i, int width)
    {
        return i;
    }

    /**
     * Generate the means of the current {@link SogData}
     */
    private void generateMeans()
    {
        logger.fine("Generating SOG means");

        byte meansL[] = new byte[width * height * channels];
        byte meansU[] = new byte[width * height * channels];

        IntFloatFunction cx = i -> splats.get(i).getPositionX();
        IntFloatFunction cy = i -> splats.get(i).getPositionY();
        IntFloatFunction cz = i -> splats.get(i).getPositionZ();
        IntFloatFunction columns[] =
        { cx, cy, cz };
        float meansMinMax[][] = computeMinMax(indices.capacity(), columns);
        for (int i = 0; i < meansMinMax.length; i++)
        {
            for (int j = 0; j < meansMinMax[i].length; j++)
            {
                meansMinMax[i][j] = logTransform(meansMinMax[i][j]);
            }
        }
        for (int i = 0; i < indices.capacity(); ++i)
        {
            int index = indices.get(i);
            float rowx = cx.apply(index);
            float rowy = cy.apply(index);
            float rowz = cz.apply(index);

            float x = meanTransform(rowx, meansMinMax[0]);
            float y = meanTransform(rowy, meansMinMax[1]);
            float z = meanTransform(rowz, meansMinMax[2]);

            int ti = layout(i, width);

            meansL[ti * 4 + 0] = (byte) ((int) x & 0xFF);
            meansL[ti * 4 + 1] = (byte) ((int) y & 0xFF);
            meansL[ti * 4 + 2] = (byte) ((int) z & 0xFF);
            meansL[ti * 4 + 3] = (byte) (0xFF);

            meansU[ti * 4 + 0] = (byte) ((((int) x) >> 8) & 0xFF);
            meansU[ti * 4 + 1] = (byte) ((((int) y) >> 8) & 0xFF);
            meansU[ti * 4 + 2] = (byte) ((((int) z) >> 8) & 0xFF);
            meansU[ti * 4 + 3] = (byte) (0xFF);
        }

        // Assign the result to the SogData
        Means means = new Means();
        means.mins = new float[]
        { meansMinMax[0][0], meansMinMax[1][0], meansMinMax[2][0], };
        means.maxs = new float[]
        { meansMinMax[0][1], meansMinMax[1][1], meansMinMax[2][1], };
        means.files = new String[]
        { "means_l.webp", "means_u.webp" };
        sogData.meta.means = means;
        sogData.meansL = meansL;
        sogData.meansU = meansU;

        logger.fine("Generating SOG means DONE");
    };

    /**
     * Apply that transform to the mean value in the way that SOG needs...
     * 
     * @param value The mean
     * @param minMax The minimum/maximum
     * @return The result
     */
    private static float meanTransform(float value, float minMax[])
    {
        float min = minMax[0];
        float max = minMax[1];
        return 65535.0f * (logTransform(value) - min) / (max - min);
    }

    /**
     * Generate the quaternions for the current {@link SogData}
     */
    private void generateQuaternions()
    {
        logger.fine("Generating SOG quaternions");

        byte quatsPixelData[] = new byte[width * height * channels];

        IntFloatFunction cw = i -> splats.get(i).getRotationW();
        IntFloatFunction cx = i -> splats.get(i).getRotationX();
        IntFloatFunction cy = i -> splats.get(i).getRotationY();
        IntFloatFunction cz = i -> splats.get(i).getRotationZ();
        float q[] = new float[]
        { 0, 0, 0, 0 };
        for (int i = 0; i < indices.capacity(); ++i)
        {
            int index = indices.get(i);
            float rowrot_0 = cw.apply(index);
            float rowrot_1 = cx.apply(index);
            float rowrot_2 = cy.apply(index);
            float rowrot_3 = cz.apply(index);
            q[0] = rowrot_0;
            q[1] = rowrot_1;
            q[2] = rowrot_2;
            q[3] = rowrot_3;
            encodeQuaternion(q, quatsPixelData, i, width);
        }

        // Assign the result to the SogData
        Quats quats = new Quats();
        quats.files = new String[]
        { "quats.webp" };
        sogData.meta.quats = quats;
        sogData.quats = quatsPixelData;

        logger.fine("Generating SOG quaternions DONE");
    };

    /**
     * Generate the scales for the current {@link SogData}
     */
    private void generateScales()
    {
        logger.fine("Generating SOG scales");

        IntFloatFunction cx = i -> splats.get(i).getScaleX();
        IntFloatFunction cy = i -> splats.get(i).getScaleY();
        IntFloatFunction cz = i -> splats.get(i).getScaleZ();
        IntFloatFunction columns[] =
        { cx, cy, cz };
        ClusteringResult1D scaleData =
            SogClustering.cluster1d(splats.size(), columns);
        byte[] scalesPixelData = convertTableToPixelData(3, scaleData.labels);

        // Assign the result to the SogData
        Scales scales = new Scales();
        scales.files = new String[]
        { "scales.webp" };
        scales.codebook = scaleData.centroids;
        sogData.meta.scales = scales;
        sogData.scales = scalesPixelData;

        logger.fine("Generating SOG scales DONE");
    }

    /**
     * Generate the colors for the current {@link SogData}
     */
    private void generateColors()
    {
        logger.fine("Generating SOG colors");

        IntFloatFunction cx = i -> splats.get(i).getShX(0);
        IntFloatFunction cy = i -> splats.get(i).getShY(0);
        IntFloatFunction cz = i -> splats.get(i).getShZ(0);
        IntFloatFunction columns[] =
        { cx, cy, cz };

        int numRows = splats.size();
        ClusteringResult1D colorData =
            SogClustering.cluster1d(numRows, columns);

        // generate and store sigmoid(opacity) [0..1]
        IntFloatFunction opacity = i -> splats.get(i).getOpacity();
        byte opacityData[] = new byte[numRows];
        for (int i = 0; i < numRows; ++i)
        {
            float alpha = Splats.opacityToAlpha(opacity.apply(i));
            opacityData[i] = (byte) Math.max(0, Math.min(255, alpha * 255));
        }

        // Append the opacity to the labels data
        byte newLabels[][] = new byte[numRows][4];
        for (int i = 0; i < numRows; i++)
        {
            newLabels[i] = Arrays.copyOf(colorData.labels[i], 4);
            newLabels[i][3] = opacityData[i];
        }
        colorData.labels = newLabels;
        byte[] colorPixelData = convertTableToPixelData(4, colorData.labels);

        // Assign the result to the SogData
        Sh0 sh0 = new Sh0();
        sh0.files = new String[]
        { "sh0.webp" };
        sh0.codebook = colorData.centroids;
        sogData.meta.sh0 = sh0;
        sogData.sh0 = colorPixelData;

        logger.fine("Generating SOG colors DONE");
    }

    /**
     * Generate the spherical harmonics for the current {@link SogData}
     */
    private void generateSh()
    {
        int shBands = splats.get(0).getShDegree();
        if (shBands == 0)
        {
            logger.fine("Generating SOG spherical harmonics (none)");
            return;
        }
        logger.fine("Generating SOG spherical harmonics");

        int shCoeffsArray[] =
        { 0, 3, 8, 15 };
        int shCoeffs = shCoeffsArray[shBands];

        // Convert the spherical harmonics data into a table
        // TODO The order here could be (x,y,z, x,y,z ...)
        // or (x,x,x... y,y,y, ...). Does it matter?
        double shDataTable[][] = new double[splats.size()][];
        for (int i = 0; i < splats.size(); i++)
        {
            Splat s = splats.get(i);
            double row[] = new double[shCoeffs * 3];
            for (int c = 0; c < shCoeffs; c++)
            {
                row[shCoeffs * 0 + c] = s.getShX(c + 1);
                row[shCoeffs * 1 + c] = s.getShY(c + 1);
                row[shCoeffs * 2 + c] = s.getShZ(c + 1);
            }
            shDataTable[i] = row;
        }
        double computedPaletteSize =
            Math.pow(2.0, Math.floor(log2(splats.size() / 1024.0)));
        int paletteSize = (int) (Math.min(64, computedPaletteSize) * 1024);

        logger.fine("Generating SOG spherical harmonics - clustering ND");
        logger.fine("  (This may take a while...)");

        // Compute the clustering on the SH data
        ClusteringResult clusteringResult =
            Clustering.compute(shDataTable, paletteSize);
        float[][] centroids = clusteringResult.centroids;
        int[] labels = clusteringResult.labels;

        // Create some sort of a "data table" from the SH centroids
        IntFloatFunction columns[] = new IntFloatFunction[shCoeffs * 3];
        for (int i = 0; i < columns.length; i++)
        {
            int c = i;
            columns[i] = (row) -> centroids[row][c];
        }

        logger.fine("Generating SOG spherical harmonics - clustering 1D");

        // Perform the clustering in the centroids
        ClusteringResult1D clusteringResult1D =
            SogClustering.cluster1d(centroids.length, columns);
        float codebookCentroids[] = clusteringResult1D.centroids;
        byte codebookLabels[][] = clusteringResult1D.labels;

        // Convert the centroids into the pixel representation
        shWidth = 64 * shCoeffs;
        shHeight = (int) Math.ceil(centroids.length / 64.0);
        byte centroidsBuf[] = new byte[shWidth * shHeight * channels];
        for (int i = 0; i < centroids.length; ++i)
        {
            byte centroidsRow[] = codebookLabels[i];

            for (int j = 0; j < shCoeffs; ++j)
            {
                byte x = centroidsRow[shCoeffs * 0 + j];
                byte y = centroidsRow[shCoeffs * 1 + j];
                byte z = centroidsRow[shCoeffs * 2 + j];

                centroidsBuf[i * shCoeffs * 4 + j * 4 + 0] = x;
                centroidsBuf[i * shCoeffs * 4 + j * 4 + 1] = y;
                centroidsBuf[i * shCoeffs * 4 + j * 4 + 2] = z;
                centroidsBuf[i * shCoeffs * 4 + j * 4 + 3] = (byte) 0xFF;
            }
        }

        // Generate the labels pixels buffer
        byte labelsBuf[] = new byte[width * height * channels];
        for (int i = 0; i < indices.capacity(); ++i)
        {
            int label = labels[indices.get(i)];
            int ti = layout(i, width);

            labelsBuf[ti * 4 + 0] = (byte) (0xFF & label);
            labelsBuf[ti * 4 + 1] = (byte) (0xFF & (label >>> 8));
            labelsBuf[ti * 4 + 2] = (byte) (0);
            labelsBuf[ti * 4 + 3] = (byte) (0xFF);
        }

        ShN shN = new ShN();
        shN.count = paletteSize;
        shN.bands = shBands;
        shN.codebook = codebookCentroids;
        shN.files = new String[]
        { "shN_centroids.webp", "shN_labels.webp" };

        sogData.meta.shN = shN;
        sogData.shNCentroids = centroidsBuf;
        sogData.shNLabels = labelsBuf;

        logger.fine("Generating SOG spherical harmonics DONE");
    };

    /**
     * Convert the given data into a 1D array representing RGBA pixels
     * 
     * @param numColumns The number of input columns
     * @param dataTable The data table
     * @return The pixel data
     */
    private byte[] convertTableToPixelData(int numColumns, byte dataTable[][])
    {
        byte data[] = new byte[width * height * channels];
        for (int i = 0; i < indices.capacity(); ++i)
        {
            int idx = indices.get(i);
            int ti = layout(i, width);
            data[ti * channels + 0] = dataTable[idx][0];
            data[ti * channels + 1] = numColumns > 1 ? dataTable[idx][1] : 0;
            data[ti * channels + 2] = numColumns > 2 ? dataTable[idx][2] : 0;
            data[ti * channels + 3] =
                numColumns > 3 ? dataTable[idx][3] : (byte) 0xFF;
        }
        return data;
    };

    /**
     * Compute the minimum/maximum values of the given data.
     * 
     * The result will be an array with a length that is equal to the number of
     * columns, each element being a 2-element array, with
     * <code>result[column][0] = minimum of that column</code>
     * <code>result[column][1] = maximum of that column</code>
     * 
     * @param numRows The number of rows
     * @param columns The colums
     * @return The result
     */
    private static float[][] computeMinMax(int numRows,
        IntFloatFunction columns[])
    {
        float minMax[][] = new float[columns.length][2];
        for (int j = 0; j < columns.length; ++j)
        {
            minMax[j][0] = Float.POSITIVE_INFINITY;
            minMax[j][1] = Float.NEGATIVE_INFINITY;
        }

        for (int i = 0; i < numRows; ++i)
        {
            for (int j = 0; j < columns.length; ++j)
            {
                float value = columns[j].apply(i);
                if (value < minMax[j][0])
                {
                    minMax[j][0] = value;
                }
                ;
                if (value > minMax[j][1])
                {
                    minMax[j][1] = value;
                }
                ;
            }
        }

        return minMax;
    };

    /**
     * Encode the given quaternion into bytes.
     * 
     * Ported from "write-sog.ts".
     * 
     * @param q The quaternion
     * @param quats The quaternions in byte representation
     * @param index The index inside the quats array
     * @param width The image width
     */
    private static void encodeQuaternion(float q[], byte quats[], int index,
        int width)
    {
        double len =
            Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2] + q[3] * q[3]);

        // normalize
        double invLen = 1.0 / len;
        q[0] *= invLen;
        q[1] *= invLen;
        q[2] *= invLen;
        q[3] *= invLen;

        // find max component
        int maxComp = indexOfMaxAbs(q);

        // invert if max component is negative
        if (q[maxComp] < 0)
        {
            q[0] = -q[0];
            q[1] = -q[1];
            q[2] = -q[2];
            q[3] = -q[3];
        }

        q[0] *= SQRT2;
        q[1] *= SQRT2;
        q[2] *= SQRT2;
        q[3] *= SQRT2;

        int idxs[][] =
        {
            { 1, 2, 3 },
            { 0, 2, 3 },
            { 0, 1, 3 },
            { 0, 1, 2 } };
        int idx[] = idxs[maxComp];

        int ti = layout(index, width);

        quats[ti * 4 + 0] = (byte) (255 * (q[idx[0]] * 0.5 + 0.5));
        quats[ti * 4 + 1] = (byte) (255 * (q[idx[1]] * 0.5 + 0.5));
        quats[ti * 4 + 2] = (byte) (255 * (q[idx[2]] * 0.5 + 0.5));
        quats[ti * 4 + 3] = (byte) (252 + maxComp);
    }

    /**
     * Returns the index of the largest absolute value in the given array
     * 
     * @param a The array
     * @return The result
     */
    private static int indexOfMaxAbs(float a[])
    {
        int index = -1;
        float maxAbs = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < a.length; i++)
        {
            float m = Math.abs(a[i]);
            if (m > maxAbs)
            {
                index = i;
                maxAbs = m;
            }
        }
        return index;
    }

    /**
     * The log-transform for SOG
     * 
     * @param value The input
     * @return The result
     */
    private static float logTransform(float value)
    {
        return (float) (Math.signum(value) * Math.log(Math.abs(value) + 1));
    }

    /**
     * Computes the base-2 logarithm of the given value
     * 
     * @param a The value
     * @return The result
     */
    private static double log2(double a)
    {
        return Math.log(a) / Math.log(2.0);
    }

}