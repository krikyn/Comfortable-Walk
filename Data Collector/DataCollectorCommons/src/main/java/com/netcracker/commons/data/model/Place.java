package com.netcracker.commons.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Class that describes entity of the place on the map.
 *
 * @author Али
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
