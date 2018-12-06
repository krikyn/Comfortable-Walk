package com.netcracker.datacollector.distancecollector;

import com.netcracker.commons.data.model.bean.Graph;

import java.util.Iterator;

/**
 * Implementation of Dijkstra’s algorithm for finding minimal paths in a graph with non-negative load on arcs
 * Current distances are stored in a binary heap
 */
public class DistanceFindingAlgorithm {

    /**
     * The graph for which calculations are made
     */
    private final Graph graph;

    /**
     * Initial vertex
     */
    private int src = -1;
    /**
     * Number of vertices
     */
    private int nVert;
    /**
     * Array of distances
     */
    private long[] distances;
    /**
     * Tree traversal of the minimal paths
     */
    private int[] tree;
    /**
     * Indexes of vertex in the heap
     */
    private int[] positions;
    /**
     * Binary heap
     */
    private Pair[] binHeap;
    /**
     * Heap size
     */
    private int heapSize;
    /**
     * Passed vertexes
     */
    private boolean[] passed;

    public DistanceFindingAlgorithm(Graph g) {
        graph = g;
        nVert = g.getCount();
    }

    /**
     * Gives the lengths of the minimum paths.
     * If the tree has not been built yet, Dijkstra’s algorithm is launched.
     *
     * @param u Number of source vertex
     * @return Array of distances to the source vertex
     */
    public long[] getDistances(int u) {
        if (u < 0 || u >= nVert) return null;
        if (u != src) {
            dijkstra(u);
        }
        return distances;
    }

    /**
     * The implementation of the Dijkstra algorithm.
     * As a result, the algorithm will build
     * tree of minimal paths and their lengths will defined.
     *
     * @param s Initial vertex
     */
    private void dijkstra(int s) {
        src = s;
        distances = new long[nVert];
        tree = new int[nVert];
        positions = new int[nVert];
        binHeap = new Pair[nVert];
        passed = new boolean[nVert];
        // Инициализация массивов
        for (int i = 0; i < nVert; ++i) {
            distances[i] = Integer.MAX_VALUE;
            tree[i] = -1;
            positions[i] = -1;
            heapSize = 0;
        }
        distances[s] = 0;

        // Инициализация кучи
        addToHeap(new Pair(s, 0));

        while (!emptyHeap()) {
            // Жадный алгоритм выбирает ближайшую вершину
            Pair minPair = extractHeap();
            int vert = minPair.vertex;
            passed[vert] = true;

            // Производим релаксацию дуг, ведущих из выбранной вершины
            for (Iterator<Graph.Arc> iArc = graph.arcs(vert); iArc.hasNext(); ) {
                Graph.Arc arc = iArc.next();
                int end = arc.to();
                if (!passed[end]) {
                    long newDist = distances[vert] + arc.weight;
                    if (positions[end] == -1) {
                        // Новая вершина - добавляем в кучу
                        addToHeap(new Pair(end, newDist));
                        tree[end] = vert;
                        distances[end] = newDist;
                    } else {
                        // Вершина уже была в куче, производим ее релаксацию.
                        Pair p = getFromHeap(positions[end]);
                        if (newDist < p.distance) {
                            changeHeap(positions[end], newDist);
                            tree[end] = vert;
                            distances[end] = newDist;
                        }
                    }
                }
            }
        }
    }

    /**
     * Changing the position of an element in the heap according to the changed
     * (decreased) distance to it.
     *
     * @param i       Position of the element in the heap
     * @param newDist New distance
     */
    private void changeHeap(int i, long newDist) {
        binHeap[i].distance = newDist;
        heapUp(i);
    }

    /**
     * Access to the heap element by index.
     *
     * @param i Element index
     */
    private Pair getFromHeap(int i) {
        return binHeap[i];
    }

    /**
     * Extract from the heap an element with a minimum distance to it.
     *
     * @return The element with the highest priority (shortest distance).
     */
    private Pair extractHeap() {
        Pair minPair = binHeap[0];
        positions[minPair.vertex] = -1;
        if (--heapSize > 0) {
            binHeap[0] = binHeap[heapSize];
            binHeap[heapSize] = null;
            positions[binHeap[0].vertex] = 0;
            heapDown(0);
        }
        return minPair;
    }

    /**
     * Adding new element to the heap
     *
     * @param pair Новый элемент
     */
    private void addToHeap(Pair pair) {
        binHeap[positions[pair.vertex] = heapSize] = pair;
        heapUp(heapSize++);
    }

    /**
     * Check if the heap is empty
     */
    private boolean emptyHeap() {
        return heapSize == 0;
    }

    /**
     * Dragging a heap element with a given index up the heap
     *
     * @param i Element index
     */
    private void heapUp(int i) {
        Pair pair = binHeap[i];
        int pred = (i - 1) / 2;
        while (pred >= 0 && pair.compareTo(binHeap[pred]) < 0) {
            positions[binHeap[pred].vertex] = i;
            binHeap[i] = binHeap[pred];
            i = pred;
            if (pred == 0) break;
            pred = (i - 1) / 2;
        }
        positions[pair.vertex] = i;
        binHeap[i] = pair;
    }

    /**
     * Dragging a heap element with a given index down the heap
     *
     * @param i Element index
     */
    private void heapDown(int i) {
        Pair pair = binHeap[i];
        int next = 2 * i + 1;
        while (next < heapSize) {
            if (next + 1 < heapSize && binHeap[next + 1].compareTo(binHeap[next]) < 0) {
                next++;
            }
            if (pair.compareTo(binHeap[next]) <= 0) {
                break;
            }
            positions[binHeap[next].vertex] = i;
            binHeap[i] = binHeap[next];
            i = next;
            next = 2 * i + 1;
        }
        positions[pair.vertex] = i;
        binHeap[i] = pair;
    }

    /**
     * Element of the heap is a pair: number of vertices and distance to this vertex
     * Comparison of pairs is made by distance
     */
    private static class Pair implements Comparable<Pair> {
        int vertex;         // Номер вершины
        long distance;      // Расстояние до нее

        Pair(int v, long d) {
            vertex = v;
            distance = d;
        }

        @Override
        public int compareTo(Pair p) {
            return distance < p.distance ? -1
                    : distance == p.distance ? vertex - p.vertex : 1;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Pair)) return false;
            Pair sndPair = (Pair) o;
            return vertex == sndPair.vertex && distance == sndPair.distance;
        }

        @Override
        public int hashCode() {
            return vertex ^ new Double(distance).hashCode();
        }

        @Override
        public String toString() {
            return "(" + vertex + "," + distance + ")";
        }
    }
}