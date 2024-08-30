package com.example.project.base;

import java.util.List;

public interface BaseService<T, ID> {
    T save(T dto); // Save or create a new entity
    T update(T dto); // Update an existing entity
    T findById(ID id); // Find an entity by ID
    List<T> findAll(); // Find all entities
    void deleteById(ID id); // Delete an entity by ID

    // Separate validation methods
    void validateForSave(T dto);
    void validateForUpdate(T dto);
}