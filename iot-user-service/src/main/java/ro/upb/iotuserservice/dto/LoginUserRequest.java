package ro.upb.iotuserservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoginUserRequest {
    @NotNull
    private String email;
    @NotNull
    private String password;
}
