package com.netcracker.datacollector.data.model.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Представление нагруженного графа списками смежности.
 * Нагрузка на дуги - вещественные числа ("длина дуги").
 */
public class Graph {
    /**
     * Представление дуги графа
     */
    public static class Arc {
        public long weight; //Нагрузка на дугу
        int to;             //Номер вершины, в которую ведет дуга

        public Arc(int to, long info) {
            this.to = to;
            this.weight = info;
        }

        public double weight() {
            return weight;
        }

        public int to() {
            return to;
        }

        public void addWeight(double delta) {
            weight += delta;
        }
    }

    ;

    private List<List<Arc>> lGraph; //Списки смежности
    private int nVertex;            //Число вершин

    /**
     * Конструктор пустого графа с заданным числом вершин
     * @param nVert Число вершин
     */
    public Graph(int nVert) {
        lGraph = new ArrayList<>();
        for (int i = 0; i < nVert; ++i) {
            lGraph.add(new ArrayList<>());
        }
        nVertex = nVert;
    }

    /**
     * Число вершин графа
     */
    public int getCount() {
        return nVertex;
    }

    /**
     * Добавление дуги в граф. Предполагается, что ранее такой дуги в графе не было.
     *
     * @param from Начало дуги (номер вершины)
     * @param to   Конец дуги (номер вершины)
     * @param info Нагрузка на дугу
     */
    public void addArc(int from, int to, long info) {
        assert from < nVertex && from >= 0;
        assert to < nVertex && to >= 0;

        lGraph.get(from).add(new Arc(to, info));
    }

    public int addVertex() {
        lGraph.add(new ArrayList<>());
        nVertex++;
        return lGraph.size() - 1;
    }

    public void removeVertex(int u) {
        lGraph.remove(u);
        nVertex--;
        for (List<Arc> list : lGraph) {
            for (Iterator<Arc> iArc = list.iterator(); iArc.hasNext(); ) {
                Arc arc = iArc.next();
                if (arc.to == u) {
                    iArc.remove();
                } else if (arc.to > u) {
                    arc.to--;
                }
            }
        }
    }

    /**
     * Итератор дуг, ведущих из заданной вершины
     *
     * @param u Исходная вершина
     */
    public Iterator<Arc> arcs(int u) {
        return lGraph.get(u).iterator();
    }
}