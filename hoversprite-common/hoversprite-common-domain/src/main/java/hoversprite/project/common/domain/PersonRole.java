package hoversprite.project.common.domain;

public enum PersonRole {
    ADMIN, FARMER, RECEPTIONIST, SPRAYER;

    public boolean hasRole(PersonRole role) {
        if (this == ADMIN) {
            return true; // ADMIN has all roles
        }
        return this == role;
    }
}