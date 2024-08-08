package com.example.project.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "PERSON")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    @Id
    @Column(name = "PERSON_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "NAME")
    private String name;
}
