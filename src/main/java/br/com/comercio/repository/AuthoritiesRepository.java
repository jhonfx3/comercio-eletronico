package br.com.comercio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.comercio.model.Authorities;

@Repository
public interface AuthoritiesRepository extends JpaRepository<Authorities, String> {

}
