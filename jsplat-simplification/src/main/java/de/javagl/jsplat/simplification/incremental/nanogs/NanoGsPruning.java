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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;

/**
 * Methods for the opacity-based pruning of NanoGS
 * 
 * Ported from https://github.com/saliteta/NanoGS
 * Commit: 62ddc34e230a01c061b762103ef69113f6259e48
 */
class NanoGsPruning
{
    /**
     * Prune the given list by the given opacity threshold.
     * 
     * The threshold is referring to an ALPHA value in [0,1].
     * 
     * Location: scripts/simplify.js#L305
     * 
     * @param state The state
     * @param threshold The threshold
     * @return The pruned list
     */
    static List<Splat> pruneByOpacity(List<Splat> state,
        float threshold)
    {
        int N = state.size();
        if (N == 0)
        {
            return state;
        }
        float ops[] = new float[N];
        IntStream.range(0, N).parallel().forEach(i ->
        {
            float vo = state.get(i).getOpacity();
            float v = Splats.opacityToAlpha(vo);
            ops[i] = v;
        });
        float median = NanoGsMath.percentileInPlace(ops, 0.5f);
        float thr = Math.min(threshold, median);
        List<Splat> keep = state.stream().parallel().filter(s -> 
        {
            float vo = s.getOpacity();
            float v = Splats.opacityToAlpha(vo);
            return v >= thr;            
        }).collect(Collectors.toList());
        return keep;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private NanoGsPruning()
    {
        // Private constructor to prevent instantiation
    }
    

}
