package br.com.iejb.sgi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.iejb.sgi.domain.UF;

public interface UFRepository extends JpaRepository<UF, Long>, 
	JpaSpecificationExecutor<UF> {

}
