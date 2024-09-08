package hoversprite.project.common.annotation;

import org.springframework.stereotype.Component;

@Component
public class SecureContext {
    private static final ThreadLocal<Boolean> ALLOWED = ThreadLocal.withInitial(() -> false);

    public void allowAccess() {
        ALLOWED.set(true);
    }

    public void denyAccess() {
        ALLOWED.set(false);
    }

    public boolean isAccessAllowed() {
        return ALLOWED.get();
    }
}