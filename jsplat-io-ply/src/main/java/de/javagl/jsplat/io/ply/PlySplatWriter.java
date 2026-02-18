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
package de.javagl.jsplat.io.ply;

import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Function;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListWriter;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.processing.SplatTransforms;
import de.javagl.ply.Descriptor;
import de.javagl.ply.Descriptors;
import de.javagl.ply.MutableDescriptor;
import de.javagl.ply.ObjectPlySource;
import de.javagl.ply.ObjectPlySource.Handle;
import de.javagl.ply.PlyType;
import de.javagl.ply.PlyWriter;
import de.javagl.ply.PlyWriters;

/**
 * Implementation of a {@link SplatListWriter} that writes PLY data
 */
public final class PlySplatWriter implements SplatListWriter
{
    /**
     * The {@link PlyFormat}
     */
    private PlyFormat plyFormat;

    /**
     * The PLY format to write
     *
     */
    public enum PlyFormat
    {
        /**
         * ASCII PLY format
         */
        ASCII,

        /**
         * Little-endian binary PLY format
         */
        BINARY_LITTLE_ENDIAN,

        /**
         * Big-endian binary PLY format
         */
        BINARY_BIG_ENDIAN
    }

    /**
     * Creates a new instance
     * 
     * @param plyFormat The {@link PlyFormat}
     */
    public PlySplatWriter(PlyFormat plyFormat)
    {
        this.plyFormat =
            Objects.requireNonNull(plyFormat, "The plyFormat may not be null");
    }

    /**
     * Implementation of a list that provides a mapped view on a delegate
     *
     * @param <S> The delegate element type
     * @param <T> The element type
     */
    private static class MappedList<S, T> extends AbstractList<T>
        implements RandomAccess
    {
        /**
         * The delegate
         */
        private List<? extends S> delegate;

        /**
         * The mapper
         */
        private final Function<S, T> mapper;

        /**
         * Creates a new instance
         * 
         * @param delegate The delegate
         * @param mapper The mapper
         */
        MappedList(List<? extends S> delegate, Function<S, T> mapper)
        {
            this.delegate = Objects.requireNonNull(delegate,
                "The delegate may not be null");
            this.mapper =
                Objects.requireNonNull(mapper, "The mapper may not be null");

        }

        @Override
        public T get(int index)
        {
            S s = delegate.get(index);
            T t = mapper.apply(s);
            return t;
        }

        @Override
        public int size()
        {
            return delegate.size();
        }
    }

    /**
     * Create a mapped view on the given list of splats that transforms each
     * splat by rotating it by 180 degrees around the x-axis for writing it out
     * as PLY.
     * 
     * @param splats The splats
     * @return The result
     */
    private static List<MutableSplat> createMapped(List<? extends Splat> splats)
    {
        if (splats.isEmpty())
        {
            return Collections.emptyList();
        }
        Splat splat = splats.get(0);
        int dimensions = splat.getShDimensions();

        // Rotate about 180 degrees around x, to convert from
        // right-up-front to right-down-back
        // @formatter:off
        float rotate180X[] = 
        { 
            1.0f,  0.0f,  0.0f, 0.0f, 
            0.0f, -1.0f,  0.0f, 0.0f, 
            0.0f,  0.0f, -1.0f, 0.0f, 
            0.0f,  0.0f,  0.0f, 1.0f 
        };
        // @formatter:on
        Consumer<MutableSplat> transform =
            SplatTransforms.createTransform(rotate180X, dimensions);
        Function<Splat, MutableSplat> mapper = (s) ->
        {
            MutableSplat transformed = Splats.copy(s);
            transform.accept(transformed);
            return transformed;
        };
        List<MutableSplat> result =
            new MappedList<Splat, MutableSplat>(splats, mapper);
        return result;
    }

    @Override
    public void writeList(List<? extends Splat> inputSplats,
        OutputStream outputStream) throws IOException
    {
        int shDegree = 0;
        if (!inputSplats.isEmpty()) 
        {
            shDegree = inputSplats.get(0).getShDegree();
        }
        Descriptor descriptor = createDescriptor(shDegree);
        ObjectPlySource plySource = new ObjectPlySource(descriptor);

        List<MutableSplat> splats = createMapped(inputSplats);
        Handle<? extends Splat> v = plySource.register("vertex", splats);

        v.withFloat("x", Splat::getPositionX);
        v.withFloat("y", Splat::getPositionY);
        v.withFloat("z", Splat::getPositionZ);

        v.withFloat("f_dc_0", (s) -> s.getShX(0));
        v.withFloat("f_dc_1", (s) -> s.getShY(0));
        v.withFloat("f_dc_2", (s) -> s.getShZ(0));

        int shDimensions = Splats.dimensionsForDegree(shDegree);
        for (int d = 0; d < shDimensions - 1; d++)
        {
            int sd = d + 1;
            int ix = (shDimensions - 1) * 0 + d;
            int iy = (shDimensions - 1) * 1 + d;
            int iz = (shDimensions - 1) * 2 + d;
            v.withFloat("f_rest_" + ix, (s) -> s.getShX(sd));
            v.withFloat("f_rest_" + iy, (s) -> s.getShY(sd));
            v.withFloat("f_rest_" + iz, (s) -> s.getShZ(sd));
        }

        v.withFloat("opacity", Splat::getOpacity);

        v.withFloat("scale_0", Splat::getScaleX);
        v.withFloat("scale_1", Splat::getScaleY);
        v.withFloat("scale_2", Splat::getScaleZ);

        // PLY uses scalar-first quaternions
        v.withFloat("rot_0", Splat::getRotationW);
        v.withFloat("rot_1", Splat::getRotationX);
        v.withFloat("rot_2", Splat::getRotationY);
        v.withFloat("rot_3", Splat::getRotationZ);

        if (plyFormat == PlyFormat.ASCII)
        {
            PlyWriter plyWriter = PlyWriters.createAscii();
            plyWriter.write(plySource, outputStream);
        }
        if (plyFormat == PlyFormat.BINARY_LITTLE_ENDIAN)
        {
            PlyWriter plyWriter = PlyWriters.createBinaryLittleEndian();
            plyWriter.write(plySource, outputStream);
        }
        if (plyFormat == PlyFormat.BINARY_BIG_ENDIAN)
        {
            PlyWriter plyWriter = PlyWriters.createBinaryBigEndian();
            plyWriter.write(plySource, outputStream);
        }
    }

    /**
     * Create a descriptor for the PLY data
     * 
     * @param shDegree The spherical harmonics degree
     * @return The descriptor
     */
    private Descriptor createDescriptor(int shDegree)
    {
        MutableDescriptor d = Descriptors.create();

        d.addProperty("vertex", "x", PlyType.FLOAT);
        d.addProperty("vertex", "y", PlyType.FLOAT);
        d.addProperty("vertex", "z", PlyType.FLOAT);
        d.addProperty("vertex", "f_dc_0", PlyType.FLOAT);
        d.addProperty("vertex", "f_dc_1", PlyType.FLOAT);
        d.addProperty("vertex", "f_dc_2", PlyType.FLOAT);

        int shDimensions = Splats.dimensionsForDegree(shDegree);
        for (int i = 0; i < shDimensions - 1; i++)
        {
            int indexX = i * 3 + 0;
            int indexY = i * 3 + 1;
            int indexZ = i * 3 + 2;
            d.addProperty("vertex", "f_rest_" + indexX, PlyType.FLOAT);
            d.addProperty("vertex", "f_rest_" + indexY, PlyType.FLOAT);
            d.addProperty("vertex", "f_rest_" + indexZ, PlyType.FLOAT);
        }
        d.addProperty("vertex", "opacity", PlyType.FLOAT);
        d.addProperty("vertex", "scale_0", PlyType.FLOAT);
        d.addProperty("vertex", "scale_1", PlyType.FLOAT);
        d.addProperty("vertex", "scale_2", PlyType.FLOAT);
        d.addProperty("vertex", "rot_0", PlyType.FLOAT);
        d.addProperty("vertex", "rot_1", PlyType.FLOAT);
        d.addProperty("vertex", "rot_2", PlyType.FLOAT);
        d.addProperty("vertex", "rot_3", PlyType.FLOAT);

        return d;
    }
}
