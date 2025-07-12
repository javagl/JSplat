/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.SplatListWriter;
import de.javagl.jsplat.Splats;

/**
 * Tests for roundtripping between different splat representations
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
    private static List<SplatFormat> FORMATS =
        Arrays.asList(SplatFormat.values());

    /**
     * The entry point
     * 
     * @param args Not used
     * @throws IOException If an IO error occurs
     */
    public static void main(String[] args) throws IOException
    {
        LoggerUtil.initLogging();
        
        Files.createDirectories(Paths.get(BASE_DIRECTORY));

        FORMATS = Arrays.asList(SplatFormat.GSPLAT, SplatFormat.PLY_BINARY_LE,
            SplatFormat.PLY_ASCII, SplatFormat.SPZ);
        
        writeAll("rotations2D", SplatGrids.createRotations2D());
        writeAll("rotations", SplatGrids.createRotations());
        writeAll("shs1", SplatGrids.createShs1());
        writeAll("shs2", SplatGrids.createShs2());
        writeAll("shs3", SplatGrids.createShs3());
        writeAll("unitCube", UnitCubeSplats.create());
        
        //roundtripAll("unitCube");

        //float epsilon = 1e-6f;
        //float epsilon = 0.02f;
        //verifySingle(SplatGrids.createRotations(), SplatFormat.SPZ, epsilon);
    }

    private static List<SplatFormat> createFormats()
    {
        return FORMATS;
    }

    private static void verifySingle(List<? extends Splat> splats,
        SplatFormat sf, float epsilon) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SplatListWriter splatWriter = Utils.createWriter(sf);
        splatWriter.writeList(splats, baos);

        SplatListReader splatReader = Utils.createReader(sf);
        ByteArrayInputStream bais =
            new ByteArrayInputStream(baos.toByteArray());
        List<MutableSplat> readSplats = splatReader.readList(bais);

        for (int i = 0; i < readSplats.size(); i++)
        {
            Splat s0 = splats.get(i);
            Splat s1 = readSplats.get(i);
            String ss0 = Splats.createString(s0);
            String ss1 = Splats.createString(s1);
            boolean equal = Splats.equalsEpsilon(s0, s1, epsilon);
            logger.info("At " + i + " original:\n" + ss0);
            logger.info("At " + i + " read:\n" + ss1);
            logger.info("At " + i + " equal? " + equal);
        }
        boolean allEqual = Splats.equalsEpsilon(splats, readSplats, epsilon);
        logger.info("All equal? " + allEqual);
    }

    private static void writeAll(String baseName, List<? extends Splat> splats)
        throws IOException
    {
        for (SplatFormat sf : createFormats())
        {
            String baseFileName = createFileName(baseName, sf);
            Utils.write(splats, sf, baseFileName);
        }
    }

    private static void roundtripAll(String baseName) throws IOException
    {
        for (SplatFormat sf : createFormats())
        {
            for (SplatFormat tf : createFormats())
            {
                convert(baseName, sf, tf);
            }
        }
    }

    private static String createFileName(String baseName, SplatFormat sf)
    {
        return Utils.createFileName(BASE_DIRECTORY, baseName, sf);
    }

    private static String createFileName(String baseName, SplatFormat sf,
        SplatFormat tf)
    {
        return createFileName(BASE_DIRECTORY, baseName, sf, tf);
    }

    private static String createFileName(String baseDirectory, String baseName,
        SplatFormat sf, SplatFormat tf)
    {
        String fileName = baseName + "_" + sf + "_to_" + tf + "."
            + Utils.createFileExtension(tf);
        return Paths.get(baseDirectory, fileName).toString();
    }

    private static void convert(String baseName, SplatFormat sf, SplatFormat tf)
        throws IOException
    {
        String baseFileName = createFileName(baseName, sf);
        List<MutableSplat> splats = Utils.read(sf, baseFileName);
        String resultFileName = createFileName(baseName, sf, tf);
        logger.info("Write " + sf + " to " + tf);
        Utils.write(splats, tf, resultFileName);
    }

}
