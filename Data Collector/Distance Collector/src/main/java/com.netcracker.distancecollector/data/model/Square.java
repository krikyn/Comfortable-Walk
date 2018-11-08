package com.netcracker.distancecollector.data.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "square")
public class Square {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer fromPoint;
    private Integer toPoint;
    private Long distance;
}