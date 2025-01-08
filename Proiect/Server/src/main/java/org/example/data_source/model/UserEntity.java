package org.example.data_source.model;

import lombok.Data;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name="app_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
    private int id;


    @Basic
    @Column(name = "username")
    private String username;

    /* Force the validation of the email on the database level too */
    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(255) CHECK (email ~* '^[A-Za-z0-9._+%-]+@[A-Za-z0-9.-]+[.][A-Za-z]+$')")
    private String email;

    @Basic
    @Column(name = "password")
    private String password;

    @Transient
    private String repeatPassword;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)  // Adăugarea cascade PERSIST
    @JoinTable(
            name = "app_users_roles",
            joinColumns = @JoinColumn(name = "app_user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private List<RoleEntity> roles = new ArrayList<>();

    public void addRole(RoleEntity role) {
        roles.add(role);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserEntity other = (UserEntity) obj;
        return id == other.id && email.equals(other.email);
    }
}
