/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.io.gltf.spz.GltfSpzSplatReader;
import de.javagl.jsplat.io.gltf.spz.GltfSpzSplatWriter;

/**
 * Experiments for writing and reading SPZ-compressed Gaussian splats in glTF.
 * 
 * This is used internally, for tracking the development of
 * https://github.com/KhronosGroup/glTF/pull/2490
 */
public class JSplatGltfRoundtripExperiments
{
    /**
     * The entry point
     * 
     * @param args Not used
     * @throws IOException If an IO error occurs
     */
    public static void main(String[] args) throws IOException
    {
        List<MutableSplat> splatsA = UnitCubeSplats.create();
        
        GltfSpzSplatWriter w = new GltfSpzSplatWriter();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        w.writeList(splatsA, os);

        GltfSpzSplatReader r = new GltfSpzSplatReader();
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        List<MutableSplat> splatsB = r.readList(is);
        
        // The huge epsilon is required for the 'scales', where a value
        // of 0.1 becomes 0.125 due to quantization
        float epsilon = 0.03f;
        for (int i=0; i<splatsA.size(); i++)
        {
            Splat splatA = splatsA.get(i);
            Splat splatB = splatsB.get(i);
            
            boolean equal = Splats.equalsEpsilon(splatA, splatB, epsilon);
            if (!equal) 
            {
                System.out.println("Splat "+i+" A");
                System.out.println(Splats.createString(splatA));
                System.out.println("Splat "+i+" B");
                System.out.println(Splats.createString(splatB));
            }
        }
        
        boolean allEqual = Splats.equalsEpsilon(splatsA, splatsB, epsilon);
        System.out.println("All equal? " + allEqual);
    }
}
