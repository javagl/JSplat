/*
 * www.javagl.de - JSplat
 *
 * Copyright 2025 Marco Hutter - http://www.javagl.de
 */
package de.javagl.jsplat.examples;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.javagl.jsplat.viewer.SplatViewer;
import de.javagl.jsplat.viewer.SplatViewers;

/**
 * An example showing how to use a {@link SplatViewer}
 */
public class SplatViewerExample
{
    /**
     * The entry point
     * 
     * @param args Not used
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> createAndShowGui());
    }

    /**
     * Create and show the GUI, to be called on the event dispatch thread
     */
    private static void createAndShowGui()
    {
        // Create the main frame
        JFrame f = new JFrame("SplatViewerExample");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new BorderLayout());

        // Create a splat viewer, and add its rendering component
        // to the main frame
        SplatViewer splatViewer = SplatViewers.createDefault();
        f.getContentPane().add(splatViewer.getRenderComponent(),
            BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel(new FlowLayout());

        // A button to load splat data and assign it to the viewer
        JButton showSplatsbutton = new JButton("Show splats");
        showSplatsbutton.addActionListener(e ->
        {
            splatViewer.setSplats(UnitCubeSplats.create());
            splatViewer.fitCamera();
        });
        buttonsPanel.add(showSplatsbutton);

        // A convenience button to fit the camera to the displayed splats
        JButton fitCameraButton = new JButton("Fit camera");
        fitCameraButton.addActionListener(e ->
        {
            splatViewer.fitCamera();
        });
        buttonsPanel.add(fitCameraButton);
        f.getContentPane().add(buttonsPanel, BorderLayout.NORTH);

        // Show the main frame
        f.setSize(1200, 809);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

}
