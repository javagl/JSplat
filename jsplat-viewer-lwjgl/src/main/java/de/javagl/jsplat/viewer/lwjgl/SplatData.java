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
package de.javagl.jsplat.viewer.lwjgl;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glUnmapBuffer;
import static org.lwjgl.opengl.GL30.GL_MAP_INVALIDATE_BUFFER_BIT;
import static org.lwjgl.opengl.GL30.GL_MAP_WRITE_BIT;
import static org.lwjgl.opengl.GL30.glMapBufferRange;
import static org.lwjgl.opengl.GL42.glMemoryBarrier;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BARRIER_BIT;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;
import static org.lwjgl.opengl.GL45.glCreateBuffers;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import de.javagl.jsplat.Splat;
import de.javagl.jsplat.Splats;
import de.javagl.jsplat.viewer.BufferUtils;

/**
 * A class summarizing the rendering data for splats
 */
class SplatData
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(SplatData.class.getName());

    /**
     * The splats that are currently displayed
     */
    private final CompoundList<Splat> splats = new CompoundList<Splat>();

    /**
     * The SH degree that is required for the currently displayed splats
     */
    private int shDegree = -1;
    
    /**
     * Whether the structure of the splats was modified
     */
    private volatile boolean structureModified = true;

    /**
     * The shader storage buffer object for the 'gaussian_data' of the vertex
     * shader
     */
    private int gaussianDataSsbo = 0;

    /**
     * The size (in bytes) of the gaussianDataSsbo
     */
    private int gaussianDataSsboSize = 0;

    /**
     * The float buffer that will be used for filling the gaussianDataSsbo
     */
    private FloatBuffer gaussianData = null;

    /**
     * The shader storage buffer object for the 'gaussian_order' of the vertex
     * shader
     */
    private int gaussianOrderSsbo = 0;

    /**
     * The size (in bytes) of the gaussianOrderSsbo
     */
    private int gaussianOrderSsboSize = 0;

    /**
     * A buffer for the sorted indices, used for filling the gaussianOrderSsbo.
     */
    private IntBuffer gaussianOrder = null;

    /**
     * The sorter for the splats, which computes the {@link #gaussianOrder}
     * values
     */
    private final SplatSorter splatSorter;

    /**
     * Creates a new instance
     * 
     * @param sortDoneCallback The callback for the sorter
     */
    SplatData(Runnable sortDoneCallback)
    {
        splatSorter = new ThreadedSplatSorter(sortDoneCallback);
    }
    
    /**
     * Return the splats
     * 
     * @return The splats
     */
    CompoundList<Splat> getSplats()
    {
        return splats;
    }
    
    /**
     * Set the given splats
     * 
     * @param splats The splats
     */
    void setSplats(List<? extends Splat> splats)
    {
        this.splats.clearDelegates();
        if (splats != null && !splats.isEmpty())
        {
            Splat s0 = splats.get(0);
            this.shDegree = s0.getShDegree();
            this.splats.addDelegate(splats);
        }
        this.structureModified = true;
    }

    /**
     * Add the given splats
     * 
     * @param splats The splats
     */
    void addSplats(List<? extends Splat> splats)
    {
        if (splats != null && !splats.isEmpty())
        {
            Splat s0 = splats.get(0);
            this.shDegree =
                Math.max(this.getShDegree(), s0.getShDegree());
            this.splats.addDelegate(splats);
            this.structureModified = true;
        }
    }
    
    /**
     * Add the given splat lists
     * 
     * @param splatLists The splat lists
     */
    void addSplatLists(List<? extends List<? extends Splat>> splatLists)
    {
        for (List<? extends Splat> splats : splatLists)
        {
            if (splats != null && !splats.isEmpty())
            {
                Splat s0 = splats.get(0);
                this.shDegree =
                    Math.max(this.getShDegree(), s0.getShDegree());
                this.splats.addDelegate(splats);
                this.structureModified = true;
            }
        }
    }
    
    /**
     * Remove the given splats
     * 
     * @param splats The splats
     */
    void removeSplats(List<? extends Splat> splats)
    {
        this.splats.removeDelegate(splats);
        this.structureModified = true;
    }

    /**
     * Clear the splats
     */
    void clearSplats()
    {
        this.splats.clearDelegates();
        this.shDegree = -1;
        this.structureModified = true;
    }
    
    /**
     * Validate the internal structures
     * 
     * May only be called when a GL context is current.
     */
    void validateStructures()
    {
        if (!this.structureModified)
        {
            return;
        }
        splatSorter.init(splats);
        ensureCapacityCpu();
        ensureCapacityGpu();
        this.structureModified = false;
    }

    /**
     * Ensure that the CPU buffers have sufficient capacity for the current
     * splats,
     */
    private void ensureCapacityCpu()
    {
        ensureGaussianDataCapacityCpu();
        ensureGaussianOrderCapacityCpu();
    }

    /**
     * Ensure that the GPU buffers have sufficient capacity for the current
     * splats.
     * 
     * May only be called when a GL context is current.
     */
    private void ensureCapacityGpu()
    {
        ensureGaussianDataCapacityGpu();
        ensureGaussianOrderCapacityGpu();
    }

    /**
     * Ensure that the gaussianData CPU buffer has a sufficient capacity
     */
    private void ensureGaussianDataCapacityCpu()
    {
        int numSplats = splats.size();
        if (numSplats == 0)
        {
            return;
        }
        logger.info("Ensure CPU capacity for " + numSplats
            + " splats with degree " + getShDegree());

        int shDimensions = Splats.dimensionsForDegree(getShDegree());
        long sizeInFloatsLong = numSplats * (11L + shDimensions * 3L);
        long sizeInBytesLong = sizeInFloatsLong * Float.BYTES;
        if (sizeInBytesLong > Integer.MAX_VALUE)
        {
            throw new OutOfMemoryError("Cannot allocate " + sizeInBytesLong
                + " bytes in a single buffer");
        }
        int sizeInFloats = (int) sizeInFloatsLong;

        if (gaussianData == null || gaussianData.capacity() < sizeInFloats)
        {
            logger.info("Allocating CPU gaussianData for " + numSplats
                + " with " + getShDegree());
            gaussianData = BufferUtils.createFloatBuffer(sizeInFloats);
        }
    }

    /**
     * Ensure that the gaussianData GPU buffer has a sufficient capacity.
     * 
     * May only be called when a GL context is current.
     */
    private void ensureGaussianDataCapacityGpu()
    {
        int numSplats = splats.size();
        if (numSplats == 0)
        {
            return;
        }
        logger.info("Ensure GPU capacity for " + numSplats
            + " splats with degree " + getShDegree());

        int shDimensions = Splats.dimensionsForDegree(getShDegree());
        int sizeInFloats = numSplats * (11 + shDimensions * 3);
        int sizeInBytes = sizeInFloats * Float.BYTES;
        if (gaussianDataSsboSize < sizeInBytes)
        {
            logger.info("Allocating GPU gaussianData for " + numSplats
                + " with " + getShDegree());
            initGaussianDataGpu(sizeInBytes);
            gaussianDataSsboSize = sizeInBytes;
        }
    }

    /**
     * Initialize the Ssbo for the Gaussian data
     * 
     * May only be called when a GL context is current.
     * 
     * @param sizeInBytes The size in bytes
     */
    private void initGaussianDataGpu(int sizeInBytes)
    {
        if (getGaussianDataSsbo() != 0)
        {
            glDeleteBuffers(getGaussianDataSsbo());
            gaussianDataSsbo = 0;
        }
        gaussianDataSsbo = glCreateBuffers();

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, getGaussianDataSsbo());
        glBufferData(GL_SHADER_STORAGE_BUFFER, sizeInBytes, GL_STATIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }
    
    /**
     * Validate the actual data contents
     * 
     * May only be called when a GL context is current.
     */
    void validateGaussianData()
    {
        updateGaussianDataCpu();
        updateGaussianDataGpu();
    }

    /**
     * Copy the data from the current splats into the CPU buffer
     */
    private void updateGaussianDataCpu()
    {
        if (splats.isEmpty())
        {
            return;
        }

        logger.fine("Update data for " + splats.size() + " splats");

        int numSplats = splats.size();
        int shDimensions = Splats.dimensionsForDegree(getShDegree());
        int stride = (11 + shDimensions * 3);
        IntStream.range(0, numSplats).parallel().forEach(i ->
        {
            int j = i * stride;
            Splat s = splats.get(i);
            gaussianData.put(j++, s.getPositionX());
            gaussianData.put(j++, s.getPositionY());
            gaussianData.put(j++, s.getPositionZ());

            gaussianData.put(j++, s.getRotationW());
            gaussianData.put(j++, s.getRotationX());
            gaussianData.put(j++, s.getRotationY());
            gaussianData.put(j++, s.getRotationZ());

            gaussianData.put(j++, (float) Math.exp(s.getScaleX()));
            gaussianData.put(j++, (float) Math.exp(s.getScaleY()));
            gaussianData.put(j++, (float) Math.exp(s.getScaleZ()));

            gaussianData.put(j++, Splats.opacityToAlpha(s.getOpacity()));

            for (int d = 0; d < shDimensions; d++)
            {
                float shX = 0.0f;
                float shY = 0.0f;
                float shZ = 0.0f;
                if (d < s.getShDimensions())
                {
                    shX = s.getShX(d);
                    shY = s.getShY(d);
                    shZ = s.getShZ(d);
                }
                gaussianData.put(j++, shX);
                gaussianData.put(j++, shY);
                gaussianData.put(j++, shZ);
            }
        });

        logger.fine("Update data for " + splats.size() + " splats DONE");

        splatSorter.init(splats);
    }

    /**
     * Fill the Ssbo for the Gaussian data.
     * 
     * May only be called when a GL context is current.
     */
    private void updateGaussianDataGpu()
    {
        int numSplats = splats.size();
        if (numSplats == 0)
        {
            return;
        }
        int shDimensions = Splats.dimensionsForDegree(getShDegree());
        int sizeInFloats = numSplats * (11 + shDimensions * 3);
        int sizeInBytes = sizeInFloats * Float.BYTES;

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, getGaussianDataSsbo());
        ByteBuffer br = glMapBufferRange(GL_SHADER_STORAGE_BUFFER, 0,
            sizeInBytes, GL_MAP_WRITE_BIT | GL_MAP_INVALIDATE_BUFFER_BIT);
        FloatBuffer slice = gaussianData.slice();
        ((Buffer) slice).limit(sizeInFloats);
        br.order(ByteOrder.nativeOrder()).asFloatBuffer().put(slice);
        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    
    /**
     * Validate the order data
     * 
     * May only be called when a GL context is current.
     * 
     * @param viewMatrix The view matrix
     */
    void validateGaussianOrderCpu(FloatBuffer viewMatrix)
    {
        if (splats.isEmpty())
        {
            return;
        }
        updateGaussianOrderCpu(viewMatrix);
    }

    /**
     * Validate the order data
     * 
     * May only be called when a GL context is current.
     */
    void validateGaussianOrderGpu()
    {
        if (splats.isEmpty())
        {
            return;
        }
        updateGaussianOrderGpu();;
    }
    
    /**
     * Ensure that the gaussianOrder CPU buffers have a sufficient capacity
     */
    private void ensureGaussianOrderCapacityCpu()
    {
        int numSplats = splats.size();
        if (numSplats == 0)
        {
            return;
        }
        logger.info("Ensure CPU capacity for " + numSplats + " splats");

        if (gaussianOrder == null || gaussianOrder.capacity() < numSplats)
        {
            logger.info("Allocating CPU gaussianOrder for " + numSplats);
            gaussianOrder = BufferUtils.createIntBuffer(numSplats);
        }
    }

    /**
     * Ensure that the gaussianOrder GPU buffers have a sufficient capacity.
     * 
     * May only be called when a GL context is current.
     */
    private void ensureGaussianOrderCapacityGpu()
    {
        int numSplats = splats.size();
        if (numSplats == 0)
        {
            return;
        }
        logger.info("Ensure GPU capacity for " + numSplats + " splats");

        int sizeInBytes = numSplats * Integer.BYTES;
        if (gaussianOrderSsboSize < sizeInBytes)
        {
            logger.info("Allocating GPU gaussianOrder for " + numSplats);
            initGaussianOrderGpu(sizeInBytes);
            gaussianOrderSsboSize = sizeInBytes;
        }
    }

    /**
     * Initialize the Ssbo for the Gaussian order.
     * 
     * May only be called when a GL context is current.
     * 
     * @param sizeInBytes The size in bytes
     */
    private void initGaussianOrderGpu(int sizeInBytes)
    {
        if (getGaussianOrderSsbo() != 0)
        {
            glDeleteBuffers(getGaussianOrderSsbo());
            gaussianOrderSsbo = 0;
        }
        gaussianOrderSsbo = glCreateBuffers();

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, getGaussianOrderSsbo());
        glBufferData(GL_SHADER_STORAGE_BUFFER, sizeInBytes, GL_STATIC_DRAW);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    /**
     * Update the gaussian order for the given view matrix
     * 
     * @param viewMatrix The view matrix.
     */
    private void updateGaussianOrderCpu(FloatBuffer viewMatrix)
    {
        splatSorter.sort(viewMatrix);
        splatSorter.apply(gaussianOrder);
    }

    /**
     * Fill the Ssbo for the Gaussian order.
     * 
     * May only be called when a GL context is current.
     */
    private void updateGaussianOrderGpu()
    {
        int numSplats = splats.size();
        int sizeInBytes = numSplats * Integer.BYTES;

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, getGaussianOrderSsbo());
        ByteBuffer br = glMapBufferRange(GL_SHADER_STORAGE_BUFFER, 0,
            sizeInBytes, GL_MAP_WRITE_BIT | GL_MAP_INVALIDATE_BUFFER_BIT);
        IntBuffer slice = gaussianOrder.slice();
        ((Buffer) slice).limit(numSplats);
        br.order(ByteOrder.nativeOrder()).asIntBuffer().put(slice);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    /**
     * Returns the SH degree
     * 
     * @return The SH degree
     */
    int getShDegree()
    {
        return shDegree;
    }

    /**
     * Returns the gaussian data SSBO
     * 
     * @return The SSBO
     */
    int getGaussianDataSsbo()
    {
        return gaussianDataSsbo;
    }

    /**
     * Returns the gaussian order SSBO
     * 
     * @return The SSBO
     */
    int getGaussianOrderSsbo()
    {
        return gaussianOrderSsbo;
    }
}