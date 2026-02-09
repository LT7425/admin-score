package com.score.admin.dto;

import java.util.List;

public class RouteNode {
    private String path;
    private String name;
    private String component;
    private RouteMeta meta;
    private List<RouteNode> children;
    private String rank;
    private String redirect;

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public RouteMeta getMeta() { return meta; }
    public void setMeta(RouteMeta meta) { this.meta = meta; }
    public List<RouteNode> getChildren() { return children; }
    public void setChildren(List<RouteNode> children) { this.children = children; }
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
    public String getRedirect() { return redirect; }
    public void setRedirect(String redirect) { this.redirect = redirect; }
}