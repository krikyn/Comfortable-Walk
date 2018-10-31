package com.netcracker.datacollector.data.model;

import com.google.maps.model.LatLng;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "maps")
@Data
@NoArgsConstructor
public class CityMap {

    @Id
    @NonNull
    @GeneratedValue
    private UUID id;

    @NonNull
    @Column(name = "type")
    private String type;

    @Column(name = "grid")
    @Lob
    int[][] grid;

    @Column(name = "base_map")
    @Lob
    LatLng[][] baseMap;

}
