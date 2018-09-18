package io.pivotal.cnde.actuator;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;

@Configuration
@Order(1)
public class ActuatorWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

    private final String roles;

    public ActuatorWebSecurityConfigurationAdapter(@Value("${spring.security.user.roles:ENDPOINT_ADMIN}") String roles) {
        this.roles = roles;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .requestMatchers(EndpointRequest.to("info", "health")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(roles)
            .and()
                .httpBasic()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
            .and()
                .csrf()
                .disable();
    }
}
