package com.example.demo.controller;

import com.example.demo.dto.request.RoleRequest;
import com.example.demo.dto.respone.ApiRespone;
import com.example.demo.dto.respone.RoleRespone;
import com.example.demo.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/role")
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    public ApiRespone<RoleRespone> createRole(@RequestBody RoleRequest request){
        return ApiRespone.<RoleRespone>builder()
                .result(roleService.create(request))
                .build();
    }

    @GetMapping
    public ApiRespone<List<RoleRespone>> getAllRole(){
        return ApiRespone.<List<RoleRespone>>builder()
                .result(roleService.getAll())
                .build();
    }
    @DeleteMapping("/{id}")
    public ApiRespone<Void> deleteRole(@PathVariable String id){
        roleService.delete(id);
        return ApiRespone.<Void>builder()
                .build();
    }
    @PutMapping("/{id}")
    public ApiRespone<RoleRespone> updateRole(@PathVariable String id,@RequestBody RoleRequest request){
        return ApiRespone.<RoleRespone>builder()
                .result(roleService.update(id,request))
                .build();
    }
}
