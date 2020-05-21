# jax-ws-todo-list-soap-backend

WebService utilizando o protocolo SOAP para uma aplicação de Lista de Tarefas,
criado como parte de um trabalho da disciplina de Sistemas Distribuídos do
Mestrado em Informática Aplicada na UNIFOR.

<!-- TOC -->

- [jax-ws-todo-list-soap-backend](#jax-ws-todo-list-soap-backend)
  - [Estrutura do projeto](#estrutura-do-projeto)
    - [Autenticação](#autenticação)
    - [Injeção de dependências](#injeção-de-dependências)
    - [Persistência de dados](#persistência-de-dados)
  - [Como executar localmente](#como-executar-localmente)

<!-- /TOC -->

## Estrutura do projeto

O projeto consiste na implementação de 4 serviços SOAP, um serviço de usuários,
um de autenticação, um de etiquetas e um de tarefas.

Os endpoints podem ser encontrados no [App.java](src/main/java/projeto_1/App.java).
E seus WSDLs podem ser encontrados usando o parâmetro `?wsdl` na rota.

No geral, cada endpoint/serviço tem seu próprio pacote na aplicação para ilustrar
a possibilidade de se separar este WebService em vários serviços SOAP com pouca
lógica compartilhada entre os serviços.

As exceções sendo os pacotes:

- `config`

  Este é um pacote de serviços de configuração, hospedando o provedor da conexão
  com o banco de dados e a classe que implementa o carregamento de configurações
  a partir das variáveis de ambiente.

- `exceptions`

  Exceções compartilhadas da aplicação para erros mais genéricos como
  [ForbiddenException](src/main/java/projeto_1/exceptions/ForbiddenException.java)
  e [InternalServerErrorException](src/main/java/projeto_1/exceptions/InternalServerErrorException.java),
  no geral as Exceptions tiveram seus nomes "fortemente inspirados" nos nomes
  dos códigos de erro HTTP.

- `labels_tasks`

  Implementa o repositório do relacionamento entre as etiquetas e as tarefas,
  num sistema distribuído, seria um candidato a gerar um serviço composto.

### Autenticação

Este backend permite o login com JWT (inseguro, não implementa lógicas para
expiração ou invalidação forçada do token), a autenticação é feita com o
header `Authorization: JWT {token}`, substituindo {token} pelo token do usuário.

### Injeção de dependências

Este projeto utiliza [Guice](https://github.com/google/guice) para injeção de
dependências. Havia a intenção de se implementar testes unitários e rotinas
de CI/CD para ele, mas o tempo não permitiu que isto fosse realizado.

### Persistência de dados

Conforme pode ser encontrado no [docker-compose.yml](./docker-compose.yml) este
WebService foi configurado para utilizar um banco de dados Postgres, mas como
utiliza JDBC, a sua migração para utilizar outros bancos de dados tende a ser
simples. Foi utilizado o padrão de Repositórios para encapsular a lógica de
acesso ao banco de dados, este seria o ponto de mudança. A conexão é estabelecida
no [ConnectionProvider.java](./src/main/java/projeto_1/config/ConnectionProvider.java).

## Como executar localmente

Antes de executar, ceritifique-se de criar e popular um arquivo `.env` com base
no [.env.example](./.env.example)

Para executar localmente usando o Docker utilize:

```sh
docker-compose up --build
```
