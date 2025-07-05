/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.javagl.jsplat.MutableSplat;

/**
 * A class writing different test data sets consisting of grids of splats
 */
public class SplatGridExamples
{
    /**
     * The base output directory
     */
    private static final String BASE_DIRECTORY = "./data/grids/";

    /**
     * The entry point
     * 
     * @param args Not used
     * @throws IOException If an IO error occurs
     */
    public static void main(String[] args) throws IOException
    {
        writeAll("basic", SplatGrids.createBasic());
        writeAll("scales", SplatGrids.createScales());
        writeAll("colors", SplatGrids.createColors());
        writeAll("opacities", SplatGrids.createOpacities());
        writeAll("rotations", SplatGrids.createRotations());
        writeAll("shs1", SplatGrids.createShs1());
        writeAll("shs3", SplatGrids.createShs3());

        writeAll("rotations2D", SplatGrids.createRotations2D());
        
        List<MutableSplat> all = new ArrayList<MutableSplat>();
        all.addAll(SplatTransforms.translateList(SplatGrids.createScales(),
            -125.0f, -75.0f, 0.0f));
        all.addAll(SplatTransforms.translateList(SplatGrids.createOpacities(),
            -125.0f, 75.0f, 0.0f));
        all.addAll(SplatTransforms.translateList(SplatGrids.createColors(),
            0.0f, -75.0f, 0.0f));
        all.addAll(SplatTransforms.translateList(SplatGrids.createRotations(),
            0.0f, 75.0f, 0.0f));
        all.addAll(SplatTransforms.translateList(SplatGrids.createShs1(), 
            125.0f, -75.0f, 0.0f));
        all.addAll(SplatTransforms.translateList(SplatGrids.createShs3(), 
            125.0f, 75.0f, 0.0f));
        List<MutableSplat> combined = all.stream()
            .map(SplatTransforms.changedDegree(1)).collect(Collectors.toList());
        writeAll("combined", combined);
        
    }

    /**
     * Write the given splats into the output directory, in all supported
     * formats, using the given base file name
     * 
     * @param baseName The base file name
     * @param splats The splats
     * @throws IOException If an IO error occurs
     */
    private static void writeAll(String baseName, List<MutableSplat> splats)
        throws IOException
    {
        Files.createDirectories(Paths.get(BASE_DIRECTORY));
        Utils.writeAll(splats, BASE_DIRECTORY, baseName);
    }

}
