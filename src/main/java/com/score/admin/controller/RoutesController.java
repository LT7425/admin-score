package com.score.admin.controller;

import com.score.admin.common.ApiResponse;
import com.score.admin.dto.RouteMeta;
import com.score.admin.dto.RouteNode;
import com.score.admin.domain.Page;
import com.score.admin.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RoutesController {
    private final UserService userService;

    public RoutesController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get-async-routes")
    public ApiResponse<List<RouteNode>> getAsyncRoutes(Authentication auth) {
        String email = auth.getName();
        List<String> roleCodes = userService.getUserRoleCodes(email);
        List<Page> pages = userService.getUserPages(email);
        List<String> userRoleLabels = roleCodes.stream().map(String::toLowerCase).collect(Collectors.toList());

        // 过滤出状态为1的页面
        List<Page> activePages = pages.stream()
                .filter(p -> p.getStatus() != null && p.getStatus() == 1)
                .collect(Collectors.toList());

        // 构建路由树
        List<RouteNode> routes = buildRouteTree(activePages, userRoleLabels);

        return ApiResponse.ok(routes);
    }

    private List<RouteNode> buildRouteTree(List<Page> pages, List<String> userRoleLabels) {
        // 创建一个Map来存储所有节点，key为页面ID
        Map<Long, RouteNode> nodeMap = new HashMap<>();

        // 先创建所有节点
        for (Page p : pages) {
            RouteNode node = new RouteNode();
            node.setPath(p.getPath());
            node.setName(p.getName());
            node.setComponent(p.getComponent());
            node.setMeta(getRouteMeta(p, userRoleLabels));
            node.setRank(p.getRank());
            node.setRedirect(p.getRedirect());
            node.setChildren(new ArrayList<>()); // 初始化children为空列表

            nodeMap.put(p.getId(), node);
        }

        // 按parentId分组，找出所有的子节点
        Map<Long, List<RouteNode>> childrenGroup = new HashMap<>();
        for (Page p : pages) {
            if (p.getParentId() != null) {
                // 确保目标父节点存在于nodeMap中
                if (nodeMap.containsKey(p.getParentId())) {
                    childrenGroup.computeIfAbsent(p.getParentId(), k -> new ArrayList<>())
                            .add(nodeMap.get(p.getId()));
                }
            }
        }

        // 为每个父节点设置其子节点，并对子节点按rank排序
        for (Map.Entry<Long, List<RouteNode>> entry : childrenGroup.entrySet()) {
            Long parentId = entry.getKey();
            List<RouteNode> children = entry.getValue();

            RouteNode parentNode = nodeMap.get(parentId);
            if (parentNode != null) {
                // 按rank排序子节点，但不考虑rank相同时的特殊处理
                children.sort(this::compareRanks);
                parentNode.setChildren(children);
            }
        }

        // 找出所有根节点（没有父节点的节点）
        List<RouteNode> rootNodes = new ArrayList<>();
        for (Page p : pages) {
            if (p.getParentId() == null) {
                rootNodes.add(nodeMap.get(p.getId()));
            }
        }

        // 按rank对根节点排序
        rootNodes.sort(this::compareRanks);

        return rootNodes;
    }

    private int compareRanks(RouteNode a, RouteNode b) {
        String rankA = a.getRank();
        String rankB = b.getRank();

        if (rankA != null && rankB != null) {
            try {
                // 先尝试转换为整数比较
                Integer intRankA = Integer.valueOf(rankA);
                Integer intRankB = Integer.valueOf(rankB);
                return intRankA.compareTo(intRankB);
            } catch (NumberFormatException e) {
                return rankA.compareTo(rankB);
            }
        }

        // 如果其中一个为null，则null排在后面
        if (rankA == null) return 1;
        return -1;
    }

    private static RouteMeta getRouteMeta(Page p, List<String> userRoleLabels) {
        RouteMeta meta = new RouteMeta();
        meta.setTitle(p.getTitle());
        meta.setIcon(p.getIcon());
        meta.setRoles(userRoleLabels);
        meta.setAuths(List.of());
        meta.setRank(p.getRank());
        meta.setParentId(p.getParentId());
        meta.setStatus(p.getStatus());
        meta.setFrameSrc(p.getFrameSrc());
        meta.setRedirect(p.getRedirect());
        meta.setKeepAlive(p.getKeepAlive());
        meta.setShowLink(p.getShowLink());
        return meta;
    }
}