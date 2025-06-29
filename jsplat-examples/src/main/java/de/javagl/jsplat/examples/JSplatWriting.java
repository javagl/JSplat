/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListWriter;
import de.javagl.jsplat.SplatStreamWriter;
import de.javagl.jsplat.io.gsplat.GsplatSplatWriter;
import de.javagl.jsplat.io.ply.PlySplatWriter;
import de.javagl.jsplat.io.ply.PlySplatWriter.PlyFormat;
import de.javagl.jsplat.io.spz.SpzSplatWriter;

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

        writeGsplat(splats, "./data/unitCube.splat");
        writePlyAscii(splats, "./data/unitCube-ascii.ply");
        writePlyBinaryLE(splats, "./data/unitCube-binaryLE.ply");
        writePlyBinaryBE(splats, "./data/unitCube-binaryBE.ply");
        writeSpz(splats, "./data/unitCube.spz");
    }

    /**
     * Write the given splats as PLY ASCII to the specified file
     * 
     * @param splats The splats
     * @param fileName The file name
     * @throws IOException If an IO error occurs
     */
    private static void writePlyAscii(List<Splat> splats, String fileName)
        throws IOException
    {
        BufferedOutputStream bos =
            new BufferedOutputStream(new FileOutputStream(fileName));
        SplatListWriter splatWriter = new PlySplatWriter(PlyFormat.ASCII);
        splatWriter.writeList(splats, bos);
        bos.close();
    }

    /**
     * Write the given splats as PLY binary little endian to the specified file
     * 
     * @param splats The splats
     * @param fileName The file name
     * @throws IOException If an IO error occurs
     */
    private static void writePlyBinaryLE(List<Splat> splats, String fileName)
        throws IOException
    {
        BufferedOutputStream bos =
            new BufferedOutputStream(new FileOutputStream(fileName));
        SplatListWriter splatWriter =
            new PlySplatWriter(PlyFormat.BINARY_LITTLE_ENDIAN);
        splatWriter.writeList(splats, bos);
        bos.close();
    }

    /**
     * Write the given splats as PLY binary big endiant to the specified file
     * 
     * @param splats The splats
     * @param fileName The file name
     * @throws IOException If an IO error occurs
     */
    private static void writePlyBinaryBE(List<Splat> splats, String fileName)
        throws IOException
    {
        BufferedOutputStream bos =
            new BufferedOutputStream(new FileOutputStream(fileName));
        SplatListWriter splatWriter =
            new PlySplatWriter(PlyFormat.BINARY_BIG_ENDIAN);
        splatWriter.writeList(splats, bos);
        bos.close();
    }

    /**
     * Write the given splats as 'gsplat' data to the specified file
     * 
     * @param splats The splats
     * @param fileName The file name
     * @throws IOException If an IO error occurs
     */
    private static void writeGsplat(List<Splat> splats, String fileName)
        throws IOException
    {
        BufferedOutputStream bos =
            new BufferedOutputStream(new FileOutputStream(fileName));
        SplatStreamWriter splatWriter = new GsplatSplatWriter();
        splatWriter.writeStream(splats.stream(), bos);
        bos.close();
    }

    /**
     * Write the given splats as SPZ data to the specified file
     * 
     * @param splats The splats
     * @param fileName The file name
     * @throws IOException If an IO error occurs
     */
    private static void writeSpz(List<Splat> splats, String fileName)
        throws IOException
    {
        BufferedOutputStream bos =
            new BufferedOutputStream(new FileOutputStream(fileName));
        SplatListWriter splatWriter = new SpzSplatWriter();
        splatWriter.writeList(splats, bos);
        bos.close();
    }

}
