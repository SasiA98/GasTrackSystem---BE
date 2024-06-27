package com.teoresi.staff.services;

import com.teoresi.staff.entities.ResourceSalaryDetails;
import com.teoresi.staff.repositories.ResourceSalaryDetiailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceSalaryDetailsService {

    private final ResourceSalaryDetiailsRepository resourceSalaryDetiailsRepository;

    public List<ResourceSalaryDetails> getAllByResourceId(Long id) {
        return resourceSalaryDetiailsRepository.findAllByResourceId(id);
    }
}
