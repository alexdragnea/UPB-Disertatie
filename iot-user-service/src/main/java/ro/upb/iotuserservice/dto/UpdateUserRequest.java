package ro.upb.iotuserservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
