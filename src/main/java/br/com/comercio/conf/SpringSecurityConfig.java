package br.com.comercio.conf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import br.com.comercio.filter.RecaptchaFilter;

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
	
	
	private RecaptchaFilter getCustomLoginFilter() throws Exception{
		RecaptchaFilter filter = new RecaptchaFilter("/login", "POST");
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
			
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				response.sendRedirect("/login?erro=1");
			}
		});
		return filter;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(getCustomLoginFilter(), RecaptchaFilter.class). sessionManagement(session -> session.invalidSessionUrl("/sessaotimeout?mensagem=expirado")
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).maximumSessions(1)
				.expiredUrl("/sessaotimeout?mensagem=maximoSessoes")).authorizeRequests()
				.antMatchers("/usuario/urlmagica").permitAll().antMatchers("/usuario/formulario").permitAll()
				.antMatchers("/usuario/novo").permitAll().antMatchers("/usuario/confirmar/**").permitAll()
				.antMatchers("/notificacao/**").permitAll().antMatchers("/categoria/**").permitAll()
				.antMatchers("/busca/**").permitAll().antMatchers("/usuario/sucessoContaCriada").permitAll()
				 .antMatchers("/").permitAll().antMatchers("/login?erro=2").permitAll(). antMatchers("/usuario/recuperar-senha/**").permitAll()
				.antMatchers("/usuario/trocar-senha-esquecida/**").permitAll()
				.antMatchers("/usuario/trocarSenhaEsquecida").permitAll().antMatchers("/produto/detalhe/**").permitAll()
				.antMatchers("/produto/formulario/**").hasRole("ADM").antMatchers("/js/**").permitAll()
				.antMatchers("/css/**").permitAll().antMatchers("/carrinho").permitAll().antMatchers("/desenvolvedor")
				.permitAll().antMatchers("/cliente/**").permitAll().antMatchers("/sessaotimeout").permitAll()
				.anyRequest().authenticated().and()
				.formLogin(form -> form.loginPage("/login").successHandler(loginSuccessHandler).permitAll()
						 .loginProcessingUrl("/login") .failureUrl("/login?erro=1"))
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
