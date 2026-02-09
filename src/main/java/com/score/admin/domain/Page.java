package com.score.admin.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pages")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 128)
    private String name;

    @Column(name = "path", length = 255)
    private String path;

    @Column(name = "component", length = 255)
    private String component;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "rank", length = 255)
    private String rank;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "icon", length = 255)
    private String icon;

    @Column(name = "show_link")
    private Boolean showLink;

    @Column(name = "keep_alive")
    private Boolean keepAlive;

    @Column(name = "status")
    private Integer status;

    @Column(name = "frame_src", length = 255)
    private String frameSrc;

    @Column(name = "redirect", length = 255)
    private String redirect;

    @ManyToMany(mappedBy = "pages", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    // Constructors
    public Page() {}

    public Page(String name, String path, String component, String title) {
        this.name = name;
        this.path = path;
        this.component = component;
        this.title = title;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getShowLink() {
        return showLink;
    }

    public void setShowLink(Boolean showLink) {
        this.showLink = showLink;
    }

    public Boolean getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFrameSrc() {
        return frameSrc;
    }

    public void setFrameSrc(String frameSrc) {
        this.frameSrc = frameSrc;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }
}