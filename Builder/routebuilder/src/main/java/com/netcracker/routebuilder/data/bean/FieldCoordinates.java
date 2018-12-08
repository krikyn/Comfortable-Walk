package com.netcracker.routebuilder.data.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * FieldCoordinates bean for saving field coordinates in the order of the arguments in the array (a[x][y])
 *
 * @author KirillVakhrushev
 */
@Data
@AllArgsConstructor
public class FieldCoordinates {

    private int x;
    private int y;

}
