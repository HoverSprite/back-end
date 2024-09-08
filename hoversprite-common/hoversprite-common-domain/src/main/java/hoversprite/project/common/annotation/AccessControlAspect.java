package hoversprite.project.common.annotation;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AccessControlAspect {

    @Autowired
    private SecureContext secureContext;

    @Before("@annotation(RestrictAccess)")
    public void checkAccess() {
        if (!secureContext.isAccessAllowed()) {
            throw new SecurityException("Access denied: This method can only be called by AutomationSprayerAssignmentTasks.");
        }
    }
}