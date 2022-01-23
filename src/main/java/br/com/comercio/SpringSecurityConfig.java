package br.com.comercio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.comercio.conf.UserDetailsServiceImplements;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	/*
	 * Eu precisava disso quando utilizava o jdbc authentication que aprendi no
	 * curso de spring boot versão nova
	 * 
	 * @Autowired private DataSource dataSource;
	 */

	@Autowired
	private UserDetailsServiceImplements userDetailsService;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/urlmagica").permitAll().antMatchers("/notificacao/**").permitAll()
				.antMatchers("/").permitAll().antMatchers("/produto/detalhe/**").permitAll().antMatchers("/carrinho")
				.permitAll().anyRequest().authenticated().and()
				.formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/")).csrf().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		// UserDetails user =
		// User.builder().username("maria").password(encoder.encode("maria")).roles("ADM").build();
		// auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(encoder);
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}

}
