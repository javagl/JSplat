/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.SplatListWriter;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.io.gsplat.GsplatSplatReader;
import de.javagl.jsplat.io.gsplat.GsplatSplatWriter;
import de.javagl.jsplat.io.ply.PlySplatReader;
import de.javagl.jsplat.io.ply.PlySplatWriter;
import de.javagl.jsplat.io.ply.PlySplatWriter.PlyFormat;
import de.javagl.jsplat.io.spz.SpzSplatReader;
import de.javagl.jsplat.io.spz.SpzSplatWriter;
import de.javagl.jsplat.io.spz.gltf.SpzGltfSplatReader;
import de.javagl.jsplat.io.spz.gltf.SpzGltfSplatWriter;

/**
 * An example/test for roundtripping between different splat representations
 */
@SuppressWarnings(
{ "javadoc", "unused" })
public class JSplatRoundtrips
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(JSplatRoundtrips.class.getName());

    private static final String BASE_DIRECTORY = "./data/roundtrip/";
    private static final String BASE_NAME = "unitCube";

    private static final int shDegree = 0;

    /**
     * The entry point
     * 
     * @param args Not used
     * @throws IOException If an IO error occurs
     */
    public static void main(String[] args) throws IOException
    {
        Files.createDirectories(Paths.get(BASE_DIRECTORY));
        writeAll();
        roundtripAll();
        // debugSingle(Format.SPZ);
    }

    private static void debugSingle(SplatFormat sf) throws IOException
    {
        String baseFileName = createFileName(sf);
        List<MutableSplat> splats = Utils.read(sf, baseFileName);
        List<MutableSplat> baseSplats = UnitCubeSplats.create();
        System.out.println("Results:");
        for (int i = 0; i < splats.size(); i++)
        {
            System.out.println("At " + i);
            System.out.println("Original:");
            System.out.println(Splats.createString(baseSplats.get(i)));
            System.out.println("Read:");
            System.out.println(Splats.createString(splats.get(i)));
        }
    }

    private static void writeAll() throws IOException
    {
        List<MutableSplat> baseSplats = UnitCubeSplats.create();

        for (SplatFormat sf : SplatFormat.values())
        {
            String baseFileName = createFileName(sf);
            Utils.write(baseSplats, sf, baseFileName);
        }
    }

    private static void roundtripAll() throws IOException
    {
        for (SplatFormat sf : SplatFormat.values())
        {
            for (SplatFormat tf : SplatFormat.values())
            {
                convert(sf, tf);
            }
        }
    }

    private static String createFileName(SplatFormat sf)
    {
        return Utils.createFileName(BASE_DIRECTORY, BASE_NAME, sf);
    }

    private static String createFileName(SplatFormat sf, SplatFormat tf)
    {
        return createFileName(BASE_DIRECTORY, BASE_NAME, sf, tf);
    }

    private static String createFileName(String baseDirectory, String baseName,
        SplatFormat sf, SplatFormat tf)
    {
        String fileName = baseName + "_" + sf + "_to_" + tf + "."
            + Utils.createFileExtension(tf);
        return Paths.get(baseDirectory, fileName).toString();
    }

    private static void convert(SplatFormat sf, SplatFormat tf)
        throws IOException
    {
        String baseFileName = createFileName(sf);
        List<MutableSplat> splats = Utils.read(sf, baseFileName);
        String resultFileName = createFileName(sf, tf);
        logger.info("Write " + sf + " to " + tf);
        Utils.write(splats, tf, resultFileName);
    }

}
