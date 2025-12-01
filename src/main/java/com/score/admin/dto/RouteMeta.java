package com.score.admin.dto;

import java.util.List;

public class RouteMeta {
    private String title;
    private String icon;
    private List<String> roles;
    private List<String> auths;
    private Integer rank;
    private Boolean keepAlive;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public List<String> getAuths() { return auths; }
    public void setAuths(List<String> auths) { this.auths = auths; }
    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }
    public Boolean getKeepAlive() { return keepAlive; }
    public void setKeepAlive(Boolean keepAlive) { this.keepAlive = keepAlive; }
}

