package br.com.comercio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.comercio.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	@Query("SELECT u from User u WHERE u.username = :username")
	User findByIdUser(@Param("username") String username);
	
}
