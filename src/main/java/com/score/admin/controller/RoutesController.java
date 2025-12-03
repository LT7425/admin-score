package com.score.admin.controller;

import com.score.admin.common.ApiResponse;
import com.score.admin.dto.RouteMeta;
import com.score.admin.dto.RouteNode;
import com.score.admin.domain.Page;
import com.score.admin.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RoutesController {
    private final UserService userService;

    public RoutesController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get-async-routes")
    public ApiResponse<List<RouteNode>> getAsyncRoutes(Authentication auth) {
        String username = auth.getName();
        List<String> roleCodes = userService.getUserRoleCodes(username);
        List<Page> pages = userService.getUserPages(username);
        List<RouteNode> routes = new ArrayList<>();
        List<String> userRoleLabels = roleCodes.stream().map(String::toLowerCase).collect(Collectors.toList());

        for (Page p : pages) {
            RouteMeta meta = new RouteMeta();
            meta.setTitle(p.getTitle());
            meta.setRoles(userRoleLabels);
            meta.setAuths(List.of());
            meta.setKeepAlive(true);

            RouteNode node = new RouteNode();
            node.setPath(p.getPath());
            node.setName(p.getName());
            node.setComponent(p.getComponent());
            node.setMeta(meta);
            routes.add(node);
        }

        return ApiResponse.ok(routes);
    }
}

