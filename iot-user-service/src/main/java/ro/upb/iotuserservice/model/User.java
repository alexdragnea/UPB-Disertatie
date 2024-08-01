package ro.upb.iotuserservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String role;
    private String[] authorities;
}
