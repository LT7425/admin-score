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
     * 根据用户名查询关联的角色列表
     */
    public List<Role> getUserRoles(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return Optional.ofNullable(user.getRoleSet())
                .orElse(Collections.emptySet())
                .stream()
                .collect(Collectors.toList());
    }

    /**
     * 根据用户名查询角色编码列表
     */
    public List<String> getUserRoleCodes(String username) {
        return getUserRoles(username).stream()
                .map(Role::getCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户名查询关联的页面（替代原 PageRepository.findByUsername）
     */
    public List<Page> getUserPages(String username) {
        List<String> roleCodes = getUserRoleCodes(username);
        // 如果角色编码为空，返回空列表
        if (roleCodes.isEmpty()) {
            return Collections.emptyList();
        }
        // 通过角色编码查询页面（使用保留的 findByRoleCodes 方法）
        return pageRepository.findByRoleCodes(roleCodes);
    }

    /**
     * 获取用户权限标识
     */
    public List<String> getUserPermissions(String username) {
        List<String> roleCodes = getUserRoleCodes(username);

        // 管理员返回全量权限
        if (roleCodes.contains("ADMIN")) {
            return Collections.singletonList("*:*:*");
        }

        // 普通用户返回页面权限（示例：页面路径作为权限标识）
        return getUserPages(username).stream()
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
