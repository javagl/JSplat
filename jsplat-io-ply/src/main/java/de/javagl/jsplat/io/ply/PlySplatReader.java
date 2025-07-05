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
    public List<MutableSplat> readList(InputStream inputStream) throws IOException
    {
        PlyReader plyReader = PlyReaders.create();
        Descriptor descriptor = plyReader.readDescriptor(inputStream);
        int shDegree = computeSphericalHarmonicsDegree(descriptor);
        int shDimensions = Splats.dimensionsForDegree(shDegree);

        List<MutableSplat> splats = new ArrayList<MutableSplat>();

        ObjectPlyTarget plyTarget = new ObjectPlyTarget();
        Handle<MutableSplat> h =
            plyTarget.register("vertex", () -> Splats.create(shDegree));
        h.withFloat("x", MutableSplat::setPositionX);
        h.withFloat("y", (s, y) -> s.setPositionY(-y));
        h.withFloat("z", (s, z) -> s.setPositionZ(-z));
        h.withFloat("f_dc_0", (s, v) -> s.setShX(0, v));
        h.withFloat("f_dc_1", (s, v) -> s.setShY(0, v));
        h.withFloat("f_dc_2", (s, v) -> s.setShZ(0, v));
        h.withFloat("opacity", MutableSplat::setOpacity);
        h.withFloat("scale_0", MutableSplat::setScaleX);
        h.withFloat("scale_1", MutableSplat::setScaleY);
        h.withFloat("scale_2", MutableSplat::setScaleZ);
        h.withFloat("rot_0", MutableSplat::setRotationW);
        h.withFloat("rot_1", MutableSplat::setRotationX);
        h.withFloat("rot_2", MutableSplat::setRotationY);
        h.withFloat("rot_3", MutableSplat::setRotationZ);
        for (int i = 0; i < shDimensions - 1; i++)
        {
            int indexX = i * 3 + 0;
            int indexY = i * 3 + 1;
            int indexZ = i * 3 + 2;
            h.withFloat("f_rest_" + indexX, (s, v) -> s.setShX(indexX + 1, v));
            h.withFloat("f_rest_" + indexY, (s, v) -> s.setShY(indexY + 1, v));
            h.withFloat("f_rest_" + indexZ, (s, v) -> s.setShZ(indexZ + 1, v));
        }
        h.consume(splats::add);

        plyReader.readContent(inputStream, plyTarget);
        return splats;
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
