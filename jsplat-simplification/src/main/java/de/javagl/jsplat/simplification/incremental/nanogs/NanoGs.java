/*
 * www.javagl.de - JSplat
 *
 * Copyright 2026 Marco Hutter - http://www.javagl.de
 * 
 * Ported from https://github.com/saliteta/NanoGS
 * Commit: 62ddc34e230a01c061b762103ef69113f6259e48
 *
 * Published under "Attribution-NonCommercial 4.0 International" license.
 * See the "NanoGS-LICENSE.txt" in the root directory of this project.
 */
package de.javagl.jsplat.simplification.incremental.nanogs;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.javagl.jsplat.Splat;
import de.javagl.jsplat.simplification.incremental.Edge;
import de.javagl.jsplat.simplification.incremental.Edges;
import de.javagl.jsplat.simplification.incremental.Pairs;

/**
 * The entry point of the NanoGS simplification.
 * 
 * Ported from https://github.com/saliteta/NanoGS 
 * Commit: 62ddc34e230a01c061b762103ef69113f6259e48
 * 
 * This class is not part of the public API.
 */
public class NanoGs
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(NanoGs.class.getName());

    /**
     * The log level
     */
    private static final Level level = Level.FINE;

    /**
     * Simplify the given list of splats
     * 
     * @param splats The splats
     * @param ratio The reduction ratio, in [0,1]
     * @return The result
     */
    public static List<Splat> simplify(List<? extends Splat> splats,
        float ratio)
    {
        // All fixed parameters for now
        float threshold = 0.1f;
        int k = 16;
        int nMc = 1;
        int seed = 0;
        float epsCov = 1e-8f;
        float lamGeo = 1.0f;
        float lamSh = 1.0f;
        float pCapRatio = 0.5f;
        
        logger.log(level, "Simplify " + splats.size() + " with ratio " + ratio);
        long beforeNs = System.nanoTime();

        List<Splat> cur = Collections.unmodifiableList(splats);
        int N0 = splats.size();
        int target = (int) Math.max(Math.ceil(N0 * ratio), 1);

        List<Splat> pruneCur = cur;
        cur = timed("Pruning",
            () -> NanoGsPruning.pruneByOpacity(pruneCur, threshold));
        logger.log(level, "Pruning: " + cur.size() + " splats left");

        float[][] Z = NanoGsSampling.makeGaussianSamples(nMc, seed);

        int iteration = 0;
        while (cur.size() > target)
        {
            int N = cur.size();
            logger.log(level, "Iteration " + iteration + ": " + N + " splats");

            int kEff = Math.min(Math.max(1, k), Math.max(1, N - 1));
            List<Splat> edgesCur = cur;
            List<Edge> edges =
                timed("  Computing edges", () -> Edges.compute(edgesCur, kEff));
            logger.log(level,
                "  Computing edges: " + edges.size() + " edges computed");

            if (edges.isEmpty())
            {
                logger.log(level, "  No more edges. Stopping");
                break;
            }

            List<Splat> weightsCur = cur;
            timed("  Computing weights", () ->
            {
                NanoGsCost.computeWeights(weightsCur, edges, Z, epsCov, lamGeo,
                    lamSh);
                return null;
            });

            int mergesNeeded = N - target;
            int pCap = (int) Math.max(1, Math.floor(pCapRatio * N0));
            int P = mergesNeeded > 0 ? Math.min(mergesNeeded, pCap) : null;

            List<Edge> pairs =
                timed("  Computing pairs", () -> Pairs.select(edges, N, P));
            logger.log(level,
                "  Computing pairs: " + pairs.size() + " pairs computed");

            if (pairs.isEmpty())
            {
                logger.log(level, "  No more pairs. Stopping");
                break;
            }

            List<Splat> mergeCur = cur;
            cur = timed("  Computing merged splats",
                () -> NanoGsMerge.mergePairs(mergeCur, pairs));

            iteration++;
        }

        long afterNs = System.nanoTime();
        logger.log(level,
            "Simplify " + splats.size() + " with ratio " + ratio + " DONE");
        logger.log(level, "Remaining splats: " + cur.size());
        logger.log(level, "Duration: " + ((afterNs - beforeNs) / 1e6) + " ms");

        return cur;
    }

    /**
     * Run the given supplier, measuring and printing execution time
     * 
     * @param <T> The return type
     * @param name The name of the task
     * @param supplier The supplier
     * @return The result
     */
    private static <T> T timed(String name, Supplier<? extends T> supplier)
    {
        logger.log(level, name + "...");
        long ns0 = System.nanoTime();
        T result = supplier.get();
        long ns1 = System.nanoTime();
        logger.log(level, name + " DONE, " + ((ns1 - ns0) / 1e6) + "ms");
        return result;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private NanoGs()
    {
        // Private constructor to prevent instantiation
    }

}
