package com.netcracker.commons.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Service for saving weather potential map in DB
 *
 * @author Kirill.Vakhrushev
 */
@Entity
@NoArgsConstructor
@Data
public class WeatherPotentialMap {

    @Id
    private int id;

    int scale;

    @Lob
    int[][] potentialField;

}
