package com.netcracker.datacollector.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Grout on 28.10.2018.
 */

@Entity
@Table(name = "places")
@Data
@NoArgsConstructor
public class Place {

    @Id
    @NonNull
    @Column(name = "google_place_id")
    private String googlePlaceId;

    @NonNull
    @Column(name = "type")
    private String type;

    @NonNull
    @Column(name = "name")
    private String name;

    @NonNull
    @Column(name = "address")
    private String address;

    @NonNull
    @Column(name = "latitude")
    private Double latitude;

    @NonNull
    @Column(name = "longitude")
    private Double longitude;
}
