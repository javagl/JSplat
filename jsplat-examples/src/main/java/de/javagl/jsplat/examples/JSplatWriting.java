/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.io.IOException;
import java.util.List;

import de.javagl.jsplat.Splat;

/**
 * An example showing how to write splat data in different formats
 */
public class JSplatWriting
{
    /**
     * The entry point
     * 
     * @param args Not used
     * @throws IOException If an IO error occurs
     */
    public static void main(String[] args) throws IOException
    {
        List<Splat> splats = UnitCubeSplats.create();
        
        // Most of what had been done here is now moved to a utility method
        Utils.writeAll(splats, "./data", "unitCube");
    }
}
