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
package de.javagl.jsplat.io.sog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Utility methods related to JSON data
 */
class JsonUtils
{
    /**
     * Read a value with the specified type from the JSON from the given input
     * stream
     * 
     * @param <T> The value type
     * @param inputStream The input stream
     * @param type The type
     * @return The value
     * @throws IOException If an IO error occurs
     */
    static <T> T readValue(InputStream inputStream, Class<T> type)
        throws IOException
    {
        ObjectMapper om = JsonUtils.createObjectMapper();
        T result = om.readValue(inputStream, type);
        return result;
    }

    /**
     * Write the given value as JSON to the given output stream
     * 
     * @param value The value
     * @param outputStream The output stream
     * @throws IOException If an IO error occurs
     */
    static void writeValue(Object value, OutputStream outputStream)
        throws IOException
    {
        ObjectMapper om = JsonUtils.createObjectMapper();
        om.writeValue(outputStream, value);
    }

    /**
     * Create a default Jackson object mapper for this class
     * 
     * @return The object mapper
     */
    private static ObjectMapper createObjectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        return objectMapper;
    }

    /**
     * Private constructor to prevent instantiation
     */
    private JsonUtils()
    {
        // Private constructor to prevent instantiation
    }

}