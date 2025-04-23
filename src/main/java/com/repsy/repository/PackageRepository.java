package com.repsy.repository;

import com.repsy.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    boolean existsByNameAndVersionAndStorageType(String name, String version, String storageType);
}
