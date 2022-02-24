package br.com.comercio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.comercio.conf.LoginSuccessHandler;
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

	@Autowired
	private LoginSuccessHandler loginSuccessHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement(session -> session.invalidSessionUrl("/sessaotimeout?mensagem=expirado")
				.maximumSessions(1).expiredUrl("/sessaotimeout?mensagem=maximoSessoes")).authorizeRequests()
				.antMatchers("/usuario/urlmagica").permitAll().antMatchers("/usuario/formulario").permitAll()
				.antMatchers("/usuario/novo").permitAll().antMatchers("/usuario/confirmar/**").permitAll()
				.antMatchers("/notificacao/**").permitAll().antMatchers("/usuario/sucessoContaCriada").permitAll().antMatchers("/")
				.permitAll().antMatchers("/produto/detalhe/**").permitAll().antMatchers("/produto/formulario/**")
				.hasRole("ADM").antMatchers("/js/**").permitAll().antMatchers("/carrinho").permitAll()
				.antMatchers("/desenvolvedor").permitAll().antMatchers("/sessaotimeout").permitAll().anyRequest()
				.authenticated().and()
				.formLogin(form -> form.loginPage("/login").successHandler(loginSuccessHandler).permitAll()
						.failureUrl("/login?erro=true"))
				.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/").deleteCookies("JSESSIONID")).csrf()
				.disable();
		// http.sessionManagement().maximumSessions(1).expiredUrl("/login?invalidsession=true");

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
