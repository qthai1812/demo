package com.example.demo.service;

import com.example.demo.dto.request.PermissionRequest;
import com.example.demo.dto.respone.PermissionRespone;
import com.example.demo.entity.Permission;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.PermissionMapper;
import com.example.demo.repository.PermissionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {
  private final PermissionRepository permissionRepository;
  private final PermissionMapper permissionMapper;

  public PermissionRespone create(PermissionRequest request) {

    Permission permission = permissionMapper.toPermission(request);

    return permissionMapper.toPermissionRespone(permissionRepository.save(permission));
  }

  public PermissionRespone update(String id,PermissionRequest request) {

        permissionRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    return permissionMapper.toPermissionRespone(permissionRepository.save(permissionMapper.toPermission(request)));
  }

  public List<PermissionRespone> getAll() {
    var permissions = permissionRepository.findAll();
    return permissions.stream().map(permissionMapper::toPermissionRespone).toList();
  }

  public void delete(String name) {
    permissionRepository.deleteById(name);
  }
}
