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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.nio.FloatBuffer;
import java.util.Objects;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import de.javagl.rendering.core.view.Camera;
import de.javagl.rendering.core.view.CameraListener;
import de.javagl.rendering.core.view.CameraUtils;
import de.javagl.rendering.core.view.Rectangles;
import de.javagl.rendering.core.view.View;
import de.javagl.rendering.core.view.Views;
import de.javagl.rendering.interaction.Control;
import de.javagl.rendering.interaction.camera.CameraControls;

/**
 * Implementation of a camera, based on the classes from the
 * https://github.com/javagl/Rendering library
 */
class RenderingCamera
{
    /**
     * A direct float buffer for up to 16 elements
     */
    private final FloatBuffer floatBuffer16 = BufferUtils.createFloatBuffer(16);

    /**
     * The {@link Control} summarizing the mouse interaction
     */
    private final Control control;

    /**
     * The {@link View}
     */
    private final View view;

    /**
     * The component that the listeners are attached to
     */
    private Component component;

    /**
     * A listener for the {@link #component} that will update the aspect ratio
     * and viewport of the {@link View} when the size of the component changes
     */
    private final ComponentListener componentListener = new ComponentAdapter()
    {
        @Override
        public void componentResized(ComponentEvent e)
        {
            updateView();
        }
    };

    /**
     * Create a new external camera. Use {@link #setComponent(Component)} to
     * attach the interaction of this camera to a rendering component.
     */
    RenderingCamera()
    {
        this.view = Views.create();
        view.getCamera().addCameraListener(new CameraListener()
        {
            @Override
            public void cameraChanged(Camera camera)
            {
                if (component != null)
                {
                    component.repaint();
                }
            }
        });
        control = CameraControls.createDefaultTrackballControl(view);
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
    FloatBuffer obtainCurrentViewMatrixBuffer()
    {
        Matrix4f m = CameraUtils.computeViewMatrix(view.getCamera());
        writeMatrixToBufferColumnMajor4f(m, floatBuffer16);
        return floatBuffer16.slice();
    }

    /**
     * Returns a direct float buffer containing the 4x4 projection matrix, in
     * column-major order.
     * 
     * This method may always return the same buffer instance. Clients may not
     * store or modify this buffer.
     * 
     * @return The buffer
     */
    FloatBuffer obtainCurrentProjectionMatrixBuffer()
    {
        Matrix4f m = view.getProjectionMatrix();
        writeMatrixToBufferColumnMajor4f(m, floatBuffer16);
        return floatBuffer16.slice();
    }

    /**
     * Returns a direct float buffer containing the camera position.
     * 
     * This method may always return the same buffer instance. Clients may not
     * store or modify this buffer.
     * 
     * @return The buffer
     */
    FloatBuffer obtainCurrentEyePositionBuffer()
    {
        Camera camera = view.getCamera();
        Point3f eyePoint = camera.getEyePoint();
        floatBuffer16.put(0, eyePoint.x);
        floatBuffer16.put(1, eyePoint.y);
        floatBuffer16.put(2, eyePoint.z);
        FloatBuffer result = floatBuffer16.slice();
        result.position(0);
        result.limit(3);
        return result;
    }
    
    /**
     * Returns the field-of-view, in y-direction, in degrees
     * 
     * @return The field of view
     */
    float getFovDegY()
    {
        return getCamera().getFovDegY();
    }

    /**
     * Writes the given matrix into the given buffer, in column-major order.
     * 
     * @param m The matrix
     * @param target The buffer
     */
    private static void writeMatrixToBufferColumnMajor4f(Matrix4f m,
        FloatBuffer target)
    {
        int i = 0;
        target.put(i++, m.m00);
        target.put(i++, m.m10);
        target.put(i++, m.m20);
        target.put(i++, m.m30);
        target.put(i++, m.m01);
        target.put(i++, m.m11);
        target.put(i++, m.m21);
        target.put(i++, m.m31);
        target.put(i++, m.m02);
        target.put(i++, m.m12);
        target.put(i++, m.m22);
        target.put(i++, m.m32);
        target.put(i++, m.m03);
        target.put(i++, m.m13);
        target.put(i++, m.m23);
        target.put(i++, m.m33);
    }

    /**
     * Set the component that will receive the mouse events for controlling this
     * camera
     * 
     * @param newComponent The component. May not be <code>null</code>.
     */
    void setComponent(Component newComponent)
    {
        Objects.requireNonNull(newComponent, "The component may not be null");

        if (component != null)
        {
            component.removeComponentListener(componentListener);
            control.detachFrom(component);
        }

        component = newComponent;

        component.addComponentListener(componentListener);
        control.attachTo(component);
        updateView();

    }

    /**
     * Update the aspect ratio and viewport of the {@link View} based on the
     * current size of the {@link #component}
     */
    private void updateView()
    {
        int w = component.getWidth();
        int h = component.getHeight();
        view.setViewport(Rectangles.create(0, 0, w, h));
        view.setAspect((float) w / h);
    }

    /**
     * Returns the {@link Camera} that is wrapped in this instance
     * 
     * @return The {@link Camera}
     */
    private Camera getCamera()
    {
        return view.getCamera();
    }

    /**
     * Reset the camera to its initial configuration
     */
    void resetCamera()
    {
        Camera camera = getCamera();
        camera.setEyePoint(new Point3f(0, 0, 1));
        camera.setViewPoint(new Point3f(0, 0, 0));
        camera.setUpVector(new Vector3f(0, 1, 0));
        camera.setFovDegY(60.0f);
    }

    /**
     * Fit the camera to show the given bounding box
     * 
     * @param minMax The bounding box
     */
    void fit(float minMax[])
    {
        Camera camera = getCamera();
        if (minMax == null)
        {
            resetCamera();
            return;
        }

        // Note: This is a VERY simple implementation that does not
        // guarantee the "tightest fitting" view configuration, but
        // generously moves the camera so that for usual scenes
        // everything is visible, regardless of the aspect ratio

        // Compute diagonal length and center of the bounding box
        Point3f min = new Point3f();
        min.x = minMax[0];
        min.y = minMax[1];
        min.z = minMax[2];

        Point3f max = new Point3f();
        max.x = minMax[3];
        max.y = minMax[4];
        max.z = minMax[5];

        float diagonalLength = max.distance(min);

        Point3f center = new Point3f();
        Point3f size = new Point3f();
        size.sub(max, min);
        center.scaleAdd(0.5f, size, min);

        // Compute the normal of the view plane (i.e. the normalized
        // direction from the view point to the eye point)
        Vector3f viewPlaneNormal = new Vector3f();
        Point3f eyePoint = camera.getEyePoint();
        Point3f viewPoint = camera.getViewPoint();
        viewPlaneNormal.sub(eyePoint, viewPoint);
        viewPlaneNormal.normalize();

        // Compute the required viewing distance, and apply
        // it to the camera
        float fovRadY = (float) Math.toRadians(camera.getFovDegY());
        float distance =
            (float) (diagonalLength * 0.5 / Math.tan(fovRadY * 0.5));

        Point3f newViewPoint = new Point3f(center);
        Point3f newEyePoint = new Point3f();
        newEyePoint.scaleAdd(distance, viewPlaneNormal, newViewPoint);

        camera.setEyePoint(newEyePoint);
        camera.setViewPoint(newViewPoint);
    }

}
