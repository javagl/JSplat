/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.SplatListWriter;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.io.ply.PlySplatReader;
import de.javagl.jsplat.io.spz.SpzSplatWriter;

/**
 * An example showing how to use JSPlat
 */
public class JSplatExample
{
    /**
     * The entry point
     * 
     * @param args Not used
     * @throws IOException If an IO error occurs
     */
    public static void main(String[] args) throws IOException
    {
        // Create an input stream for the PLY data
        InputStream is = new FileInputStream("./data/unitCube-ascii.ply");
        
        // Create a PLY reader, and read the splats
        SplatListReader r = new PlySplatReader();
        List<MutableSplat> splats = r.readList(is);

        // Print some information about the first few splats...
        int n = Math.min(3, splats.size());
        for (int i = 0; i < n; i++)
        {
            Splat splat = splats.get(i);
            System.out.println("Splat " + i + ":");
            System.out.println(Splats.createString(splat));
        }

        // Create an output stream for the SPZ data
        OutputStream os = new FileOutputStream("./data/unitCube-output.spz");
        
        // Create an SPZ writer, and write the splats
        SplatListWriter w = new SpzSplatWriter();
        w.writeList(splats, os);
    }

}
