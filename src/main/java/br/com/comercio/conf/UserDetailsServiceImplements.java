package br.com.comercio.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import br.com.comercio.model.Usuario;
import br.com.comercio.repository.UsuarioRepository;

@Component
public class UserDetailsServiceImplements implements UserDetailsService {

	@Autowired
	private UsuarioRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("usuario nao encontrado");
		}
		if(!user.isEnabled()) {
			throw new UsernameNotFoundException("usuario nao encontrado");
		}
		return new User(user.getUsername(), user.getPassword(), true, true, true, true, user.getAuthorities());
	}

}
