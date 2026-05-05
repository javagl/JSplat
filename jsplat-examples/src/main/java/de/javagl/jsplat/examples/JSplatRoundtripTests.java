/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.SplatListWriter;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.io.gltf.GltfSplatReader;
import de.javagl.jsplat.io.gltf.GltfSplatWriter;
import de.javagl.jsplat.io.gltf.spz.GltfSpzSplatReader;
import de.javagl.jsplat.io.gltf.spz.GltfSpzSplatWriter;
import de.javagl.jsplat.io.spz.SpzSplatReader;
import de.javagl.jsplat.io.spz.SpzSplatWriter;

/**
 * Internal tests
 */
public class JSplatRoundtripTests
{
    /**
     * The entry point
     * 
     * @param args Not used
     * @throws IOException If an IO error occurs
     */
    public static void main(String[] args) throws IOException
    {
        runTestWith(GltfSplatWriter::new, GltfSplatReader::new, 1e-6);
        runTestWith(SpzSplatWriter::new, SpzSplatReader::new, 0.08);
        runTestWith(GltfSpzSplatWriter::new, GltfSpzSplatReader::new, 0.08);
    }

    /**
     * Run a test that consists of creating dummy splats, writing them with the
     * writer from the given supplier, reading them with the reader from the
     * given supplier, and comparing the results, printing unspecified
     * information about differences.
     * 
     * @param ws The writer supplier
     * @param rs The reader supplier
     * @param epsilon An epsilon for the comparison
     * @throws IOException If an IO error occurs
     */
    private static void runTestWith(Supplier<? extends SplatListWriter> ws,
        Supplier<? extends SplatListReader> rs, double epsilon)
        throws IOException
    {
        int numSplats = 1;

        SplatListWriter w = ws.get();
        SplatListReader r = rs.get();

        System.out.println("Running test");
        System.out.println("  writer: " + w.getClass().getSimpleName());
        System.out.println("  reader: " + r.getClass().getSimpleName());

        List<MutableSplat> splatsA = createDummySplats(numSplats);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        w.writeList(splatsA, os);

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        List<MutableSplat> splatsB = r.readList(is);

        for (int i = 0; i < splatsA.size(); i++)
        {
            Splat splatA = splatsA.get(i);
            Splat splatB = splatsB.get(i);

            boolean equal = Splats.equalsEpsilon(splatA, splatB, epsilon);
            if (!equal)
            {
                System.out.println("Splat " + i + " A");
                System.out.println(Splats.createString(splatA));
                System.out.println("Splat " + i + " B");
                System.out.println(Splats.createString(splatB));
            }
        }

        boolean allEqual = Splats.equalsEpsilon(splatsA, splatsB, epsilon);
        System.out.println("All equal? " + allEqual);

    }

    /**
     * Create a dummy splats with values that do not make sense, but are all
     * different and easily identifiable (for tests)
     * 
     * @param n The number of splats
     * @return The splats
     */
    private static List<MutableSplat> createDummySplats(int n)
    {
        List<MutableSplat> splats = new ArrayList<MutableSplat>();
        for (int i = 0; i < n; i++)
        {
            splats.add(createDummySplat(i));
        }
        return splats;
    }

    /**
     * Create a dummy splat with values that do not make sense, but are all
     * different and easily identifiable (for tests)
     * 
     * @param offset An offset for the values
     * @return The splat
     */
    static MutableSplat createDummySplat(double offset)
    {
        MutableSplat s = Splats.create(3);
        s.setPositionX(offset + 1.1);
        s.setPositionY(offset + 1.2);
        s.setPositionZ(offset + 1.3);

        s.setScaleX(offset + 2.1);
        s.setScaleY(offset + 2.2);
        s.setScaleZ(offset + 2.3);

        // Some formats assume unit quaternions.
        // I'm looking at you, SPZ.
        double rx = offset;
        double ry = 0.2;
        double rz = 0.3;
        double rw = 0.4;
        double invLen =
            1.0f / Math.sqrt(rx * rx + ry * ry + rz * rz + rw * rw);
        s.setRotationX(rx * invLen);
        s.setRotationY(ry * invLen);
        s.setRotationZ(rz * invLen);
        s.setRotationW(rw * invLen);

        s.setOpacity(offset + 4.1);

        // Some formats are performing a quantization on the
        // spherical harmonics components that assumes the
        // coefficients to be in [-1.0, 1.0]. 
        // I'm looking at you, SPZ.
        int dims = s.getShDimensions();
        for (int i = 0; i < dims; i++)
        {
            double index0 = (i * 3 + 0);
            double index1 = (i * 3 + 1);
            double index2 = (i * 3 + 2);
            double a0 = index0 / 47.0;
            double a1 = index1 / 47.0;
            double a2 = index2 / 47.0;
            double shx = -1.0 + a0 * 2.0;
            double shy = -1.0 + a1 * 2.0;
            double shz = -1.0 + a2 * 2.0;
            s.setShX(i, shx);
            s.setShY(i, shy);
            s.setShZ(i, shz);
        }
        return s;
    }

}
