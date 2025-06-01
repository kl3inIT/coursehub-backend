package com.coursehub.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class RoleEntity extends BaseEntity{

    @Column
    private String code;

    @Column
    private String description;

    @OneToMany(mappedBy = "roleEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserEntity> userRoleList = new HashSet<>();

}
