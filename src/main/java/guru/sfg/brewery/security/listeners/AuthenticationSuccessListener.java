package guru.sfg.brewery.security.listeners;

import guru.sfg.brewery.domain.security.LoginSuccess;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.LoginSuccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener {

    private final LoginSuccessRepository loginSuccessRepository;

    @EventListener
    public void listen(AuthenticationSuccessEvent event) {
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken) {

            LoginSuccess.LoginSuccessBuilder loginSuccessBuilder = LoginSuccess.builder();
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) event.getSource();

            if (token.getPrincipal() instanceof User) {
                User user = (User) token.getPrincipal();
                loginSuccessBuilder.user(user);
            }
            if (token.getDetails() instanceof WebAuthenticationDetails) {
                WebAuthenticationDetails details = (WebAuthenticationDetails) token.getDetails();
                loginSuccessBuilder.sourceIp(details.getRemoteAddress());
            }
            LoginSuccess loginSuccess = loginSuccessRepository.save(loginSuccessBuilder.build());
            log.debug("LoginSuccess saved: {}", loginSuccess);
        }
    }
}
