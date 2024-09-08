package hoversprite.project.common.base;

import java.util.List;

public interface BaseService<T, R, ID, ROLE> {
    T save(ID userId, R dto, ROLE role); // Save or create a new entity
    T update(ID userId, ID id, R dto, ROLE role); // Update an existing entity
    T findById(ID id); // Find an entity by ID
    List<T> findAll(); // Find all entities
    void deleteById(ID id); // Delete an entity by ID
}