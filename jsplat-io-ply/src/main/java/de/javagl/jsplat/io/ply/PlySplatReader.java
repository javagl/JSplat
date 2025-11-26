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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.Splats;
import de.javagl.ply.Descriptor;
import de.javagl.ply.ElementDescriptor;
import de.javagl.ply.ObjectPlyTarget;
import de.javagl.ply.ObjectPlyTarget.Handle;
import de.javagl.ply.PlyReader;
import de.javagl.ply.PlyReaders;
import de.javagl.ply.PlyType;
import de.javagl.ply.PropertyDescriptor;

/**
 * Implementation of a {@link SplatListReader} that reads PLY data
 */
public final class PlySplatReader implements SplatListReader
{
    /**
     * Creates a new instance
     */
    public PlySplatReader()
    {
        // Default constructor
    }

    @Override
    public List<MutableSplat> readList(InputStream inputStream)
        throws IOException
    {
        PlyReader plyReader = PlyReaders.create();
        Descriptor descriptor = plyReader.readDescriptor(inputStream);
        int shDegree = computeSphericalHarmonicsDegree(descriptor);
        int shDimensions = Splats.dimensionsForDegree(shDegree);

        List<MutableSplat> splats = new ArrayList<MutableSplat>();

        ObjectPlyTarget plyTarget = new ObjectPlyTarget();
        Handle<MutableSplat> h =
            plyTarget.register("vertex", () -> Splats.create(shDegree));

        // Convert from right-down-front to right-up-front by
        // negating the y- and z-component
        // TODO: This Float/Double check is not so pretty, but
        // a quick solution for the time being.
        if (isDouble(descriptor, "x"))
        {
            h.withDouble("x", (s, x) -> s.setPositionX(x.floatValue()));
        }
        else
        {
            h.withFloat("x", MutableSplat::setPositionX);
        }
        if (isDouble(descriptor, "y"))
        {
            h.withDouble("y", (s, y) -> s.setPositionY(-y.floatValue()));
        }
        else
        {
            h.withFloat("y", (s, y) -> s.setPositionY(-y));
        }
        if (isDouble(descriptor, "z"))
        {
            h.withDouble("z", (s, z) -> s.setPositionZ(-z.floatValue()));
        }
        else
        {
            h.withFloat("z", (s, z) -> s.setPositionZ(-z));
        }        

        h.withFloat("f_dc_0", (s, v) -> s.setShX(0, v));
        h.withFloat("f_dc_1", (s, v) -> s.setShY(0, v));
        h.withFloat("f_dc_2", (s, v) -> s.setShZ(0, v));

        h.withFloat("opacity", MutableSplat::setOpacity);
        h.withFloat("scale_0", MutableSplat::setScaleX);
        h.withFloat("scale_1", MutableSplat::setScaleY);
        h.withFloat("scale_2", MutableSplat::setScaleZ);

        // Convert from right-down-front to right-up-front by
        // negating the y- and z-component
        // PLY uses scalar-first quaternions
        h.withFloat("rot_0", MutableSplat::setRotationW);
        h.withFloat("rot_1", MutableSplat::setRotationX);
        h.withFloat("rot_2", (s, y) -> s.setRotationY(-y));
        h.withFloat("rot_3", (s, z) -> s.setRotationZ(-z));

        // TODO The coordinate system conversion may have to affect
        // the SHs, but nothing seems to be specified here in a
        // way that allows tools and viewers to agree on something.
        for (int d = 0; d < shDimensions - 1; d++)
        {
            int sd = d + 1;
            int ix = d * 3 + 0;
            int iy = d * 3 + 1;
            int iz = d * 3 + 2;
            h.withFloat("f_rest_" + ix, (s, v) -> s.setShX(sd, v));
            h.withFloat("f_rest_" + iy, (s, v) -> s.setShY(sd, v));
            h.withFloat("f_rest_" + iz, (s, v) -> s.setShZ(sd, v));
        }
        h.consume(splats::add);

        plyReader.readContent(inputStream, plyTarget);
        return splats;
    }

    /**
     * Returns whether the specified property is stored in "double".
     * 
     * Apparently, some PLY files store the "x", "y", and "z" properties of the
     * "vertex" elements in "double". This method assumes that the given
     * descriptor contains a single element descriptor with a property that has
     * the given name, and returns whether the type of that property is
     * {@link PlyType#DOUBLE}.
     * 
     * @param descriptor The PLY {@link Descriptor}
     * @param propertyName The property name
     * @return The result
     */
    private static boolean isDouble(Descriptor descriptor, String propertyName)
    {
        List<ElementDescriptor> elementDescriptors =
            descriptor.getElementDescriptors();
        for (int e = 0; e < elementDescriptors.size(); e++)
        {
            ElementDescriptor elementDescriptor = elementDescriptors.get(e);
            List<PropertyDescriptor> propertyDescriptors =
                elementDescriptor.getPropertyDescriptors();
            for (int p = 0; p < propertyDescriptors.size(); p++)
            {
                PropertyDescriptor propertyDescriptor =
                    propertyDescriptors.get(p);
                String name = propertyDescriptor.getName();
                if (name.equals(propertyName))
                {
                    PlyType type = propertyDescriptor.getType();
                    return type == PlyType.DOUBLE;
                }
            }
        }
        return false;
    }

    /**
     * Make an educated guess about the spherical harmonics degree of the PLY
     * data from the given descriptor.
     * 
     * @param descriptor The descriptor
     * @return The spherical harmonics degree
     */
    private static int computeSphericalHarmonicsDegree(Descriptor descriptor)
    {
        List<ElementDescriptor> elementDescriptors =
            descriptor.getElementDescriptors();
        if (elementDescriptors.isEmpty())
        {
            return -1;
        }
        ElementDescriptor elementDescriptor = elementDescriptors.get(0);
        List<PropertyDescriptor> propertyDescriptors =
            elementDescriptor.getPropertyDescriptors();
        Set<String> propertyNames = propertyDescriptors.stream()
            .map(PropertyDescriptor::getName).collect(Collectors.toSet());
        if (propertyNames.contains("f_rest_44"))
        {
            return 3;
        }
        if (propertyNames.contains("f_rest_23"))
        {
            return 2;
        }
        if (propertyNames.contains("f_rest_8"))
        {
            return 1;
        }
        return 0;
    }

}
