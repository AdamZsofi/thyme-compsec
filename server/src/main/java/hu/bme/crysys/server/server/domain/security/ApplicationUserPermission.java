package hu.bme.crysys.server.server.domain.security;

public enum ApplicationUserPermission {
    CAFF_FILE_READ("caff_file:read"),
    CAFF_FILE_WRITE("caff_file:write"),
    CAFF_COMMENT_READ("caff_comment:read"),
    CAFF_COMMENT_WRITE("caff_comment:write"),
    USER_DATA_READ("user_data:read"),
    USER_DATA_WRITE("user_data:write");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
