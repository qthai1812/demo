package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
      @Id
      @GeneratedValue(strategy = GenerationType.UUID)
      String id;
      String username;
      String password;
      String firstname;
      String lastname;
      LocalDate dob;

      @ManyToMany
      Set<Role> roles;


}
