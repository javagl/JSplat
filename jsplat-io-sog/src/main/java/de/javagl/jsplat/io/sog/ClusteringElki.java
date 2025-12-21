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

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.javagl.jsplat.io.sog.Clustering.ClusteringResult;
import elki.clustering.kmeans.KDTreePruningKMeans;
import elki.clustering.kmeans.KDTreePruningKMeans.Split;
import elki.clustering.kmeans.KMeans;
import elki.clustering.kmeans.initialization.KMeansInitialization;
import elki.clustering.kmeans.initialization.KMeansPlusPlus;
import elki.data.Cluster;
import elki.data.Clustering;
import elki.data.NumberVector;
import elki.data.model.KMeansModel;
import elki.database.Database;
import elki.database.StaticArrayDatabase;
import elki.database.ids.DBIDRef;
import elki.database.ids.DBIDs;
import elki.datasource.ArrayAdapterDatabaseConnection;
import elki.datasource.DatabaseConnection;
import elki.distance.minkowski.SquaredEuclideanDistance;
import elki.logging.LoggingConfiguration;
import elki.utilities.random.RandomFactory;

/**
 * Internal implementation of {@link Clustering} functionality, using ELKI
 */
class ClusteringElki
{
    /**
     * Compute the clustering result from the given data
     * 
     * @param data The data
     * @param desiredK The desired number of clusters
     * @param iterations The maximum iterations
     * @return The result, hopefully...
     */
    static ClusteringResult compute(double data[][], int desiredK,
        int iterations)
    {
        hackyWorkaroundForElkiLogging();

        // Create an ELKI database from the data.
        // Important: The ID will be accessed internally, so an explicit
        // start ID has to be defined.
        int startId = 0;
        DatabaseConnection dbc =
            new ArrayAdapterDatabaseConnection(data, null, startId);
        Database d = new StaticArrayDatabase(dbc, null);
        d.initialize();

        // Create the clusterer, and compute the clustering
        int k = Math.min(data.length, desiredK);
        KMeans<NumberVector, KMeansModel> kMeans =
            createClusterer(k, iterations);
        Clustering<KMeansModel> clustering = kMeans.autorun(d);

        int numRows = data.length;
        int numCols = data[0].length;
        return translate(numRows, numCols, desiredK, clustering);
    }

    /**
     * Translate the given clustering into the representation that is required
     * for SOG
     * 
     * @param numRows The number of rows
     * @param numCols The number of columns
     * @param desiredK The desired number of clusters (may be larger than the
     *        actual number)
     * @param clustering The clustering
     * @return The result
     */
    private static ClusteringResult translate(int numRows, int numCols,
        int desiredK, Clustering<KMeansModel> clustering)
    {
        int labels[] = new int[numRows];
        float centroids[][] = new float[desiredK][];
        List<Cluster<KMeansModel>> clusters = clustering.getAllClusters();
        for (int c = 0; c < desiredK; c++)
        {
            // Add dummy centroids if the requested number of clusters was
            // larger than the number of data points
            if (c >= clusters.size())
            {
                centroids[c] = new float[numCols];
                continue;
            }

            // Assign the cluster indices as the "labels" for the data points
            int clusterIndex = c;
            Cluster<KMeansModel> cluster = clusters.get(c);
            Consumer<DBIDRef> consumer = new Consumer<DBIDRef>()
            {
                @Override
                public void accept(DBIDRef t)
                {
                    // This assumes that the startId was set explicitly.
                    // I know, the method comment says "NOT FOR PUBLIC USE",
                    // but we need these indices, and there doesn't seem
                    // to be a nice solution for that.
                    int internalIndex = t.internalGetIndex();
                    labels[internalIndex] = clusterIndex;
                }
            };
            DBIDs ids = cluster.getIDs();
            ids.forEach(consumer);

            // Extract the means as the "centroids"
            KMeansModel model = cluster.getModel();
            double[] mean = model.getMean();
            float centroid[] = new float[mean.length];
            for (int i = 0; i < mean.length; i++)
            {
                centroid[i] = (float) mean[i];
            }
            centroids[c] = centroid;
        }

        ClusteringResult clusteringResult = new ClusteringResult();
        clusteringResult.centroids = centroids;
        clusteringResult.labels = labels;
        return clusteringResult;
    }

    /**
     * Create an unspecified ELKI K-Means clusterer
     * 
     * @param k The number of clusters
     * @param iterations The maximum iterations
     * @return The clusterer
     */
    private static KMeans<NumberVector, KMeansModel> createClusterer(int k,
        int iterations)
    {
        // Many options here...
        RandomFactory rnd = RandomFactory.DEFAULT;
        KMeansInitialization initializer = new KMeansPlusPlus<Object>(rnd);
        Split split = Split.MIDPOINT;
        int leafsize = 1000;
        KMeans<NumberVector, KMeansModel> kMeans =
            new KDTreePruningKMeans<NumberVector>(
                SquaredEuclideanDistance.STATIC, k, iterations, initializer,
                split, leafsize);
        return kMeans;
    }

    /**
     * Whatever they did there, it was wrong...
     */
    private static void hackyWorkaroundForElkiLogging()
    {
        // Disable logging to not warn about empty clusters
        System.setProperty("java.util.logging.config.file",
            "ElkiIsMessingWithLoging");
        Logger logger = Logger.getLogger("elki");
        logger.setLevel(Level.OFF);
        LoggingConfiguration.setLevelFor("elki.clustering.kmeans", "SEVERE");
    }

    /**
     * Private constructor to prevent instantiation
     */
    private ClusteringElki()
    {
        // Private constructor to prevent instantiation
    }

}
