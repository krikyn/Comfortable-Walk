package com.netcracker.data.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "user_application")
public class User {
    @Id
    private String id;
    private String name;
    private String picture;
}
