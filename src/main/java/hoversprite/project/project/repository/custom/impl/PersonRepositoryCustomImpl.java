package hoversprite.project.project.repository.custom.impl;

import hoversprite.project.project.model.entity.Person;
import hoversprite.project.project.repository.custom.PersonRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class PersonRepositoryCustomImpl implements PersonRepositoryCustom {


    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Person> getUsersById(List<Integer> ids) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
        Root<Person> personRoot = cq.from(Person.class);
        cq.where(personRoot.get("id").in(ids));
        return em.createQuery(cq).getResultList();
    }
}
