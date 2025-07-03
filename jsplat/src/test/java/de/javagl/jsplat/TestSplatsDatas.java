/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class TestSplatsDatas
{
    @Test
    public void testSplatsDatas() throws IOException
    {
        float epsilon = 0.0f;

        MutableSplat sa0 = Utils.createDummySplat(0.0f);
        MutableSplat sa1 = Utils.createDummySplat(100.0f);
        List<MutableSplat> splatsA = Arrays.asList(sa0, sa1);

        SplatData splatData = SplatDatas.fromSplats(splatsA);
        List<MutableSplat> splatsB = SplatDatas.toList(splatData);

        assertTrue(Splats.equalsEpsilon(splatsA, splatsB, epsilon));
    }

}
