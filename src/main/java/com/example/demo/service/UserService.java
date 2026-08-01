package com.example.demo.service;

import com.example.demo.dto.request.UserRequest;
import com.example.demo.dto.respone.UserRespone;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final RoleRepository roleRepository;

  public UserRespone createUser(UserRequest request) {

    User user = userMapper.toUser(request);

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    var roles = roleRepository.findAllById(request.getRoles());

    user.setRoles(new HashSet<>(roles));

    return userMapper.toUserResponse(userRepository.save(user));
  }

  @PostAuthorize("returnObject.username == authentication.name or hasRole('ADMIN')")
  public UserRespone getUserById(String id) {
    User user =
        userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    return userMapper.toUserResponse(user);
  }

  @PreAuthorize("hasRole('ADMIN')")
  public List<UserRespone> getAllUsers() {
    var listUser = userRepository.findAll();
    return listUser.stream().map(userMapper::toUserResponse).toList();
  }

  public UserRespone getMyInfo() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null) {
      // Ném ra lỗi chưa xác thực (Bạn có thể dùng ErrorCode của riêng bạn)
      throw new AppException(ErrorCode.USER_NOT_AUTHENTED);
    }

    User user =
        userRepository
            .getUserByUsername(authentication.getName())
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    return userMapper.toUserResponse(user);
  }

  public UserRespone updateUser(String id, UserRequest request) {
    User user =
        userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    // Cập nhật các thông tin từ request vào user entity trước
    userMapper.updateUser(user, request);

    var roles = roleRepository.findAllById(request.getRoles());

    user.setRoles(new HashSet<>(roles));

    // Lấy password MỚI (sau khi đã map) để mã hóa, rồi set lại vào user
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return userMapper.toUserResponse(userRepository.save(user));
  }

  public void deleteUser(String id) {
    if (!userRepository.existsById(id)) {
      throw new AppException(ErrorCode.USER_NOT_FOUND);
    }
    userRepository.deleteById(id);
  }
}
