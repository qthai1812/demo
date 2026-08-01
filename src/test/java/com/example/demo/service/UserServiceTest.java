package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.respone.UserRespone;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import java.time.LocalDate;
import java.util.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource("/test.properties")
public class UserServiceTest {
  @Autowired UserService userService;

  @MockitoBean UserRepository userRepository;

  @MockitoBean RoleRepository roleRepository;

  UserRequest userRequest;
  UserRespone userRespone;
  Role role;
  User user;

  @BeforeEach
  void initData() {
    var dob = LocalDate.of(2005, 8, 1);
    userRequest =
        UserRequest.builder()
            .username("cute1805")
            .password("thaidui1805")
            .firstname("quoc")
            .lastname("thai")
            .dob(dob)
            .build();
    user =
        User.builder()
            .id("04bf85c2-c9e7-48eb-a66a-e7b84f9fb4da")
            .password("abccsdfds")
            .username("cute1805")
            .firstname("quoc")
            .lastname("thai")
            .dob(dob)
            .build();
    userRespone =
        UserRespone.builder()
            .id("04bf85c2-c9e7-48eb-a66a-e7b84f9fb4da")
            .username("cute1805")
            .firstname("quoc")
            .lastname("thai")
            .dob(dob)
            .build();
    role = Role.builder().name("ADMIN").description("has all permission").build();
  }

  @Test
  void createUser_Valid_Success() {

    // Mockito.when(userRepository.existsByUsername(ArgumentMatchers.anyString()))
    // .thenReturn(false);
    Mockito.when(userRepository.save(ArgumentMatchers.any())).thenReturn(user);
    Mockito.when(roleRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(role));

    // Dua du lieu request vao ham

    var response = userService.createUser(userRequest);

    Assertions.assertThat(response.getId()).isEqualTo("04bf85c2-c9e7-48eb-a66a-e7b84f9fb4da");
    Assertions.assertThat(response.getUsername()).isEqualTo("cute1805");
  }

  @Test
  void createUser_HasExisted_Faile() {
    Mockito.when(userRepository.existsByUsername(ArgumentMatchers.any())).thenReturn(true);
    var exception = assertThrows(AppException.class, () -> userService.createUser(userRequest));

    Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1001);
    Assertions.assertThat(exception.getErrorCode().getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
    Assertions.assertThat(exception.getErrorCode().getMessage()).isEqualTo("User already existed");
  }

  @Test
  @WithMockUser(username = "cute1805")
  void getMyInfo_Success() {
    Mockito.when(userRepository.getUserByUsername(ArgumentMatchers.anyString()))
        .thenReturn(Optional.of(user));
    var response = userService.getMyInfo();
    Assertions.assertThat(response.getUsername()).isEqualTo("cute1805");
    Assertions.assertThat(response.getFirstname()).isEqualTo("quoc");
  }

  @Test
  @WithMockUser(username = "cute")
  void getMyInfo_UserNotFound_Fail() {

    var exception = assertThrows(AppException.class, () -> userService.getMyInfo());

    Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);
    Assertions.assertThat(exception.getErrorCode().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @WithMockUser(username = "cute1805", roles = "ADMIN")
  void getUserById_Success() {
    String id = "04bf85c2-c9e7-48eb-a66a-e7b84f9fb4da";

    user.setRoles(new HashSet<>(Set.of(role)));
    Mockito.when(userRepository.findById(ArgumentMatchers.anyString()))
        .thenReturn(Optional.of(user));
    var response = userService.getUserById(id);

    Assertions.assertThat(response.getUsername()).isEqualTo("cute1805");
    Assertions.assertThat(response.getFirstname()).isEqualTo("quoc");
  }

  @Test
  void getUserById_UserNotFound_Fail() {
    String id = "04bf85c2-c9e7-48eb-a66a";
    var exception = assertThrows(AppException.class, () -> userService.getUserById(id));

    Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1002);
    Assertions.assertThat(exception.getErrorCode().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void GetAllUsers_Success() {
    List<User> listUsers = new ArrayList<>();
    listUsers.add(user);

    List<UserRespone> listUsersRespone = new ArrayList<>();
    listUsersRespone.add(userRespone);

    Mockito.when(userRepository.findAll()).thenReturn(listUsers);
    var response = userService.getAllUsers();

    Assertions.assertThat(response).isEqualTo(listUsersRespone);
  }
}
