package com.coursehub.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_role")
@Getter
@Setter
public class UserRoleEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference(value = "user-userRole")
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference(value = "role-userRole")
    @JoinColumn(name = "role_id")
    private RoleEntity roleEntity;

}
