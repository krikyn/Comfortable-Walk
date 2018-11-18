package com.netcracker.datacollector.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Grout on 28.10.2018.
 *
 * Класс описывающий сущность места.
 */

@Entity
@Table(name = "places")
@Data
@NoArgsConstructor
public class Place {

    @Id
    @Column(name = "google_place_id")
    private String googlePlaceId;

    @NotNull
    @Column(name = "type")
    private String type;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "address")
    private String address;

    @NotNull
    @Column(name = "latitude")
    private Double latitude;

    @NotNull
    @Column(name = "longitude")
    private Double longitude;
}
