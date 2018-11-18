package com.netcracker.datacollector.data.model;

import com.google.maps.model.LatLng;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by Grout on 28.10.2018.
 *
 * Класс описывающий сущность карты.
 * */

@Entity
@Table(name = "maps")
@Data
@NoArgsConstructor
public class CityMap {

    @Id
    @GeneratedValue
    private UUID id;

    @NotNull
    @Column(name = "type")
    private String type;

    @Column(name = "grid")
    @Lob
    int[][] grid;

    @Column(name = "base_map")
    @Lob
    LatLng[][] baseMap;

}
