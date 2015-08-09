package br.com.iejb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.iejb.model.Membro;

public interface MembroRepository extends JpaRepository<Membro, Long> {

	/*@Query(value="Select m from Membro m where m.cpf = :cpf")
	public Membro pesquisarMembroPorCPF(@Param(name="cpf") String cpf);*/
}
