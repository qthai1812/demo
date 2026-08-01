package com.example.demo.service;

import com.example.demo.dto.request.RoleRequest;
import com.example.demo.dto.respone.RoleRespone;
import com.example.demo.entity.Role;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.RoleMapper;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
  private final RoleRepository roleRepository;
  private final RoleMapper roleMapper;
  private final PermissionRepository permissionRepository;

  public RoleRespone create(RoleRequest roleRequest) {

    // 1. Map các trường cơ bản (name, description...) từ Request sang Entity.
    // Chú ý: Lúc này Role chưa có Permission vì đã bị ignore = true trong Mapper.
    Role role = roleMapper.toRole(roleRequest);

    // 2. Lấy danh sách Permission từ Database dựa vào danh sách ID/Name gửi lên từ Request
    // (Giả sử roleRequest.getPermissions() trả về danh sách các ID/Name của quyền)
    var permissions = permissionRepository.findAllById(roleRequest.getPermissions());

    // 3. Set danh sách quyền vừa tìm được vào cho Role
    role.setPermission(new HashSet<>(permissions));

    // 4. Lưu Role (lúc này đã chứa đầy đủ thông tin và danh sách quyền) và Map ra Response
    return roleMapper.toRoleRespone(roleRepository.save(role));
  }

  public List<RoleRespone> getAll() {
    var roles = roleRepository.findAll();
    return roles.stream().map(roleMapper::toRoleRespone).toList();
  }

  public void delete(String id) {
    roleRepository.deleteById(id);
  }

  public RoleRespone update(String id, RoleRequest request) {
    var role =
        roleRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    var permissions = permissionRepository.findAllById(request.getPermissions());
    role.setPermission(new HashSet<>(permissions));
    roleMapper.updateRole(role, request);

    return roleMapper.toRoleRespone(roleRepository.save(role));
  }
}
