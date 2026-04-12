package gestcode.server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Classe principal de configuració de seguretat (Spring Security).
 * Gestiona les rutes permeses, els filtres, i proveïdors d'autenticació.
 *
 * @author Jordi Verdalet Carrera
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Constructor del SecurityConfig.
     *
     * @param jwtFilter          Filtre de JWT custom.
     * @param userDetailsService Servei per obtenir detalls d'usuaris.
     * @author Jordi Verdalet Carrera
     */
    public SecurityConfig(JwtFilter jwtFilter, CustomUserDetailsService userDetailsService,
                          CustomAccessDeniedHandler accessDeniedHandler,
                          CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
        this.accessDeniedHandler = accessDeniedHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * Configura la cadena de filtres de seguretat.
     *
     * @param http L'objecte HttpSecurity a configurar.
     * @return La SecurityFilterChain inicialitzada.
     * @throws Exception Si falla la configuració.
     * @author Jordi Verdalet Carrera
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authRequest -> {
                    authRequest.requestMatchers("/api/auth/**").permitAll();
                    authRequest.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll();
                    authRequest.anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Proporciona la implementació base per proveir l'autenticació amb Data Access
     * Object (DAO) juntament amb l'encriptador de contrasenyes.
     *
     * @return El proveïdor d'autenticació.
     * @author Jordi Verdalet Carrera
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Exposa el gestor d'autenticació principal emprat per a logejar-se de
     * l'aplicació.
     *
     * @param config Configuració d'autenticació de Spring.
     * @return Gestor d'autenticació.
     * @throws Exception Si no es pot obtenir l'AuthenticationManager.
     * @author Jordi Verdalet Carrera
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defineix l'algoritme de codificació de password BCryptPasswordEncoder pel
     * sistema.
     *
     * @return El PasswordEncoder inicialitzat.
     * @author Jordi Verdalet Carrera
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
