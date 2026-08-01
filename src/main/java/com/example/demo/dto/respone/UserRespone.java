package com.example.demo.dto.respone;

import com.example.demo.entity.Role;
import java.time.LocalDate;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRespone {
  String id;
  String username;
  String firstname;
  String lastname;
  LocalDate dob;
  Set<Role> roles;
}
