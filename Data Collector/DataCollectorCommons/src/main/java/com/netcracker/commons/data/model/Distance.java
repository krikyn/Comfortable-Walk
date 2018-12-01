package com.netcracker.commons.data.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "distances")
public class Distance {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer fromPoint;
    private Integer toPoint;
    private Long distance;
}