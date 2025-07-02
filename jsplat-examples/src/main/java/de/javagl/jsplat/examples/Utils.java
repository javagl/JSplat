/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.SplatListWriter;
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
 * Utilities for the JSplat examples
 */
class Utils
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(Utils.class.getName());

    /**
     * Create a {@link SplatListReader} for the specified format
     * 
     * @param format The format
     * @return The {@link SplatListReader}
     */
    static SplatListReader createReader(SplatFormat format)
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

    /**
     * Create a {@link SplatListWriter} for the specified format
     * 
     * @param format The format
     * @return The {@link SplatListWriter}
     */
    static SplatListWriter createWriter(SplatFormat format)
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

    /**
     * Create a file name in the given base directory, using the given base
     * name, including the format string, with the appropriate file extension
     * 
     * @param baseDirectory The base directory
     * @param baseName The base name for the file
     * @param sf The splat format
     * @return The file name
     */
    static String createFileName(String baseDirectory, String baseName,
        SplatFormat sf)
    {
        String fileName = baseName + "_" + sf + "." + createFileExtension(sf);
        return Paths.get(baseDirectory, fileName).toString();
    }

    /**
     * Create the appropriate file extension for the given format, without the
     * dot.
     * 
     * @param format The format
     * @return The extension
     */
    static String createFileExtension(SplatFormat format)
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

    /**
     * Read a list of splats from the specified file in the given format.
     * 
     * @param format The format
     * @param fileName The file name
     * @return The splats
     * @throws IOException If an IO error occurs
     */
    static List<MutableSplat> read(SplatFormat format, String fileName)
        throws IOException
    {
        BufferedInputStream bis =
            new BufferedInputStream(new FileInputStream(fileName));
        SplatListReader splatReader = createReader(format);
        List<MutableSplat> splats = splatReader.readList(bis);
        bis.close();
        return splats;
    }

    /**
     * Write the given list of splats to the specified file in the given format.
     * 
     * @param splats The splats
     * @param format The format
     * @param fileName The file name
     * @throws IOException If an IO error occurs
     */
    static void write(List<? extends Splat> splats, SplatFormat format,
        String fileName) throws IOException
    {
        BufferedOutputStream bos =
            new BufferedOutputStream(new FileOutputStream(fileName));
        SplatListWriter splatWriter = createWriter(format);
        splatWriter.writeList(splats, bos);
        bos.close();
    }

    /**
     * Write the given list of splats to the specified output directory, in each
     * format that is supported.
     * 
     * @param splats The splats
     * @param baseDirectory The base directory
     * @param baseName The base name for the files
     * @throws IOException If an IO error occurs
     */
    static void writeAll(List<? extends Splat> splats, String baseDirectory,
        String baseName) throws IOException
    {
        for (SplatFormat format : SplatFormat.values())
        {
            String fileName = createFileName(baseDirectory, baseName, format);
            BufferedOutputStream bos =
                new BufferedOutputStream(new FileOutputStream(fileName));
            SplatListWriter splatWriter = createWriter(format);
            splatWriter.writeList(splats, bos);
            bos.close();
        }
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Utils()
    {
        // Private constructor to prevent instantiation
    }

}
