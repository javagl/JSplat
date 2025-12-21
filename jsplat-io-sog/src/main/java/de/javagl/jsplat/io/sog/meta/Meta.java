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
package de.javagl.jsplat.io.sog.meta;

/**
 * SOG metadata
 */
public class Meta
{
    /**
     * File format version (integer)
     */
    public int version;

    /**
     * Number of gaussians (<= W*H of the images)
     */
    public int count;

    /**
     * True iff scene was trained with anti-aliasing
     */
    public boolean antialias;
    
    /**
     * The asset information
     */
    public Asset asset;

    /**
     * Ranges for decoding *log-transformed* positions (see §3.1).
     */
    public Means means;

    /**
     * The scales
     */
    public Scales scales;

    /**
     * The quats
     */
    public Quats quats;

    /**
     * The spherical harmonics
     */
    public Sh0 sh0;

    /**
     * Present only if higher-order SH exist:
     */
    public ShN shN;
}