package hoversprite.project.auth.onetimecode;

import com.querydsl.jpa.impl.JPAQuery;
import hoversprite.project.auth.onetimecode.QOneTimeCode;
import hoversprite.project.common.domain.PersonRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class OneTimeCodeRepositoryCustomImpl implements OneTimeCodeRepositoryCustom {

    @PersistenceContext
    private EntityManager em;


    @Override
    public boolean otpExistedForUser(Long userId) {
        OneTimeCode oneTimeCode = new JPAQuery<OneTimeCode>(em)
                .from(QOneTimeCode.oneTimeCode)
                .where(QOneTimeCode.oneTimeCode.user.eq(userId))
                .fetchFirst();

        return oneTimeCode != null;
    }

    @Override
    public Optional<OneTimeCode> findByUserId(Long userId) {
        return Optional.ofNullable(new JPAQuery<OneTimeCode>(em)
                .from(QOneTimeCode.oneTimeCode)
                .where(QOneTimeCode.oneTimeCode.user.eq(userId))
                .fetchFirst());
    }
}
