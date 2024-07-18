package com.client.staff.libs.web.services;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SearchService<Model, SearchCriteria> {
    JpaSpecificationExecutor<Model> getRepository();

    default Page<Model> search(SearchCriteria searchCriteria, Pageable pageable) {
        Specification<Model> searchSpecification = convertSearchCriteriaToSpecification(searchCriteria);
        return getRepository().findAll(searchSpecification, pageable);
    }

    Specification<Model> convertSearchCriteriaToSpecification(SearchCriteria paramSearchCriteria);

    default Specification<Model> setSpecificationFromCriteria(List<Specification<Model>> criteriDiRicerca) {
        Specification<Model> specificheDiRicerca = null;
        for (Specification<Model> specificaDiRicerca : criteriDiRicerca) {
            if (specificheDiRicerca == null) {
                specificheDiRicerca = Specification.where(specificaDiRicerca);
                continue;
            }
            specificheDiRicerca = specificheDiRicerca.and(specificaDiRicerca);
        }
        return specificheDiRicerca;
    }
}
