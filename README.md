# server

Aplicação do lado do Servidor (Todo List)

## Execução local

Antes de executar, ceritifique-se de criar e popular um arquivo `.env` com base
no [.env.example](./.env.example)

## Docker

Para executar localmente usando o Docker utilize:

```sh
docker-compose up --build
```

## Heroku

Para executar localmente usando o Heroku utilize:

```sh
./gradlew shadowJar # ou .\gradlew.bat para o Windows
heroku local web
```

**OBS**: Lembre-se de trocar o valor das variáveis POSTGRES\_ ao executar pelo HEROKU.
