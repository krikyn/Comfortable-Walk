package com.netcracker.weatherdatacollector.entity;

import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Table(name = "weatherMap")
@Data
@Proxy(lazy=false)
public class WeatherPotentialMap {

    @Id
    @NonNull
    private int id;

    @NonNull
    @Column(name = "scale")
    int scale;

    @Lob
    @NonNull
    @Column(name = "field")
    int[][] potentialField;

    public int[][] getPotentialField() {
        return potentialField;
    }

    public void setPotentialField(int[][] potentialField) {
        this.potentialField = potentialField;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setScale(int scale) {
        this.id = scale;
    }

    public int getScale() {
        return id;
    }
}
