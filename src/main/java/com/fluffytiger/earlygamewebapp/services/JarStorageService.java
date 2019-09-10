package com.fluffytiger.earlygamewebapp.services;

import com.fluffytiger.earlygamewebapp.exceptions.StorageException;
import com.fluffytiger.earlygamewebapp.model.JarMeta;
import com.fluffytiger.earlygamewebapp.model.OS;
import com.fluffytiger.earlygamewebapp.properties.FileStorageProperties;
import com.fluffytiger.earlygamewebapp.repositories.JarMetaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class JarStorageService {
    private final JarMetaRepository metas;
    private final Path location;

    public JarStorageService(JarMetaRepository metas, FileStorageProperties properties) throws IOException {
        this.metas = metas;
        this.location = Paths.get(properties.getDirectory()).toAbsolutePath().normalize();

        if (!Files.exists(this.location)) {
            Files.createDirectories(this.location);
        }
    }

    public Iterable<JarMeta> list() {
        return metas.findAll();
    }

    @Cacheable(value = "jars", key = "#version")
    public Iterable<JarMeta> findJarsWithVersion(String version) {
        return metas.findAllByVersion(version);
    }

    @Cacheable(value = "version")
    public Optional<String> findLatestVersion() {
        return metas.findLatestVersion();
    }

    @Caching(
            evict = { @CacheEvict(value = "jars", allEntries = true), @CacheEvict(value = "version", allEntries = true) }
    )
    public JarMeta store(MultipartFile file, OS osName, String version) {
        try {
            if (file.isEmpty()) throw new StorageException("Empty file");

            final String generatedPath;
            try (InputStream stream = file.getInputStream()) {
                final File newFile = File.createTempFile(osName+"-"+version+"-", ".jar", location.toFile());
                Files.copy(stream, newFile.toPath(), REPLACE_EXISTING);
                generatedPath = newFile.getName();
            }

            return this.metas.save(new JarMeta(generatedPath, file.getSize(), version, osName));

        } catch (IOException e) { throw new StorageException("Failed to store file", e); }
    }

    @Caching(
            evict = { @CacheEvict(value = "jars", allEntries = true), @CacheEvict(value = "version") }
    )
    public void delete(long id) throws IOException {
        final Optional<JarMeta> meta = this.metas.findById(id);
        final Path path = this.location.resolve(meta.orElseThrow(FileNotFoundException::new).getPath());
        Files.deleteIfExists(path);
        this.metas.deleteById(id);
    }

    public FileSystemResource getFile(long id) throws FileNotFoundException {
        final Optional<JarMeta> meta = this.metas.findById(id);
        final Path path = this.location.resolve(meta.orElseThrow(FileNotFoundException::new).getPath());
        return new FileSystemResource(path);
    }

    public Resource load(long id) throws FileNotFoundException {
        final Optional<JarMeta> meta = this.metas.findById(id);

        if (!meta.isPresent()) throw new FileNotFoundException("id=" + id);

        try {
            Path fileLocation = this.location.resolve(meta.get().getPath());
            Resource resource = new UrlResource(fileLocation.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("name=" + meta.get().getPath());
            }

        } catch (MalformedURLException e) {
            throw new FileNotFoundException("name=" + meta.get().getPath());
        }
    }
}
