package com.project.market.infra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/login", "/sign-up", "/login*").permitAll()
                .anyRequest().authenticated();

        http.formLogin()
                .loginPage("/account/login.html")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/perform_login");

        http.logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID");

        http.rememberMe()
                .rememberMeParameter("remember");
//                .userDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
