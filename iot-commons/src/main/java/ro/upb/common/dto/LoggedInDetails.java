package ro.upb.common.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LoggedInDetails {
    private String userId;
    private List<String> roles;
    private String email;
    private String firstName;
    private String lastName;

    @Override
    public String toString() {
        return "LoggedInDetails{" +
                "userId='" + userId + '\'' +
                ", roles=" + roles +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
