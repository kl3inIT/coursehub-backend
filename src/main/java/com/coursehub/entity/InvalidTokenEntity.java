package com.coursehub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "invalid_token")
@Getter
@Setter
public class InvalidTokenEntity {

    @Id
    private String id;

    @Column
    private Date expiryTime;

}
