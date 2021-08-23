package curso.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = "curso.springboot.model")//mapeia o pacote model para que todas atualizações reflitan no banco de dados
@ComponentScan(basePackages = {"curso.*"})//mapeia todos os pacotes forçando os controllers serem encontrados
@EnableJpaRepositories(basePackages = {"curso.springboot.repository"})//mapeia e força o spring a reconhecer todos os repository
@EnableTransactionManagement //Habilita a parte de transação do banco de dados
@EnableWebMvc //ativa recursos MVC para implementação da pagina de login
public class Application implements WebMvcConfigurer{

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
		//Faz a emcriptação da senha
		/*BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String result = encoder.encode("123");
		System.out.println(result); */
		
	}
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) { //Metodo faz o redirecionamento para pagina de login
		
		registry.addViewController("/login").setViewName("/login");
		registry.setOrder(Ordered.LOWEST_PRECEDENCE);
	}

}
