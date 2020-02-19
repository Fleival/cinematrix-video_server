package com.denspark.config;

import com.denspark.db.cinemix.persistance.UserRepository;
import com.denspark.security.CustomRememberMeServices;
import com.denspark.security.google2fa.CustomAuthenticationProvider;
import com.denspark.security.google2fa.CustomWebAuthenticationDetailsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class CinemixSpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Autowired
    private LogoutSuccessHandler myLogoutSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

    @Autowired
    private UserRepository userRepository;

    public CinemixSpringSecurityConfig() {
        super();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider());
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers(
                "/resources/**"
                , "/api/**"
                , "/assets/*"
                , "/css/*"
                , "/fonts/*"
                , "/static/img/*"
                , "/js/*"
                , "/tasks/*");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        // @formatter:off

        http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/get_user*").hasRole("USER")
                    .antMatchers("/update_user_favorite_movie*").hasRole("USER")
                    .antMatchers("/").permitAll()
                    .antMatchers("/registration").permitAll()
                    .antMatchers("/user/registration*").permitAll()
                    .antMatchers("/registrationConfirm").permitAll()
//                    .antMatchers("/invalidSession*").anonymous()
//                    .antMatchers("/user/updatePassword*", "/user/savePassword*", "/updatePassword*").hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
//                    .antMatchers("/get_user").permitAll()
//                    .anyRequest().hasAuthority("READ_PRIVILEGE")
                .and()
                .httpBasic()
                .and()
                .formLogin()
                    .loginPage("/login")
//                    .defaultSuccessUrl("/homepage.html")
//                    .failureUrl("/login?error=true")
                    .successHandler(myAuthenticationSuccessHandler)
                    .failureHandler(authenticationFailureHandler)
                    .authenticationDetailsSource(authenticationDetailsSource)
                .permitAll()
                .and()
                .sessionManagement()
                    .invalidSessionUrl("/?status=invalidSession")
                    .maximumSessions(1).sessionRegistry(sessionRegistry()).and()
                    .sessionFixation().none()
                .and()
                .logout()
                    .logoutSuccessHandler(myLogoutSuccessHandler)
                    .invalidateHttpSession(false)
                    .logoutSuccessUrl("/")
                    .deleteCookies("JSESSIONID")
                    .permitAll()
                .and()
                    .rememberMe().rememberMeServices(rememberMeServices()).key("theKey");

        // @formatter:on
    }

//     beans

    @Bean
    public DaoAuthenticationProvider authProvider() {
        final CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        CustomRememberMeServices rememberMeServices = new CustomRememberMeServices("theKey", userDetailsService, new InMemoryTokenRepositoryImpl());
        return rememberMeServices;
    }
}
