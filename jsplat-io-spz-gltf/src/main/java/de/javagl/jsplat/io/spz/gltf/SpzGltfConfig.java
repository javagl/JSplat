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
package de.javagl.jsplat.io.spz.gltf;

/**
 * Internal class with flags, attempting to track the development of 
 * https://github.com/KhronosGroup/glTF/pull/2490 and certain quirks and
 * assumptions that are made by CesiumJS.
 */
class SpzGltfConfig
{
    /**
     * A flag to insert certain up-axis conversion matrices in the
     * glTF, to handle certain expectations that are made by CesiumJS.
     */
    final boolean APPLY_UP_AXIS_TRANSFORMS = true;

    /**
     * Whether the glTF should be created with the KHR_gaussian_splatting
     * base extension.
     * 
     * This is currently <code>false</code> by default, because there is no
     * support for the base extension in CesiumJS (as of 2025-08-23).
     */
    boolean USE_BASE_EXTENSION;

    /**
     * The string that is used as a prefix for the glTF attribute names.
     * 
     * This is used for disambiguating the attribute names, as part of
     * https://github.com/KhronosGroup/glTF/issues/2111
     */
    String ATTRIBUTE_PREFIX;
    
    /**
     * The name of the SPZ extension.
     * 
     * This will be <code>KHR_spz_gaussian_splats_compression</code> by default,
     * and <code>KHR_gaussian_splatting_compression_spz</code> when
     * {@link #USE_BASE_EXTENSION} is <code>true</code>.
     */
    String SPZ_EXTENSION_NAME;    
    
    /**
     * Default constructor
     * 
     * @param useBaseExtension Whether the base extension should be used
     */
    SpzGltfConfig(boolean useBaseExtension)
    {
        this.USE_BASE_EXTENSION = useBaseExtension;
        if (USE_BASE_EXTENSION)
        {
            ATTRIBUTE_PREFIX = "KHR_gaussian_splatting:";
            SPZ_EXTENSION_NAME = "KHR_gaussian_splatting_compression_spz";
        }
        else
        {
            ATTRIBUTE_PREFIX = "_";
            SPZ_EXTENSION_NAME = "KHR_spz_gaussian_splats_compression";
        }
    }
    
}
