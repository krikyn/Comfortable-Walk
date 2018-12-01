package com.netcracker.datacollector.util;

import com.netcracker.commons.data.model.bean.Graph;

import java.util.Iterator;

/**
 * Реализация алгоритма Дейкстры поиска минимальных путей в графе
 * с неотрицательной нагрузкой на дугах.
 * Хранение текущих расстояний до вершин графа производится в
 * двоичной куче.
 */
public class DistanceFindingAlgorithm {
    /**
     * Пара из номера вершины и растояния до нее - элемент кучи.
     * Сравнение пар производится по расстояниям.
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
            if (o == null && !(o instanceof Pair)) return false;
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

    private final Graph graph;      // Граф, для которого производятся вычисления

    private int src = -1;           // Начальная вершина, пути из которой анализируются
    private int nVert;              // число вершин в графе

    private long[] distances;       // Массив расстояний
    private int[] tree;             // Дерево обхода по минимальным путям

    private int[] positions;        // Индексы вершин в куче
    private Pair[] binHeap;         // Двоичная куча
    private int heapSize;           // Размер кучи
    private boolean[] passed;       // Массив пройденных вершин

    public DistanceFindingAlgorithm(Graph g) {
        graph = g;
        nVert = g.getCount();
    }

    /**
     * Выдает длины минимальных путей.
     * Если дерево еще не построено, запускается алгоритм Дейкстры.
     *
     * @param u Номер исходной вершины
     * @return Массив расстояний до указанных вершин
     */
    public long[] getDistances(int u) {
        if (u < 0 || u >= nVert) return null;
        if (u != src) {
            dijkstra(u);
        }
        return distances;
    }

    /**
     * Реализация алгоритма Дейкстры.
     * В результате работы алгоритма будет построено
     * дерево минимальных путей и определены их длины.
     *
     * @param s Начальная вершина
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
     * Изменение позиции элемента в куче в соответствии с изменившимся
     * (уменьшившимся) расстоянием до нее.
     *
     * @param i       Позиция элемента в куче
     * @param newDist Новое расстояние
     */
    private void changeHeap(int i, long newDist) {
        binHeap[i].distance = newDist;
        heapUp(i);
    }

    /**
     * Доступ к элементу кучи по индексу.
     *
     * @param i Индекс элемента
     */
    private Pair getFromHeap(int i) {
        return binHeap[i];
    }

    /**
     * Извлечение из кучи элемента с минимальным расстоянием до него.
     *
     * @return Элемент с наивысшим приоритетом (наименьшим расстоянием).
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
     * Добавление нового элемента в кучу.
     *
     * @param pair Новый элемент
     */
    private void addToHeap(Pair pair) {
        binHeap[positions[pair.vertex] = heapSize] = pair;
        heapUp(heapSize++);
    }

    /**
     * Проверка, пуста ли куча.
     */
    private boolean emptyHeap() {
        return heapSize == 0;
    }

    /**
     * Протаскивание элемента кучи с заданным индексом вверх по куче
     *
     * @param i Индекс элемента
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
     * Протаскивание элемента кучи с заданным индексом вниз по куче
     *
     * @param i Индекс элемента
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
}