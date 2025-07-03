/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class TestSplats
{
    @Test
    public void testSplatsEquals() throws IOException
    {
        float epsilon = 1e-6f;
        
        MutableSplat sa = Utils.createDummySplat();
        MutableSplat sb = Utils.createDummySplat();
        assertTrue(Splats.equalsEpsilon(sa, sb, epsilon));
        
        // Degree
        sb = Splats.create(0);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));

        sb = Splats.create(1);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));

        sb = Splats.create(2);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        
        // Position
        sb = Utils.createDummySplat();
        sb.setPositionX(-999.0f);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        
        sb = Utils.createDummySplat();
        sb.setPositionY(-999.0f);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        
        sb = Utils.createDummySplat();
        sb.setPositionZ(-999.0f);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        
        // Scale
        sb = Utils.createDummySplat();
        sb.setScaleX(-999.0f);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        
        sb = Utils.createDummySplat();
        sb.setScaleY(-999.0f);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        
        sb = Utils.createDummySplat();
        sb.setScaleZ(-999.0f);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        
        // Rotation
        sb = Utils.createDummySplat();
        sb.setRotationX(-999.0f);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        
        sb = Utils.createDummySplat();
        sb.setRotationY(-999.0f);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        
        sb = Utils.createDummySplat();
        sb.setRotationZ(-999.0f);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        
        sb = Utils.createDummySplat();
        sb.setRotationW(-999.0f);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        
        // Opacity
        sb = Utils.createDummySplat();
        sb.setOpacity(-999.0f);
        assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        
        // Spherical harmonics
        int dimensions = sa.getShDimensions();
        for (int d=0; d<dimensions; d++)
        {
            sb = Utils.createDummySplat();
            sb.setShX(d, -999.0f);
            assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
            
            sb = Utils.createDummySplat();
            sb.setShY(d, -999.0f);
            assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
            
            sb = Utils.createDummySplat();
            sb.setShZ(d, -999.0f);
            assertFalse(Splats.equalsEpsilon(sa, sb, epsilon));
        }
    }

}
