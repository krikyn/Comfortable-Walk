package com.netcracker.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
// daba уср не надо, надо юзер :)
@Table(name = "usr")
public class User {
    @Id
    private String id;
    private String name;
    private String picture;
}
