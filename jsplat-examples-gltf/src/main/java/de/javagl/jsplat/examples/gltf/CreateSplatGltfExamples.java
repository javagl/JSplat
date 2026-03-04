/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jsplat.examples.gltf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import de.javagl.jgltf.model.impl.DefaultMeshPrimitiveModel;
import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.SplatListWriter;
import de.javagl.jsplat.examples.UnitCubeSplats;
import de.javagl.jsplat.examples.UnitShSplats;
import de.javagl.jsplat.io.ply.PlySplatWriter;
import de.javagl.jsplat.io.ply.PlySplatWriter.PlyFormat;
import de.javagl.jsplat.io.spz.SpzSplatWriter;

/**
 * Methods to create splat glTF example files
 */
public class CreateSplatGltfExamples
{
    /**
     * The entry point of the application
     * 
     * @param args Not used
     * @throws IOException When an IO error occurs
     */
    public static void main(String[] args) throws IOException
    {
        createGltfs();
        createCustom();
    }
    
    /**
     * Create the main glTF examples
     *  
     * @throws IOException When an IO error occurs
     */
    private static void createGltfs() throws IOException
    {
        String baseDir = "./data/";
        Files.createDirectories(Paths.get(baseDir));

        {
            GenericGltfSplatWriter w = new GenericGltfSplatWriter();
            createSplatsInMesh(w);
            w.write(new FileOutputStream(baseDir + "SplatsInMesh.glb"));
        }
        {
            GenericGltfSplatWriter w = new GenericGltfSplatWriter();
            createMeshInSplats(w);
            w.write(new FileOutputStream(baseDir + "MeshInSplats.glb"));
        }
        {
            GenericGltfSplatWriter w = new GenericGltfSplatWriter();
            createShGrid(w);
            w.write(new FileOutputStream(baseDir + "ShGrid.glb"));
        }
        {
            GenericGltfSplatWriter w = new GenericGltfSplatWriter();
            w.addSplats(SplatRotationTests.createRotationsX(), null);
            w.write(new FileOutputStream(baseDir + "RotationsX.glb"));
        }
        {
            GenericGltfSplatWriter w = new GenericGltfSplatWriter();
            w.addSplats(SplatRotationTests.createRotationsY(), null);
            w.write(new FileOutputStream(baseDir + "RotationsY.glb"));
        }
        {
            GenericGltfSplatWriter w = new GenericGltfSplatWriter();
            w.addSplats(SplatRotationTests.createRotationsZ(), null);
            w.write(new FileOutputStream(baseDir + "RotationsZ.glb"));
        }
        {
            GenericGltfSplatWriter w = new GenericGltfSplatWriter();
            w.addSplats(SplatScaleTests.createScales(), null);
            w.write(new FileOutputStream(baseDir + "Scales.glb"));
        }
        {
            GenericGltfSplatWriter w = new GenericGltfSplatWriter();
            createMixedDegrees(w);
            w.write(new FileOutputStream(baseDir + "MixedDegrees.glb"));
        }
        {
            GenericGltfSplatWriter w = new GenericGltfSplatWriter();
            w.addSplats(SplatDepthTests.createDepthTest(), null);
            w.write(new FileOutputStream(baseDir + "Depths.glb"));
        }
        {
            GenericGltfSplatWriter w = new GenericGltfSplatWriter();
            createScaledScales(w);
            w.write(new FileOutputStream(baseDir + "ScaledScales.glb"));
        }
    }

    /**
     * Create some highly specific tests 
     * 
     * @throws IOException When an IO error occurs
     */
    private static void createCustom() throws IOException
    {
        String baseDir = "./data/";
        Files.createDirectories(Paths.get(baseDir));

        {
            SplatListWriter w = new SpzSplatWriter();
            List<MutableSplat> splats = SplatRotationTests.createRotationsX();
            w.writeList(splats,
                new FileOutputStream(baseDir + "RotationsX.spz"));
        }
        {
            SplatListWriter w = new SpzSplatWriter();
            List<MutableSplat> splats = SplatRotationTests.createRotationsY();
            w.writeList(splats,
                new FileOutputStream(baseDir + "RotationsY.spz"));
        }
        {
            SplatListWriter w = new SpzSplatWriter();
            List<MutableSplat> splats = SplatRotationTests.createRotationsZ();
            w.writeList(splats,
                new FileOutputStream(baseDir + "RotationsZ.spz"));
        }
        {
            SplatListWriter w = new PlySplatWriter(PlyFormat.BINARY_LITTLE_ENDIAN);
            List<MutableSplat> splats = SplatDepthTests.createDepthTest();
            w.writeList(splats,
                new FileOutputStream(baseDir + "Depths.ply"));
        }
    }

    /**
     * Fill the given writer with an example splat data set and a mesh that
     * fully encloses the splats.
     * 
     * @param w The writer
     */
    private static void createSplatsInMesh(GenericGltfSplatWriter w)
    {
        List<MutableSplat> splats = UnitCubeSplats.create();
        w.addSplats(splats, null);

        DefaultMeshPrimitiveModel p = GltfModelElements.createUnitCube();
        float[] matrix = Matrices.createMatrixScale(20.0f);
        w.addMeshPrimitive(p, matrix);

    }

    /**
     * Fill the given writer with an example splat data set and a mesh that is
     * contained in the splats.
     * 
     * @param w The writer
     */
    private static void createMeshInSplats(GenericGltfSplatWriter w)
    {
        List<MutableSplat> splats = UnitCubeSplats.create();
        w.addSplats(splats, null);

        DefaultMeshPrimitiveModel p = GltfModelElements.createUnitCube();
        float[] matrix = Matrices.createMatrixScale(5.0f);
        w.addMeshPrimitive(p, matrix);
    }

    /**
     * Fill the given writer with an example splat data set that consists of 6
     * spherical harmonics test splat mesh primitives.
     * 
     * Each mesh primitive will contain splats that indicate the corners of a
     * cube, and contain one splat at the center that looks red from the right,
     * cyan from the left, green from the top, magenta from the bottom, blue
     * from the front, yellow from the back.
     *
     * The primitives will be attached to matrices that describe rotations of
     * (x:0), (x:90), (x:180), (y:-90), (y:90), and (x:270) degrees, causing all
     * different faces to face the viewer.
     * 
     * @param w The writer
     */
    private static void createShGrid(GenericGltfSplatWriter w)
    {
        List<MutableSplat> splats = UnitShSplats.createDeg3();
        {
            float[] matrix = Matrices.createMatrixX(0, -150, -75, 0);
            w.addSplats(splats, matrix);
        }
        {
            float[] matrix = Matrices.createMatrixX(90, -0, -75, 0);
            w.addSplats(splats, matrix);
        }
        {
            float[] matrix = Matrices.createMatrixX(180, 150, -75, 0);
            w.addSplats(splats, matrix);
        }

        {
            float[] matrix = Matrices.createMatrixY(-90, -150, 75, 0);
            w.addSplats(splats, matrix);
        }
        {
            float[] matrix = Matrices.createMatrixY(90, -0, 75, 0);
            w.addSplats(splats, matrix);
        }
        {
            float[] matrix = Matrices.createMatrixX(270, 150, 75, 0);
            w.addSplats(splats, matrix);
        }
    }
    
    /**
     * Fill the given writer with two splat mesh primitives, the first
     * one having SH degree 3, and the second one having SH degree 2.
     * 
     * @param w The writer
     */
    private static void createMixedDegrees(GenericGltfSplatWriter w)
    {
        {
            float[] matrix =
                Matrices.createMatrixTranslation(-75.0f, 0.0f, 0.0f);
            List<MutableSplat> splats = UnitShSplats.createDeg2();
            w.addSplats(splats, matrix);
        }
        {
            float[] matrix =
                Matrices.createMatrixTranslation(75.0f, 0.0f, 0.0f);
            List<MutableSplat> splats = UnitShSplats.createDeg3();
            w.addSplats(splats, matrix);
        }
    }
    

    /**
     * TODO
     * 
     * @param w The writer
     */
    private static void createScaledScales(GenericGltfSplatWriter w)
    {
        List<MutableSplat> splats = SplatScaleTests.createScales();
        {
            float[] matrix = Matrices.createMatrixScale(0.5f);
            matrix[12] = -200.0f;
            w.addSplats(splats, matrix);
        }
        {
            float[] matrix = Matrices.createMatrixScale(1.0f);
            w.addSplats(splats, matrix);
        }
        {
            float[] matrix = Matrices.createMatrixScale(5.0f);
            matrix[12] = 450.0f;
            w.addSplats(splats, matrix);
        }
    }


}
