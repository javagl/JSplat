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
package de.javagl.jsplat.app;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import de.javagl.common.ui.JSpinners;

/**
 * A panel containing controls for transforms
 */
class TransformPanel extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -177129752930137068L;

    /**
     * The spinner for the translation along x
     */
    private JSpinner translationXSpinner;

    /**
     * The spinner for the translation along y
     */
    private JSpinner translationYSpinner;

    /**
     * The spinner for the translation along z
     */
    private JSpinner translationZSpinner;

    /**
     * The spinner for the rotation around x
     */
    private JSpinner rotationDegXSpinner;

    /**
     * The spinner for the rotation around y
     */
    private JSpinner rotationDegYSpinner;

    /**
     * The spinner for the rotation around z
     */
    private JSpinner rotationDegZSpinner;

    /**
     * The spinner for the scale along x
     */
    private JSpinner scaleXSpinner;

    /**
     * The spinner for the scale along y
     */
    private JSpinner scaleYSpinner;

    /**
     * The spinner for the scale along z
     */
    private JSpinner scaleZSpinner;

    
    /**
     * Default constructor
     */
    TransformPanel()
    {
        super(new GridLayout(0, 1));
        add(createTranslationPanel());
        add(createRotationPanel());
        add(createScalePanel());
    }

    /**
     * Create the panel with the translation controls
     * 
     * @return The panel
     */
    private JPanel createTranslationPanel()
    {
        JPanel p = new JPanel(new GridLayout(0, 1));

        JPanel px = new JPanel(new BorderLayout());
        px.add(new JLabel("Translation X:"), BorderLayout.WEST);
        SpinnerNumberModel modelX =
            new SpinnerNumberModel(0.0, -1000000.0, 1000000.0, 0.001);
        translationXSpinner = new JSpinner(modelX);
        JSpinners.setSpinnerDraggingEnabled(translationXSpinner, true);
        px.add(translationXSpinner, BorderLayout.CENTER);
        p.add(px);

        JPanel py = new JPanel(new BorderLayout());
        py.add(new JLabel("Translation Y:"), BorderLayout.WEST);
        SpinnerNumberModel modelY =
            new SpinnerNumberModel(0.0, -1000000.0, 1000000.0, 0.001);
        translationYSpinner = new JSpinner(modelY);
        JSpinners.setSpinnerDraggingEnabled(translationYSpinner, true);
        py.add(translationYSpinner, BorderLayout.CENTER);
        p.add(py);

        JPanel pz = new JPanel(new BorderLayout());
        pz.add(new JLabel("Translation Z:"), BorderLayout.WEST);
        SpinnerNumberModel modelZ =
            new SpinnerNumberModel(0.0, -1000000.0, 1000000.0, 0.001);
        translationZSpinner = new JSpinner(modelZ);
        JSpinners.setSpinnerDraggingEnabled(translationZSpinner, true);
        pz.add(translationZSpinner, BorderLayout.CENTER);
        p.add(pz);

        return p;
    }

    /**
     * Create the panel with the rotation controls
     * 
     * @return The panel
     */
    private JPanel createRotationPanel()
    {
        JPanel p = new JPanel(new GridLayout(0, 1));

        JPanel px = new JPanel(new BorderLayout());
        px.add(new JLabel("Rotation X:"), BorderLayout.WEST);
        SpinnerNumberModel modelX =
            new SpinnerNumberModel(0.0, -180.0, 180.0, 0.1);
        rotationDegXSpinner = new JSpinner(modelX);
        JSpinners.setSpinnerDraggingEnabled(rotationDegXSpinner, true);
        px.add(rotationDegXSpinner, BorderLayout.CENTER);
        p.add(px);

        JPanel py = new JPanel(new BorderLayout());
        py.add(new JLabel("Rotation Y:"), BorderLayout.WEST);
        SpinnerNumberModel modelY =
            new SpinnerNumberModel(0.0, -180.0, 180.0, 0.1);
        rotationDegYSpinner = new JSpinner(modelY);
        JSpinners.setSpinnerDraggingEnabled(rotationDegYSpinner, true);
        py.add(rotationDegYSpinner, BorderLayout.CENTER);
        p.add(py);

        JPanel pz = new JPanel(new BorderLayout());
        pz.add(new JLabel("Rotation Z:"), BorderLayout.WEST);
        SpinnerNumberModel modelZ =
            new SpinnerNumberModel(0.0, -180.0, 180.0, 0.1);
        rotationDegZSpinner = new JSpinner(modelZ);
        JSpinners.setSpinnerDraggingEnabled(rotationDegZSpinner, true);
        pz.add(rotationDegZSpinner, BorderLayout.CENTER);
        p.add(pz);

        return p;
    }

    /**
     * Create the panel with the scale controls
     * 
     * @return The panel
     */
    private JPanel createScalePanel()
    {
        JPanel p = new JPanel(new GridLayout(0, 1));

        JPanel px = new JPanel(new BorderLayout());
        px.add(new JLabel("Scale X:"), BorderLayout.WEST);
        SpinnerNumberModel modelX =
            new SpinnerNumberModel(0.0, -1000.0, 1000.0, 0.001);
        scaleXSpinner = new JSpinner(modelX);
        JSpinners.setSpinnerDraggingEnabled(scaleXSpinner, true);
        px.add(scaleXSpinner, BorderLayout.CENTER);
        p.add(px);

        JPanel py = new JPanel(new BorderLayout());
        py.add(new JLabel("Scale Y:"), BorderLayout.WEST);
        SpinnerNumberModel modelY =
            new SpinnerNumberModel(0.0, -1000.0, 1000.0, 0.001);
        scaleYSpinner = new JSpinner(modelY);
        JSpinners.setSpinnerDraggingEnabled(scaleYSpinner, true);
        py.add(scaleYSpinner, BorderLayout.CENTER);
        p.add(py);

        JPanel pz = new JPanel(new BorderLayout());
        pz.add(new JLabel("Scale Z:"), BorderLayout.WEST);
        SpinnerNumberModel modelZ =
            new SpinnerNumberModel(0.0, -1000.0, 1000.0, 0.001);
        scaleZSpinner = new JSpinner(modelZ);
        JSpinners.setSpinnerDraggingEnabled(scaleZSpinner, true);
        pz.add(scaleZSpinner, BorderLayout.CENTER);
        p.add(pz);

        return p;
    }
    
    /**
     * Add the given listener to all relevant UI components
     * 
     * @param e The listener
     */
    void addChangeListener(ChangeListener e)
    {
        rotationDegXSpinner.addChangeListener(e);
        rotationDegYSpinner.addChangeListener(e);
        rotationDegZSpinner.addChangeListener(e);

        translationXSpinner.addChangeListener(e);
        translationYSpinner.addChangeListener(e);
        translationZSpinner.addChangeListener(e);

        scaleXSpinner.addChangeListener(e);
        scaleYSpinner.addChangeListener(e);
        scaleZSpinner.addChangeListener(e);
    }

    /**
     * Returns the value of the given spinner as a float value
     * 
     * @param spinner The spinner
     * @return The value
     */
    private static float getFloat(JSpinner spinner)
    {
        Object value = spinner.getValue();
        Number number = (Number) value;
        float f = number.floatValue();
        return f;
    }

    /**
     * Returns the current rotation angle around x, in radians
     * 
     * @return The angle
     */
    private float getRotationRadX()
    {
        float angleDegX = getFloat(rotationDegXSpinner);
        float angleRadX = (float) Math.toRadians(angleDegX);
        return angleRadX;
    }

    /**
     * Returns the current rotation angle around y, in radians
     * 
     * @return The angle
     */
    private float getRotationRadY()
    {
        float angleDegY = getFloat(rotationDegYSpinner);
        float angleRadY = (float) Math.toRadians(angleDegY);
        return angleRadY;
    }

    /**
     * Returns the current rotation angle around z, in radians
     * 
     * @return The angle
     */
    private float getRotationRadZ()
    {
        float angleDegZ = getFloat(rotationDegZSpinner);
        float angleRadZ = (float) Math.toRadians(angleDegZ);
        return angleRadZ;
    }

    /**
     * Returns the current translation along x
     * 
     * @return The translation
     */
    private float getTranslationX()
    {
        return getFloat(translationXSpinner);
    }

    /**
     * Returns the current translation along y
     * 
     * @return The translation
     */
    private float getTranslationY()
    {
        return getFloat(translationYSpinner);
    }

    /**
     * Returns the current translation along z
     * 
     * @return The translation
     */
    private float getTranslationZ()
    {
        return getFloat(translationZSpinner);
    }

    /**
     * Returns the current scale along x
     * 
     * @return The scale
     */
    private float getScaleX()
    {
        return getFloat(scaleXSpinner);
    }

    /**
     * Returns the current scale along y
     * 
     * @return The scale
     */
    private float getScaleY()
    {
        return getFloat(scaleYSpinner);
    }

    /**
     * Returns the current scale along z
     * 
     * @return The scale
     */
    private float getScaleZ()
    {
        return getFloat(scaleZSpinner);
    }
    
    /**
     * Returns the current transform
     * 
     * @return The transform
     */
    Transform getTransform()
    {
        Transform t = new Transform();
        
        t.translationX = getTranslationX();
        t.translationY = getTranslationY();
        t.translationZ = getTranslationZ();
        
        t.rotationRadX = getRotationRadX();
        t.rotationRadY = getRotationRadY();
        t.rotationRadZ = getRotationRadZ();
        
        t.scaleX = getScaleX();
        t.scaleY = getScaleY();
        t.scaleZ = getScaleZ();
        return t;
    }
    
    /**
     * Set the current transform
     * @param transform The transform
     */
    void setTransform(Transform transform)
    {
        translationXSpinner.setValue(transform.translationX);
        translationYSpinner.setValue(transform.translationY);
        translationZSpinner.setValue(transform.translationZ);
        
        rotationDegXSpinner.setValue(Math.toDegrees(transform.rotationRadX));
        rotationDegYSpinner.setValue(Math.toDegrees(transform.rotationRadY));
        rotationDegZSpinner.setValue(Math.toDegrees(transform.rotationRadZ));

        scaleXSpinner.setValue(transform.scaleX);
        scaleYSpinner.setValue(transform.scaleY);
        scaleZSpinner.setValue(transform.scaleZ);
    }
}
