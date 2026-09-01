# Campeonato Pokémon - Simulador de Batallas (Backend + Frontend)

Simulador interactivo de batallas Pokémon para dos jugadores en local. Cada jugador crea su entrenador, monta un equipo de 3 Pokémon y se enfrenta al rival en una batalla por turnos al más puro estilo de los juegos clásicos: con animaciones, sonidos, música de fondo, intro de lanzamiento, cuadro de HP, pokeballs en espera, panel de ataques por tipo y posibilidad de cambiar de Pokémon en mitad del combate.

El proyecto se divide en dos partes:

- **Backend (Java + Spring Boot)**: define el dominio (Pokémon, ataques, tipos, tabla de efectividad, entrenadores) y expone endpoints REST (`/api/trainers/create`, `/api/pokemons`).
- **Frontend (React + Vite)**: toda la experiencia de juego ocurre aquí — configuración de jugadores, draft de Pokémon, vídeo de entrada y arena de combate.

---

## Características

### Pantalla de configuración de entrenador

- **Registro por turnos** de los dos jugadores: nombre + género (chico/chica).
- **Mensaje de bienvenida personalizado por género** ("¡Bienvenido…" o "¡Bienvenida…").
- **Sprite animado del entrenador** que aparece según el género elegido.
- Música de fondo (`Theme.mp3`) durante todo el setup, con botón para silenciar.
- Validación cliente + servidor (Bean Validation en el backend, `trainerValidator` en el frontend).

### Fase de selección (Draft)

- Cada jugador elige **3 Pokémon** por turnos: primero el Jugador 1 escoge sus 3, después el Jugador 2.
- **Selección única por entrenador**: un mismo jugador no puede repetir Pokémon dentro de su equipo, pero el rival sí puede coger el mismo.
- Al seleccionar un Pokémon, suena su **grito característico**.
- Los Pokémon disponibles muestran su nombre, gif animado y tipo traducido al español.
- El catálogo se carga desde el backend (`GET /api/pokemons`); si el backend está caído, hay fallback local automático.

### Transición a la batalla

- Al cerrar la selección se reproduce un **vídeo de entrada a pantalla completa** (`entrada.mp4`) con su propio audio.
- La **música de fondo se pausa** mientras el vídeo se reproduce.
- La **música de combate** (`combate.m4a`) sólo arranca cuando el vídeo termina.

### Intro de lanzamiento de Pokémon

Antes de empezar el combate, cada jugador "lanza" a su primer Pokémon al campo con diálogo, animación de pokéball, aparición del sprite y grito. Luego aparecen los cuadros de HP, pokeballs y panel de ataques.

### Sistema de combate

- **Turnos alternos por velocidad**: el Pokémon más rápido ataca primero, luego se va alternando el turno.
- **3 ataques por Pokémon**, coloreados por tipo (rojo = Fuego, azul = Agua, verde = Planta, gris = Normal).
- **Tabla de efectividad** sincronizada 1:1 con el backend (`TypeChart.java`):
  - AGUA → FUEGO ×2 · AGUA → PLANTA ×0.5
  - FUEGO → PLANTA ×2 · FUEGO → AGUA ×0.5
  - PLANTA → AGUA ×2 · PLANTA → FUEGO ×0.5
  - NORMAL ×1 contra todo
- **Fórmula de daño**: `daño = floor(daño_base × multiplicador)`. HP nunca baja de 0.
- **Mensajes contextuales**: "¡Es súper efectivo!" / "No es muy efectivo…".
- **Animaciones**: embestida del atacante, proyectil viajando (por tipo), sacudida del defensor, parpadeo de la barra de HP.
- **Cambio de Pokémon**: relevo manual con "Cambiar" (consume turno; el Pokémon retirado conserva su HP residual). Relevo automático al recibir KO.
- **Condición de victoria**: cuando un entrenador se queda sin Pokémon vivos.

### Control de música

Botón 🔊/🔇 en cada pantalla pausa/reanuda la música. La música de combate no se solapa con la del vídeo de entrada.

---

## Pokémon disponibles

| Pokémon | Tipo | HP | Velocidad | Ataques |
|---|---|---|---|---|
| **Charizard** | Fuego | 78 | 100 | Lanzallamas (40, FUEGO) · Cola Dragón (20, NORMAL) · Vuelo (25, NORMAL) |
| **Blastoise** | Agua | 79 | 78 | Hidrobomba (40, AGUA) · Mordisco (20, NORMAL) · Puño Certero (25, NORMAL) |
| **Venusaur** | Planta | 80 | 80 | Rayo Solar (40, PLANTA) · Hoja Afilada (20, NORMAL) · Terremoto (25, NORMAL) |

**Punto único de verdad**: el catálogo vive en el backend (`PokemonCatalog.java`) y se expone por `GET /api/pokemons`. El frontend consume ese endpoint; `frontend/src/constants/pokemons.js` sólo se usa como fallback offline si el backend no responde.

---

## Tecnologías

### Backend
- **Java 25 LTS** (soporte hasta septiembre 2029).
- **Spring Boot 4.1.1** con starters modulares: `spring-boot-starter-webmvc`, `spring-boot-starter-validation`, `spring-boot-starter-test`, `spring-boot-starter-webmvc-test`.
- **Apache Maven 3.9+** para build y dependencias.
- **JUnit 5** + **MockMvc** para tests (`@WebMvcTest` del módulo `org.springframework.boot.webmvc.test.autoconfigure`).
- **SLF4J** como fachada de logging.

### Frontend
- **React 19 + Vite 8** (HMR ultra rápido).
- **JavaScript** (preparado para migración a TypeScript).
- **ESLint 9** para linting.
- **Vitest 4** + **@testing-library/react** para testing.
- Audio HTML5 nativo (sin librerías externas) y vídeo HTML5 para la intro.

### DevOps
- **GitHub Actions**: dos jobs (backend + frontend) con caché Maven/npm; el CI ejecuta tests, lint y build en cada push/PR.
- **PowerShell** para automatización local (`start-dev.ps1`, `watch-tests.ps1`).

---

## Estructura del proyecto

```
pokemonv2/
├── src/main/java/com/pokemon/         # Backend (Spring Boot)
│   ├── Main.java                      # Punto de entrada @SpringBootApplication
│   ├── WebConfig.java                 # CORS centralizado (configurable via app.cors.allowed-origins)
│   ├── Pokemon.java                   # Modelo del Pokémon (con SLF4J logging)
│   ├── Attack.java                    # Modelo del ataque
│   ├── PokemonType.java               # Enum AGUA/FUEGO/PLANTA/NORMAL
│   ├── TypeChart.java                 # Tabla de efectividad entre tipos
│   ├── Trainer.java                   # Modelo del entrenador
│   ├── TrainerDTO.java                # DTO de entrada con Bean Validation
│   ├── TrainerResponse.java           # DTO de salida (no filtra la entidad)
│   ├── TrainerController.java         # POST /api/trainers/create
│   ├── PokemonCatalog.java            # Catálogo Pokémon (fuente de verdad)
│   ├── PokemonDTO.java                # DTO de Pokémon para la API
│   ├── PokemonController.java         # GET /api/pokemons
│   └── GlobalExceptionHandler.java    # @RestControllerAdvice → HTTP 400
│
├── src/main/resources/
│   └── application.properties         # spring.application.name, CORS, logging
│
├── src/test/java/com/pokemon/         # 18 tests JUnit 5
│   ├── AppTest.java                   # Daño y efectividad
│   ├── PokemonTest.java               # Validaciones del modelo Pokémon
│   ├── TrainerTest.java               # Validaciones del modelo Trainer
│   ├── TrainerControllerTest.java     # MockMvc del endpoint de creación
│   └── PokemonControllerTest.java     # MockMvc del endpoint del catálogo
│
├── frontend/                          # Frontend (React 19 + Vite 8)
│   ├── public/
│   │   ├── gifs/                      # Sprites, intro, lanzamiento…
│   │   └── sounds/                    # Música, clics, gritos
│   └── src/
│       ├── App.jsx
│       ├── components/
│       │   ├── TrainerSetup.jsx       # Registro de los 2 jugadores
│       │   ├── PokemonSelection.jsx   # Draft de Pokémon + transición/vídeo
│       │   └── BattleArena.jsx        # Arena de combate
│       ├── constants/
│       │   ├── config.js              # URLs de API, rutas de audio, configuración
│       │   ├── pokemons.js            # Catálogo fallback (offline)
│       │   └── typeChart.js           # Tabla de tipos (mirror del backend)
│       ├── services/
│       │   ├── trainerService.js      # POST /api/trainers/create
│       │   └── pokemonService.js      # GET /api/pokemons (con fallback local)
│       ├── utils/
│       │   └── validators.js          # Validación cliente
│       └── test/
│           └── setup.js               # setup Vitest + jest-dom
│
├── .github/workflows/ci.yml           # CI (backend + frontend)
├── watch-tests.ps1                    # Watcher: recompila y lanza `mvn test` al detectar cambios
├── test-helpers.ps1                   # Utilidades PS compartidas
├── start-dev.ps1                      # Arranca backend + frontend en dos ventanas
└── pom.xml                            # Configuración Maven
```

---

## Prerrequisitos

- **JDK 25 LTS** o superior — https://adoptium.net/
- **Apache Maven 3.9+** — https://maven.apache.org/
- **Node.js 20 LTS** o superior — https://nodejs.org/

---

## Instalación y ejecución

### 1. Clonar el repositorio

```sh
git clone <URL_DEL_REPOSITORIO>
cd pokemonv2
```

### 2. Arranque rápido (Windows)

```powershell
./start-dev.ps1
```

Arranca el backend (Spring Boot en `http://localhost:8080`) y el frontend (Vite en `http://localhost:5173`) en dos ventanas separadas. Antes de arrancar Vite, hace polling TCP al puerto 8080 hasta que el backend responde.

### 3. Arranque manual

**Backend** (desde la raíz del proyecto):

```sh
mvn spring-boot:run
```

**Frontend** (en otra terminal):

```sh
cd frontend
npm install
npm run dev
```

Si quieres apuntar a un backend distinto, crea `frontend/.env` con `VITE_API_URL=http://otra-url:puerto`.

---

## Cómo jugar

1. **Pantalla de inicio**: pulsa `Comenzar` en el modal de bienvenida.
2. **Registro del Jugador 1**: nombre + género → `Registrar Jugador 1`.
3. **Registro del Jugador 2**: turno al segundo jugador tras unos segundos.
4. **Draft de Pokémon**: Jugador 1 elige 3, luego Jugador 2. Cada selección reproduce el grito.
5. **Vídeo de entrada**: `entrada.mp4` a pantalla completa.
6. **Intro de lanzamiento**: cada jugador lanza su primer Pokémon.
7. **Combate**: el más veloz ataca primero. Estudia los tipos para maximizar daño. Usa `Cambiar` si quieres relevar a tu Pokémon activo.
8. **KO y relevo automático**: cuando un Pokémon cae, sale el siguiente disponible.
9. **Final**: se anuncia el ganador y se bloquean los controles.

---

## Testing

### Backend

```sh
mvn test                        # 18 tests JUnit 5 + MockMvc
./watch-tests.ps1               # Modo watch: recompila y ejecuta tests al detectar cambios
./watch-tests.ps1 -DebounceMillis 2000
```

`watch-tests.ps1` vigila `src/**/*.java` y en cada cambio hace `mvn clean test`. Utiliza `ConcurrentQueue` en lugar del `lock` que fallaba en el script original.

### Frontend

```sh
cd frontend
npm test                        # 14 tests Vitest
npm run test:watch              # Modo watch
npm run test:ui                 # Interfaz gráfica
npm run lint                    # ESLint
npm run build                   # Build de producción
```

### CI

Cada push/PR ejecuta ambos jobs (backend y frontend) con caché Maven/npm. Los tests que fallen suben `target/surefire-reports/` como artefacto.

---

## Endpoints REST

| Método | Ruta | Descripción | Cuerpo | Respuesta |
|---|---|---|---|---|
| `POST` | `/api/trainers/create` | Crea un entrenador | `{ "name": "Ash", "gender": "chico" }` | `TrainerResponse` con `name`, `gender`, `team` |
| `GET`  | `/api/pokemons` | Devuelve el catálogo | — | Array de `PokemonDTO` |

Errores 400 con mensaje legible vienen del `GlobalExceptionHandler`.

---

## Notas de arquitectura

- **Fuente única de verdad**: el catálogo vive en el backend (`PokemonCatalog`). El frontend consume el endpoint; `constants/pokemons.js` es sólo fallback.
- **CORS centralizado**: `WebConfig` con la propiedad `app.cors.allowed-origins` (coma-separada). Ya no hay `@CrossOrigin` disperso por los controladores.
- **Validación en capas**: Bean Validation (`@Valid`, `@NotBlank`, `@Pattern`) + `GlobalExceptionHandler` en el backend; `trainerValidator` en el frontend.
- **Logging**: `Pokemon` usa SLF4J en lugar de `System.out.println`.
- **Audio**: `Audio` nativo de HTML5 con refs compartidas entre pantallas para que la música persista.
- **Música de combate**: arranca tras `onEnded` del vídeo de entrada.

---

## Techdebt / próximas mejoras

- **Lint del frontend**: quedan 6 errores + 3 warnings pre-existentes (`react-hooks/set-state-in-effect` y `react-hooks/refs`). El CI ejecuta el lint con `continue-on-error: true` hasta refactorizarlo.
- **EvoSuite retirado**: la versión 1.0.6 (última disponible) no soporta Java 25. Si se necesita generación automática de tests, valorar reemplazarlo por una estrategia asistida por IA o volver a introducir Randoop con toolchain de un JDK compatible.
- Migrar frontend a **TypeScript**.
- Convertir `lanzar.gif` a vídeo `.mp4` para poder controlar la velocidad con `playbackRate`.
- Más Pokémon, más tipos, estados alterados, ataques con efectos secundarios.
- Modo "Versus IA" para juego en solitario.
- Persistencia de partidas y leaderboard.
