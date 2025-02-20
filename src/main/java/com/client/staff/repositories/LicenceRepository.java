package com.client.staff.repositories;

import com.client.staff.entities.Licence;
import com.client.staff.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenceRepository extends CrudRepository<Licence, Long> {

}
