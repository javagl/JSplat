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
package de.javagl.jsplat.viewer;

import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * Methods to create {@link SplatViewer} instances
 */
public class SplatViewers
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(SplatViewers.class.getName());

    /**
     * Try to create an unspecified {@link SplatViewer} instance.
     * 
     * This will use the first {@link SplatViewerFactory} implementation that is
     * found using a service loader. If none can be found, then a warning is
     * printed and <code>null</code> is returned.
     * 
     * @return The {@link SplatViewer} instance
     */
    public static SplatViewer createDefault()
    {
        ServiceLoader<SplatViewerFactory> factories =
            ServiceLoader.load(SplatViewerFactory.class);
        for (SplatViewerFactory factory : factories)
        {
            logger.fine("Found factory: " + factory);
            return factory.create();
        }
        logger.severe("Found no implementation of SplatViewerFactory");
        return null;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private SplatViewers()
    {
        // Private constructor to prevent instantiation
    }

}
