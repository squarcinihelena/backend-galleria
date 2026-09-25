# Galleria Backend

API REST em Spring Boot para gestão de clientes, produtos, pedidos e usuários, com autenticação via JWT.

## Requisitos

- Java 17+
- Maven (ou use o wrapper `mvnw`)
- PostgreSQL

## Configuração

O banco de dados e o segredo do JWT são lidos de variáveis de ambiente:

| Variável | Descrição | Padrão |
|---|---|---|
| `DB_HOST` | Host do PostgreSQL | `localhost` |
| `DB_PORT` | Porta do PostgreSQL | `5433` |
| `DB_NAME` | Nome do banco | `galleriadb` |
| `DB_USER` | Usuário do banco | `postgres` |
| `DB_PASSWORD` | Senha do banco | *(obrigatório)* |
| `JWT_SECRET` | Chave secreta (Base64) para assinar os tokens JWT | *(obrigatório)* |

Um `docker-compose.yml` está incluso para subir o PostgreSQL localmente:

```bash
docker-compose up -d
```

## Como rodar

```powershell
$env:DB_PASSWORD="postgrespassword"; $env:JWT_SECRET="VG1E7mz5VNcZ1HsYtbb6VM+QUJoCEkoyZAPIVWDIUnQ"; ./mvnw.cmd spring-boot:run
```

A API sobe em `http://localhost:8080`.

## Autenticação

A maioria das rotas exige um token JWT. Faça login em `POST /usuarios/logar` e envie o token retornado no header:

```
Authorization: Bearer <token>
```

## Rotas

### Usuários (`/usuarios`)

| Método | Rota | Autenticação | Descrição |
|---|---|---|---|
| POST | `/usuarios/logar` | Não | Autentica e retorna o token JWT |
| POST | `/usuarios` | Não | Cadastra um usuário |
| GET | `/usuarios` | Sim | Lista todos os usuários |
| GET | `/usuarios/{id}` | Sim | Busca usuário por id |
| PUT | `/usuarios/{id}` | Sim | Atualiza usuário |
| DELETE | `/usuarios/{id}` | Sim | Remove usuário |

### Clientes (`/clientes`)

| Método | Rota | Descrição |
|---|---|---|
| POST | `/clientes` | Cadastra um cliente |
| GET | `/clientes` | Lista todos os clientes |
| GET | `/clientes/{id}` | Busca cliente por id |
| PUT | `/clientes/{id}` | Atualiza cliente |
| DELETE | `/clientes/{id}` | Remove cliente |

### Produtos (`/produtos`)

| Método | Rota | Descrição |
|---|---|---|
| POST | `/produtos` | Cadastra um produto |
| GET | `/produtos` | Lista todos os produtos |
| GET | `/produtos/{id}` | Busca produto por id |
| PUT | `/produtos/{id}` | Atualiza produto |
| DELETE | `/produtos/{id}` | Remove produto |

### Pedidos (`/pedidos`)

| Método | Rota | Descrição |
|---|---|---|
| POST | `/pedidos` | Cadastra um pedido |
| GET | `/pedidos` | Lista todos os pedidos |
| GET | `/pedidos/{id}` | Busca pedido por id |

Todas as rotas acima (exceto login e cadastro de usuário) exigem o header `Authorization`.
