package com.netcracker.commons.data.model.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Graph bean for representation of a loaded graph with adjacency lists.
 * @author prokhorovartem
 */
public class Graph {
    /**
     * Representation of arc graph
     */
    public static class Arc {
        /**
         * arc weight
         */
        public long weight;
        /**
         * vertex number where arc goes
         */
        int to;

        Arc(int to, long info) {
            this.to = to;
            this.weight = info;
        }

        public int to() {
            return to;
        }
    }

    /**
     * adjacency lists
     */
    private List<List<Arc>> lGraph;
    /**
     * number of vertex
     */
    private int nVertex;

    /**
     * Constructor of empty graph with given the number of vertices
     * @param nVertices number of vertices
     */
    public Graph(int nVertices) {
        lGraph = new ArrayList<>();
        for (int i = 0; i < nVertices; ++i) {
            lGraph.add(new ArrayList<>());
        }
        nVertex = nVertices;
    }

    /**
     * The number of vertices of the graph
     */
    public int getCount() {
        return nVertex;
    }

    /**
     * Adding an arc to a graph.
     *
     * @param from Start of arc (vertex number)
     * @param to   End of arc (vertex number)
     * @param info Arc weight
     */
    public void addArc(int from, int to, long info) {
        assert from < nVertex && from >= 0;
        assert to < nVertex && to >= 0;

        lGraph.get(from).add(new Arc(to, info));
    }

    /**
     * Iterator of arcs leading from a source vertex
     *
     * @param u Source vertex
     */
    public Iterator<Arc> arcs(int u) {
        return lGraph.get(u).iterator();
    }
}