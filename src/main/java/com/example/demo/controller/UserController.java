package com.example.demo.controller;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.respone.ApiRespone;
import com.example.demo.dto.respone.UserRespone;
import com.example.demo.service.UserService;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ApiRespone<UserRespone> createUser(@RequestBody @Valid UserRequest request) {



        return ApiRespone.<UserRespone>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiRespone<UserRespone> getUser(@PathVariable String id) {



        return ApiRespone.<UserRespone>builder()
                .result(userService.getUserById(id))
                .build();
    }

    @GetMapping
    public ApiRespone<List<UserRespone>> getAllUsers() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. Lấy Username (Chính là cái 'subject' mà bạn đã set lúc tạo JWT)
        String username = authentication.getName();

        // 3. Lấy danh sách Quyền (Scope/Roles)
        var authorities = authentication.getAuthorities();

        // 4. Log ra màn hình console
        log.info("User đang đăng nhập là: {}", username);
        log.info("Các quyền của user này: {}", authorities);

        return ApiRespone.<List<UserRespone>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/myInfo")
    public ApiRespone<UserRespone> getMyInfo(){
        return ApiRespone.<UserRespone>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{id}")
    public ApiRespone<UserRespone> updateUser(@PathVariable String id, @Valid @RequestBody UserRequest request) {
        return ApiRespone.<UserRespone>builder()
                .result(userService.updateUser(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiRespone<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ApiRespone.<String>builder()
                .result("User has been deleted")
                .build();
    }
}