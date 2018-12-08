package com.netcracker.features.data.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for saving user's info in DB
 *
 * @author prokhorovartem
 */
@Entity
@Data
@Table(name = "user_application")
public class User {
    @Id
    private String id;
    private String name;
    private String picture;
}
