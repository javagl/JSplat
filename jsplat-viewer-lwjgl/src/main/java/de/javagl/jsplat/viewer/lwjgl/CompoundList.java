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

import java.util.AbstractList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Set;

/**
 * Implementation of a list that provides a view on multiple other lists
 *
 * @param <T> The element type
 */
class CompoundList<T> extends AbstractList<T> implements RandomAccess
{
    /**
     * The delegate lists
     */
    private final Set<List<? extends T>> delegates;

    /**
     * Creates a new instance
     */
    CompoundList()
    {
        this.delegates = new LinkedHashSet<List<? extends T>>();
    }

    /**
     * Add the given delegate list
     * 
     * @param delegate The delegate list
     */
    void addDelegate(List<? extends T> delegate)
    {
        Objects.requireNonNull(delegate, "The delegate may not be null");
        delegates.add(delegate);
    }

    /**
     * Remove the given delegate list
     * 
     * @param delegate The delegate list
     */
    void removeDelegate(List<? extends T> delegate)
    {
        delegates.remove(delegate);
    }

    /**
     * Clear the delegate list
     */
    void clearDelegates()
    {
        this.delegates.clear();
    }

    @Override
    public T get(int index)
    {
        if (index < 0)
        {
            throw new IndexOutOfBoundsException(
                "Index may not be negative, but is " + index);
        }
        int localIndex = index;
        for (List<? extends T> delegate : delegates)
        {
            if (localIndex < delegate.size())
            {
                return delegate.get(localIndex);
            }
            localIndex -= delegate.size();
        }
        throw new IndexOutOfBoundsException(
            "Index must be smaller than " + size() + ", but is " + index);
    }

    @Override
    public int size()
    {
        int size = 0;
        for (List<? extends T> delegate : delegates)
        {
            size += delegate.size();
        }
        return size;
    }

}