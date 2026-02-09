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

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User ensureAdminUser() {
        String adminEmail = "admin@example.com";
        return userRepository.findByEmail(adminEmail).orElseGet(() -> {
            User u = new User();
            u.setUsername("admin");
            u.setEmail(adminEmail);
            u.setPassword(passwordEncoder.encode("admin123"));

            // 获取ADMIN角色并关联（保持不变）
            Role adminRole = roleRepository.findByCode("admin").orElseGet(() -> {
                Role role = new Role();

                role.setName("管理员");
                role.setDescription("拥有全部页面权限");
                return roleRepository.save(role);
            });

            u.getRoleSet().add(adminRole);
            return userRepository.save(u);
        });
    }

    @Transactional
    public User register(String email, String username, String rawPassword) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BusinessException(1002, "邮箱已被注册");
        }
        User u = new User();
        u.setEmail(email);
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));

        // 直接获取已存在的USER角色
        Role userRole = roleRepository.findByCode("user")
                .orElseThrow(() -> new BusinessException(1003, "系统未初始化，缺少默认用户角色"));

        u.getRoleSet().add(userRole);
        return userRepository.save(u);
    }

    /**
     * 根据用户邮箱查询关联的角色列表
     */
    public List<Role> getUserRoles(String email) {
        return userRepository.findRolesByEmail(email);
    }

    /**
     * 根据用户邮箱查询角色编码列表
     */
    public List<String> getUserRoleCodes(String email) {
        return getUserRoles(email).stream()
                .map(Role::getCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户邮箱查询关联的页面
     */
    public List<Page> getUserPages(String email) {
        List<String> roleCodes = getUserRoleCodes(email);
        // 如果角色编码为空，返回空列表
        if (roleCodes.isEmpty()) {
            return Collections.emptyList();
        }
        return pageRepository.findByRoleCodes(roleCodes);
    }

    /**
     * 获取用户权限标识
     */
    public List<String> getUserPermissions(String email) {
        List<String> roleCodes = getUserRoleCodes(email);

        // 管理员返回全量权限
        if (roleCodes.contains("admin")) {
            return Collections.singletonList("*:*:*");
        }

        // 普通用户返回页面权限（示例：页面路径作为权限标识）
        return getUserPages(email).stream()
                .map(Page::getPath)
                .filter(Objects::nonNull)
                .map(path -> "page:" + path + ":access")
                .collect(Collectors.toList());
    }

    // 自定义异常
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String username) {
            super("用户不存在: " + username);
        }
    }
}