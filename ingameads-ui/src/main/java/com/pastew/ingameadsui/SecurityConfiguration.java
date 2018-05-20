package com.pastew.ingameadsui;

import com.pastew.ingameadsui.User.SpringDataUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.AuthProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/", "images/**", "ingameads.css").permitAll()
            .antMatchers(HttpMethod.GET, "/mygames/**").hasRole(Roles.USER)
            .antMatchers(HttpMethod.GET, "/offers/**").hasRole(Roles.USER)
            //.antMatchers(HttpMethod.GET, "/images").hasRole(Roles.USER)
            //.antMatchers("/h2/**").hasRole("ADMIN").anyRequest()
        .and()
            .formLogin()
            .permitAll()
                .loginPage("/login")
        .and()
            .logout()
            .logoutSuccessUrl("/");

        // To enable h2 console.
        http.csrf().disable();
        http.headers().frameOptions().disable();
    }

    @Autowired
    public void configureJpaBasedUsers(AuthenticationManagerBuilder auth,
                                       SpringDataUserDetailsService userDetailsService) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
