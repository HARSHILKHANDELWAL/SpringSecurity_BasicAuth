package config;

import CalculatorController.CalculatorController;
import handler.CustomAuthenticationFailureHandler;
import handler.MyAuthenticationEntryPoint;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MyAppSecurity {
    @Autowired()
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        System.out.println("in sec fil chainnnnnn");
        try {
            http.cors((cors) -> cors
                            .configurationSource(apiConfigurationSource())
                    )
                    .csrf().disable()
                    .authorizeHttpRequests((authorize) -> authorize
                            .requestMatchers("//user/logout").permitAll()
                            .requestMatchers("/Calculator").hasRole("ADMIN")
                            .requestMatchers("/CalculatorController").hasRole("ADMIN")
                            .requestMatchers("/user/profile").hasRole("USER")
                            .requestMatchers("/admin/profile").hasRole("ADMIN")
                            .requestMatchers("/CustomServlet").permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin(
                            (form) -> form
                                    .loginPage("/login")
//                                    .failureHandler(authenticationFailureHandler)
                                    .permitAll()

//                        Customizer.withDefaults()
                    )
                    .httpBasic(Customizer.withDefaults());



        }
        catch(Exception e){
            System.out.println("fdfdfdfd");
        }
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        System.out.println("invoked userdetails");
        User.UserBuilder users = User.withDefaultPasswordEncoder();
        UserDetails user = users
                .username("user")
                .password("password")
                .roles("USER")
                .build();
        UserDetails admin = users
                .username("admin")
                .password("password")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }


    @Bean
    CorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
//        configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Origin","Authorization", "Accept"));
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



}
