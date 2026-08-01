package com.example.demo.controller;

import com.example.demo.dto.request.PermissionRequest;
import com.example.demo.dto.respone.ApiRespone;
import com.example.demo.dto.respone.PermissionRespone;
import com.example.demo.service.PermissionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {
  private final PermissionService permissionService;

  @PostMapping
  public ApiRespone<PermissionRespone> createPermission(@RequestBody PermissionRequest request) {
    return ApiRespone.<PermissionRespone>builder()
        .result(permissionService.create(request))
        .build();
  }

  @GetMapping
  public ApiRespone<List<PermissionRespone>> getAllPermission() {
    return ApiRespone.<List<PermissionRespone>>builder().result(permissionService.getAll()).build();
  }

  @DeleteMapping("/{id}")
  public ApiRespone<Void> deletePermissionById(@PathVariable String id) {
    permissionService.delete(id);
    return ApiRespone.<Void>builder().build();
  }

  @PutMapping("/{id}")
  public ApiRespone<PermissionRespone> updatePermission(
      @PathVariable String id, @RequestBody PermissionRequest request) {
    return ApiRespone.<PermissionRespone>builder()
        .result(permissionService.update(id,request))
        .build();
  }
}
