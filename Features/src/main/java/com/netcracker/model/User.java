package com.netcracker.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "usr")
public class User {
    @Id
    private String id;
    private String name;
    private String picture;
}
