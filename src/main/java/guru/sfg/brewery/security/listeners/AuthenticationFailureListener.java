package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginFailureRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class AuthenticationFailureListener {

    public static final int LOCKING_RETRIES = 3;

    private final LoginFailureRepository loginFailureRepository;

    private final UserRepository userRepository;

    @EventListener
    public void listen(AuthenticationFailureBadCredentialsEvent event) {
        if(event.getSource() instanceof UsernamePasswordAuthenticationToken){
            LoginFailure.LoginFailureBuilder loginFailureBuilder = LoginFailure.builder();
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            if(token.getPrincipal() instanceof String){
                String userName = (String) token.getPrincipal();
                loginFailureBuilder.userName(userName);
                userRepository.findByUsername(userName)
                        .ifPresent(loginFailureBuilder::user);
            }
            if(token.getDetails() instanceof WebAuthenticationDetails){
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                loginFailureBuilder.sourceIp(details.getRemoteAddress());
            }
            LoginFailure loginFailure = loginFailureRepository.save(loginFailureBuilder.build());
            log.debug("LoginFailure saved: {}", loginFailure);

            Optional.ofNullable(loginFailure.getUser())
                    .ifPresent(this::lockUserAccount);
        }
    }

    private void lockUserAccount(User user){
        List<LoginFailure> loginFailures = loginFailureRepository.findAllByUserAndCreatedDateAfter(user,
                Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        if(loginFailures.size() >= LOCKING_RETRIES){
            log.info("locking user account: {} ...", user);
            user.setAccountNonLocked(false);
            userRepository.save(user);
        }
    }
}
