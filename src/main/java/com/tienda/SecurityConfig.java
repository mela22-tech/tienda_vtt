package com.tienda;

import com.tienda.domain.Ruta;
import com.tienda.service.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
// Que esta pasando
    //A continuación van las rutas por las que los usuarios va a solicitar acceso o uso
    //Más tarde esto se borra...
    //Siguen las rutas que todos los usuario tienen accesso.

    /*   public static final String[] PUBLIC_URLS = {"/", "/index",
        "/fav/**", "/webjars/**", "/js/**", "/login", "/acceso_denegado", "/consultas/**"};

    //Siguen las rutas que el usuario rol USUARIO tiene accesso.
    public static final String[] USUARIO_URLS = {"/facturar/carrito"};

    //Siguen las rutas que el usuario rol VENDEDOR tiene accesso.
    public static final String[] VENDEDOR_URLS = {"/categoria/listado",
        "/producto/listado"};

    //Siguen las rutas que el usuario rol ADMIN tiene acceso.
    public static final String[] ADMIN_URLS = {"/categoria/**",
        "/producto/**", "/usuario/**", "/admin/**"};
     */
    //El siguiente método se utiliza para generar todo lo referente a autorización, procesos de login y logout
    //Mas tarde de cambio un poco...
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, @Lazy RutaService rutaService) throws Exception {
        //Se establecen cuales rutas se acceden desde qué roles...

        var rutas = rutaService.getRutas();

        http.authorizeHttpRequests(request -> {
            for (Ruta ruta : rutas) {
                if (ruta.isRequiereRol()) {
                    request.requestMatchers(ruta.getRuta()).hasRole(ruta.getRol().getRol());
                } else {
                    request.requestMatchers(ruta.getRuta()).permitAll();
                }
            }
            request.anyRequest().authenticated();
        });

        //Se establece el proceso para hcer "login"
        http.formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
        );

        //Se establece el proceso para hacer "logout"
        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        //Se establece el recurso para cuando hay alguna "excepción"
        http.exceptionHandling(ex -> ex.accessDeniedPage("/acceso_denegado"));

        //Se establece qué hacer con las sesiones concurrente de un mismo usuario
        http.sessionManagement(ses -> ses
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );
        return http.build();
    }

    //Se define el método para encriptar la clave
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Se generan usuario en memoria... en un rato esto de borra...
    /**
     * @param build * @Bean public UserDetailsService users(PasswordEncoder
     * passwordEncoder) { UserDetails user1 = User.builder().username("juan")
     * .password(passwordEncoder.encode("123")) .roles("ADMIN") .build();
     * UserDetails user2 = User.builder().username("rebeca")
     * .password(passwordEncoder.encode("456")) .roles("VENDEDOR") .build();
     * UserDetails user3 = User.builder().username("pedro")
     * .password(passwordEncoder.encode("789")) .roles("USUARIO") .build();
     * return new InMemoryUserDetailsManager(user1, user2, user3); }
     * @param passwordEncoder
     * @param userDetailsService
     * @throws java.lang.Exception
     */
    @Autowired
    public void configurerGlobal(AuthenticationManagerBuilder build,
            @Lazy PasswordEncoder passwordEncoder,
            @Lazy UserDetailsService userDetailsService) throws Exception {
        build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
