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

    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static final String ROLE_USER = "ROLE_USER";

    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String permission;

    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles;
}
