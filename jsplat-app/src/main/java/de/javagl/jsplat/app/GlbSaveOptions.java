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

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * A panel that serves as an accessory for the save file chooser, to select the
 * compression that should be applied in GLB files
 */
class GlbSaveOptions extends ExtensionBasedSaveOptions
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 37867883086984812L;

    /**
     * The button for no compression
     */
    private JRadioButton noneButton;

    /**
     * The button for SPZ compression
     */
    private JRadioButton spzButton;

    /**
     * Creates a new instance
     * 
     * @param fileChooser The file chooser
     */
    GlbSaveOptions(JFileChooser fileChooser)
    {
        super(fileChooser, "GLB save options", ".glb");

        noneButton = new JRadioButton("No compression");
        noneButton.setSelected(true);
        spzButton = new JRadioButton("SPZ compression");

        ButtonGroup group = new ButtonGroup();
        group.add(noneButton);
        group.add(spzButton);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(noneButton);
        panel.add(spzButton);
        add(panel, BorderLayout.NORTH);
    }

    /**
     * Returns whether SPZ compression should be applied
     * 
     * @return The state
     */
    boolean shouldApplySpzCompression()
    {
        return spzButton.isSelected();
    }

}
