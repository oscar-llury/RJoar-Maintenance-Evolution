package jroar.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	public UserRepositoryAuthenticationProvider authenticationProvider;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// Páginas privadas
		http.authorizeRequests().antMatchers("/rate").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/comment").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/profile").hasAnyRole("USER");
        http.authorizeRequests().antMatchers("/profileEdit").hasAnyRole("USER");
		http.authorizeRequests().antMatchers("/control").hasAnyRole("USER");
		http.authorizeRequests().antMatchers("/control/mount").hasAnyRole("USER");
		http.authorizeRequests().antMatchers("/control/drop").hasAnyRole("ADMIN");
		http.authorizeRequests().antMatchers("/control/dropall").hasAnyRole("ADMIN");
		http.authorizeRequests().antMatchers("/control/shout").hasAnyRole("USER");
		http.authorizeRequests().antMatchers("/logout").authenticated();

		// Todas las demás páginas serán públicas, es decir, no requieren autenticación
		http.authorizeRequests().anyRequest().permitAll();

		// Disable CSRF protection
		http.csrf().disable();

		// Login form
		http.formLogin().loginPage("/login");
		http.formLogin().usernameParameter("username");
		http.formLogin().passwordParameter("password");
		http.formLogin().defaultSuccessUrl("/");
		http.formLogin().failureUrl("/loginerror");

		// Logout
		http.logout().logoutUrl("/logout");
		http.logout().invalidateHttpSession(true);
		http.logout().logoutSuccessUrl("/");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		// Database authentication provider
		auth.authenticationProvider(authenticationProvider);
	}
}
