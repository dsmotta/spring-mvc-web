package curso.springboot.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import curso.springboot.model.Pessoa;

@Repository
@Transactional
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
	
	@Query("select p from Pessoa p where p.nome like %?1%") //linguagem PQL usando parametro com like se usar outro parametro usar and %?2%
	List<Pessoa> findByName(String nome);
	
	@Query("select p from Pessoa p where p.sexopessoa like %?1%") //linguagem PQL usando parametro com like se usar outro parametro usar and %?2%
	List<Pessoa> findBySexo(String sexo);
	
	@Query("select p from Pessoa p where p.nome like %?1% and p.sexopessoa = ?2") 
	List<Pessoa> findPessoaByNameSexo(String nome, String sexopessoa);
	
	/*Método para pesquisa usando paginacao*/
	default Page<Pessoa> findPessoaByNamePage(String nome, Pageable pageable){
		
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);
		
		/*Estamos consultando a pesquisa para consultar partes do nome do banco de dados, igual ao like do sql */
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		/*Une o objeto com o valor e a configuração para consultar*/
		Example<Pessoa> example = Example.of(pessoa, exampleMatcher);
		
		Page<Pessoa> pessoas = findAll(example, pageable);
		
		return pessoas;
		
	}
	
	/*Método para pesquisa usando paginacao*/
	default Page<Pessoa> findPessoaByNameSexoPage(String nome, String sexo, Pageable pageable){
		
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);
		pessoa.setSexopessoa(sexo);
		
		/*Estamos consultando a pesquisa para consultar partes do nome do banco de dados, igual ao like do sql */
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("sexopessoa", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		/*Une o objeto com o valor e a configuração para consultar*/
		Example<Pessoa> example = Example.of(pessoa, exampleMatcher);
		
		Page<Pessoa> pessoas = findAll(example, pageable);
		
		return pessoas;
		
	}
	
}

