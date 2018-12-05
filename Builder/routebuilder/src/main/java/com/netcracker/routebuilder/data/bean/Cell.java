package com.netcracker.routebuilder.data.bean;

import lombok.Data;

/**
 * Cell bean for saving states of the potential field cells on which the algorithm A* works
 *
 * @author KirillVakhrushev
 */
@Data
public class Cell {
    /**
     * the value of the cell on the potential map
     */
    private int value;
    /**
     * parent cell reference
     */
    private Cell parent;
    /**
     * flag for cells that are not considered in the algorithm
     */
    private boolean isClosed;
    /**
     * cell field coordinates
     */
    private FieldCoordinates fieldCoordinates;
    /**
     * cell geo coordinates
     */
    private GeoCoordinates geoCoordinates;

    /**
     * heuristic function value = distance(hx) + cost(gx)
     */
    private Double fx;
    /**
     * the value of the cost function of reaching the cell from the start cell
     */
    private Double gx;
    /**
     * the value of the function of the heuristic estimate of the distance from the cell to the end cell
     */
    private Double hx;

    /**
     * Constructor of Cell to initialize the cells before running the algorithm
     *
     * @param value            the value of the cell on the potential map
     * @param fieldCoordinates cell field coordinates
     * @param geoCoordinates   cell geo coordinates
     */
    public Cell(int value, FieldCoordinates fieldCoordinates, GeoCoordinates geoCoordinates) {
        this.value = value;
        this.fieldCoordinates = fieldCoordinates;
        this.geoCoordinates = geoCoordinates;
        gx = Double.MAX_VALUE;
        parent = null;
        isClosed = false;
    }

    /**
     * Сell availability recognition
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * Update cell parameters
     *
     * @param parent parent cell reference
     * @param gx     the value of the cost function of reaching the cell from the start cell
     * @param hx     the value of the function of the heuristic estimate of the distance from the cell to the end cell
     */
    public void updateParameters(Cell parent, Double gx, Double hx) {
        this.parent = parent;
        this.fx = gx + hx;
        this.gx = gx;
        this.hx = hx;
    }
}
