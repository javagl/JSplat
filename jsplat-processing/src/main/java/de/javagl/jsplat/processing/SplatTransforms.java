/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.processing;

import java.util.List;
import java.util.function.Consumer;

import de.javagl.jsplat.MutableSplat;

/**
 * Utility methods to transform splats.
 * 
 * This is highly experimental.
 */
public class SplatTransforms
{
    /**
     * Transform all splats in the given list with the given matrix.
     * 
     * The matrix is assumed to be a 16-element array representing a 4x4 matrix
     * in column-major order
     * 
     * @param list The list
     * @param matrix4 The matrix
     * @return The given list
     */
    public static <T extends MutableSplat> List<T> transformList(List<T> list,
        float matrix4[])
    {
        int dims = list.get(0).getShDimensions();
        Consumer<MutableSplat> transform = createTransform(matrix4, dims);
        list.forEach(transform);
        return list;
    }

    /**
     * Create a consumer that applies the given transform to a given splat.
     * 
     * The matrix is assumed to be a 16-element array representing a 4x4 matrix
     * in column-major order
     * 
     * @param matrix4 The matrix
     * @param dims The splat dimensions
     * @return The transform
     */
    private static Consumer<MutableSplat> createTransform(float matrix4[],
        int dims)
    {
        // Extract the upper 3x3 matrix from the 4x4 matrix
        // @formatter:off
        float matrix3[] = new float[] 
        {
            matrix4[0],
            matrix4[1],
            matrix4[2],
            matrix4[4],
            matrix4[5],
            matrix4[6],
            matrix4[8],
            matrix4[9],
            matrix4[10],
        };
        // @formatter:on

        SplatPositionTransformer pt = new SplatPositionTransformer(matrix4);
        SplatRotationRotator rr = new SplatRotationRotator(matrix3);
        SplatShRotator sr = new SplatShRotator(matrix3, dims);
        SplatScaleScaler ss = new SplatScaleScaler(matrix3);
        Consumer<MutableSplat> transform = s ->
        {
            sr.rotateSh(s);
            rr.rotate(s);
            pt.transform(s);
            ss.scale(s);
        };
        return transform;
    }

    /**
     * Translate all splats in the given list by the given amount, in-place
     * 
     * @param list The list
     * @param dx The translation in x-direction
     * @param dy The translation in y-direction
     * @param dz The translation in z-direction
     * @return The given list
     */
    public static <T extends MutableSplat> List<T> translateList(List<T> list,
        float dx, float dy, float dz)
    {
        list.forEach(s -> translate(s, dx, dy, dz));
        return list;
    }

    /**
     * Translate the given splat by the given amount
     * 
     * @param s The splat
     * @param dx The translation in x-direction
     * @param dy The translation in y-direction
     * @param dz The translation in z-direction
     */
    private static void translate(MutableSplat s, float dx, float dy, float dz)
    {
        s.setPositionX(s.getPositionX() + dx);
        s.setPositionY(s.getPositionY() + dy);
        s.setPositionZ(s.getPositionZ() + dz);
    }

    /**
     * Scale all splats in the given list by the given amount, in-place
     * 
     * @param list The list
     * @param sx The scale in x-direction
     * @param sy The scale in y-direction
     * @param sz The scale in z-direction
     * @return The given list
     */
    static <T extends MutableSplat> List<T> scaleList(List<T> list, float sx,
        float sy, float sz)
    {
        list.forEach(s -> scale(s, sx, sy, sz));
        return list;
    }

    /**
     * Scale the given splat by the given amount, linearly
     * 
     * @param s The splat
     * @param sx The scale in x-direction
     * @param sy The scale in y-direction
     * @param sz The scale in z-direction
     */
    private static void scale(MutableSplat s, float sx, float sy, float sz)
    {
        s.setScaleX((float) Math.log(Math.exp(s.getScaleX()) * sx));
        s.setScaleY((float) Math.log(Math.exp(s.getScaleY()) * sy));
        s.setScaleZ((float) Math.log(Math.exp(s.getScaleZ()) * sz));
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatTransforms()
    {
        // Private constructor to prevent instantiation
    }

}
