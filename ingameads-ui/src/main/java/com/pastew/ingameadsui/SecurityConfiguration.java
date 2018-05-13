package com.pastew.ingameadsui;

import com.pastew.ingameadsui.User.SpringDataUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/", "images/**", "main.css").permitAll()
            .antMatchers(HttpMethod.GET, "/mygames/**").hasRole(Roles.USER)
            .antMatchers(HttpMethod.GET, "/offers/**").hasRole(Roles.USER)
            //.antMatchers(HttpMethod.GET, "/images").hasRole(Roles.USER)
            //.antMatchers("/h2/**").hasRole("ADMIN").anyRequest()
        .and()
            .formLogin()
            .permitAll()
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
    }

    @SuppressWarnings("deprecation")
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }
}
