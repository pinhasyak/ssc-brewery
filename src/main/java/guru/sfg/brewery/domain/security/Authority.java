package guru.sfg.brewery.domain.security;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Authority {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String CUSTOMER = "CUSTOMER";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String role;

    @ManyToMany(mappedBy = "authorities")
    private Set<User> users;
    public void st(){
        User.builder()
                .username("dkfj")
                .password("dkfjkd")
                .build();
    }
}
