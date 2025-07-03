/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat;

/**
 * Utilities for the tests
 */
class Utils
{
    /**
     * Private constructor to prevent instantiation
     */
    private Utils()
    {
        // Private constructor to prevent instantiation
    }

    /**
     * Create a dummy splat with values that do not make sense, but
     * are all different and easily identifiable (for tests)
     * 
     * @return The splat
     */
    static MutableSplat createDummySplat()
    {
        return Utils.createDummySplat(0.0f);
    }

    /**
     * Create a dummy splat with values that do not make sense, but
     * are all different and easily identifiable (for tests)
     * 
     * @param offset An offset for the values
     * @return The splat
     */
    static MutableSplat createDummySplat(float offset)
    {
        MutableSplat s = Splats.create(3);
        s.setPositionX(offset + 1.1f);
        s.setPositionY(offset + 1.2f);
        s.setPositionZ(offset + 1.3f);
    
        s.setScaleX(offset + 2.1f);
        s.setScaleY(offset + 2.2f);
        s.setScaleZ(offset + 2.3f);
    
        s.setRotationX(offset + 3.1f);
        s.setRotationY(offset + 3.2f);
        s.setRotationZ(offset + 3.3f);
        s.setRotationW(offset + 3.3f);
    
        s.setOpacity(offset + 4.1f);
    
        for (int i=0; i<s.getShDimensions(); i++)
        {
            s.setShX(i, offset + i + 0.1f);
            s.setShY(i, offset + i + 0.2f);
            s.setShZ(i, offset + i + 0.3f);
        }
        return s;
    }

}
