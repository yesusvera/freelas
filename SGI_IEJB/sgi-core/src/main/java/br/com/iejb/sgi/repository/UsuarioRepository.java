package br.com.iejb.sgi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.iejb.sgi.domain.Usuario;

/**
 * 
 * @author yesus
 *
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, 
	JpaSpecificationExecutor<Usuario> {

	
	@Query(value="SELECT usr FROM br.com.iejb.sgi.domain.Usuario usr where usr.login = :login and usr.senha = :senha and usr.ativo = true")
	public List<Usuario> getUsuarios(@Param("login") String login, @Param("senha") String senha);
}
