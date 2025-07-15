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

import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of a {@link SplatSorter} that sorts in a background task and
 * informs a callback when the sorting is done.
 */
class ThreadedSplatSorter extends BasicSplatSorter implements SplatSorter
{
    /**
     * Implementation of a blocking queue that always retains the last element
     *
     * @param <E> The element type
     */
    private static class SingleElementBlockingQueue<E>
        extends LinkedBlockingQueue<E>
    {
        /**
         * Serial UID
         */
        private static final long serialVersionUID = -6214104872631457367L;

        /**
         * Default constructor
         */
        SingleElementBlockingQueue()
        {
            super(1);
        }

        @Override
        public boolean offer(E e)
        {
            clear();
            return super.offer(e);
        }

        @Override
        public boolean offer(E e, long timeout, TimeUnit unit)
            throws InterruptedException
        {
            clear();
            return super.offer(e, timeout, unit);
        }

        @Override
        public boolean add(E e)
        {
            clear();
            return super.add(e);
        }

        @Override
        public void put(E e) throws InterruptedException
        {
            clear();
            super.put(e);
        }
    }

    /**
     * Create the executor service for this splat sorter
     * 
     * @return The executor service
     */
    private static ExecutorService createExecutorService()
    {
        // Use daemon threads for the executor service
        ThreadFactory threadFactory = (r) ->
        {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        };

        // Create the executor service using a single-element queue
        ThreadPoolExecutor e =
            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS,
                new SingleElementBlockingQueue<Runnable>(), threadFactory);
        return e;
    }

    /**
     * The executor service
     */
    private final ExecutorService executorService;

    /**
     * A callback that will be called after a sorting operation completes
     */
    private Runnable sortDoneCallback;

    /**
     * Creates a new instance
     * 
     * @param sortDoneCallback The callback that will be called when a sorting
     *        pass completed
     */
    ThreadedSplatSorter(Runnable sortDoneCallback)
    {
        this.sortDoneCallback = Objects.requireNonNull(sortDoneCallback,
            "The sortDoneCallback may not be null");
        this.executorService = createExecutorService();
    }

    @Override
    public void sort(FloatBuffer viewMatrix)
    {
        float mx = viewMatrix.get(0 * 4 + 2);
        float my = viewMatrix.get(1 * 4 + 2);
        float mz = viewMatrix.get(2 * 4 + 2);
        float mw = viewMatrix.get(3 * 4 + 2);
        if (!viewMatrixChanged(mx, my, mz, mw))
        {
            return;
        }
        executorService.submit(() ->
        {
            performSort(mx, my, mz, mw);
            finishSort();
            sortDoneCallback.run();
        });
    }
}
