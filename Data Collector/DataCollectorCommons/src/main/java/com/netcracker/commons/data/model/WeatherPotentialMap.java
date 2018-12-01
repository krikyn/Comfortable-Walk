package com.netcracker.commons.data.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

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
