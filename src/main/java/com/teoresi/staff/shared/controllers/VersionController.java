package com.teoresi.staff.shared.controllers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Getter
@RestController
@RequiredArgsConstructor
@RequestMapping("version")
public class VersionController {

    @Value("${spring.application.version}")
    private String version;

    @GetMapping
    public String getVersion() {
        return this.version;
    }
}
