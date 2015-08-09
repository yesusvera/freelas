package br.com.iejb.sgi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.iejb.sgi.domain.Membro;

public interface MembroRepository extends JpaRepository<Membro, Long>, 
	JpaSpecificationExecutor<Membro> {

	
	@Query(value="SELECT m FROM br.com.iejb.sgi.domain.Membro m where m.hashConfirmacaoCadastro = :hashConfirmacaoCadastro")
	public List<Membro> buscarMembroPorHashConfirmacao(@Param("hashConfirmacaoCadastro")String hashConfirmacaoCadastro);
}
