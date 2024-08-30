package com.example.project.base;

import com.example.project.validator.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Validator;

import java.util.List;

public abstract class AbstractService<T, ID> implements BaseService<T, ID> {

    @Autowired
    private ValidationUtils validationUtils;

    @Override
    public T save(T dto) {
        validateForSave(validationUtils, dto);
        validationUtils.throwErrorIfHasMessages();
        return executeSave(dto);
    }

    @Override
    public T update(T dto) {
        validateForUpdate(validationUtils, dto);
        validationUtils.throwErrorIfHasMessages();
        return executeUpdate(dto);
    }

    @Override
    public abstract T findById(ID id);

    @Override
    public abstract List<T> findAll();

    @Override
    public abstract void deleteById(ID id);

    protected abstract void validateForSave(ValidationUtils validator, T dto);

    protected abstract void validateForUpdate(ValidationUtils validator, T dto);

    protected abstract T executeSave(T dto); // Concrete implementation of save

    protected abstract T executeUpdate(T dto); // Concrete implementation of update
}