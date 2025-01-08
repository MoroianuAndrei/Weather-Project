package org.example.data_source.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
@Entity
@Table(name = "role")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /* See Role enum class from config.util for the default roles */
    @Column(name = "name")
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Collection<UserEntity> users = new ArrayList<>();

    public void addUser(UserEntity user) {
        users.add(user);
    }

    public void setRole(Role role) {
        this.name= role.name();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RoleEntity other = (RoleEntity) obj;
        return id == other.id && name.equals(other.name);
    }
}