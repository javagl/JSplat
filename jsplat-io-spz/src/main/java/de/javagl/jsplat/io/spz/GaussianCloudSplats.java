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
package de.javagl.jsplat.io.spz;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;
import de.javagl.jspz.GaussianCloud;
import de.javagl.jspz.GaussianClouds;

/**
 * Methods to convert between Gaussian clouds and splats.
 * 
 * This class is not part of the public API.
 */
public class GaussianCloudSplats
{
    /**
     * Returns a list of {@link Splat} objects, created from the given Gaussian
     * cloud.
     * 
     * @param g The Gaussian cloud
     * @return The splats
     */
    public static List<MutableSplat> toSplats(GaussianCloud g)
    {
        int shDegree = g.getShDegree();
        int shDimensions = Splats.dimensionsForDegree(shDegree);

        List<MutableSplat> splats = new ArrayList<MutableSplat>();

        int n = g.getNumPoints();
        FloatBuffer positions = g.getPositions();
        FloatBuffer scales = g.getScales();
        FloatBuffer rotations = g.getRotations();
        FloatBuffer alphas = g.getAlphas();
        FloatBuffer colors = g.getColors();
        FloatBuffer sh = g.getSh();

        for (int i = 0; i < n; i++)
        {
            MutableSplat splat = Splats.create(shDegree);

            splat.setPositionX(positions.get(i * 3 + 0));
            splat.setPositionY(positions.get(i * 3 + 1));
            splat.setPositionZ(positions.get(i * 3 + 2));

            splat.setScaleX(scales.get(i * 3 + 0));
            splat.setScaleY(scales.get(i * 3 + 1));
            splat.setScaleZ(scales.get(i * 3 + 2));

            splat.setRotationX(rotations.get(i * 4 + 0));
            splat.setRotationY(rotations.get(i * 4 + 1));
            splat.setRotationZ(rotations.get(i * 4 + 2));
            splat.setRotationW(-rotations.get(i * 4 + 3));

            splat.setOpacity(alphas.get(i));

            splat.setShX(0, colors.get(i * 3 + 0));
            splat.setShY(0, colors.get(i * 3 + 1));
            splat.setShZ(0, colors.get(i * 3 + 2));

            if (shDimensions >= 4)
            {
                // Convert the component order as required for SPZ
                int index = (i * (shDimensions - 1)) * 3;
                splat.setShX(1, sh.get(index + 0));
                splat.setShX(2, sh.get(index + 1));
                splat.setShX(3, sh.get(index + 2));
                splat.setShY(1, sh.get(index + 3));
                splat.setShY(2, sh.get(index + 4));
                splat.setShY(3, sh.get(index + 5));
                splat.setShZ(1, sh.get(index + 6));
                splat.setShZ(2, sh.get(index + 7));
                splat.setShZ(3, sh.get(index + 8));
            }
            splats.add(splat);
        }
        return splats;
    }

    /**
     * Creates a new Gaussian cloud from the given splats
     * 
     * @param splats The splats
     * @return The Gaussian cloud
     */
    public static GaussianCloud fromSplats(List<? extends Splat> splats)
    {
        Splat splat0 = splats.get(0);
        int shDegree = splat0.getShDegree();
        int shDimensions = splat0.getShDimensions();

        GaussianCloud g = GaussianClouds.create(splats.size(), shDegree);
        FloatBuffer positions = g.getPositions();
        FloatBuffer scales = g.getScales();
        FloatBuffer rotations = g.getRotations();
        FloatBuffer alphas = g.getAlphas();
        FloatBuffer colors = g.getColors();
        FloatBuffer sh = g.getSh();

        for (int i = 0; i < splats.size(); i++)
        {
            Splat splat = splats.get(i);

            positions.put(i * 3 + 0, splat.getPositionX());
            positions.put(i * 3 + 1, splat.getPositionY());
            positions.put(i * 3 + 2, splat.getPositionZ());

            scales.put(i * 3 + 0, splat.getScaleX());
            scales.put(i * 3 + 1, splat.getScaleY());
            scales.put(i * 3 + 2, splat.getScaleZ());

            rotations.put(i * 4 + 0, splat.getRotationX());
            rotations.put(i * 4 + 1, splat.getRotationY());
            rotations.put(i * 4 + 2, splat.getRotationZ());
            rotations.put(i * 4 + 3, -splat.getRotationW());

            alphas.put(i, splat.getOpacity());

            colors.put(i * 3 + 0, splat.getShX(0));
            colors.put(i * 3 + 1, splat.getShY(0));
            colors.put(i * 3 + 2, splat.getShZ(0));

            if (shDimensions >= 4)
            {
                // Convert the component order as required for SPZ
                int index = (i * (shDimensions - 1)) * 3;
                sh.put(index + 0, splat.getShX(1));
                sh.put(index + 1, splat.getShX(2));
                sh.put(index + 2, splat.getShX(3));
                sh.put(index + 3, splat.getShY(1));
                sh.put(index + 4, splat.getShY(2));
                sh.put(index + 5, splat.getShY(3));
                sh.put(index + 6, splat.getShZ(1));
                sh.put(index + 7, splat.getShZ(2));
                sh.put(index + 8, splat.getShZ(3));
            }
        }
        return g;
    }

}
