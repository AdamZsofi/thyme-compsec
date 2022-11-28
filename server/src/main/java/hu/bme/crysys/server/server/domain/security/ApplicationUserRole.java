package hu.bme.crysys.server.server.domain.security;

import com.google.common.collect.Sets;

import java.util.Set;

import static hu.bme.crysys.server.server.domain.security.ApplicationUserPermission.*;

public enum ApplicationUserRole {
    USER(Sets.newHashSet(CAFF_FILE_READ, CAFF_COMMENT_WRITE,
            CAFF_COMMENT_READ, CAFF_COMMENT_WRITE,
            USER_DATA_READ)),
    ADMIN(Sets.newHashSet(CAFF_FILE_READ, CAFF_FILE_WRITE,
            CAFF_COMMENT_READ, CAFF_COMMENT_WRITE,
            USER_DATA_READ, USER_DATA_WRITE));

    private final Set<ApplicationUserPermission> permissionSet;

    ApplicationUserRole(Set<ApplicationUserPermission> permissionSet) {
        this.permissionSet = permissionSet;
    }

    public Set<ApplicationUserPermission> getPermissionSet() {
        return permissionSet;
    }
}
