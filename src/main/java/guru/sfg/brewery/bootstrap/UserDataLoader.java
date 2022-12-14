package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.*;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.RoleRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(authorityRepository.count() == 0){
            loadSecurityData();
        }

    }

    private void loadSecurityData() {
//      Beer auths
        Authority createBeerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BEER_CREATE).build());
        Authority updateBeerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BEER_UPDATE).build());
        Authority deleteBeerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BEER_DELETE).build());
        Authority readBeerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BEER_READ).build());

//      Brewery auths
        Authority createBreweryAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BREWERY_CREATE).build());
        Authority updateBreweryAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BREWERY_UPDATE).build());
        Authority deleteBreweryAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BREWERY_DELETE).build());
        Authority readBreweryAuthority = authorityRepository.save(Authority.builder().permission(Authorities.BREWERY_READ).build());

//      Beer auths
        Authority createCustomerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.CUSTOMER_CREATE).build());
        Authority updateCustomerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.CUSTOMER_UPDATE).build());
        Authority deleteCustomerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.CUSTOMER_DELETE).build());
        Authority readCustomerAuthority = authorityRepository.save(Authority.builder().permission(Authorities.CUSTOMER_READ).build());

        Role adminRole = roleRepository.save(Role.builder().name(Roles.ROLE_ADMIN).build());
        Role customerRole = roleRepository.save(Role.builder().name(Roles.ROLE_CUSTOMER).build());
        Role userRole = roleRepository.save(Role.builder().name(Roles.ROLE_USER).build());

        adminRole.setAuthorities(new HashSet<>(Set.of(
                createBeerAuthority, updateBeerAuthority, deleteBeerAuthority, readBeerAuthority,
                createBreweryAuthority, updateBreweryAuthority, deleteBreweryAuthority, readBreweryAuthority,
                createCustomerAuthority, updateCustomerAuthority, deleteCustomerAuthority, readCustomerAuthority
        )));

        customerRole.setAuthorities(new HashSet<>(Set.of(
                readBeerAuthority,
                readCustomerAuthority,
                readBreweryAuthority
        )));

        userRole.setAuthorities(new HashSet<>(Set.of(readBeerAuthority)));

        roleRepository.saveAll(Arrays.asList(adminRole, customerRole, userRole));

        userRepository.save(User.builder()
                .username("spring")
                .password(passwordEncoder.encode("guru"))
                .role(adminRole)
                .build());

        userRepository.save(User.builder()
                .username("user")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .build());

        userRepository.save(User.builder()
                .username("scott")
                .password(passwordEncoder.encode("tiger"))
                .role(customerRole)
                .build());

        userRepository.findAll().forEach(user -> {
            log.info("user data: {}", user);
            user.getAuthorities().forEach(authority -> log.info("user authority: {}", authority.getPermission()));
        });
    }
}
