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

    enum Format
    {
        GSPLAT, PLY_ASCII, PLY_BINARY_LE, PLY_BINARY_BE, SPZ, SPZ_GLTF
    }

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

    private static void debugSingle(Format sf) throws IOException
    {
        String baseFileName = createFileName(sf);
        List<MutableSplat> splats = read(sf, baseFileName);
        List<Splat> baseSplats = UnitCubeSplats.create();
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
        List<Splat> baseSplats = UnitCubeSplats.create();

        for (Format sf : Format.values())
        {
            String baseFileName = createFileName(sf);
            write(baseSplats, sf, baseFileName);
        }
    }

    private static void roundtripAll() throws IOException
    {
        for (Format sf : Format.values())
        {
            for (Format tf : Format.values())
            {
                convert(sf, tf);
            }
        }
    }

    private static SplatListReader createReader(Format format)
    {
        switch (format)
        {
            case GSPLAT:
                return new GsplatSplatReader();
            case PLY_ASCII:
            case PLY_BINARY_LE:
            case PLY_BINARY_BE:
                return new PlySplatReader();
            case SPZ:
                return new SpzSplatReader();
            case SPZ_GLTF:
                return new SpzGltfSplatReader();
        }
        logger.severe("Unknown format: " + format);
        return null;
    }

    private static SplatListWriter createWriter(Format format)
    {
        switch (format)
        {
            case GSPLAT:
                return new GsplatSplatWriter();
            case PLY_ASCII:
                return new PlySplatWriter(PlyFormat.ASCII);
            case PLY_BINARY_LE:
                return new PlySplatWriter(PlyFormat.BINARY_LITTLE_ENDIAN);
            case PLY_BINARY_BE:
                return new PlySplatWriter(PlyFormat.BINARY_BIG_ENDIAN);
            case SPZ:
                return new SpzSplatWriter();
            case SPZ_GLTF:
                return new SpzGltfSplatWriter();
        }
        logger.severe("Unknown format: " + format);
        return null;
    }

    private static String createFileExtension(Format format)
    {
        switch (format)
        {
            case GSPLAT:
                return "splat";
            case PLY_ASCII:
            case PLY_BINARY_LE:
            case PLY_BINARY_BE:
                return "ply";
            case SPZ:
                return "spz";
            case SPZ_GLTF:
                return "glb";
        }
        logger.severe("Unknown format: " + format);
        return null;
    }

    private static String createFileName(Format sf)
    {
        String baseFileName = BASE_DIRECTORY + BASE_NAME + "_" + sf + "."
            + createFileExtension(sf);
        return baseFileName;
    }

    private static String createFileName(Format sf, Format tf)
    {
        String resultFileName = BASE_DIRECTORY + BASE_NAME + "_" + sf + "_to_"
            + tf + "." + createFileExtension(tf);
        return resultFileName;
    }

    private static void convert(Format sf, Format tf) throws IOException
    {
        String baseFileName = createFileName(sf);
        List<MutableSplat> splats = read(sf, baseFileName);
        String resultFileName = createFileName(sf, tf);
        logger.info("Write " + sf + " to " + tf);
        write(splats, tf, resultFileName);
    }

    private static void write(List<? extends Splat> splats, Format format,
        String fileName) throws IOException
    {
        BufferedOutputStream bos =
            new BufferedOutputStream(new FileOutputStream(fileName));
        SplatListWriter splatWriter = createWriter(format);
        splatWriter.writeList(splats, bos);
        bos.close();
    }

    private static List<MutableSplat> read(Format format, String fileName)
        throws IOException
    {
        BufferedInputStream bis =
            new BufferedInputStream(new FileInputStream(fileName));
        SplatListReader splatReader = createReader(format);
        List<MutableSplat> splats = splatReader.readList(bis);
        bis.close();
        return splats;
    }
}
