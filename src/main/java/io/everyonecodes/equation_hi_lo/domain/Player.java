package io.everyonecodes.equation_hi_lo.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity // Tells JPA provider(Hibernate) to treat this class as a database entity
@Data

public class Player {
    //id identifies the primary key field of the entity

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(unique = true, nullable = false) private String username;
    //unique true means ensure that no two records in database share the same username
    //null table false means to prevent inserting a user without a username

    @Column(nullable = false) private String password;
    //null table ensures that users must have passwords to the database

    @Column(nullable = false)
    private String roles;
}