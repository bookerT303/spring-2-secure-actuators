# Securing the Spring Boot 2 Actuators

## Create a codebase (if needed)
Start with the spring.io initializr to create a starting codebase that has:
- Web
- Security
- Actuators

## Check your dependencies

Then you need to edit the build.gradle to add:
```
    compile('org.springframework.boot:spring-boot-starter-actuator')
    compile('org.springframework.boot:spring-boot-actuator-autoconfigure')
    compile('org.springframework.boot:spring-boot-starter-security')
```

## Default actuators
At this point we will have only the `/actuator/info` and `/actuator/health'
endpoints.

## Control the actuators exposed
Need to set properties for exposing the other actuator endpoints:
```
management.endpoints.web.exposure.include=*
management.endpoints.web.exposure.exclude=beans
```

If concerned about inadvertently exposing an actuator because of the '*' then list the exact actuators to expose like
```
management.endpoints.web.exposure.include=info,health,metrics,env,loggers,mappings
```
For a [list of actuator endpoints](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html)

Its still not ready....

## Restore the health actuator details
if we want the `/actuator/health` details, set:
```
management.endpoint.health.show-details=when_authorized
```

## Secure the actuators
To secure the actuators we start with these properties:
```
spring.security.user.name=user
spring.security.user.password=password
spring.security.user.roles=ACTUATOR
```

AND we need this configuration:
```java
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
```

We have **secured** and **control** the actuator endpoints.
