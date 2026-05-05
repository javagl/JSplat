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
public class SplatGridBuilder
{
    /**
     * Interface for classes that can receive an object and three double
     * values
     * 
     * @param <T> The object type
     */
    public static interface Consumer3D<T>
    {
        /**
         * Accept the given object and values
         * 
         * @param t The object
         * @param x The x value
         * @param y The y value
         * @param z The z value
         */
        void accept(T t, double x, double y, double z);
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
    private final List<BiConsumer<MutableSplat, Double>> consumersX;

    /**
     * The consumers for the y-values
     */
    private final List<BiConsumer<MutableSplat, Double>> consumersY;

    /**
     * The consumers for the z-values
     */
    private final List<BiConsumer<MutableSplat, Double>> consumersZ;

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
    public SplatGridBuilder(int sizeX, int sizeY, int sizeZ,
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
        this.consumersX = new ArrayList<BiConsumer<MutableSplat, Double>>();
        this.consumersY = new ArrayList<BiConsumer<MutableSplat, Double>>();
        this.consumersZ = new ArrayList<BiConsumer<MutableSplat, Double>>();
        this.consumers3D = new ArrayList<Consumer3D<MutableSplat>>();
    }

    /**
     * Register the given consumer for the x-coordinates
     * 
     * @param consumer The consumer
     */
    void registerX(BiConsumer<MutableSplat, Double> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        consumersX.add(consumer);
    }

    /**
     * Register the given consumer for the y-coordinates
     * 
     * @param consumer The consumer
     */
    void registerY(BiConsumer<MutableSplat, Double> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        consumersY.add(consumer);
    }

    /**
     * Register the given consumer for the z-coordinates
     * 
     * @param consumer The consumer
     */
    void registerZ(BiConsumer<MutableSplat, Double> consumer)
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
    public void registerX(double min, double max,
        BiConsumer<MutableSplat, Double> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        registerX((s, x) ->
        {
            double fx = min + x * (max - min);
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
    public void registerY(double min, double max,
        BiConsumer<MutableSplat, Double> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        registerY((s, y) ->
        {
            double fy = min + y * (max - min);
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
    public void registerZ(double min, double max,
        BiConsumer<MutableSplat, Double> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        registerZ((s, z) ->
        {
            double fz = min + z * (max - min);
            consumer.accept(s, fz);
        });
    }

    /**
     * Register the given 3D consumer
     * 
     * @param consumer The consumer
     */
    public void register(Consumer3D<MutableSplat> consumer)
    {
        Objects.requireNonNull(consumer, "The consumer may not be null");
        consumers3D.add(consumer);
    }

    /**
     * Generate the splats
     * 
     * @return The splats
     */
    public List<MutableSplat> generate()
    {
        List<MutableSplat> result = new ArrayList<MutableSplat>();
        for (int x = 0; x < sizeX; x++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int z = 0; z < sizeZ; z++)
                {
                    double fx = 0.0;
                    double fy = 0.0;
                    double fz = 0.0;
                    if (sizeX > 1)
                    {
                        fx = (double)x / (sizeX - 1);
                    }
                    if (sizeY > 1)
                    {
                        fy = (double)y / (sizeY - 1);
                    }
                    if (sizeZ > 1)
                    {
                        fz = (double)z / (sizeZ - 1);
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
    private MutableSplat generate(double fx, double fy, double fz)
    {
        MutableSplat s = supplier.get();
        for (BiConsumer<MutableSplat, Double> consumer : consumersX)
        {
            consumer.accept(s, fx);
        }
        for (BiConsumer<MutableSplat, Double> consumer : consumersY)
        {
            consumer.accept(s, fy);
        }
        for (BiConsumer<MutableSplat, Double> consumer : consumersZ)
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