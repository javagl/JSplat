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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.javagl.jsplat.Splat;
import de.javagl.jsplat.SplatListReader;
import de.javagl.jsplat.SplatListWriter;
import de.javagl.jsplat.app.common.UriLoading;
import de.javagl.jsplat.app.common.UriTransferHandler;
import de.javagl.jsplat.app.common.UriUtils;
import de.javagl.jsplat.io.gsplat.GsplatSplatReader;
import de.javagl.jsplat.io.gsplat.GsplatSplatWriter;
import de.javagl.jsplat.io.ply.PlySplatReader;
import de.javagl.jsplat.io.ply.PlySplatWriter;
import de.javagl.jsplat.io.ply.PlySplatWriter.PlyFormat;
import de.javagl.jsplat.io.spz.SpzSplatReader;
import de.javagl.jsplat.io.spz.SpzSplatWriter;
import de.javagl.jsplat.io.spz.gltf.SpzGltfSplatWriter;
import de.javagl.swing.tasks.SwingTask;
import de.javagl.swing.tasks.SwingTaskExecutors;

/**
 * The main JSplat application class
 */
class JSplatApplication
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(JSplatApplication.class.getName());

    /**
     * The Action for opening a file
     *
     * @see #openFile()
     */
    private final Action openFileAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -5125243029591873126L;

        // Initialization
        {
            putValue(NAME, "Open file...");
            putValue(SHORT_DESCRIPTION, "Open a file");
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_F));
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            openFile();
        }
    };

    /**
     * The Action for saving a file
     *
     * @see #saveFile()
     */
    private final Action saveFileAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -5125243029591873126L;

        // Initialization
        {
            putValue(NAME, "Save as...");
            putValue(SHORT_DESCRIPTION, "Save splat data as a file");
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_S));
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            saveFile();
        }
    };

    /**
     * The Action for exiting the application
     *
     * @see #exitApplication()
     */
    private final Action exitAction = new AbstractAction()
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -51252430991873126L;

        // Initialization
        {
            putValue(NAME, "Exit");
            putValue(SHORT_DESCRIPTION, "Exit the application");
            putValue(MNEMONIC_KEY, Integer.valueOf(KeyEvent.VK_X));
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            exitApplication();
        }

    };

    /**
     * The main frame of the application
     */
    private final JFrame frame;

    /**
     * The menu bar of the application
     */
    private final JMenuBar menuBar;

    /**
     * The FileChooser for opening files
     */
    private final JFileChooser openFileChooser;

    /**
     * The FileChooser for saving files
     */
    private final JFileChooser saveFileChooser;

    /**
     * The {@link PlySaveOptions} used as an accessory for the save file chooser
     */
    private PlySaveOptions plySaveOptions;

    /**
     * The {@link GlbSaveOptions} used as an accessory for the save file chooser
     */
    private GlbSaveOptions glbSaveOptions;

    /**
     * The {@link JSplatApplicationPanel}
     */
    private JSplatApplicationPanel applicationPanel;

    /**
     * The splats that are currently displayed in the application panel
     */
    private List<? extends Splat> currentSplats;

    /**
     * Default constructor
     */
    JSplatApplication()
    {
        frame = new JFrame("JSplatApplication");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        UriTransferHandler transferHandler =
            new UriTransferHandler(uri -> openUriInBackground(uri));
        frame.setTransferHandler(transferHandler);

        menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        frame.setJMenuBar(menuBar);

        openFileChooser = new JFileChooser(".");
        openFileChooser.setFileFilter(new FileNameExtensionFilter(
            "Splat Files (.splat, .ply, .spz, .glb)", "splat", "ply", "spz",
            "glb"));

        saveFileChooser = new JFileChooser(".");

        JPanel accessory = new JPanel(new GridLayout(0, 1));
        plySaveOptions = new PlySaveOptions(saveFileChooser);
        glbSaveOptions = new GlbSaveOptions(saveFileChooser);
        accessory.add(plySaveOptions);
        accessory.add(glbSaveOptions);
        saveFileChooser.setAccessory(accessory);

        saveFileChooser.setFileFilter(new FileNameExtensionFilter(
            "Splat Files (.splat, .ply, .spz, .glb)", "splat", "ply", "spz",
            "glb"));
        saveFileAction.setEnabled(false);

        applicationPanel = new JSplatApplicationPanel();
        frame.getContentPane().add(applicationPanel);
    }

    /**
     * Returns the {@link JSplatApplicationPanel}
     * 
     * @return The application panel
     */
    JSplatApplicationPanel getApplicationPanel()
    {
        return applicationPanel;
    }

    /**
     * Returns the main frame of this application
     *
     * @return The main frame
     */
    JFrame getFrame()
    {
        return frame;
    }

    /**
     * Returns the path of the 'open' file chooser
     *
     * @return The path
     */
    Path getOpenFileChooserPath()
    {
        return openFileChooser.getCurrentDirectory().toPath();
    }

    /**
     * Set the path of the 'open' file chooser
     *
     * @param path The path
     */
    void setOpenFileChooserPath(Path path)
    {
        openFileChooser.setCurrentDirectory(path.toFile());
    }

    /**
     * Returns the path of the 'save' file chooser
     *
     * @return The path
     */
    Path getSaveFileChooserPath()
    {
        return saveFileChooser.getCurrentDirectory().toPath();
    }

    /**
     * Set the path of the 'save' file chooser
     *
     * @param path The path
     */
    void setSaveFileChooserPath(Path path)
    {
        saveFileChooser.setCurrentDirectory(path.toFile());
    }

    /**
     * Create the file menu
     *
     * @return The file menu
     */
    private JMenu createFileMenu()
    {
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(new JMenuItem(openFileAction));
        fileMenu.add(new JMenuItem(saveFileAction));
        fileMenu.add(new JSeparator());

        fileMenu.add(new JSeparator());

        fileMenu.add(new JMenuItem(exitAction));
        return fileMenu;
    }

    /**
     * Open the file chooser to select a file which will be loaded upon
     * confirmation
     */
    private void openFile()
    {
        int returnState = openFileChooser.showOpenDialog(frame);
        if (returnState == JFileChooser.APPROVE_OPTION)
        {
            File file = openFileChooser.getSelectedFile();
            openUriInBackground(file.toURI());
        }
    }

    /**
     * Execute the task of loading the data in a background thread, showing a
     * modal dialog.
     *
     * @param uri The URI to load from
     */
    void openUriInBackground(URI uri)
    {
        logger.info("Loading " + uri);

        if (UriUtils.isLocalFile(uri))
        {
            URI directory = UriUtils.getParent(uri);
            File directoryFile = Paths.get(directory).toFile();
            openFileChooser.setCurrentDirectory(directoryFile);
        }

        SplatListReader reader = findReader(uri.toString());
        if (reader == null)
        {
            return;
        }
        Function<InputStream, List<? extends Splat>> loader = (inputStream) ->
        {
            try
            {
                return reader.readList(inputStream);
            }
            catch (IOException e)
            {
                throw new UncheckedIOException(e);
            }
        };
        UriLoading.loadInBackground(uri, loader, (resultUri, resultSplats) ->
        {
            currentSplats = resultSplats;
            if (currentSplats != null)
            {
                saveFileAction.setEnabled(true);
                applicationPanel.setSplats(currentSplats);
            }
            else
            {
                saveFileAction.setEnabled(false);
            }
        });
    }

    /**
     * Find a reader for the file with the given name, based on the file
     * extension, case-insensitively. If no reader can be found, then
     * <code>null</code> is returned.
     * 
     * @param fileName The file name
     * @return The reader
     */
    private static SplatListReader findReader(String fileName)
    {
        String name = fileName.toLowerCase();
        if (name.endsWith("splat"))
        {
            return new GsplatSplatReader();
        }
        if (name.endsWith("ply"))
        {
            return new PlySplatReader();
        }
        if (name.endsWith("spz"))
        {
            return new SpzSplatReader();
        }
        if (name.endsWith("glb"))
        {
            return new GlbSplatListReader();
        }
        logger.warning(
            "Could not determine type from file name for '" + fileName + "'");
        return null;
    }

    /**
     * Save the data to a user-selected file
     */
    private void saveFile()
    {
        int returnState = saveFileChooser.showSaveDialog(frame);
        if (returnState == JFileChooser.APPROVE_OPTION)
        {
            File file = saveFileChooser.getSelectedFile();
            if (file.exists())
            {
                int overwriteReturnState = JOptionPane.showConfirmDialog(null,
                    "File exists. Overwrite?", "Confirmation",
                    JOptionPane.YES_NO_OPTION);
                if (overwriteReturnState != JOptionPane.YES_OPTION)
                {
                    return;
                }
            }
            saveFile(file);
        }
    }

    /**
     * Save the data to the given file
     *
     * @param file The file
     */
    private void saveFile(File file)
    {
        SplatListWriter writer = findWriter(file.toString());
        if (writer == null)
        {
            String message =
                "Could not determine type from file name for '" + file + "'";
            JOptionPane.showMessageDialog(null, message, "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        saveInBackground(writer, file);
    }

    /**
     * Find a writer for the file with the given name, based on the file
     * extension, case-insensitively. If no writer can be found, then
     * <code>null</code> is returned.
     * 
     * @param fileName The file name
     * @return The writer
     */
    private SplatListWriter findWriter(String fileName)
    {
        String name = fileName.toLowerCase();
        if (name.endsWith("splat"))
        {
            return new GsplatSplatWriter();
        }
        if (name.endsWith("ply"))
        {
            PlyFormat plyFormat = plySaveOptions.getPlyFormat();
            return new PlySplatWriter(plyFormat);
        }
        if (name.endsWith("spz"))
        {
            return new SpzSplatWriter();
        }
        if (name.endsWith("glb"))
        {
            if (glbSaveOptions.shouldApplySpzCompression())
            {
                return new SpzGltfSplatWriter();
            }
            return new SpzGltfSplatWriter();
        }
        logger.warning(
            "Could not determine type from file name for '" + fileName + "'");
        return null;
    }

    /**
     * Save the data to the given file, in a background thread
     *
     * @param w The writer
     * @param file The file
     */
    private void saveInBackground(SplatListWriter w, File file)
    {
        class Task extends SwingTask<Void, Void>
        {
            @Override
            public Void doInBackground()
            {
                setProgress(-1.0);
                saveUnchecked(w, file);
                return null;
            }
        }
        Task task = new Task();
        SwingTaskExecutors.create(task).setDialogUncaughtExceptionHandler()
            .setTitle("Saving...").build().execute();

    }

    /**
     * Save the current splats to the specified file, using the given writer
     * 
     * @param w The writer
     * @param file The file
     */
    private void saveUnchecked(SplatListWriter w, File file)
    {
        try
        {
            save(w, file);
        }
        catch (IOException e)
        {
            logger.severe(e.getMessage());
        }
    }

    /**
     * Save the current splats to the specified file, using the given writer
     * 
     * @param w The writer
     * @param file The file
     * @throws IOException If an IO error occurs
     */
    private void save(SplatListWriter w, File file) throws IOException
    {
        OutputStream outputStream = new FileOutputStream(file);
        w.writeList(currentSplats, outputStream);
        outputStream.close();
    }

    /**
     * Exit the application
     */
    private void exitApplication()
    {
        frame.setVisible(false);
        frame.dispose();
    }

}
