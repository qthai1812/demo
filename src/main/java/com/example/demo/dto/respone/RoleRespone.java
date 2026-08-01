package com.example.demo.dto.respone;

import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRespone {
  String name;
  String description;
  Set<PermissionRespone> permission;
}
