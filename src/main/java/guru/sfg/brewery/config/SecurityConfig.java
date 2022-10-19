package guru.sfg.brewery.config;

import org.springframework.cglib.proxy.NoOp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        ((HttpSecurity)((HttpSecurity)((ExpressionUrlAuthorizationConfigurer
                .AuthorizedUrl)http
                .authorizeRequests(authorize  -> {
                    authorize
                            .antMatchers("/", "/webjars/**","/login", "/resources/**").permitAll()
                            .antMatchers("/beers/find", "/beers*").permitAll()
                            .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                            .antMatchers(HttpMethod.GET, "/api/v1/beerupc/{upc}").permitAll();
                })
                .authorizeRequests().anyRequest()).authenticated().and()).formLogin().and()).httpBasic();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("spring")
                .password("{bcrypt}$2a$10$1tOrsI0E/0b.d.POEPxuke.58JQjz5tSAAt0hMFMfQUsZL9thM8o2") // guru
                .roles("ADMIN")
                .and()
                .withUser("user")
                .password("{sha256}138e2cacf1ed106e5e672e5b8b2beacf167fb20a6c53261f407ca98e18bd75d292a7ee769220549d") //password
                .roles("USER")
                .and()
                .withUser("scott")
                .password("{ldap}{SSHA}QHhVJ9npPLtp/i2YCgJxW99zwuWmWTjxr5NHdg==") //tiger
                .roles("CUSTOMER");

    }
}
