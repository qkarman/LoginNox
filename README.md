# ◈ NOX — Adaptive Security System

> Sistema de autenticación biomimético inspirado en la inteligencia del cuervo.  
> NOX no solo verifica tu contraseña — observa cómo te comportas antes de dejarte entrar.

---

## ¿Qué es NOX?

La mayoría de sistemas de login hacen una sola pregunta: *¿la contraseña es correcta?*

NOX hace una pregunta diferente antes de eso: *¿este comportamiento parece legítimo?*

Inspirado en los cuervos — criaturas capaces de reconocer rostros, recordar comportamientos y anticipar amenazas — NOX construye un perfil de comportamiento por usuario y evalúa cada intento de acceso antes de verificar la contraseña. Si el patrón es sospechoso, el acceso se bloquea independientemente de si la contraseña es correcta.

---

## Arquitectura

```
Request HTTP
    ↓
[Spring Security Filter Chain]
    ↓
[HuellaDigitalContextual]     ← Extrae IP, UserAgent, hora, día (todo hasheado)
    ↓
[PatronComportamiento]        ← Compara con historial de intentos del usuario
    ↓
[ScoreConfianza]              ← Calcula puntuación 0-100 con señales fuertes/débiles
    ↓
[NoxDecisionEngine]           ← PERMITIR / DESAFIO / BLOQUEAR
    ↓
[Spring AuthenticationManager] ← Solo si NOX lo aprueba
    ↓
[JwtUtil]                     ← Genera token JWT firmado HS256
    ↓
Response con token
```

### Capas del sistema

| Capa | Clases |
|------|--------|
| **Controladores** | `NoxAuthController`, `NoxProtectedController` |
| **Biomimética** | `NoxDecisionEngine`, `ScoreConfianza`, `PatronComportamiento`, `HuellaDigitalContextual`, `NivelConfianza`, `DecisionAcceso` |
| **Seguridad** | `JwtFilter`, `JwtUtil`, `SecurityConfig`, `EncoderConfig` |
| **Servicios** | `NoxLoginService`, `UserService`, `PasswordService`, `EventoLoginService` |
| **Persistencia** | `UserRepository`, `EventoLoginRepository` |
| **Entidades** | `User`, `Password`, `EventoLogin` |

---

## El algoritmo biomimético

### 1. Observación — `HuellaDigitalContextual`
Extrae el contexto del intento sin guardar datos sensibles:
- IP del cliente → hasheada con SHA-256
- UserAgent → hasheado con SHA-256
- Hora del día (0-23)
- Día de la semana (1-7)

### 2. Memoria — `PatronComportamiento`
Mantiene un historial de los últimos 20 intentos por usuario con una ventana deslizante de 10 minutos. Detecta:
- Frecuencia excesiva (5+ intentos en 1 minuto)
- Cambio de dispositivo entre intentos

### 3. Evaluación — `ScoreConfianza`
Parte de 100 puntos y aplica penalizaciones por señales detectadas:

| Señal | Penalización | Tipo |
|-------|-------------|------|
| Patrón anómalo detectado | -35 | Fuerte |
| Dispositivo inestable | -25 | Fuerte |
| Hora inusual (< 6am o > 10pm) | -15 | Débil* |
| Día inusual (fin de semana) | -10 | Débil* |

*Las señales débiles solo aplican si al menos una señal fuerte está activa. Esto evita falsos positivos para usuarios con hábitos no convencionales.

**Clasificación del score:**
- ≥ 80 → `ALTA`
- ≥ 50 → `MEDIA`  
- < 50 → `BAJA`

### 4. Decisión — `NoxDecisionEngine`

```
intentos ≥ 5 AND nivel == BAJA  →  BLOQUEAR  (HTTP 403)
nivel == MEDIA                   →  DESAFIO   (HTTP 401)
nivel == ALTA                    →  PERMITIR  (pasa a auth)
```

### 5. Persistencia — `EventoLogin`
Cada intento (exitoso o fallido) se persiste en MySQL con email, IP, timestamp y resultado.

---

## Stack tecnológico

**Backend**
- Java 21
- Spring Boot 3
- Spring Security 6
- JWT (jjwt 0.12)
- BCrypt
- Spring Data JPA
- MySQL 8

**Frontend**
- HTML5 + CSS3 + JavaScript vanilla
- Google Fonts (Share Tech Mono, Rajdhani)
- Live Server (desarrollo)

---

## Instalación y configuración

### Requisitos
- Java 21+
- Maven 3.8+
- MySQL 8+

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/nox-login.git
cd nox-login
```

### 2. Crear la base de datos

```sql
CREATE DATABASE login_db;
```

### 3. Configurar `application.properties`

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/login_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=unaClaveSeguraDeMinimo32CaracteresAqui1234
jwt.expiration=3600000

# Servidor
server.port=8082
```

> ⚠️ **Nunca subas `application.properties` con credenciales reales a GitHub.** Agrégalo al `.gitignore`.

### 4. Ejecutar el backend

```bash
mvn spring-boot:run
```

### 5. Abrir el frontend

Abre la carpeta `frontend/` en VS Code y usa Live Server sobre `login.html`.  
El frontend se conecta por defecto a `http://localhost:8082`.

---

## API Endpoints

### Públicos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/nox-auth/register` | Registrar nuevo usuario |
| `POST` | `/nox-auth/login` | Iniciar sesión |

**Registro:**
```json
POST /nox-auth/register
{
  "userName": "usuario",
  "email": "usuario@ejemplo.com",
  "password": { "hash": "Password1!" }
}
```

**Login:**
```json
POST /nox-auth/login
{
  "email": "usuario@ejemplo.com",
  "password": "Password1!"
}
```

**Respuesta exitosa:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "message": "Login autorizado por NOX"
}
```

**Respuesta bloqueada (HTTP 403):**
```json
{
  "message": "Acceso bloqueado por comportamiento sospechoso"
}
```

### Protegidos (requieren `Authorization: Bearer <token>`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/nox/status` | Verifica sesión activa |

---

## Validación de contraseña

NOX requiere contraseñas con:
- Mínimo 8 caracteres
- Al menos una mayúscula
- Al menos un número
- Al menos un carácter especial (`!@#$%^&*...`)

---

## Estructura del proyecto

```
src/main/java/com/nox/login/
├── auth/
│   ├── NoxAuthController.java
│   └── NoxProtectedController.java
├── biometria/
│   ├── DecisionAcceso.java
│   ├── HuellaDigitalContextual.java
│   ├── NivelConfianza.java
│   ├── NoxDecisionEngine.java
│   ├── PatronComportamiento.java
│   └── ScoreConfianza.java
├── dto/
│   └── LoginRequestDTO.java
├── entity/
│   ├── EventoLogin.java
│   ├── Password.java
│   └── User.java
├── excepciones/
│   ├── GlobalExcepcionHandler.java
│   ├── PasswordInvalidoException.java
│   └── RecursoNoEncontradoExcepcion.java
├── repository/
│   ├── EventoLoginRepository.java
│   └── UserRepository.java
├── security/
│   ├── EncoderConfig.java
│   ├── JwtFilter.java
│   ├── JwtUtil.java
│   └── SecurityConfig.java
└── service/
    ├── EventoLoginService.java
    ├── IUserService.java
    ├── NoxLoginService.java
    ├── PasswordService.java
    └── UserService.java
```

---

## .gitignore recomendado

```
# Credenciales
src/main/resources/application.properties

# Build
target/
*.class

# IDE
.idea/
*.iml
.vscode/

# OS
.DS_Store
Thumbs.db
```

---

## Mejoras planeadas para producción

- [ ] Persistencia del patrón de comportamiento en BD (actualmente en RAM)
- [ ] Recuperación de contraseña por email (Spring Mail + token temporal)
- [ ] Pesos del score dinámicos con ML adaptativo
- [ ] Refresh token mechanism
- [ ] Rate limiting a nivel de red (nginx / API Gateway)
- [ ] Roles y permisos (RBAC)
- [ ] Tests unitarios para `ScoreConfianza` y `NoxDecisionEngine`
- [ ] Variables de entorno para secretos

---

## Concepto

> *"El cuervo no ataca a quien lo amenaza. Primero observa. Recuerda. Y cuando llega el momento, actúa con precisión."*

NOX nació de investigar el comportamiento corvino como modelo de toma de decisiones en sistemas de seguridad. El resultado es un **Adaptive Authentication Pipeline** con early exit: si el comportamiento es bloqueante, la contraseña nunca se evalúa. Solo el cuervo decide quién entra.

---

## Autor

Qkarman  
Backend especializado en Java · Spring Boot · Backend.
