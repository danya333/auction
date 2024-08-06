package com.mrv.auction.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    @Enumerated
    private Role role;
    private String username;
    private String password;
    @Transient
    private String passwordConfirmation;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Ad> ads;
}

