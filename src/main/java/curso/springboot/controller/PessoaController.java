package curso.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.ProfissaoRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ReportUtil reportUtil;
	
	@Autowired
	private ProfissaoRepository profissaoRepository;
	
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
		
	}
	
	@RequestMapping(method = RequestMethod.POST, value="**/salvarpessoa", consumes= {"multipart/form-data"})//dois asteristicos ignora qualquer coisa antes do salvarpessoa
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult, final MultipartFile file) throws IOException { //@valid, BindingResult usados nas validações
				
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		if(bindingResult.hasErrors()) {//verifica se tem erros .... se ouver entra no bloco de instruções
			ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
			andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
			andView.addObject("pessoaobj", pessoa);
			
			List<String> msg = new ArrayList<String>();//cria uma lista tipo String com nome msg para armazenar as mensagens das validações
			for(ObjectError objectError : bindingResult.getAllErrors()){  //o erro é adcionado ao objeto
				
				msg.add(objectError.getDefaultMessage());//vem das anotações @NotEmpty e outras...
			}
			
			andView.addObject("msg", msg);
			andView.addObject("profissoes", profissaoRepository.findAll());
			return andView;
		}
		
		if(file.getSize() > 0) { //cadastrando novo curriculo
			
			pessoa.setCurriculo(file.getBytes());
			pessoa.setTipoFileCurriculo(file.getContentType());
			pessoa.setNomeFileCurriculo(file.getOriginalFilename());
		}else {
			if(pessoa.getId() > 0 && pessoa.getId() != 0L) { //editando
			
				Pessoa pessoaTemp = pessoaRepository.findById(pessoa.getId()).get();
				pessoa.setCurriculo(pessoaTemp.getCurriculo());
				pessoa.setTipoFileCurriculo(pessoaTemp.getTipoFileCurriculo());
				pessoa.setNomeFileCurriculo(pessoaTemp.getNomeFileCurriculo());
			}
		}
		
		pessoaRepository.save(pessoa);
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
		
	}
	
	@GetMapping("**/baixarcurriculo/{idpessoa}")
	public void baixarcurriculo(@PathVariable("idpessoa") Long idpessoa, HttpServletResponse response) throws IOException {
		
		/*Consultar objeto pessoa no Banco de Dados */
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		if(pessoa.getCurriculo() != null) {
			
			/*Setar o tamanho da resposta*/
			response.setContentLength(pessoa.getCurriculo().length);
			
			/*Tipo do arquivo que será feito o download ou pode ser generica "application/octet-stream"*/
			response.setContentType(pessoa.getTipoFileCurriculo());
			
			/*Define o cabeçalho da resposta*/
			String headerkey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNomeFileCurriculo());
			response.setHeader(headerkey, headerValue);
			
			/*Finaliza a resposta passando o arquivo*/
			response.getOutputStream().write(pessoa.getCurriculo());
		}
		
	}

	
	@RequestMapping(method = RequestMethod.GET, value="**/listapessoas") //dois asteristicos ignora qualquer coisa antes do listapessoas
	public ModelAndView pessoas() {
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("pessoaobj", new Pessoa());
		return andView;
	}
	
	@GetMapping("/editarpessoa/{idpessoa}")// Intercepta a URL e faz passagem de parametro no caso o id 
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		andView.addObject("pessoaobj", pessoa.get());
		andView.addObject("profissoes", profissaoRepository.findAll());
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		return andView;
		
	}
	
	@GetMapping("/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		
		pessoaRepository.deleteById(idpessoa);
		
		ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
		andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		andView.addObject("pessoaobj", new Pessoa());
		
		return andView;
	
	}
	
	@PostMapping("**/pesquisapessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("sexopesquisa") String sexopesquisa, @PageableDefault(size=5, sort= {"nome"}) Pageable pageable) {
		
		Page<Pessoa> pessoas = null;
		
		if(sexopesquisa != null && !sexopesquisa.isEmpty() ) {
			
			pessoas = pessoaRepository.findPessoaByNameSexoPage(nomepesquisa, sexopesquisa, pageable);
			
		}else {
			
			pessoas = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		}
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("nomepesquisa", nomepesquisa);
		
		return modelAndView;
		
	}
	
	/* Relatório */
	@GetMapping("**/pesquisapessoa")
	public void imprimePdf(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("sexopesquisa") String sexopesquisa, HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();/* Cria uma lista */ 
		
		if(sexopesquisa != null && !sexopesquisa.isEmpty() && nomepesquisa != null && !nomepesquisa.isEmpty()) { /* Busca por nome e sexo */
			
			pessoas = pessoaRepository.findPessoaByNameSexo(nomepesquisa, sexopesquisa);
			
		}else if(nomepesquisa != null && !nomepesquisa.isEmpty()) {/* Busca por nome */
			
			pessoas = pessoaRepository.findByName(nomepesquisa);
			
		}else if(sexopesquisa != null && !sexopesquisa.isEmpty()) {/* Busca por sexo */
		
		pessoas = pessoaRepository.findBySexo(sexopesquisa);
		
	}
		
		else {/* Busca todos */
			
			Iterable<Pessoa> iterator = pessoaRepository.findAll();
			for (Pessoa pessoa : iterator) {
				pessoas.add(pessoa);
			}
		}
		
		/* Chama o serviço que faz a geração do relatório*/
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		
		/* Tamanho da resposta*/
		response.setContentLength(pdf.length);
		
		/* Definir na resposta o tipo de arquivo*/
		response.setContentType("application/octet-stream");
		
		/* Definir cabecalho de resposta */
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		
		/* Finaliza a resposta pro navegador*/
		response.getOutputStream().write(pdf);
	}
	
	
	@GetMapping("/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		
		ModelAndView andView = new ModelAndView("cadastro/telefones");
		andView.addObject("pessoaobj", pessoa.get());
		andView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		andView.addObject("teleobj", new Telefone());
		return andView;
	}
	
	@PostMapping("**/addTelefone/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		telefone.setPessoa(pessoa);
		
		if (telefone != null && telefone.getNumero().isEmpty() || telefone.getTipo().isEmpty()) {
			
			List<String> msg = new ArrayList<String>();
			if(telefone.getNumero().isEmpty()) {
				msg.add("Numero inválido!");
			}
			if(telefone.getTipo().isEmpty()) {
				msg.add("Tipo inválido!");
			}
			ModelAndView andView = new ModelAndView("cadastro/telefones");
			andView.addObject("pessoaobj", pessoa);
			andView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			andView.addObject("teleobj", telefone);
			andView.addObject("msg", msg);
			
			return andView;
		}else {
		
		telefoneRepository.save(telefone);
			
			ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
			modelAndView.addObject("pessoaobj", pessoa);
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			modelAndView.addObject("teleobj", new Telefone());
			
			
			return modelAndView;
		}
			
	}
	
	@GetMapping("**/removeTelefone/{idTelefone}")
	public ModelAndView removeTelefone(@PathVariable("idTelefone") Long idTElefone) {
		
		Pessoa pessoa = telefoneRepository.findById(idTElefone).get().getPessoa();
		
		telefoneRepository.deleteById(idTElefone);
		
		ModelAndView andView = new ModelAndView("cadastro/telefones");
		andView.addObject("pessoaobj", pessoa);
		andView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		andView.addObject("teleobj", new Telefone());
		
		return andView;
		
	}
	
	@GetMapping("**/editarTelefone/{idTelefone}")
	public ModelAndView editaTelefone(@PathVariable("idTelefone") Long idTelefone) {
		
		Pessoa pessoa = telefoneRepository.findById(idTelefone).get().getPessoa();
		
		ModelAndView andView = new ModelAndView("cadastro/telefones");
		andView.addObject("pessoaobj", pessoa);
		andView.addObject("teleobj", telefoneRepository.findById(idTelefone));
		
		return andView;
		
	}

	
	/*Paginação*/
	
	@GetMapping("/pessoaspag")
	public ModelAndView carregaPessoaPaginacao(@PageableDefault(size = 5) Pageable pageable, ModelAndView model,
			@RequestParam("nomepesquisa") String nomepesquisa) {
		
		Page<Pessoa> pagePessoa = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		model.addObject("pessoas", pagePessoa);
		model.addObject("pessoaobj", new Pessoa());
		model.addObject("nomepesquisa", nomepesquisa);
		model.setViewName("cadastro/cadastropessoa");
		
		return model;
		
	}

}
