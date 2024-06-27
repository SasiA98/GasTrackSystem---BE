package com.teoresi.staff.services;

import com.teoresi.staff.entities.ResourceHourlyCost;
import com.teoresi.staff.repositories.ResourceHourlyCostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceHourlyCostService {

    private final ResourceHourlyCostRepository resourceHourlyCostRepository;

    public List<ResourceHourlyCost> getAllByResourceId(Long id) {
        return resourceHourlyCostRepository.findAllByResourceId(id);
    }
}
