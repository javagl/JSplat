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
import java.awt.FlowLayout;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.javagl.jsplat.Splat;
import de.javagl.jsplat.viewer.SplatViewer;
import de.javagl.jsplat.viewer.SplatViewers;

/**
 * The main panel for the JSplat application
 */
class JSplatApplicationPanel extends JPanel
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(JSplatApplicationPanel.class.getName());

    /**
     * Serial UID
     */
    private static final long serialVersionUID = 18913879699207660L;

    /**
     * The splat viewer (or <code>null</code> if it cannot be initialized)
     */
    private final SplatViewer splatViewer;

    /**
     * Whether the camera should be fit to a loaded data set.
     */
    private boolean doFit = true;
    
    /**
     * A label for status messages
     */
    private JLabel statusLabel;

    /**
     * Default constructor
     */
    JSplatApplicationPanel()
    {
        super(new BorderLayout());
        this.splatViewer = SplatViewers.createDefault();
        if (this.splatViewer == null)
        {
            add(new JLabel("Could not create SplatViewer instance"));
        }
        else
        {
            add(splatViewer.getRenderComponent(), BorderLayout.CENTER);
        }

        JPanel controlPanel = new JPanel(new FlowLayout());

        JButton resetButton = new JButton("Reset camera");
        resetButton.addActionListener(e ->
        {
            if (splatViewer != null)
            {
                splatViewer.resetCamera();
            }
            doFit = true;
        });
        controlPanel.add(resetButton);

        JButton fitButton = new JButton("Fit camera");
        fitButton.addActionListener(e ->
        {
            if (splatViewer != null)
            {
                splatViewer.fitCamera();
            }
        });
        controlPanel.add(fitButton);

        controlPanel.add(createButton("unitCube", UnitCubeSplats::create));
        /*
         * controlPanel .add(createButton("rotations",
         * SplatGrids::createRotations)); controlPanel
         * .add(createButton("rotations2D", SplatGrids::createRotations2D));
         * controlPanel.add(createButton("shs1", SplatGrids::createShs1));
         * controlPanel.add(createButton("shs2", SplatGrids::createShs2));
         * controlPanel.add(createButton("shs3", SplatGrids::createShs3));
         */

        add(controlPanel, BorderLayout.NORTH);

        statusLabel = new JLabel(" ");
        add(statusLabel, BorderLayout.SOUTH);
        
        setSplats(null);
    }

    /**
     * Create a button with the given text to set the splats provided by the
     * given supplier into the viewer
     * 
     * @param text The text
     * @param supplier The supplier
     * @return The button
     */
    private JButton createButton(String text,
        Supplier<? extends List<? extends Splat>> supplier)
    {
        JButton button = new JButton(text);
        button.addActionListener(e ->
        {
            setSplats(supplier.get());
        });
        return button;
    }

    /**
     * Set the splats that should be displayed
     * 
     * @param splats The splats
     */
    void setSplats(List<? extends Splat> splats)
    {
        if (splatViewer == null)
        {
            logger.warning("No SplatViewer was craeted");
            return;
        }
        splatViewer.setSplats(splats);
        if (splats != null && doFit)
        {
            splatViewer.fitCamera();
            doFit = false;
        }
        
        if (splats == null || splats.size() == 0)
        {
            statusLabel.setText("No splats");
        }
        else 
        {
            int count = splats.size();
            int degree = splats.get(0).getShDegree();
            statusLabel.setText(count + " splats with degree " + degree);
        }
    }

}
