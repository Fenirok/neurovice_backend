package com.project.neurovice_backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.neurovice_backend.user.dto.CreateChildRequest;
import com.project.neurovice_backend.user.dto.CreateChildResponse;
import com.project.neurovice_backend.user.service.ChildService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/child")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    @PostMapping("/create")
    public CreateChildResponse createChild(
            //@RequestParam Long parentId,
            @RequestBody CreateChildRequest request
    ) {
        return childService.createChild(request);
    }
}
