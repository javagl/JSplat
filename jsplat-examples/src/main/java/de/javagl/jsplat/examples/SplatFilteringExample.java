/*
 * www.javagl.de - JSplat
 *
 * Copyright 2026 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.io.ply.PlySplatWriter;
import de.javagl.jsplat.io.ply.PlySplatWriter.PlyFormat;

/**
 * An example showing how to filter a list of splats to remove splats with
 * invalid values (infinite or NaN values, or NaN opacities
 */
public class SplatFilteringExample
{
    /**
     * The entry point
     * 
     * @param args Not used
     * @throws IOException If an IO error occurs
     */
    public static void main(String[] args) throws IOException
    {
        List<Splat> initial = createWithInvalid();
        List<Splat> filtered = filter(initial);

        PlySplatWriter w = new PlySplatWriter(PlyFormat.ASCII);
        w.writeList(initial, new FileOutputStream("./data/invalid.ply"));
        w.writeList(filtered, new FileOutputStream("./data/filtered.ply"));
    }

    /**
     * Filter the given list of splats, removing all splats that contain
     * non-finite values or NaN opacity values.
     * 
     * @param <T> The type
     * @param splats The splats
     * @return The filtered list
     */
    private static <T extends Splat> List<T> filter(List<T> splats)
    {
        Predicate<T> f = SplatFilteringExample::allValuesValid;
        List<T> filtered =
            splats.stream().filter(f).collect(Collectors.toList());
        int numRemoved = splats.size() - filtered.size();
        System.out.println(
            "Removed " + numRemoved + " of " + splats.size() + " splats");
        return filtered;
    }

    /**
     * Returns whether all values in the given splat are valid, meaning that
     * they are all finite, and the opacity is not NaN.
     * 
     * @param splat The splat
     * @return The result
     */
    private static boolean allValuesValid(Splat splat)
    {
        if (!Float.isFinite(splat.getPositionX()))
        {
            return false;
        }

        if (!Float.isFinite(splat.getPositionY()))
        {
            return false;
        }

        if (!Float.isFinite(splat.getPositionZ()))
        {
            return false;
        }

        if (!Float.isFinite(splat.getScaleX()))
        {
            return false;
        }

        if (!Float.isFinite(splat.getScaleY()))
        {
            return false;
        }

        if (!Float.isFinite(splat.getScaleZ()))
        {
            return false;
        }

        if (!Float.isFinite(splat.getRotationX()))
        {
            return false;
        }

        if (!Float.isFinite(splat.getRotationY()))
        {
            return false;
        }

        if (!Float.isFinite(splat.getRotationZ()))
        {
            return false;
        }

        if (!Float.isFinite(splat.getRotationW()))
        {
            return false;
        }

        // Note: The opacity may be infinite! (But not NaN)
        if (Float.isNaN(splat.getOpacity()))
        {
            return false;
        }

        int dims = splat.getShDimensions();
        for (int d = 0; d < dims; d++)
        {
            if (!Float.isFinite(splat.getShX(d)))
            {
                return false;
            }
            if (!Float.isFinite(splat.getShY(d)))
            {
                return false;
            }
            if (!Float.isFinite(splat.getShZ(d)))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Create a list of splats, containing some splats with invalid components,
     * i.e. values that are NaN or infinite.
     * 
     * @return The splats
     */
    private static List<Splat> createWithInvalid()
    {
        List<MutableSplat> splats = new ArrayList<MutableSplat>();
        for (int c = 0; c < 8; c++)
        {
            float x = -0.5f + ((c & 1) == 0 ? 0.0f : 1.0f);
            float y = -0.5f + ((c & 2) == 0 ? 0.0f : 1.0f);
            float z = -0.5f + ((c & 4) == 0 ? 0.0f : 1.0f);
            add(splats, 0, 100.0f, x, y, z);
        }

        add(splats, 0, 100.0f, Float.NaN, 0.0f, 0.0f);
        add(splats, 0, 100.0f, 0.0f, Float.NaN, 0.0f);
        add(splats, 0, 100.0f, 0.0f, 0.0f, Float.NaN);

        add(splats, 0, 100.0f, Float.POSITIVE_INFINITY, 0.0f, 0.0f);
        add(splats, 0, 100.0f, 0.0f, Float.POSITIVE_INFINITY, 0.0f);
        add(splats, 0, 100.0f, 0.0f, 0.0f, Float.POSITIVE_INFINITY);

        return Collections.unmodifiableList(splats);
    }

    /**
     * Add an example splat to the given list. Many details are not specified.
     * 
     * @param splats The splats
     * @param degree The degree
     * @param size The size
     * @param npx The normalized position in x-direction
     * @param npy The normalized position in y-direction
     * @param npz The normalized position in z-direction
     */
    private static void add(List<MutableSplat> splats, int degree, float size,
        float npx, float npy, float npz)
    {
        MutableSplat splat = Splats.create(degree);

        splat.setPositionX(npx * size);
        splat.setPositionY(npy * size);
        splat.setPositionZ(npz * size);

        splat.setScaleX(1.0f);
        splat.setScaleY(1.0f);
        splat.setScaleZ(1.0f);

        splat.setRotationX(0.0f);
        splat.setRotationY(0.0f);
        splat.setRotationZ(0.0f);
        splat.setRotationW(1.0f);

        splat.setOpacity(Splats.alphaToOpacity(1.0f));
        splat.setShX(0, Splats.colorToDirectCurrent(1.0f));
        splat.setShY(0, Splats.colorToDirectCurrent(1.0f));
        splat.setShZ(0, Splats.colorToDirectCurrent(1.0f));

        splats.add(splat);
    }

}
