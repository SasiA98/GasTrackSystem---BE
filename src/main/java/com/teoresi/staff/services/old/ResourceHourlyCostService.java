package com.teoresi.staff.services.old;

import com.teoresi.staff.entities.old.ResourceHourlyCost;
import com.teoresi.staff.repositories.old.customs.ResourceHourlyCostRepository;
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
