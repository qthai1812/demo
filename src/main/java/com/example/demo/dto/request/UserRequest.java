package com.example.demo.dto.request;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
  String username;

  @Size(min = 6, message = "password at least 8 characters")
  String password;

  String firstname;
  String lastname;
  LocalDate dob;
  List<String> roles;
}
