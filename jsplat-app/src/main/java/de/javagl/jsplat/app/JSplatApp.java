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

import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.javagl.common.ui.properties.PropertiesHandles;
import de.javagl.common.ui.properties.PropertiesManager;
import de.javagl.jsplat.app.common.LoggerUtil;

/**
 * The class containing the entry point for the JSplat application
 */
public class JSplatApp
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(JSplatApp.class.getName());

    /**
     * The name of the file that stores application properties
     */
    private static final String PROPERTIES_FILE_NAME =
        "de.javagl.jsplat.app.JSplatApp.properties";

    /**
     * The entry point of this application
     *
     * @param args The command line arguments
     */
    public static void main(String[] args)
    {
        LoggerUtil.initLogging();
        System.setProperty("sun.awt.noerasebackground", "true");
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        boolean usePlatformLookAndFeel = false;
        if (usePlatformLookAndFeel)
        {
            setPlatformLookAndFeel();
        }
        SwingUtilities.invokeLater(() -> createAndShowGui());
    }

    /**
     * Create and show the GUI, to be called on the event dispatch thread
     */
    private static void createAndShowGui()
    {
        PropertiesManager pm =
            new PropertiesManager(PropertiesHandles.get(PROPERTIES_FILE_NAME));

        JSplatApplication application = new JSplatApplication();
        JFrame frame = application.getFrame();

        pm.saveOnClose(frame);

        pm.registerRectangle("frame.bounds", frame::getBounds,
            frame::setBounds);
        pm.registerPath("openFileChooserPath",
            application::getOpenFileChooserPath,
            application::setOpenFileChooserPath);
        pm.registerPath("saveFileChooserPath",
            application::getSaveFileChooserPath,
            application::setSaveFileChooserPath);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        pm.restore();

        frame.setVisible(true);

    }

    /**
     * Try to set the default platform look and feel
     */
    private static void setPlatformLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e)
        {
            logger.warning(e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            logger.warning(e.getMessage());
        }
        catch (InstantiationException e)
        {
            logger.warning(e.getMessage());
        }
        catch (IllegalAccessException e)
        {
            logger.warning(e.getMessage());
        }
    }

    /**
     * Private constructor to prevent instantiation
     */
    private JSplatApp()
    {
        // Private constructor to prevent instantiation
    }

}
