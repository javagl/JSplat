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
package de.javagl.jsplat.processing;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.javagl.jsplat.Splat;

/**
 * Utility methods for sorting splats based on different criteria.
 * 
 * Note: This is experimental and preliminary. The sorting criteria may not
 * make sense for real-world applications.
 */
public class SplatSorting
{
    /**
     * Sort the given splats by their "volume", in descending order.
     * 
     * The "volume" is just the product of the scales along all axes.
     * 
     * @param splats The splats
     */
    public static void sortByVolumeDescending(List<? extends Splat> splats)
    {
        Comparator<Splat> comparator = SplatComparing::compareByVolume;
        Collections.sort(splats, comparator.reversed());
    }
    
    /**
     * Sort the given splats by their maximum scale factor, in descending order.
     * 
     * @param splats The splats
     */
    public static void sortByMaxScaleDescending(List<? extends Splat> splats)
    {
        Comparator<Splat> comparator = SplatComparing::compareByMaxScale;
        Collections.sort(splats, comparator.reversed());
    }

    /**
     * Sort the given splats by their "importance", in descending order.
     * 
     * The "importance" is just the product of the scales along all axes,
     * multiplied with the opacity. 
     * 
     * @param splats The splats
     */
    public static void sortByImportanceDescending(List<? extends Splat> splats)
    {
        Comparator<Splat> comparator = SplatComparing::compareByImportance;
        Collections.sort(splats, comparator.reversed());
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatSorting()
    {
        // Private constructor to prevent instantiation
    }
}
