API REST 
Spring boot + springDoc + JUnite

(https://start.spring.io/)

### Passo 1: Configuração do Projeto

1. **Criar um Projeto Spring Boot**:
    - Acesse [Spring Initializr](https://start.spring.io/).
    - Selecione as seguintes dependências:
        - Spring Web
        - Spring Data JPA
        - Oracle Driver
        - Spring Boot DevTools (opcional para facilitar o desenvolvimento)
        - Springdoc OpenAPI UI (para gerar a documentação)
2. **Descompacte o arquivo gerado** e abra no seu IDE (Eclipse, IntelliJ, VSCode, etc.).

### Organização do Repositório

Estruture o projeto seguindo as boas práticas de organização em pacotes:

```scss
src/
└── main/
    ├── java/
    │   └── com/
    │       └── exemplo/
    │           └── provaapi/
    │               ├── controller/
    │               │   └── ProdutoController.java
    │               ├── model/
    │               │   └── Produto.java
    │               ├── repository/
    │               │   └── ProdutoRepository.java
    │               ├── service/
    │               │   └── ProdutoService.java
    │               └── config/
    │               │   └── SpringDocConfig.java
										└── ProvaApiApplication.java
    ├── resources/
    │   ├── application.properties
    │   
    └── test/
        └── java/
            └── com/
                └── exemplo/
                    └── provaapi/
                        └── controller/
		                        └── ProdutoControllerTest.java 
		                        └── ProdutoControllerTestCenarios.java
		                         └── ProdutoApiApplicationTests.java
		                          └── testarcriacaoproduto.java
		                          
		                          
                        

```

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/a401c1e8-15df-4722-bc1f-1f0db3e59ab4/25742793-c9e3-48d9-8e96-590be891de16/image.png)

[]()

### Explicação dos Pacotes

1. **controller**: Contém as classes responsáveis por receber as requisições HTTP e enviar as respostas.
2. **model**: Contém as classes que representam as entidades do banco de dados.
3. **repository**: Contém as interfaces que conectam o Spring Data JPA ao banco de dados.
4. **service**: Contém as regras de negócio e orquestra chamadas entre o repositório e o controlador.
5. **config**: Contém as configurações globais do projeto, como o SpringDoc OpenAPI.

### Passo 2: Configuração do Banco de Dados Oracle

1. **Adicione a configuração do banco de dados** no `application.properties` :

```

----------------------------------------------- /oracle 21c \--------------------
# Configuração do Hibernate
spring.jpa.hibernate.ddl-auto=update

# Configurações do Oracle
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
spring.datasource.username=system
spring.datasource.password=oracle123
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# Porta do servidor
server.port=8094

-------------------------------------banco h2-----------
//CONEXÃO COM O BANCO H2

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

# Porta do servidor
server.port=8094

```

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/a401c1e8-15df-4722-bc1f-1f0db3e59ab4/073c00b7-38a1-4928-822f-7b64533edbd0/image.png)

### Passo 3: Entidade `Produto`

Crie a classe `Produto` que será mapeada para a tabela do banco de dados.

```java
package com.exemplo.provaapi.controller.model;

import jakarta.persistence.*;

@Entity
@SequenceGenerator(
        name = "produto_seq",
        sequenceName = "produto_seq",
        allocationSize = 1
)
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produto_seq")
    private Long id;

    private String nome;
    private Double preco;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}

```

### Passo 4: Repositório

Crie a interface `ProdutoRepository` que irá acessar o banco de dados.

```java

package com.exemplo.provaapi.controller.repository;

import com.exemplo.provaapi.controller.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}

```

### Passo 5: Serviço

Crie o serviço que irá realizar a lógica de negócio.

```java

package com.exemplo.provaapi.controller.service;

import com.exemplo.provaapi.controller.model.Produto;
import com.exemplo.provaapi.controller.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto criarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Optional<Produto> atualizarProduto(Long id, Produto produto) {
        return produtoRepository.findById(id).map(p -> {
            p.setNome(produto.getNome());
            p.setPreco(produto.getPreco());
            return produtoRepository.save(p);
        });
    }

    public boolean deletarProduto(Long id) {
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
            return true;
        }
        return false;
    }

}

```

### Passo 6: Controlador (Controller)

Crie o controlador para expor as rotas REST.

```java
package com.exemplo.provaapi.controller.controller;

import com.exemplo.provaapi.controller.model.Produto;
import com.exemplo.provaapi.controller.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os produtos", description = "Retorna uma lista de todos os produtos cadastrados")
    public List<Produto> listarTodos() {
        return produtoService.listarTodos();
    }

    @PostMapping
    @Operation(summary = "Criar um novo produto", description = "Adiciona um produto ao sistema")
    public Produto criarProduto(@RequestBody Produto produto) {
        return produtoService.criarProduto(produto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um produto", description = "Atualiza os detalhes de um produto existente")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable Long id, @RequestBody Produto produto) {
        return produtoService.atualizarProduto(id, produto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um produto", description = "Remove um produto do sistema pelo ID")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        if (produtoService.deletarProduto(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

```

### Passo 7: Configuração do SpringDoc OpenAPI

O SpringDoc OpenAPI gera automaticamente a documentação da sua API. Se você já adicionou a dependência `springdoc-openapi-ui`, a documentação será gerada automaticamente. Para personalizar, você pode criar uma configuração adicional.

```java

package com.exemplo.provaapi.controller.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST de Produtos")
                        .version("1.0")
                        .description("Documentação da API REST para gerenciamento de produtos."));
        // Para testar o layout da documentação springDoc no navegador
        // http://localhost:8080/swagger-ui/index.html#/
    }
}

```

A documentação estará disponível em: **http://localhost:8080/swagger-ui.html.**

### Passo 8: Testando a API

1. Execute o aplicativo.
2. Acesse `http://localhost:8080/swagger-ui.html` para visualizar e interagir com a documentação da API gerada pelo SpringDoc.

### Passo 9: Verifique a Conexão com o Banco de Dados

- Certifique-se de que o Oracle está em execução na sua máquina ou configure a URL de conexão para o seu banco de dados remoto.
- Verifique se a tabela `Produto` foi criada corretamente no banco de dados.

### 

### **2. Testar Endpoints da API**

### **2.1. Criar um Produto (POST)**

1. **Selecione o método HTTP**: `POST`.
2. **Defina a URL**:
    - `http://localhost:8080/api/produtos`
3. No corpo da requisição:
    - Vá para a aba **Body**.
    - Escolha a opção **raw** e selecione o tipo de conteúdo como **JSON**.
    - Insira o seguinte JSON no corpo da requisição:
        
        ```json
        {
          "nome": "Celular",
          "preco": 899.99
        }
        {
          "nome": "Notebook",
          "preco": 3499.99
        }
        {
          "nome": "Tablet",
          "preco": 1299.99
        }
        {
          "nome": "Monitor",
          "preco": 999.99
        }
        {
          "nome": "Teclado",
          "preco": 249.99
        }
        {
          "nome": "Mouse",
          "preco": 149.99
        }
        {
          "nome": "Fone",
          "preco": 199.99
        }
        {
          "nome": "Cadeira",
          "preco": 1199.99
        }
        {
          "nome": "Roteador",
          "preco": 399.99
        }
        {
          "nome": "Carregador",
          "preco": 89.99
        }
        {
          "nome": "Pendrive",
          "preco": 59.99
        }
        {
          "nome": "Câmera",
          "preco": 2499.99
        }
        {
          "nome": "Lâmpada",
          "preco": 79.99
        }
        {
          "nome": "Webcam",
          "preco": 319.99
        }
        {
          "nome": "Microfone",
          "preco": 449.99
        }
        {
          "nome": "Console",
          "preco": 2499.99
        }
        {
          "nome": "Headset",
          "preco": 549.99
        }
        {
          "nome": "Impressora",
          "preco": 1399.99
        }
        {
          "nome": "Scanner",
          "preco": 799.99
        }
        {
          "nome": "Smartwatch",
          "preco": 1199.99
        }
        ```
        
4. Clique no botão **Send**.
5. Verifique a resposta:
    - Se tudo estiver configurado corretamente, você verá algo como:
        
        ```json
        
        {
          "id": 1,
          "nome": "Produto Teste",
          "preco": 99.99
        }
        
        ```
        

---

### **2.2. Listar Todos os Produtos (GET)**

1. **Selecione o método HTTP**: `GET`.
2. **Defina a URL**:
    - `http://localhost:8080/api/produtos`
3. Clique no botão **Send**.
4. A resposta deve retornar um array de produtos cadastrados:
    
    ```json
    
    [
      {
        "id": 1,
        "nome": "Produto Teste",
        "preco": 99.99
      }
    ]
    
    ```
    

---

### **2.3. Buscar Produto por ID (GET)**

1. **Selecione o método HTTP**: `GET`.
2. **Defina a URL** (substitua o `1` pelo ID desejado):
    - `http://localhost:8080/api/produtos/1`
3. Clique em **Send**.
4. A resposta deve retornar o produto com o ID correspondente:
    
    ```json
    
    {
      "id": 1,
      "nome": "Produto Teste",
      "preco": 99.99
    }
    
    ```
    

---

### **2.4. Atualizar um Produto (PUT)**

1. **Selecione o método HTTP**: `PUT`.
2. **Defina a URL**:
    - `http://localhost:8080/api/produtos/1` (substitua `1` pelo ID do produto).
3. No corpo da requisição:
    - Vá para a aba **Body** e escolha **raw** com o tipo **JSON**.
    - Insira o seguinte JSON:
        
        ```json
        {
          "nome": "Produto Atualizado",
          "preco": 149.99
        }
        
        ```
        
4. Clique no botão **Send**.
5. A resposta deve retornar o produto atualizado:
    
    ```json
    
    {
      "id": 1,
      "nome": "Produto Atualizado",
      "preco": 149.99
    }
    
    ```
    

---

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/a401c1e8-15df-4722-bc1f-1f0db3e59ab4/e127b4da-b5a6-4d4a-8745-2a6c2b593e61/image.png)

### **2.5. Deletar um Produto (DELETE)**

1. **Selecione o método HTTP**: `DELETE`.
2. **Defina a URL**:
    - `http://localhost:8080/api/produtos/1` (substitua `1` pelo ID do produto).
3. Clique em **Send**.
4. A resposta deve ser um **status HTTP 204 (No Content)**, indicando que o produto foi excluído.

### Criando Testes Unitários com JUnit

### Classe de Teste Unitário

### Código do Teste para o `ProdutoControllerTest`

Classe :`ProdutoControllerTest`

```java

package com.exemplo.provaapi.controller;

import com.exemplo.provaapi.controller.controller.ProdutoController;
import com.exemplo.provaapi.controller.model.Produto;
import com.exemplo.provaapi.controller.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
public class
ProdutoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    @Test
    void listarTodos_deveRetornarListaDeProdutos() throws Exception {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPreco(100.0);

        when(produtoService.listarTodos()).thenReturn(Collections.singletonList(produto));

        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nome").value("Produto Teste"));
    }
}

```

### Teste unitário :`ProdutoControllerTestCenarios`

```java

package com.exemplo.provaapi.controller;

import com.exemplo.provaapi.controller.controller.ProdutoController;
import com.exemplo.provaapi.controller.model.Produto;
import com.exemplo.provaapi.controller.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
public class ProdutoControllerTestCenarios {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    // Cenário de Sucesso
    @Test
    void listarTodos_deveRetornarListaDeProdutos() throws Exception {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPreco(100.0);

        when(produtoService.listarTodos()).thenReturn(Collections.singletonList(produto));

        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nome").value("Produto Teste"));
    }

    // Cenário de Borda: Lista de produtos vazia
    @Test
    void listarTodos_listaVaziaDeveRetornarStatusOk() throws Exception {
        when(produtoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

}

```

### Teste unitário :`ProvaApiApplicationTests`

```java

package com.exemplo.provaapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProvaApiApplicationTests {

	@Test
	void contextLoads() {
	}

}

```

### Teste unitário :`testarcriacaoproduto`

```java
package com.exemplo.provaapi.controller;

import com.exemplo.provaapi.controller.model.Produto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class testarcriacaoproduto {

    @Test
    void testarCriacaoProduto() {
        Produto produto = new Produto();
        produto.setNome("Mesa");
        produto.setPreco(500.0);

        assertNotNull(produto);
        assertEquals("Mesa", produto.getNome());
    }

}

```

---

Aqui estão as dependências necessárias para configurar o projeto com Spring Boot, JUnit, Mockito, conexão com Oracle, e documentação com SpringDoc OpenAPI:

```xml

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.4.1</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>provaapi</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>provaapi</name>
	<description>Demo project for Spring Boot</description>
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<scope>runtime</scope>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>2.2.0</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>com.oracle.database.jdbc</groupId>
			<artifactId>ojdbc11</artifactId>
			<version>21.3.0.0</version>
		</dependency>

	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>

```

### Explicação das Dependências

1. **Spring Boot Starter Web**: Configuração para criar aplicações web RESTful.
2. **Spring Boot Starter Data JPA**: Suporte ao JPA (Java Persistence API) para integração com bancos de dados.
3. **Oracle JDBC Driver**: Driver necessário para conexão com banco de dados Oracle.
4. **Spring Boot Starter Test**: Inclui ferramentas para testes (JUnit, Mockito, etc.).
5. **SpringDoc OpenAPI UI**: Ferramenta para documentação da API REST usando OpenAPI (Swagger UI).
6. **Lombok**: Reduz código repetitivo, como getters, setters, e construtores.
7. **H2 Database (Opcional)**: Banco de dados em memória para testes e desenvolvimento rápido.

---

### Repositório Oracle JDBC Driver

O Oracle JDBC Driver pode exigir a configuração de um repositório específico, pois ele não é distribuído em repositórios públicos como Maven Central. Nesse caso, você pode baixar o driver manualmente no site da Oracle e instalá-lo localmente ou configurar o repositório no `pom.xml`:

```xml

<repositories>
    <repository>
        <id>oracle-repo</id>
        <url>https://maven.oracle.com</url>
    </repository>
</repositories>

```

Caso use o repositório da Oracle, será necessário autenticação. Acesse o site da [Oracle Maven Repository](https://www.oracle.com/database/technologies/maven-central.html) para configurar.

**Executar o comando para verificar a versão:**
Após a conexão ser estabelecida, execute o seguinte comando SQL:

```sql

SELECT * FROM v$version;

```

Os comandos DML (Data Manipulation Language) são usados para manipular os dados nas tabelas do banco de dados. Aqui estão os principais comandos DML utilizados no Oracle Developer, com exemplos:

COMANDOS SQL :

Aqui estão as instruções de SQL baseadas no cenário fornecido, considerando os dados do produto como exemplos para simular operações em um banco Oracle:

### **1. Instruções de Inserção - `INSERT`**

```sql

INSERT INTO produtos (id, nome, preco)
VALUES (1, 'Produto Teste', 99.99);

```

### **2. Instruções de Consulta - `SELECT`**

### a) Listar todos os produtos:

```sql

SELECT * FROM produtos;

```

### b) Buscar um produto por ID:

```sql

SELECT * FROM produtos WHERE id = 1;

```

### **3. Instruções de Atualização - `UPDATE`**

```sql

UPDATE produtos
SET nome = 'Produto Atualizado', preco = 149.99
WHERE id = 1;

```

### **4. Instruções de Remoção - `DELETE`**

```sql

DELETE FROM produtos
WHERE id = 1;

```

### **5. Instruções de Relacionamento entre Tabelas**

### a) Criando tabelas relacionadas:

```sql

-- Tabela de Categorias
CREATE TABLE categorias (
    id NUMBER PRIMARY KEY,
    nome VARCHAR2(255) NOT NULL
);

-- Adicionando uma coluna de relacionamento na tabela de produtos
ALTER TABLE produtos ADD categoria_id NUMBER;

-- Definindo a chave estrangeira
ALTER TABLE produtos
ADD CONSTRAINT fk_categoria FOREIGN KEY (categoria_id)
REFERENCES categorias (id);

```

### b) Inserindo dados de exemplo em categorias:

```sql

INSERT INTO categorias (id, nome) VALUES (1, 'Eletrônicos');
INSERT INTO categorias (id, nome) VALUES (2, 'Roupas');

```

### c) Relacionando um produto com uma categoria:

```sql

UPDATE produtos
SET categoria_id = 1
WHERE id = 1;

```

### d) Consultando produtos com suas categorias usando `JOIN`:

```sql

SELECT p.id, p.nome AS produto, p.preco, c.nome AS categoria
FROM produtos p
JOIN categorias c ON p.categoria_id = c.id;

```

### e) Consultando produtos com ou sem categorias usando `LEFT JOIN`:

```sql

SELECT p.id, p.nome AS produto, p.preco, c.nome AS categoria
FROM produtos p
LEFT JOIN categorias c ON p.categoria_id = c.id;

```

Esses comandos correspondem às operações realizadas na API usando as instruções SQL necessárias para manipular os dados diretamente no banco de dados Oracle.

# PL/SQL

Abaixo estão os exemplos de scripts utilizando PL/SQL para executar as operações descritas, com base nos dados fornecidos.

---

### **1. Inserção - `INSERT` com PL/SQL**

```sql

BEGIN
  INSERT INTO produtos (id, nome, preco)
  VALUES (1, 'Produto Teste', 99.99);
  COMMIT;
END;
/

```

---

### **2. Consulta - `SELECT` com PL/SQL**

### a) Listar todos os produtos:

```sql

DECLARE
  CURSOR c_produtos IS
    SELECT id, nome, preco FROM produtos;
  v_id produtos.id%TYPE;
  v_nome produtos.nome%TYPE;
  v_preco produtos.preco%TYPE;
BEGIN
  OPEN c_produtos;
  LOOP
    FETCH c_produtos INTO v_id, v_nome, v_preco;
    EXIT WHEN c_produtos%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE('ID: ' || v_id || ', Nome: ' || v_nome || ', Preço: ' || v_preco);
  END LOOP;
  CLOSE c_produtos;
END;
/

```

### b) Buscar produto por ID:

```sql

DECLARE
  v_id produtos.id%TYPE := 1;
  v_nome produtos.nome%TYPE;
  v_preco produtos.preco%TYPE;
BEGIN
  SELECT nome, preco
  INTO v_nome, v_preco
  FROM produtos
  WHERE id = v_id;

  DBMS_OUTPUT.PUT_LINE('Nome: ' || v_nome || ', Preço: ' || v_preco);
EXCEPTION
  WHEN NO_DATA_FOUND THEN
    DBMS_OUTPUT.PUT_LINE('Produto não encontrado.');
END;
/

```

---

### **3. Atualização - `UPDATE` com PL/SQL**

```sql

BEGIN
  UPDATE produtos
  SET nome = 'Produto Atualizado', preco = 149.99
  WHERE id = 1;

  IF SQL%ROWCOUNT > 0 THEN
    DBMS_OUTPUT.PUT_LINE('Produto atualizado com sucesso.');
  ELSE
    DBMS_OUTPUT.PUT_LINE('Produto não encontrado.');
  END IF;

  COMMIT;
END;
/

```

---

### **4. Remoção - `DELETE` com PL/SQL**

```sql

BEGIN
  DELETE FROM produtos
  WHERE id = 1;

  IF SQL%ROWCOUNT > 0 THEN
    DBMS_OUTPUT.PUT_LINE('Produto removido com sucesso.');
  ELSE
    DBMS_OUTPUT.PUT_LINE('Produto não encontrado.');
  END IF;

  COMMIT;
END;
/

```

---

### **5. Relacionamento entre Tabelas com PL/SQL**

### a) Criar as tabelas e relacionamento:

```sql

BEGIN
  EXECUTE IMMEDIATE 'CREATE TABLE categorias (
    id NUMBER PRIMARY KEY,
    nome VARCHAR2(255) NOT NULL
  )';

  EXECUTE IMMEDIATE 'ALTER TABLE produtos ADD categoria_id NUMBER';
  EXECUTE IMMEDIATE 'ALTER TABLE produtos
                     ADD CONSTRAINT fk_categoria FOREIGN KEY (categoria_id)
                     REFERENCES categorias (id)';
END;
/

```

### b) Inserir categorias e associar ao produto:

```sql

BEGIN
  INSERT INTO categorias (id, nome) VALUES (1, 'Eletrônicos');
  INSERT INTO categorias (id, nome) VALUES (2, 'Roupas');

  UPDATE produtos
  SET categoria_id = 1
  WHERE id = 1;

  COMMIT;
END;
/

```

### c) Consultar produtos com categorias usando `JOIN`:

```sql

DECLARE
  CURSOR c_produtos IS
    SELECT p.id, p.nome AS produto, p.preco, c.nome AS categoria
    FROM produtos p
    JOIN categorias c ON p.categoria_id = c.id;
  v_id produtos.id%TYPE;
  v_nome produtos.nome%TYPE;
  v_preco produtos.preco%TYPE;
  v_categoria categorias.nome%TYPE;
BEGIN
  OPEN c_produtos;
  LOOP
    FETCH c_produtos INTO v_id, v_nome, v_preco, v_categoria;
    EXIT WHEN c_produtos%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE('ID: ' || v_id || ', Produto: ' || v_nome || ', Preço: ' || v_preco || ', Categoria: ' || v_categoria);
  END LOOP;
  CLOSE c_produtos;
END;
/

```

### d) Consultar produtos com ou sem categorias usando `LEFT JOIN`:

```sql

DECLARE
  CURSOR c_produtos IS
    SELECT p.id, p.nome AS produto, p.preco, c.nome AS categoria
    FROM produtos p
    LEFT JOIN categorias c ON p.categoria_id = c.id;
  v_id produtos.id%TYPE;
  v_nome produtos.nome%TYPE;
  v_preco produtos.preco%TYPE;
  v_categoria categorias.nome%TYPE;
BEGIN
  OPEN c_produtos;
  LOOP
    FETCH c_produtos INTO v_id, v_nome, v_preco, v_categoria;
    EXIT WHEN c_produtos%NOTFOUND;
    DBMS_OUTPUT.PUT_LINE('ID: ' || v_id || ', Produto: ' || v_nome || ', Preço: ' || v_preco || ', Categoria: ' || NVL(v_categoria, 'Sem Categoria'));
  END LOOP;
  CLOSE c_produtos;
END;
/

```

---

Esses scripts cobrem todas as operações principais (`INSERT`, `SELECT`, `UPDATE`, `DELETE`) e utilizam PL/SQL para consultas avançadas e tratamento de dados com relacionamento entre tabelas.
