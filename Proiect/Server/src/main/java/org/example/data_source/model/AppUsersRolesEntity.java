package org.example.data_source.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "app_users_roles")
public class AppUsersRolesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "app_user_id")
    private int appUserId;

    @Column(name = "role_id")
    private int roleId;
}
