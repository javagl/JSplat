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
package de.javagl.jsplat.app.common;

import java.awt.Dimension;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.javagl.swing.tasks.SwingTask;
import de.javagl.swing.tasks.SwingTaskExecutors;

/**
 * Internal methods for loading data from a URI in a background thread
 */
public class UriLoading
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(UriLoading.class.getName());

    /**
     * Load the data from the given URI in a background thread, and pass the
     * result to the given consumer (on the event dispatch thread)
     * 
     * @param <T> The type of the result
     * 
     * @param uri The URI
     * @param loader The loader
     * @param consumer The result consumer
     */
    public static <T> void loadInBackground(URI uri,
        Function<? super InputStream, ? extends T> loader,
        BiConsumer<? super URI, ? super T> consumer)
    {
        logger.fine("Loading " + uri);

        SwingTask<T, Void> swingTask = new SwingTask<T, Void>()
        {
            @Override
            protected T doInBackground() throws Exception
            {
                setProgress(-1.0);
                try (InputStream inputStream = uri.toURL().openStream())
                {
                    return loader.apply(inputStream);
                }
            }
        };
        swingTask.addDoneCallback(finishedTask ->
        {
            try
            {
                T result = finishedTask.get();
                consumer.accept(uri, result);
            }
            catch (CancellationException e)
            {
                logger.info(
                    "Cancelled loading " + uri + " (" + e.getMessage() + ")");
                return;
            }
            catch (InterruptedException e)
            {
                logger.info("Interrupted while loading " + uri + " ("
                    + e.getMessage() + ")");
                Thread.currentThread().interrupt();
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();

                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));

                JTextArea textArea = new JTextArea();
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                textArea.setEditable(false);
                textArea.append(
                    "Loading error: " + e.getMessage() + "\n" + sw.toString());
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(800, 600));

                JOptionPane.showMessageDialog(null, scrollPane,
                    "Undhandled Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        });
        SwingTaskExecutors.create(swingTask).setTitle("Loading")
            .setMillisToPopup(10).setCancelable(true).build().execute();

    }

    /**
     * Load the data from the given URI in a background thread, and pass the
     * result to the given consumer (on the event dispatch thread)
     *
     * @param uri The URI
     * @param consumer The result consumer
     */
    public static void loadInBackground(URI uri,
        BiConsumer<? super URI, ? super ByteBuffer> consumer)
    {
        loadInBackground(uri, UriLoading::readAsByteBufferUnchecked, consumer);
    }

    /**
     * Read the given stream into a byte buffer
     * 
     * @param inputStream The input stream
     * @return The byte buffer
     */
    private static ByteBuffer readAsByteBufferUnchecked(InputStream inputStream)
    {
        try
        {
            return readAsByteBuffer(inputStream);
        }
        catch (IOException e)
        {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Read the given stream into a byte buffer
     * 
     * @param inputStream The input stream
     * @return The byte buffer
     * @throws IOException If an IO error occurs
     */
    private static ByteBuffer readAsByteBuffer(InputStream inputStream)
        throws IOException
    {
        byte[] data = readStream(inputStream);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return buffer;
    }

    /**
     * Reads the data from the given inputStream and returns it as a byte array.
     * The caller is responsible for closing the stream.
     *
     * @param inputStream The input stream to read
     * @return The data from the inputStream
     * @throws IOException If an IO error occurs, or if the thread that executes
     *         this method is interrupted.
     */
    public static byte[] readStream(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[16384];
        while (true)
        {
            int read = inputStream.read(buffer);
            if (read == -1)
            {
                break;
            }
            baos.write(buffer, 0, read);
            if (Thread.currentThread().isInterrupted())
            {
                throw new IOException("Interrupted while reading stream",
                    new InterruptedException());
            }
        }
        baos.flush();
        return baos.toByteArray();
    }

    /**
     * Private constructor to prevent instantiation
     */
    private UriLoading()
    {
        // Private constructor to prevent instantiation
    }

}