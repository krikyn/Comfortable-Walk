package com.netcracker.routebuilder.data.model;

import lombok.*;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Table(name = "weatherMap")
@Data
// daba смысл этой аннотации? у сущности нет связей вообще
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

    // daba ломбок сюда, @Data уже есть
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