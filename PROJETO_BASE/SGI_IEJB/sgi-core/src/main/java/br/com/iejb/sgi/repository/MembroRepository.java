package br.com.iejb.sgi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.iejb.sgi.domain.Membro;

public interface MembroRepository extends JpaRepository<Membro, Long>, 
	JpaSpecificationExecutor<Membro> {

}
