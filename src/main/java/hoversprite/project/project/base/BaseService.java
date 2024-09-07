package hoversprite.project.project.base;

import java.util.List;

public interface BaseService<T, ID, ROLE> {
    T save(ID userId, T dto, ROLE role); // Save or create a new entity
    T update(ID userId, ID id, T dto, ROLE role); // Update an existing entity
    T findById(ID id); // Find an entity by ID
    List<T> findAll(); // Find all entities
    void deleteById(ID id); // Delete an entity by ID
}