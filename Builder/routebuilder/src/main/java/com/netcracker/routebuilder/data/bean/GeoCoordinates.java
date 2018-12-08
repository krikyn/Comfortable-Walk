package com.netcracker.routebuilder.data.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * GeoCoordinates bean for saving geo coordinates, where x - longitude, y - latitude
 *
 * @author KirillVakhrushev
 */
@Data
@AllArgsConstructor
public class GeoCoordinates {

    private Double x;
    private Double y;

}
