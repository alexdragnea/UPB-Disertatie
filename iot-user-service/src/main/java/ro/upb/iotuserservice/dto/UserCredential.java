package ro.upb.iotuserservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UserCredential {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
}
