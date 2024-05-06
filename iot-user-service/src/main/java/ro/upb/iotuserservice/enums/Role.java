package ro.upb.iotuserservice.enums;

import lombok.Getter;

import static ro.upb.iotuserservice.constants.AUTHORITIES.USER_AUTHORITIES;

@Getter
public enum Role {
    ROLE_USER(USER_AUTHORITIES);

    private final String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

}
