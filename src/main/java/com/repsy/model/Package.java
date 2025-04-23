package com.repsy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(
        name = "packages",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "version", "storageType"})
)
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Package name cannot be null")
    @Size(min = 1, max = 255, message = "Package name must be between 1 and 255 characters")
    private String name;

    @NotNull(message = "Package version cannot be null")
    @Size(min = 1, max = 50, message = "Package version must be between 1 and 50 characters")
    private String version;

    @Size(max = 255, message = "Author name must be less than 255 characters")
    private String author;

    private String storageType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long size;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "package_dependencies",
            joinColumns = @JoinColumn(name = "package_id")
    )
    private List<Dependency> dependencies;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
