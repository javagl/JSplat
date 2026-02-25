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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.AbstractList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

/**
 * A panel for maintaining a list of splat data sets
 */
class DataSetsPanel extends JPanel
{
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -3477336710371239875L;

    /**
     * The list showing the data sets
     */
    private final JList<DataSet> list;
    
    /**
     * The list model for the data sets
     */
    private DefaultListModel<DataSet> listModel;
    
    /**
     * The button to remove the selected data set
     */
    private JButton removeSelectedButton;
    
    /**
     * Creates a new instance
     * 
     * @param removalCallback A callback that will receive removed data sets
     */
    DataSetsPanel(Consumer<DataSet> removalCallback)
    {
        super(new BorderLayout());
        listModel = new DefaultListModel<DataSet>();
        list = new JList<DataSet>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel p = new JPanel(new FlowLayout());
        removeSelectedButton = new JButton("Remove");
        removeSelectedButton.setEnabled(false);
        removeSelectedButton.addActionListener(e ->
        {
            DataSet removedDataSet = getSelectedDataSet();
            removeDataSet(removedDataSet);
            removalCallback.accept(removedDataSet);
        });
        p.add(removeSelectedButton);
        add(p, BorderLayout.SOUTH);
    }
    
    /**
     * Add a listener to be informed about selection changes in the list
     * 
     * @param listener The listener
     */
    void addSelectionListener(ListSelectionListener listener)
    {
        list.addListSelectionListener(listener);
    }
    
    /**
     * Add the given data set
     * 
     * @param dataSet The data set
     */
    void addDataSet(DataSet dataSet)
    {
        listModel.addElement(dataSet);
        list.setSelectedIndex(listModel.size() - 1);
        removeSelectedButton.setEnabled(true);
    }
    
    /**
     * Remove the given data set
     * 
     * @param dataSet The data set
     */
    void removeDataSet(DataSet dataSet)
    {
        int oldIndex = list.getSelectedIndex();
        listModel.removeElement(dataSet);
        if (oldIndex == 0)
        {
            list.setSelectedIndex(0);
        }
        else
        {
            list.setSelectedIndex(oldIndex - 1);
        }
        if (listModel.getSize() == 0)
        {
            removeSelectedButton.setEnabled(false);
        }
    }
    
    /**
     * Returns the data set that is currently selected (or <code>null</code>
     * if the list is empty)
     * 
     * @return The selected data set
     */
    DataSet getSelectedDataSet()
    {
        DataSet selectedDataSet = list.getSelectedValue();
        return selectedDataSet;
    }

    /**
     * Returns a list of the data sets in this panel
     * 
     * @return The list
     */
    List<DataSet> getDataSets()
    {
        return new AbstractList<DataSet>() 
        {
            @Override
            public DataSet get(int index)
            {
                return listModel.getElementAt(index);
            }

            @Override
            public int size()
            {
                return listModel.getSize();
            }
        };
    }
    
    
}
