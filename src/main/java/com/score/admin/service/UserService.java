package com.score.admin.service;

import com.score.admin.domain.User;
import com.score.admin.domain.Role;
import com.score.admin.domain.Page;
import com.score.admin.repository.UserRepository;
import com.score.admin.repository.RoleRepository;
import com.score.admin.repository.PageRepository;
import com.score.admin.common.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PageRepository pageRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, 
                      RoleRepository roleRepository,
                      PageRepository pageRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.pageRepository = pageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public User ensureAdminUser() {
        return userRepository.findByUsername("admin").orElseGet(() -> {
            User u = new User();
            u.setUsername("admin");
            u.setPassword(passwordEncoder.encode("admin123"));
            u.setRoles("ADMIN");
            
            // 获取ADMIN角色并关联
            Role adminRole = roleRepository.findByCode("ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setCode("ADMIN");
                role.setName("管理员");
                role.setDescription("拥有全部页面权限");
                return roleRepository.save(role);
            });
            
            u.getRoleSet().add(adminRole);
            return userRepository.save(u);
        });
    }

    @Transactional
    public User register(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new BusinessException(1002, "用户名已存在");
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRoles("USER");
        
        // 获取USER角色并关联
        Role userRole = roleRepository.findByCode("USER").orElseGet(() -> {
            Role role = new Role();
            role.setCode("USER");
            role.setName("用户");
            role.setDescription("普通用户页面权限");
            return roleRepository.save(role);
        });
        
        u.getRoleSet().add(userRole);
        return userRepository.save(u);
    }

    /**
     * 根据用户名获取用户的所有角色
     */
    public List<Role> getUserRoles(String username) {
        return roleRepository.findByUsername(username);
    }

    /**
     * 根据用户名获取用户的所有页面权限
     */
    public List<Page> getUserPages(String username) {
        return pageRepository.findByUsername(username);
    }

    /**
     * 根据角色代码列表获取用户的页面权限
     */
    public List<Page> getPagesByRoleCodes(List<String> roleCodes) {
        return pageRepository.findByRoleCodes(roleCodes);
    }

    /**
     * 获取用户的角色代码列表
     */
    public List<String> getUserRoleCodes(String username) {
        return getUserRoles(username).stream()
                .map(Role::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的权限字符串列表（兼容旧格式）
     */
    public List<String> getUserPermissions(String username) {
        List<String> roleCodes = getUserRoleCodes(username);
        
        // 如果用户有ADMIN角色，返回全部权限
        if (roleCodes.contains("ADMIN")) {
            return Arrays.asList("*:*:*");
        }
        
        // 普通用户返回空列表或根据页面生成的权限
        return Arrays.asList();
    }
}
