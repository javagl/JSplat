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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Internal utility methods for URI handling
 */
public class UriUtils
{
    /**
     * Returns whether the given URI denotes a file in the local file system.
     *
     * @param uri The URI
     * @return Whether the URI is a local file
     */
	public static boolean isLocalFile(URI uri)
    {
        String host = uri.getHost();
        if (host != null && !host.isEmpty())
        {
            return false;
        }
        try
        {
            URL url = uri.toURL();
            String protocol = url.getProtocol();
            return "file".equalsIgnoreCase(protocol);
        }
        catch (MalformedURLException e)
        {
            return false;
        }
    }

    /**
     * Returns the parent of the given URI. For files, this will be the
     * directory. For directories, it will be the parent directory.
     *
     * @param uri The URI
     * @return The parent URI
     */
	public static URI getParent(URI uri)
    {
        if (uri.getPath().endsWith("/"))
        {
            return uri.resolve("..");
        }
        return uri.resolve(".");
    }

    /**
     * Replaces the extension of the given file with the given new extension.
     * The extension of the file is the part up to (but excluding) the dot "."
     * (or the full file name if it does not contain a dot).
     *
     * @param fileName The file name
     * @param newExtensionWithDot The new extension, including the dot.
     * @return The new file name
     */
    private static String changeFileExtension(String fileName,
        String newExtensionWithDot)
    {
        String baseName = fileName;
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0)
        {
            baseName = fileName.substring(0, dotIndex);
        }
        return baseName + newExtensionWithDot;
    }

    /**
     * Returns a file object that is created by replacing the extension
     * of the file that is given via the URI with the new extension.
     *
     * If the given URI does not denote a file in the local file system,
     * then <code>null</code> is returned.
     *
     * @param uri The URI
     * @param newExtensionWithDot The new extension, including the dot
     * @return The derived file
     */
    public static File deriveFile(URI uri, String newExtensionWithDot)
    {
        if (!isLocalFile(uri))
        {
            return null;
        }
        Path path = Paths.get(uri);
        Path parent = path.getParent();
        Path fileName = path.getFileName();
        String resultFileName = UriUtils.changeFileExtension(
            fileName.toString(), newExtensionWithDot);
        Path resultPath = parent.resolve(resultFileName);
        return resultPath.toFile();
    }

    /**
     * Private constructor to prevent instantiation
     */
    private UriUtils()
    {
        // Private constructor to prevent instantiation
    }
}
