package com.score.admin.dto;

import jakarta.validation.constraints.NotBlank;

public class GreetingRequest {
    @NotBlank(message = "{request.name.notBlank}")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
