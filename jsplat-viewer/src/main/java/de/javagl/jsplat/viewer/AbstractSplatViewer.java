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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.FloatBuffer;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import de.javagl.jsplat.Splat;

/**
 * Abstract base implementation of a {@link SplatViewer}
 */
public abstract class AbstractSplatViewer implements SplatViewer
{

    /**
     * The list of commands that should be executed once, immediately prior to
     * rendering in the next frame.
     */
    protected final List<Runnable> preRenderCommands;

    /**
     * The camera that is used for rendering
     */
    private RenderingCamera renderingCamera;

    /**
     * Default constructor
     */
    protected AbstractSplatViewer()
    {
        this.preRenderCommands = new CopyOnWriteArrayList<Runnable>();

        addPreRenderCommand(() ->
        {
            this.renderingCamera = new RenderingCamera();
            Component renderComponent = getRenderComponent();
            renderingCamera.setComponent(renderComponent);
        });

    }

    /**
     * Returns the splats that are currently displayed, or <code>null</code> if
     * there are no splats
     * 
     * @return The splats
     */
    protected abstract List<? extends Splat> getSplats();

    @Override
    public void fitCamera()
    {
        addPreRenderCommand(() ->
        {
            fitCameraInternal();
        });
    }
        
    /**
     * Internal version of {@link #fitCamera()}, to be called as a pre-render
     * command
     */
    private void fitCameraInternal()
    {
        List<? extends Splat> splats = getSplats();
        if (splats == null)
        {
            renderingCamera.resetCamera();
            return;
        }
        float minMax[] = new float[]
        { Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
            Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY,
            Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY };
        for (Splat s : splats)
        {
            minMax[0] = Math.min(minMax[0], s.getPositionX());
            minMax[1] = Math.min(minMax[1], s.getPositionY());
            minMax[2] = Math.min(minMax[2], s.getPositionZ());
            minMax[3] = Math.max(minMax[3], s.getPositionX());
            minMax[4] = Math.max(minMax[4], s.getPositionY());
            minMax[5] = Math.max(minMax[5], s.getPositionZ());
        }
        renderingCamera.fit(minMax);
    }

    
    @Override
    public void resetCamera()
    {
        addPreRenderCommand(() ->
        {
            resetCameraInternal();
        });
    }
        
    /**
     * Internal version of {@link #resetCamera()}, to be called as a pre-render
     * command
     */
    private void resetCameraInternal()
    {
        renderingCamera.resetCamera();
    }
    
    @Override
    public final void addPreRenderCommand(Runnable command)
    {
        Objects.requireNonNull(command, "The command may not be null");
        this.preRenderCommands.add(command);
        Component c = getRenderComponent();
        if (c != null)
        {
            c.repaint();
        }
    }

    /**
     * To be called by implementations immediately before rendering, to process
     * and clear the list of pre-render commands.
     */
    protected void processPreRenderCommands()
    {
        Deque<Runnable> commands =
            new LinkedList<Runnable>(this.preRenderCommands);
        this.preRenderCommands.clear();
        while (!commands.isEmpty())
        {
            Runnable command = commands.poll();
            command.run();
        }
    }

    @Override
    public abstract Component getRenderComponent();

    /**
     * Returns a direct float buffer containing the 4x4 projection matrix, in
     * column-major order.
     * 
     * This method may always return the same buffer instance. Clients may not
     * store or modify this buffer.
     * 
     * @return The buffer
     */
    protected final FloatBuffer obtainCurrentProjectionMatrixBuffer()
    {
        return renderingCamera.obtainCurrentProjectionMatrixBuffer();
    }

    /**
     * Returns a direct float buffer containing the 4x4 view matrix, in
     * column-major order.
     * 
     * This method may always return the same buffer instance. Clients may not
     * store or modify this buffer.
     * 
     * @return The buffer
     */
    protected final FloatBuffer obtainCurrentViewMatrixBuffer()
    {
        return renderingCamera.obtainCurrentViewMatrixBuffer();
    }

    /**
     * Returns a direct float buffer containing the camera position.
     * 
     * This method may always return the same buffer instance. Clients may not
     * store or modify this buffer.
     * 
     * @return The buffer
     */
    protected final FloatBuffer obtainCurrentEyePositionBuffer()
    {
        return renderingCamera.obtainCurrentEyePositionBuffer();
    }

    /**
     * Returns the field-of-view of the camera, in y-direction, in degrees
     * 
     * @return The field of view
     */
    protected final float getCameraFovDegY()
    {
        return renderingCamera.getFovDegY();
    }

    /**
     * Read the specified resource as a string
     * 
     * @param c The class against which the resource is resolved
     * @param resourceName The resource name
     * @return The string
     * @throws IOException If an IO error occurs
     */
    private static String readResourceAsString(Class<?> c, String resourceName)
        throws IOException
    {
        InputStream resourceInputStream =
            AbstractSplatViewer.class.getResourceAsStream(resourceName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[8192];
        while (true)
        {
            int read = resourceInputStream.read(buffer);
            if (read < 0)
            {
                break;
            }
            baos.write(buffer, 0, read);
        }
        return baos.toString();
    }

    /**
     * Read the specified resource as a string
     * 
     * @param c The class against which the resource is resolved
     * @param resourceName The resource name
     * @return The string
     */
    protected static String readResourceAsStringUnchecked(Class<?> c,
        String resourceName)
    {
        try
        {
            return readResourceAsString(c, resourceName);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

}