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

import de.javagl.jsplat.app.common.ExtensionBasedSaveOptions;
import de.javagl.jsplat.io.ply.PlySplatWriter.PlyFormat;

/**
 * A panel that serves as an accessory for the save file chooser, to select the
 * {@link PlyFormat} that should be used for saving a PLY file
 */
class PlySaveOptions extends ExtensionBasedSaveOptions
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 370867883086984812L;

    /**
     * The binary LE button
     */
    private JRadioButton binaryLeButton;

    /**
     * The binary BE button
     */
    private JRadioButton binaryBeButton;

    /**
     * The ASCII button
     */
    private JRadioButton asciiButton;

    /**
     * Creates a new instance
     * 
     * @param fileChooser The file chooser
     */
    PlySaveOptions(JFileChooser fileChooser)
    {
        super(fileChooser, "PLY save options", ".ply");

        binaryLeButton = new JRadioButton("Binary (Little Endian)");
        binaryLeButton.setSelected(true);
        binaryBeButton = new JRadioButton("Binary (Big Endian)");
        asciiButton = new JRadioButton("ASCII");

        ButtonGroup group = new ButtonGroup();
        group.add(binaryLeButton);
        group.add(binaryBeButton);
        group.add(asciiButton);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(binaryLeButton);
        panel.add(binaryBeButton);
        panel.add(asciiButton);
        add(panel, BorderLayout.NORTH);
    }

    /**
     * Returns the {@link PlyFormat} that is selected
     * 
     * @return The {@link PlyFormat}
     */
    PlyFormat getPlyFormat()
    {
        if (binaryLeButton.isSelected())
        {
            return PlyFormat.BINARY_LITTLE_ENDIAN;
        }
        if (binaryBeButton.isSelected())
        {
            return PlyFormat.BINARY_BIG_ENDIAN;
        }
        if (asciiButton.isSelected())
        {
            return PlyFormat.ASCII;
        }
        return PlyFormat.BINARY_LITTLE_ENDIAN;
    }

}
