# Treinamento Autoguiado - REST API (BACKEND)

<a href="https://spring.io/projects/spring-boot/">
    <img src="https://img.shields.io/badge/springboot-6DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white"/>
</a>

<a href="https://swagger.io/">
    <img src="https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white"/>
</a>

<a href="https://docs.docker.com/">
    <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white"/>
</a>


## Configuração inicial:

### Clonar o projeto:
```bash
git clone https://tools.ages.pucrs.br/treinamentoAutoguiado/backend.git
```

### Versão do java

Garanta que esteja utilizando o JDK >= 17. É possível verificar a versão do Java com o comando:

```bash
java -version
```

Se necessário, instale a versão 17 ou superior no [site oficial](https://www.oracle.com/java/technologies/downloads/)


## Como rodar o projeto
O projeto está organizado em containers. Para rodar o projeto em ambiente de desenvolvimento:

### Executar o projeto com Docker:

Executar o docker no projeto:
```bash
docker-compose up
```

Quando terminar de utilizar o container

```bash
docker-compose down -v
```

O comando "-v" garante que o docker vai apagar o volume criado pelo container, caso esqueça de utilizar o "-v"
basta utilizar o seguinte comando para apagar volumes que não estão sendo utilizados:

```bash
docker volume prune
```

Caso você esteja utilizando Windows e esteja tendo erros no container db-init, tente alterar o End of Line Sequence do arquivo [db-init.sh](./db/init-db.sh) para o tipo LF.

### Swagger

Com o backend rodando, é possível acessar a seguinte URL para ver o swagger com a explicação de todas as rotas disponibilizadas pela API:

```
http://localhost:8080/swagger-ui/index.html
```

### Como rodar o projeto localmente

É recomendado que seja executado o banco de dados sempre pelo docker para evitar configurações adicionais. Caso queria fazer testes ou precise executar o backend localmente siga os passos abaixo.

Para subir somente o banco de dados no docker execute o comando para inicialização do container **db**:

```bash
docker-compose up -d db
```

Após inicializar o docker-compose apenas com o banco de dados, execute o Backend do projeto localmente pela classe `ApiApplication.java`.

## Organização do Projeto

O projeto segue a arquitetura **MVC** (*Model-View-Controller*). Abaixo está a estrutura das pastas que está sendo utilizada:

```
src/main/java/br/pucrs/ages/treinamentoautoguiado/api
│── config/          # Classes de configuração (segurança, OpenAPI (Swagger), etc.)
│── controller/      # Controllers (pontos de entrada da API)
│── dto/             # Data Transfer Objects (DTOs) para requisições e respostas
│── entity/          # Entidades do banco de dados
│── model/           # Modelos adicionais não diretamente ligados a entidades
│── repository/      # Interfaces de acesso ao banco de dados (Spring Data JPA)
│── responses/       # Estruturas de resposta personalizadas da API
│── security/        # Configurações e filtros de segurança (JWT, autenticação)
│── service/         # Regras de negócio e lógica da aplicação
│── util/            # Utilitários e classes auxiliares
```

### Descrição das Pastas

- **`config/`**: Classes de configuração, como segurança (`CustomSecurityConfig`) e documentação da API (`OpenApiConfig`).
- **`controller/`**: Implementa os endpoints da API, lidando com as requisições HTTP e chamando os serviços correspondentes.
- **`dto/`**: Objetos de transferência de dados (DTOs), usados para transportar dados entre a camada de controle e a lógica de negócios.
- **`entity/`**: Definição das entidades do banco de dados, mapeadas com JPA.
- **`model/`**: Modelos que representam conceitos do domínio, mas que não são entidades diretamente persistidas.
- **`repository/`**: Interfaces que utilizam o Spring Data JPA para acesso ao banco de dados.
- **`responses/`**: Estruturas específicas de resposta para melhorar a padronização e facilitar retornos da API.
- **`security/`**: Configurações e filtros relacionados à segurança da aplicação, como autenticação JWT.
- **`service/`**: Implementa a lógica de negócio, separando as regras da camada de apresentação.
- **`util/`**: Classes auxiliares e manipuladores de exceções (`ApiExceptionHandler`).

## Testes unitários

### Estrutura

Os arquivos de testes unitários estão localizados no diretório `src/test/` e seguem a mesma estrutura de pacotes do diretório `src/main/`. Cada classe de teste é correspondente à classe alvo no diretório principal. Exemplo: 

- **Implementação**: `src/main/java/br/.../service/UserService.java`
- **Teste**: `src/test/java/br/.../service/UserServiceTest.java`

### Nomenclatura

Os métodos de teste seguem a convenção de nomenclatura: `test<NomeDoMétodoQueSeráTestado>_should<ResultadoEsperado>_when<Condições>`.

Exemplo:
- `testFetchAllUsers_shouldReturnUserResponses_whenUsersExist`

### Como rodar os testes

No diretório raiz do projeto, use o comando:

```bash
mvn clean test
```

### Cobertura

Para facilitar a visualização da cobertura de testes do projeto, está sendo utilizado a biblioteca **JaCoCo**. A biblioteca faz com que seja gerado um relatório em `.html` para acompanhar a cobertura de código.

O relatório fica disponível no diretório `/target/site/jacoco/index.html` e deve estar visível conforme a imagem abaixo:

<img src="https://i.imgur.com/iB1lrh6.png"/>

