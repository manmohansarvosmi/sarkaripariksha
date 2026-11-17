package com.helixion.sarkariPariksha.Repository;

import com.helixion.sarkariPariksha.Entity.PackMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackMasterRepository extends JpaRepository<PackMaster, Long> {

    List<PackMaster> findByActiveTrue();

    Optional<PackMaster> findByIdAndActiveTrue(Long id);

    Optional<PackMaster> findByPackCodeAndActiveTrue(String packCode);
}

