package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity //ativa os modulos de segurança
public class WebConfigSecurity extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@Override //configura as solicitações de acesso Http
	protected void configure(HttpSecurity http) throws Exception {
			
		http.csrf()
		.disable() //desativa as configurações padrão de memória
		.authorizeRequests() //Permitir restringir acessos
		.antMatchers(HttpMethod.GET, "/").permitAll() //qualquer usuário acessa
		.antMatchers("**/materialize/**").permitAll()
		.antMatchers(HttpMethod.GET, "/cadastropessoa").hasAnyRole("ADMIN") //define os acessos de acordo om seu papel ROLE
		.anyRequest().authenticated()
		.and().formLogin().permitAll() //permite qualquer usuario
		.loginPage("/login") //procura a pagina login
		.defaultSuccessUrl("/cadastropessoa") //se o logim for feito com sucesso sera redirecionado para pagina cadastropessoa
		.failureUrl("/login?error=true") // se houver falha no login envia para proprio login com parametro de erro true
		.and().logout().logoutSuccessUrl("/login") //Mapeia URL de Logout e invalida usuário autenticado e redireciona para o login
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
		
	}
	
	@Override //Cria autenticação do usuário com banco de dados ou em memória
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		//Autenticação de lggin em memória
		/*auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder()) //autenticação da senha usando encriptação
		.withUser("douglas")
		.password("$2a$10$AnQatD/M1sRwTKzeMwgu5u8VO4PYNNu4K/s6J.9PrvvX2D.9S9cH6")//senha encriptada
		.roles("ADMIN"); */
		
		/*implementação usando authenticação por consulta ao banco de dados*/
		auth.userDetailsService(implementacaoUserDetailsService)
		.passwordEncoder(new BCryptPasswordEncoder());
		
		
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception { //ignora URL especificas
		
		web.ignoring().antMatchers("/materialize/**");
		
		
	}
		
	
	
}
