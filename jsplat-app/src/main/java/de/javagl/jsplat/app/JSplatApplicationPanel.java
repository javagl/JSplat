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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import de.javagl.common.ui.GuiUtils;
import de.javagl.common.ui.JSpinners;
import de.javagl.common.ui.JSplitPanes;
import de.javagl.common.ui.panel.collapsible.AccordionPanel;
import de.javagl.jsplat.MutableSplat;
import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;
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
    private boolean doFit;
    
    /**
     * A label for status messages
     */
    private JLabel statusLabel;

    /**
     * The spinner for the FOV
     */
    private JSpinner fovDegYSpinner;
    
    /**
     * The panel containing the list of data sets
     */
    private DataSetsPanel dataSetsPanel;
    
    /**
     * The panel for controlling the transforms
     */
    private TransformPanel transformPanel;

    /**
     * Default constructor
     */
    JSplatApplicationPanel()
    {
        super(new BorderLayout());
        
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        add(mainSplitPane, BorderLayout.CENTER);
        
        this.splatViewer = SplatViewers.createDefault();
        if (this.splatViewer == null)
        {
            mainSplitPane.setRightComponent(
                new JLabel("Could not create SplatViewer instance"));
        }
        else
        {
            JPanel container = new JPanel(new GridLayout(1,1));
            Component renderComponent = splatViewer.getRenderComponent();
            renderComponent.setMinimumSize(new Dimension(10, 10));
            container.add(renderComponent);
            mainSplitPane.setRightComponent(container);
        }

        JPanel controlPanel = createControlPanel();
        mainSplitPane.setLeftComponent(controlPanel);
        JSplitPanes.setDividerLocation(mainSplitPane, 0.3);

        statusLabel = new JLabel(" ");
        add(statusLabel, BorderLayout.SOUTH);
        
        doFit = true;
    }
    
    /**
     * Create the control panel
     * 
     * @return The panel
     */
    private JPanel createControlPanel()
    {
        JPanel p = new JPanel(new BorderLayout());
        
        AccordionPanel accordionPanel = new AccordionPanel();
        
        accordionPanel.addToAccordion("Camera", createCameraPanel());
        accordionPanel.addToAccordion("Dummy data sets",
            createDummyDataSetsPanel());
        accordionPanel.addToAccordion("Data sets", createDataSetsPanel());
        accordionPanel.addToAccordion("Transform", createTransformPanel());

        p.add(accordionPanel, BorderLayout.CENTER);
        return p;
    }
    
    /**
     * Create the camera panel
     * 
     * @return The panel
     */
    private JPanel createCameraPanel()
    {
        JPanel p = new JPanel(new GridLayout(0, 1));
        
        JButton resetButton = new JButton("Reset camera");
        resetButton.addActionListener(e ->
        {
            float fovDegY = getFovDegY();
            if (splatViewer != null)
            {
                splatViewer.resetCamera();
            }
            fovDegYSpinner.setValue(fovDegY);
            doFit = true;
        });
        p.add(resetButton);

        JButton fitButton = new JButton("Fit camera");
        fitButton.addActionListener(e ->
        {
            if (splatViewer != null)
            {
                splatViewer.fitCamera();
            }
        });
        p.add(fitButton);
        p.add(createFovDegYSpinnerPanel());
        
        return p;
    }

    /**
     * Create a panel with a spinner for controlling the camera FOV
     * 
     * @return The panel
     */
    private JPanel createFovDegYSpinnerPanel()
    {
        JPanel p = new JPanel(new BorderLayout());

        p.add(new JLabel("FOV"), BorderLayout.WEST);
        SpinnerNumberModel model =
            new SpinnerNumberModel(60.0, 5.0, 160.0, 1.0);
        fovDegYSpinner = new JSpinner(model);
        fovDegYSpinner.addChangeListener(e ->
        {
            if (splatViewer == null)
            {
                return;
            }
            splatViewer.setCameraFovDegY(getFovDegY());
        });
        JSpinners.setSpinnerDraggingEnabled(fovDegYSpinner, true);
        p.add(fovDegYSpinner, BorderLayout.CENTER);
        
        return p;
    }
    
    /**
     * Returns the current FOV selected in the spinner
     * 
     * @return The FOV, in degrees
     */
    private float getFovDegY()
    {
        Object value = fovDegYSpinner.getValue();
        Number number = (Number) value;
        float f = number.floatValue();
        return f;
        
    }

    /**
     * Create the dummy data sets panel
     * 
     * @return The panel
     */
    private JPanel createDummyDataSetsPanel()
    {
        JPanel p = new JPanel(new GridLayout(0, 1));
        
        p.add(createButton("unitCube", UnitCubeSplats::create));
        p.add(createButton("unitSh", UnitShSplats::create));
        
        return p;
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
            addSplats(text, supplier.get());
        });
        return button;
    }
    
    /**
     * Create the data sets panel
     * 
     * @return The panel
     */
    private JPanel createDataSetsPanel()
    {
        Consumer<DataSet> removalCallback = (removedDataSet) -> 
        {
            removeDataSet(removedDataSet);
        };
        this.dataSetsPanel = new DataSetsPanel(removalCallback);
        dataSetsPanel.addSelectionListener((e) -> 
        {
            DataSet selectedDataSet = dataSetsPanel.getSelectedDataSet();
            if (selectedDataSet == null)
            {
                GuiUtils.setDeepEnabled(transformPanel, false);
                return;
            }
            GuiUtils.setDeepEnabled(transformPanel, true);
            Transform transform = selectedDataSet.getTransform();
            transformPanel.setTransform(transform);
        });
        return dataSetsPanel;
    }
    
    /**
     * Create the transform panel
     *  
     * @return The panel
     */
    private JPanel createTransformPanel()
    {
        this.transformPanel = new TransformPanel();
        GuiUtils.setDeepEnabled(transformPanel, false);
        transformPanel.setTransform(new Transform());
        ChangeListener listener = e ->
        {
            if (splatViewer == null)
            {
                return;
            }
            DataSet selectedDataSet = dataSetsPanel.getSelectedDataSet();
            if (selectedDataSet == null)
            {
                return;
            }
            Transform transform = transformPanel.getTransform();
            selectedDataSet.setTransform(transform);
            splatViewer.updateSplats();
        };
        transformPanel.addChangeListener(listener);
        return transformPanel;
    }


    /**
     * Add the splats that should be displayed
     * 
     * @param name The name
     * @param splats The splats
     */
    void addSplats(String name, List<? extends Splat> splats)
    {
        if (splatViewer == null)
        {
            logger.warning("No SplatViewer was craeted");
            return;
        }
        
        DataSet dataSet = new DataSet(name, splats);
        dataSetsPanel.addDataSet(dataSet);
        
        List<MutableSplat> currentSplats = dataSet.getCurrentSplats();
        splatViewer.addSplats(currentSplats);
        
        if (doFit)
        {
            splatViewer.fitCamera();
            doFit = false;
        }
        updateStatus();
    }
    
    /**
     * Update the status label
     */
    private void updateStatus()
    {
        List<DataSet> dataSets = dataSetsPanel.getDataSets();
        List<List<? extends Splat>> allSplats =
            new ArrayList<List<? extends Splat>>();
        for (DataSet dataSet : dataSets)
        {
            allSplats.add(dataSet.getCurrentSplats());
        }
        int count = allSplats.size();
        float minMax[] = computeMinMax(allSplats);
        String b = boundsToString(minMax);
        statusLabel.setText(count + " splats, bounds: " + b);
    }

    /**
     * Remove the splats of the given data set from the viewer
     * 
     * @param dataSet The data set
     */
    private void removeDataSet(DataSet dataSet)
    {
        if (splatViewer == null)
        {
            logger.warning("No SplatViewer was craeted");
            return;
        }
        splatViewer.removeSplats(dataSet.getCurrentSplats());
        List<DataSet> dataSets = dataSetsPanel.getDataSets();
        if (dataSets.isEmpty())
        {
            doFit = true;
        }
        updateStatus();
    }

    /**
     * Creates a string representation of the bounds, as computed with
     * {@link #computeMinMax(Iterable)}
     * 
     * @param minMax The bounds
     * @return The string
     */
    private static String boundsToString(float minMax[])
    {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        DecimalFormat df = new DecimalFormat("0.0###", symbols);
        String s = "(" + df.format(minMax[0]) + ", " + df.format(minMax[1])
            + ", " + df.format(minMax[2]) + ")-(" + df.format(minMax[3]) + ", "
            + df.format(minMax[4]) + ", " + df.format(minMax[5]) + ")";
        return s;
    }

    /**
     * Compute the bounding box of the given splats, as a 6-element array with
     * (minX, minY, minZ, maxX, maxY, maxZ)
     * 
     * @param splats The splats
     * @return The bounding box
     */
    private static float[] computeMinMax(
        Iterable<? extends Iterable<? extends Splat>> splats)
    {
        float minMax[] = new float[]
        { Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
            Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY,
            Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY };
        for (Iterable<? extends Splat> i : splats)
        {
            for (Splat s : i)
            {
                minMax[0] = Math.min(minMax[0], s.getPositionX());
                minMax[1] = Math.min(minMax[1], s.getPositionY());
                minMax[2] = Math.min(minMax[2], s.getPositionZ());
                minMax[3] = Math.max(minMax[3], s.getPositionX());
                minMax[4] = Math.max(minMax[4], s.getPositionY());
                minMax[5] = Math.max(minMax[5], s.getPositionZ());
            }
        }
        return minMax;
    }

    /**
     * Returns a list of all current splats.
     * 
     * Note: This may be a bit costly and memory-consuming. It is only 
     * intended for splats that are about to be saved to a file.
     * 
     * @return The list of all splats
     */
    List<? extends Splat> getAllSplats()
    {
        List<DataSet> dataSets = dataSetsPanel.getDataSets();
        if (dataSets.isEmpty())
        {
            return Collections.emptyList();
        }
        if (dataSets.size() == 1)
        {
            DataSet dataSet = dataSets.get(0);
            return dataSet.getCurrentSplats();
        }
        int maxShDegree = -1;
        for (DataSet dataSet : dataSets)
        {
            maxShDegree = Math.max(maxShDegree, dataSet.getShDegree());
        }
        List<Splat> allSplats = new ArrayList<Splat>();
        for (DataSet dataSet : dataSets)
        {
            List<MutableSplat> splats = dataSet.getCurrentSplats();
            for (MutableSplat splat : splats)
            {
                MutableSplat newSplat = Splats.create(maxShDegree);
                Splats.setAny(splat, newSplat);
                allSplats.add(newSplat);
            }
        }
        return allSplats;
    }

}
