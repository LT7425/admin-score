package com.score.admin.dto;

import java.util.List;

public class RouteMeta {
    private String title;
    private String icon;
    private List<String> roles;
    private List<String> auths;
    private String rank;
    private Boolean keepAlive;
    private Boolean showLink;
    private Long parentId;
    private Integer status;
    private String frameSrc;
    private String redirect;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public List<String> getAuths() { return auths; }
    public void setAuths(List<String> auths) { this.auths = auths; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }

    public Boolean getKeepAlive() { return keepAlive; }
    public void setKeepAlive(Boolean keepAlive) { this.keepAlive = keepAlive; }

    public Boolean getShowLink() { return showLink; }
    public void setShowLink(Boolean showLink) { this.showLink = showLink; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getFrameSrc() { return frameSrc; }
    public void setFrameSrc(String frameSrc) { this.frameSrc = frameSrc; }

    public String getRedirect() { return redirect; }
    public void setRedirect(String redirect) { this.redirect = redirect; }
}
