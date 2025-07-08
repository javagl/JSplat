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

import java.awt.Component;
import java.util.List;

import de.javagl.jsplat.Splat;

/**
 * Interface for a class that can render gaussian splats
 */
public interface SplatViewer
{
    /**
     * Set the splats that should be displayed.
     * 
     * This may only be called as part of a
     * {@link #addPreRenderCommand(Runnable)}.
     * 
     * @param splats The splats
     */
    void setSplats(List<? extends Splat> splats);

    /**
     * Fit the camera to show the current splats.
     * 
     * This may only be called as part of a
     * {@link #addPreRenderCommand(Runnable)}.
     */
    void fitCamera();

    /**
     * Returns the main rendering component
     * 
     * @return The rendering component
     */
    Component getRenderComponent();

    /**
     * Add a command to be executed before the next rendering pass, and trigger
     * a rendering.
     * 
     * For GL-based texture viewers, the given command will be be executed while
     * the GL context is current.
     * 
     * @param command The command
     */
    void addPreRenderCommand(Runnable command);

}