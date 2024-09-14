package hoversprite.project.partner;

import com.querydsl.jpa.impl.JPAQuery;
import hoversprite.project.common.domain.PersonRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
class PersonRepositoryCustomImpl implements PersonRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Person> getUsersById(List<Long> ids) {

        return new JPAQuery<Person>(em)
                .from(QPerson.person)
                .where(QPerson.person.id.in(ids))
                .fetch();
    }

    @Override
    public List<Person> getSprayersThatNotExcluded(List<Long> excludedIds) {
        return new JPAQuery<Person>(em)
                .from(QPerson.person)
                .where(QPerson.person.id.notIn(excludedIds)
                        .and(QPerson.person.role.eq(PersonRole.SPRAYER)))
                .fetch();
    }

    @Override
    public boolean existsByEmail(String emailAddress) {
        Person person = new JPAQuery<Person>(em)
                .from(QPerson.person)
                .where(QPerson.person.emailAddress.like(emailAddress))
                .fetchFirst();
        return person != null;
    }

}
