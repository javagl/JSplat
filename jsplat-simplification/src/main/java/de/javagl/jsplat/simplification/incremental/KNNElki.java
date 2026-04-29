/*
 * www.javagl.de - JSplat
 *
 * Copyright 2026 Marco Hutter - http://www.javagl.de
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
package de.javagl.jsplat.simplification.incremental;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import elki.data.DoubleVector;
import elki.data.NumberVector;
import elki.data.type.TypeUtil;
import elki.database.Database;
import elki.database.StaticArrayDatabase;
import elki.database.ids.DBIDRange;
import elki.database.ids.DBIDRef;
import elki.database.ids.KNNList;
import elki.database.query.distance.DistanceQuery;
import elki.database.query.knn.KNNSearcher;
import elki.database.relation.Relation;
import elki.datasource.ArrayAdapterDatabaseConnection;
import elki.datasource.DatabaseConnection;
import elki.distance.minkowski.EuclideanDistance;
import elki.index.KNNIndex;
import elki.index.tree.spatial.kd.MinimalisticMemoryKDTree;

/**
 * Internal implementation of {@link KNN} functionality, using ELKI
 */
class KNNElki implements KNN
{
    /**
     * The database ID range
     */
    private final DBIDRange dbIds;

    /**
     * The KNN index
     */
    private final KNNIndex<NumberVector> knnIndex;

    /**
     * The distance query
     */
    private final DistanceQuery<NumberVector> distanceQuery;

    /**
     * Creates a new instance
     * 
     * @param data The data
     */
    KNNElki(double data[][])
    {
        // Create an ELKI database from the data.
        int startId = 0;
        DatabaseConnection dbc =
            new ArrayAdapterDatabaseConnection(data, null, startId);
        Database d = new StaticArrayDatabase(dbc, null);
        d.initialize();

        // Obtain the one and only relation and its ID range
        Relation<NumberVector> relation =
            d.getRelation(TypeUtil.NUMBER_VECTOR_FIELD);
        this.dbIds = (DBIDRange) relation.getDBIDs();

        // Crate the KD tree for the spatial query
        MinimalisticMemoryKDTree.Factory<NumberVector> indexFactory =
            new MinimalisticMemoryKDTree.Factory<>();
        this.knnIndex = indexFactory.instantiate(relation);
        this.knnIndex.initialize();
        this.distanceQuery = EuclideanDistance.STATIC.instantiate(relation);
    }

    @Override
    public List<Integer> compute(double query[], int k)
    {
        // Prepare the actual query
        KNNSearcher<NumberVector> knnSearcher =
            knnIndex.kNNByObject(distanceQuery, k, 0);
        NumberVector queryPoint = new DoubleVector(query);

        // Do it!
        KNNList knn = knnSearcher.getKNN(queryPoint, k);

        // Extract the indices from the KNN result
        List<Integer> resultIndices = new ArrayList<Integer>();
        Consumer<DBIDRef> consumer = new Consumer<DBIDRef>()
        {
            @Override
            public void accept(DBIDRef t)
            {
                int index = dbIds.getOffset(t);
                resultIndices.add(index);
            }
        };
        knn.forEach(consumer);
        return resultIndices;
    }
}
