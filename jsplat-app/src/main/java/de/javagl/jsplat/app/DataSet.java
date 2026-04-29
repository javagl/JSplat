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
package de.javagl.jsplat.app;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.processing.SplatTransforms;

/**
 * Internal representation of a splat data set inside the UI
 */
class DataSet
{
    /**
     * A name for the data set, to be displayed in the UI
     */
    private final String name;

    /**
     * The initial (untransformed) splats
     */
    private final List<? extends Splat> initialSplats;

    /**
     * The SH degree of the initial splats
     */
    private final int shDegree;

    /**
     * The current transform that is applied to the splats
     */
    private Transform currentTransform;

    /**
     * The current splats, computed from the initial ones using the transform
     */
    private final List<MutableSplat> currentSplats;

    /**
     * Default constructor
     * 
     * @param name The name, for display purposes
     * @param initialSplats The initial splats
     */
    DataSet(String name, List<? extends Splat> initialSplats)
    {
        this.name = Objects.requireNonNull(name, "The name may not be null");
        this.initialSplats = Objects.requireNonNull(initialSplats,
            "The initialSplats may not be null");
        if (initialSplats.isEmpty())
        {
            this.shDegree = 0;
        }
        else
        {
            this.shDegree = initialSplats.get(0).getShDegree();
        }
        this.currentTransform = new Transform();
        this.currentSplats = Splats.copyList(initialSplats);
    }

    /**
     * Set the transform that should be applied to the initial splats to obtain
     * the transformed splats
     * 
     * @param transform The transform
     */
    void setTransform(Transform transform)
    {
        this.currentTransform = transform;
        float[] matrix = Transforms.toMatrix(transform);
        int shDimensions = Splats.dimensionsForDegree(shDegree);
        Consumer<MutableSplat> t =
            SplatTransforms.createTransform(matrix, shDimensions);

        int n = initialSplats.size();
        IntStream.range(0, n).parallel().forEach(i ->
        {
            Splat initialSplat = initialSplats.get(i);
            MutableSplat currentSplat = currentSplats.get(i);
            Splats.setAny(initialSplat, currentSplat);
            t.accept(currentSplat);
        });
    }
    
    /**
     * Returns the name of this data set
     * 
     * @return The name
     */
    String getName()
    {
        return name;
    }
    
    /**
     * Returns the SH degree of the splats
     * 
     * @return The degree
     */
    int getShDegree()
    {
        return shDegree;
    }

    /**
     * Returns the current transform of this data set
     * 
     * @return The transform
     */
    Transform getTransform()
    {
        return currentTransform;
    }

    /**
     * Returns the current splats, with the current transform applied to them
     * 
     * @return The splats
     */
    List<MutableSplat> getCurrentSplats()
    {
        return currentSplats;
    }

    @Override
    public String toString()
    {
        return name + " (" + initialSplats.size() + " splats)";
    }

}