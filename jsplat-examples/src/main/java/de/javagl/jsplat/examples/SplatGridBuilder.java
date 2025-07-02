/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import de.javagl.jsplat.MutableSplat;

/**
 * Utility class for creating splat test data
 */
class SplatGridBuilder
{
    /**
     * Interface for classes that can receive an object and three floating
     * point values
     * 
     * @param <T> The object type
     */
    static interface Consumer3D<T>
    {
        /**
         * Accept the given object and values
         * 
         * @param t The object
         * @param x The x value
         * @param y The y value
         * @param z The z value
         */
        void accept(T t, float x, float y, float z);
    }

    /**
     * The size of the grid in x-direction
     */
    private final int sizeX;

    /**
     * The size of the grid in y-direction
     */
    private final int sizeY;

    /**
     * The size of the grid in z-direction
     */
    private final int sizeZ;
    
    /**
     * The supplier for the splats to be created
     */
    private final Supplier<? extends MutableSplat> supplier;
    
    /**
     * The consumers for the x-values
     */
    private final List<BiConsumer<MutableSplat, Float>> consumersX;

    /**
     * The consumers for the y-values
     */
    private final List<BiConsumer<MutableSplat, Float>> consumersY;

    /**
     * The consumers for the z-values
     */
    private final List<BiConsumer<MutableSplat, Float>> consumersZ;

    /**
     * The consumers for the 3D values
     */
    private final List<Consumer3D<MutableSplat>> consumers3D;

    /**
     * Creates a new instance
     * 
     * @param sizeX The size of the grid in x-direction
     * @param sizeY The size of the grid in y-direction
     * @param sizeZ The size of the grid in z-direction
     * @param supplier The supplier for the splat instances
     */
    SplatGridBuilder(int sizeX, int sizeY, int sizeZ,
        Supplier<? extends MutableSplat> supplier)
    {
        if (sizeX < 1 || sizeY < 1 || sizeZ < 1)
        {
            throw new IllegalArgumentException(
                "The size must be at least 1, but is " + sizeX + "x" + sizeY
                    + "x" + sizeZ);
        }
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.supplier =
            Objects.requireNonNull(supplier, "The supplier may not be null");
        this.consumersX = new ArrayList<BiConsumer<MutableSplat, Float>>();
        this.consumersY = new ArrayList<BiConsumer<MutableSplat, Float>>();
        this.consumersZ = new ArrayList<BiConsumer<MutableSplat, Float>>();
        this.consumers3D = new ArrayList<Consumer3D<MutableSplat>>();
    }

    /**
     * Register the given consumer for the x-coordinates
     * 
     * @param consumer The consumer
     */
    void registerX(BiConsumer<MutableSplat, Float> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        consumersX.add(consumer);
    }

    /**
     * Register the given consumer for the y-coordinates
     * 
     * @param consumer The consumer
     */
    void registerY(BiConsumer<MutableSplat, Float> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        consumersY.add(consumer);
    }

    /**
     * Register the given consumer for the z-coordinates
     * 
     * @param consumer The consumer
     */
    void registerZ(BiConsumer<MutableSplat, Float> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        consumersZ.add(consumer);
    }

    /**
     * Register the given consumer for the x-coordinates, mapping the
     * range [0,1] to the given [minimum,maximum] range.
     * 
     * @param min The minimum
     * @param max The maximum
     * @param consumer The consumer
     */
    void registerX(float min, float max,
        BiConsumer<MutableSplat, Float> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        registerX((s, x) ->
        {
            float fx = min + x * (max - min);
            consumer.accept(s, fx);
        });
    }

    /**
     * Register the given consumer for the y-coordinates, mapping the
     * range [0,1] to the given [minimum,maximum] range.
     * 
     * @param min The minimum
     * @param max The maximum
     * @param consumer The consumer
     */
    void registerY(float min, float max,
        BiConsumer<MutableSplat, Float> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        registerY((s, y) ->
        {
            float fy = min + y * (max - min);
            consumer.accept(s, fy);
        });
    }

    /**
     * Register the given consumer for the z-coordinates, mapping the
     * range [0,1] to the given [minimum,maximum] range.
     * 
     * @param min The minimum
     * @param max The maximum
     * @param consumer The consumer
     */
    void registerZ(float min, float max,
        BiConsumer<MutableSplat, Float> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        registerZ((s, z) ->
        {
            float fz = min + z * (max - min);
            consumer.accept(s, fz);
        });
    }

    /**
     * Register the given 3D consumer
     * 
     * @param consumer The consumer
     */
    void register(Consumer3D<MutableSplat> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        consumers3D.add(consumer);
    }

    /**
     * Generate the splats
     * 
     * @return The splats
     */
    List<MutableSplat> generate()
    {
        List<MutableSplat> result = new ArrayList<MutableSplat>();
        for (int x = 0; x < sizeX; x++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int z = 0; z < sizeZ; z++)
                {
                    float fx = 0.0f;
                    float fy = 0.0f;
                    float fz = 0.0f;
                    if (sizeX > 1)
                    {
                        fx = x / (float) (sizeX - 1);
                    }
                    if (sizeY > 1)
                    {
                        fy = y / (float) (sizeY - 1);
                    }
                    if (sizeZ > 1)
                    {
                        fz = z / (float) (sizeZ - 1);
                    }

                    MutableSplat s = generate(fx, fy, fz);
                    result.add(s);
                }
            }
        }
        return result;
    }

    /**
     * Generate the splat for the given coordinates
     * 
     * @param fx The x-coordinate
     * @param fy The y-coordinate
     * @param fz The z-coordinate
     * @return The splat
     */
    private MutableSplat generate(float fx, float fy, float fz)
    {
        MutableSplat s = supplier.get();
        for (BiConsumer<MutableSplat, Float> consumer : consumersX)
        {
            consumer.accept(s, fx);
        }
        for (BiConsumer<MutableSplat, Float> consumer : consumersY)
        {
            consumer.accept(s, fy);
        }
        for (BiConsumer<MutableSplat, Float> consumer : consumersZ)
        {
            consumer.accept(s, fz);
        }
        for (Consumer3D<MutableSplat> consumer : consumers3D)
        {
            consumer.accept(s, fx, fy, fz);
        }
        return s;
    }
}