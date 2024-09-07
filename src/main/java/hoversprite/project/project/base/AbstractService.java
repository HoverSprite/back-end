package hoversprite.project.project.base;

import hoversprite.project.project.validator.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class AbstractService<T, ID, ROLE> implements BaseService<T, ID, ROLE> {

    @Autowired
    private ValidationUtils validationUtils;

    @Override
    public T save(ID userId, T dto, ROLE role) {
        validateForSave(validationUtils, dto, role);
        validationUtils.throwErrorIfHasMessages();
        return executeSave(userId, dto, role);
    }

    @Override
    public T update(ID userId, ID id, T dto, ROLE role) {
        validateForUpdate(validationUtils, dto, role);
        validationUtils.throwErrorIfHasMessages();
        return executeUpdate(userId, id, dto, role);
    }

    @Override
    public abstract T findById(ID id);

    @Override
    public abstract List<T> findAll();

    @Override
    public abstract void deleteById(ID id);

    protected abstract void validateForSave(ValidationUtils validator, T dto, ROLE role);

    protected abstract void validateForUpdate(ValidationUtils validator, T dto, ROLE role);

    protected abstract T executeSave(ID userId, T dto, ROLE role); // Concrete implementation of save

    protected abstract T executeUpdate(ID userId, ID id, T dto, ROLE role); // Concrete implementation of update
}