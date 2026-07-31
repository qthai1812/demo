package com.example.demo.controller;


import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.respone.UserRespone;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class UserControllerIntergrationTest {


    @Container
    static  final MySQLContainer<?> MY_SQL_CONTAINER =  new MySQLContainer<>("mysql:8.0.33");


    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username",MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password",MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driverClassName",()->"com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto",()->"update");




    }



    @Autowired
    MockMvc mockMvc;
    UserService userService;
    UserRequest userRequest;
    UserRespone userRespone;

    @BeforeEach
    void initData(){
        var dob = LocalDate.of(2005,8,1);
        userRequest =  UserRequest.builder()
                .username("cute1805")
                .password("adn132")
                .firstname("quoc")
                .lastname("thai")
                .dob(dob)
                .roles(new ArrayList<>())
                .build();
        userRespone = UserRespone.builder()
                .id("240c95b7-e5d4-4701-9b5f-008440b3a45d")
                .username("cute1805")
                .firstname("quoc")
                .lastname("thai")
                .dob(dob)
                .build();

    }

    @Test
    void createUser_Valid_Acess() throws Exception {
        // Chuan bi data request
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writer().writeValueAsString(userRequest);



        // Dung MockMvc de ban request Post vao API /users
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(request))

                // Kiem tra ket qua tra ve

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1000))
                .andExpect(MockMvcResultMatchers.jsonPath("result.username").value("cute1805"));

    }

    @Test
    void createUser_Invalid_Password() throws Exception {
        userRequest.setPassword("adn");
        // Chuan bi du lieu de truyen vao request
        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writer().writeValueAsString(userRequest);



        // Dung MockMvc de ban request vao API /users
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                // Kiem tra ket qua tra ve
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Invalid validation key 6"));

    }



}
