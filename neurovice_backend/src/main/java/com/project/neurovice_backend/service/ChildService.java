package com.project.neurovice_backend.service;

import com.project.neurovice_backend.dto.CreateChildRequest;
import com.project.neurovice_backend.dto.CreateChildResponse;
import com.project.neurovice_backend.model.child;
import com.project.neurovice_backend.repository.childRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChildService {

    private final childRepository childRepository;

    public CreateChildResponse createChild(CreateChildRequest request) {

        if (childRepository.existsByAadharId(request.getAadharId())) {
            throw new RuntimeException("Child with this Aadhar already exists");
        }

        child child = new child();
        child.setParentId(1L);
        child.setChildName(request.getChildName());
        child.setDateOfBirth(request.getDateOfBirth());
        child.setGender(request.getGender());
        child.setAadharId(request.getAadharId());

        child saved = childRepository.save(child);

        return new CreateChildResponse(saved.getChildId());
    }
}
 