/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.util.List;
import java.util.function.Function;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;

/**
 * Utility methods to transform splats
 */
class SplatTransforms
{
    /**
     * Translate all splats in the given list by the given amount, in-place
     * 
     * @param list The list
     * @param dx The translation in x-direction
     * @param dy The translation in y-direction
     * @param dz The translation in z-direction
     * @return The given list
     */
    static <T extends MutableSplat> List<T> translateList(List<T> list,
        float dx, float dy, float dz)
    {
        Function<MutableSplat, MutableSplat> t = translate(dx, dy, dz);
        list.forEach(s -> t.apply(s));
        return list;
    }

    /**
     * Creates a function that returns a splat with a different degree than the
     * input, initialized from the given splat.
     * 
     * @param degree The degree
     * @return The function
     */
    static Function<Splat, MutableSplat> changedDegree(int degree)
    {
        return s ->
        {
            MutableSplat t = Splats.create(degree);
            Splats.setAny(s, t);
            return t;
        };
    }

    /**
     * Create a functions that translates a splat by the given amount, in-place.
     * 
     * @param dx The translation in x-direction
     * @param dy The translation in y-direction
     * @param dz The translation in z-direction
     * @return The consumer
     */
    static Function<MutableSplat, MutableSplat> translate(float dx, float dy,
        float dz)
    {
        return s ->
        {
            s.setPositionX(s.getPositionX() + dx);
            s.setPositionY(s.getPositionY() + dy);
            s.setPositionZ(s.getPositionZ() + dz);
            return s;
        };
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatTransforms()
    {
        // Private constructor to prevent instantiation
    }

}
