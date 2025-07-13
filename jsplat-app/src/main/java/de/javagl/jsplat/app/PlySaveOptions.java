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
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import de.javagl.common.ui.GuiUtils;
import de.javagl.jsplat.io.ply.PlySplatWriter.PlyFormat;

/**
 * A panel that serves as an accessory for the save file chooser, to select the
 * {@link PlyFormat} that should be used for saving a PLY file
 */
class PlySaveOptions extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 3708678183086984812L;

    /**
     * The file chooser
     */
    private JFileChooser fileChooser;

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
     * The file name text field, obtained from the file chooser (quirky)
     */
    private JTextField fileNameTextField;

    /**
     * Creates a new instance
     * 
     * @param fileChooser The file chooser
     */
    PlySaveOptions(JFileChooser fileChooser)
    {
        super(new BorderLayout());
        this.fileChooser = fileChooser;

        setBorder(BorderFactory.createTitledBorder("PLY save options"));

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

        fileChooser.addPropertyChangeListener(e -> updateOptions());

        fileNameTextField = findFileNameTextField(fileChooser);
        installDocumentListener();

        updateOptions();
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

    /**
     * Install a document listener to the file name text field to update the
     * options when the file name changes
     */
    private void installDocumentListener()
    {
        if (fileNameTextField == null)
        {
            return;
        }
        Document document = fileNameTextField.getDocument();
        document.addDocumentListener(new DocumentListener()
        {
            @Override
            public void removeUpdate(DocumentEvent e)
            {
                updateOptions();
            }

            @Override
            public void insertUpdate(DocumentEvent e)
            {
                updateOptions();
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
                updateOptions();
            }
        });
    }

    /**
     * Returns the current file name from the file name text field
     * 
     * @return The file name
     */
    private String getCurrentFileName()
    {
        if (fileNameTextField != null)
        {
            return fileNameTextField.getText();
        }
        File file = fileChooser.getSelectedFile();
        if (file != null)
        {
            return file.toString();
        }
        return null;
    }

    /**
     * Update the options based on the current file name
     */
    private void updateOptions()
    {
        String fileName = getCurrentFileName();
        if (fileName == null)
        {
            GuiUtils.setDeepEnabled(this, false);
            return;
        }
        boolean isPly = fileName.toLowerCase().endsWith(".ply");
        GuiUtils.setDeepEnabled(this, isPly);
    }

    /**
     * Find the first text field in the given container, returning
     * <code>null</code> if none is found
     * 
     * @param container The container
     * @return The text field
     */
    private static JTextField findFileNameTextField(Container container)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JTextField)
            {
                return (JTextField) component;
            }
            else if (component instanceof Container)
            {
                Container childContainer = (Container) component;
                JTextField textField = findFileNameTextField(childContainer);
                if (textField != null)
                {
                    return textField;
                }
            }
        }
        return null;
    }

}
