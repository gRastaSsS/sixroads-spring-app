package com.fluffytiger.earlygamewebapp.repositories;

import com.fluffytiger.earlygamewebapp.model.JarMeta;
import com.fluffytiger.earlygamewebapp.model.OS;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JarMetaRepository extends CrudRepository<JarMeta, Long> {
    Iterable<JarMeta> findAllByVersion(String version);

    @Query(value = "SELECT MAX(version) FROM jars", nativeQuery = true)
    Optional<String> findLatestVersion();
}
