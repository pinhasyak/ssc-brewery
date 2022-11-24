package guru.sfg.brewery.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class RestHeaderAuthFilter extends AbstractAuthenticationProcessingFilter {

    public RestHeaderAuthFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Request is to process authentication");
        }
        try {
            Authentication authResult = this.attemptAuthentication(request, response);
            if(authResult != null) {
                this.successfulAuthentication(request, response, chain, authResult);
            } else {
                chain.doFilter(req, res);
            }
        }catch (AuthenticationException authenticationException){
            log.error("Authentication Failed", authenticationException);
            unsuccessfulAuthentication(request, response, authenticationException);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        if (this.log.isDebugEnabled()) {
            this.log.debug("Authentication request failed: " + failed.toString(), failed);
            this.log.debug("Updated SecurityContextHolder to contain null Authentication");
        }
        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        String userName = getUserName(httpServletRequest);
        String userPass = getUserPass(httpServletRequest);
        log.debug("Authentication user: {}", userName);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userName, userPass);
        if(!StringUtils.isEmpty(userName)) {
            return this.getAuthenticationManager().authenticate(authenticationToken);
        }else {
            return null;
        }
    }

    private String getUserPass(HttpServletRequest httpServletRequest) {
        return Optional.ofNullable(httpServletRequest.getHeader("Api-Secret")).orElse("");
    }

    private String getUserName(HttpServletRequest httpServletRequest) {
        return Optional.ofNullable(httpServletRequest.getHeader("Api-Key")).orElse("");
    }
}
