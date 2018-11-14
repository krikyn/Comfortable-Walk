package com.netcracker.datacollector.data.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
// daba везде таблицы названы во множественном числе, тут в единственном
@Table(name = "square")
public class Square {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer fromPoint;
    private Integer toPoint;
    private Long distance;
}