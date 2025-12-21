package com.project.neurovice_backend.controller;

import com.project.neurovice_backend.dto.CreateChildRequest;
import com.project.neurovice_backend.dto.CreateChildResponse;
import com.project.neurovice_backend.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
