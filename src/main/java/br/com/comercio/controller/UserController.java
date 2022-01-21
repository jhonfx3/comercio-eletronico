package br.com.comercio.controller;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
	@Autowired
	private DataSource dataSource;

	@GetMapping("/urlmagica")
	public String urlMagica(Model model) {
		System.out.println("chamando url magica...!");
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
		manager.setDataSource(dataSource);

		try {
			UserDetails loadUserByUsername = manager.loadUserByUsername("joao");
			if (loadUserByUsername != null) {
				System.out.println("ja tem o joao");
			}
		} catch (UsernameNotFoundException e) {
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority("ROLE_ADM"));
			UserDetails user = new User("joao", encoder.encode("102938AS"), authorities);
			manager.createUser(user);
			Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		return "home";
	}
}
