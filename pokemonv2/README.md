# Campeonato Pokémon - Simulador de Batallas (Backend + Frontend)

Simulador interactivo de batallas Pokémon para dos jugadores en local. Cada jugador crea su entrenador, monta un equipo de 3 Pokémon y se enfrenta al rival en una batalla por turnos al más puro estilo de los juegos clásicos: con animaciones, sonidos, música de fondo, intro de lanzamiento, cuadro de HP, pokeballs en espera, panel de ataques por tipo y posibilidad de cambiar de Pokémon en mitad del combate.

El proyecto se divide en dos partes:

- **Backend (Java + Spring Boot)**: define el dominio (Pokémon, ataques, tipos, tabla de efectividad, entrenadores) y expone un pequeño endpoint REST para registrar entrenadores.
- **Frontend (React + Vite)**: toda la experiencia de juego ocurre aquí — configuración de jugadores, draft de Pokémon, vídeo de entrada y arena de combate.

---

## 🚀 Características

### Pantalla de configuración de entrenador

- **Registro por turnos** de los dos jugadores: nombre + género (chico/chica).
- **Mensaje de bienvenida personalizado por género** ("¡Bienvenido…" o "¡Bienvenida…").
- **Sprite animado del entrenador** que aparece según el género elegido.
- Música de fondo (`Theme.mp3`) durante todo el setup, con botón 🔊/🔇 para silenciar.

### Fase de selección (Draft)

- Cada jugador elige **3 Pokémon** por turnos: primero el Jugador 1 escoge sus 3, después el Jugador 2.
- **Selección única por entrenador**: un mismo jugador no puede repetir Pokémon dentro de su equipo, pero el rival sí puede coger el mismo.
- Al seleccionar un Pokémon, suena su **grito característico**.
- Los Pokémon disponibles muestran su nombre, gif animado y tipo traducido al español.

### Transición a la batalla

- Al cerrar la selección se reproduce un **vídeo de entrada a pantalla completa** (`entrada.mp4`) con su propio audio.
- La **música de fondo se pausa** mientras el vídeo se reproduce para que no se solapen audios.
- La **música de combate** (`combate.m4a`) sólo arranca cuando el vídeo termina, así no hay solapamiento ni silencios extraños.

### Intro de lanzamiento de Pokémon

Antes de empezar el combate, cada jugador "lanza" a su primer Pokémon al campo:

1. Diálogo `¡<Entrenador> envía a <Pokémon>!`.
2. Animación con el gif `lanzar.gif` (pokéball cayendo y rebotando).
3. La pokéball "se abre": aparece el sprite real del Pokémon y suena su grito.
4. Se repite para el Jugador 2.
5. Sólo entonces aparecen los cuadros de HP, las pokeballs y el panel de ataques.

### Sistema de combate

- **Turnos alternos por velocidad**: el Pokémon más rápido ataca primero, luego se va alternando el turno.
- **3 ataques por Pokémon**, elegibles desde un panel con botones coloreados por tipo (rojo = Fuego, azul = Agua, verde = Planta, gris = Normal).
- **Tabla de efectividad** sincronizada 1:1 con el backend (`TypeChart.java`):
  - AGUA → FUEGO ×2 · AGUA → PLANTA ×0.5
  - FUEGO → PLANTA ×2 · FUEGO → AGUA ×0.5
  - PLANTA → AGUA ×2 · PLANTA → FUEGO ×0.5
  - NORMAL ×1 contra todo
- **Fórmula de daño**: `daño = floor(daño_base × multiplicador)`. HP nunca baja de 0.
- **Mensajes contextuales**:
  - "¡Es súper efectivo!" cuando el multiplicador es mayor a 1.
  - "No es muy efectivo…" cuando es menor a 1.
- **Animaciones de combate**: embestida del atacante, proyectil viajando (por tipo), sacudida del defensor al recibir daño, parpadeo de la barra de HP.
- **Proyectiles por tipo**: Fuego (naranja), Agua (azul), Planta (verde-amarillo). Los ataques de tipo Normal son cuerpo a cuerpo (sin proyectil).

### Cambio de Pokémon

- Botón **"Cambiar"** en el panel de acciones.
- Abre un selector con los **Pokémon disponibles** del equipo activo: distintos del activo y con HP > 0.
- Al cambiar:
  - El Pokémon que sale **conserva su HP residual** (si vuelve a entrar más adelante, vuelve con ese HP, no a máximo).
  - Se reproduce su grito y se aplica la animación de retirada.
  - **Consume el turno** del jugador (regla clásica).
- Botón **"Volver"** para cancelar la selección si el jugador se arrepiente.

### Cuadros laterales por jugador

Encima de cada Pokémon, en la zona izquierda o derecha de la arena, aparecen:

- **Cuadro pequeño con las pokeballs en espera** (`espera.gif`): tantas pokeballs como Pokémon **aún vivos** le quedan al entrenador. Si uno cae KO, su pokeball desaparece. A la derecha de las pokeballs se muestra el **nombre del entrenador**.
- **Cuadro grande de HP**: nombre del Pokémon activo, barra de vida coloreada según el porcentaje (verde > 50%, amarillo entre 20% y 50%, rojo ≤ 20%) y HP numérico (`X / Y`).

Ambos cuadros sólo aparecen una vez su Pokémon ya ha sido lanzado al campo.

### KO (Pokémon debilitado)

- Cuando un Pokémon baja a 0 HP:
  1. Suena su grito.
  2. Diálogo `¡<Pokémon> se ha debilitado!`.
  3. Animación de desvanecimiento (opacidad → 0, caída, escala de grises).
- Se envía el **siguiente Pokémon vivo** del equipo (no necesariamente el siguiente en orden si hubo cambios previos).
- Después del KO, el turno lo recibe el equipo que acaba de mandar al nuevo Pokémon.

### Condición de victoria

- Cuando un entrenador se queda sin Pokémon con HP > 0:
  - Diálogo `¡<Ganador> ha ganado la batalla!`.
  - El panel de ataques desaparece y los controles quedan bloqueados.

### Control de música

- Botón 🔊/🔇 en cada pantalla:
  - En el setup y la selección: pausa/reanuda `Theme.mp3`.
  - En la batalla: pausa/reanuda `combate.m4a`.
- La pausa es **definitiva**: ningún efecto secundario "revive" la música por detrás.

---

## 🎲 Pokémon disponibles

| Pokémon | Tipo | HP | Velocidad | Ataques |
|---|---|---|---|---|
| **Charizard** | Fuego | 78 | 100 | Lanzallamas (40, FUEGO) · Cola Dragón (20, NORMAL) · Vuelo (25, NORMAL) |
| **Blastoise** | Agua | 79 | 78 | Hidrobomba (40, AGUA) · Mordisco (20, NORMAL) · Puño Certero (25, NORMAL) |
| **Venusaur** | Planta | 80 | 80 | Rayo Solar (40, PLANTA) · Hoja Afilada (20, NORMAL) · Terremoto (25, NORMAL) |

Los datos están duplicados (sincronizados) entre `src/main/java/com/pokemon/Main.java` (backend) y `frontend/src/constants/pokemons.js` + `frontend/src/constants/typeChart.js` (frontend).

---

## 🛠️ Tecnologías

### Backend
- **Java 25 LTS** (soporte hasta septiembre 2029)
- **Spring Boot 4.0.6**
- **Apache Maven 3.9+** para build y dependencias
- Testing:
  - **JUnit 5** (tests unitarios)
  - **EvoSuite** (generación automática de tests)
  - Scripts PowerShell para automatizar el flujo de tests (`merge-tests.ps1`, `watch-tests.ps1`, `test-helpers.ps1`)

### Frontend
- **React 18 + Vite** (HMR ultra rápido)
- **JavaScript** (preparado para migración a TypeScript)
- **ESLint** para linting
- **Vitest** para testing (configurable)
- **Zod** para validación de esquemas
- Audio HTML5 nativo (sin librerías externas) y vídeo HTML5 para la intro

---

## 📂 Estructura del proyecto

```
pokemonv2/
├── src/main/java/com/pokemon/         # Backend (Spring Boot)
│   ├── Main.java                      # Punto de entrada + catálogo de Pokémon
│   ├── Pokemon.java                   # Modelo del Pokémon (HP, ataques, recibir daño)
│   ├── Attack.java                    # Modelo del ataque (nombre, daño, tipo)
│   ├── PokemonType.java               # Enum de tipos (AGUA, FUEGO, PLANTA, NORMAL)
│   ├── TypeChart.java                 # Tabla de efectividad entre tipos
│   ├── Trainer.java                   # Modelo del entrenador
│   ├── TrainerDTO.java                # DTO para la API
│   └── TrainerController.java         # Endpoint REST /api/trainers/create
│
├── frontend/                          # Frontend (React + Vite)
│   ├── public/
│   │   ├── gifs/                      # Sprites animados, intro, lanzamiento, etc.
│   │   │   ├── charizard.gif / blastoise.gif / venusaur.gif
│   │   │   ├── trainer-boy.gif / trainer-girl.gif
│   │   │   ├── espera.gif             # Pokeball en espera (cuadro pequeño)
│   │   │   ├── lanzar.gif             # Animación de lanzamiento de Pokémon
│   │   │   ├── entrada.mp4            # Vídeo de transición a la batalla
│   │   │   └── carga.gif
│   │   └── sounds/
│   │       ├── Theme.mp3              # Música de setup/selección
│   │       ├── combate.m4a            # Música de la arena
│   │       ├── Click Button.mp3       # Sonido de clic
│   │       └── Grito_de_{Charizard|Blastoise|Venusaur}.ogg
│   └── src/
│       ├── App.jsx
│       ├── components/
│       │   ├── TrainerSetup.jsx       # Registro de los 2 jugadores
│       │   ├── PokemonSelection.jsx   # Draft de Pokémon + transición/vídeo
│       │   └── BattleArena.jsx        # Arena de combate (intro, ataques, KO, cambios)
│       ├── constants/
│       │   ├── config.js              # URLs de API, rutas de audio, configuración
│       │   ├── pokemons.js            # Catálogo (espejo de Main.java)
│       │   └── typeChart.js           # Tabla de tipos (espejo de TypeChart.java)
│       ├── hooks/
│       │   └── useAudio.js
│       ├── services/
│       │   └── trainerService.js      # Llamadas a la API REST
│       └── utils/
│           └── validators.js
│
└── pom.xml                            # Configuración Maven
```

---

## 📋 Prerrequisitos

- **JDK 25 LTS** o superior — https://adoptium.net/
- **Apache Maven 3.9+** — https://maven.apache.org/
- **Node.js LTS** (incluye npm) — https://nodejs.org/

---

## ⚙️ Instalación y ejecución

### 1. Clonar el repositorio

```sh
git clone <URL_DEL_REPOSITORIO>
cd pokemonv2
```

### 2. Backend (Spring Boot)

Desde la raíz del proyecto:

```sh
mvn compile
mvn spring-boot:run
```

La API queda escuchando en `http://localhost:8080` (configurable). El frontend está configurado para apuntar por defecto a esta URL mediante `VITE_API_URL` (ver `frontend/src/constants/config.js`).

### 3. Frontend (React + Vite)

En otra terminal:

```sh
cd frontend
npm install
npm run dev
```

Por defecto Vite arranca en `http://localhost:5173`. Abre esa URL en el navegador y empieza a jugar.

> Si quieres apuntar a un backend distinto, crea un fichero `frontend/.env` con `VITE_API_URL=http://otra-url:puerto`.

---

## 🎮 Cómo jugar

1. **Pantalla de inicio**: pulsa `Comenzar` en el modal de bienvenida.
2. **Registro del Jugador 1**: escribe tu nombre, elige género (chico/chica) y pulsa `Registrar Jugador 1`. Aparece tu sprite y un mensaje de bienvenida personalizado.
3. **Registro del Jugador 2**: la app espera unos segundos y pasa el turno al segundo jugador.
4. **Draft de Pokémon**:
   - El Jugador 1 elige 3 Pokémon (uno por uno; sonará el grito de cada uno).
   - Luego el Jugador 2 elige los suyos. Puede repetir Pokémon que ya cogió el rival.
5. **Vídeo de entrada**: se reproduce `entrada.mp4` a pantalla completa. La música de fondo se pausa.
6. **Intro de lanzamiento**:
   - El Jugador 1 lanza su primer Pokémon (gif de pokéball → sprite real).
   - El Jugador 2 hace lo propio.
7. **Combate**:
   - Empieza atacando el Pokémon con más velocidad.
   - El panel de ataques aparece en el lado del jugador en turno. Elige uno de los 3 ataques.
   - Anímate a estudiar el tipo del ataque y el tipo del rival: la ventaja elemental dobla el daño.
   - Pulsa `Cambiar` si quieres relevar a tu Pokémon activo (consume tu turno, pero conserva su HP residual).
8. **KO y relevo automático**: cuando un Pokémon cae, sale el siguiente disponible. Si un entrenador no tiene más, ¡pierde la batalla!
9. **Final**: el ganador se anuncia en el cuadro de diálogo y los controles quedan bloqueados.

---

## 🧪 Testing del backend

El proyecto incluye varios scripts PowerShell para automatizar el ciclo de testing con JUnit + EvoSuite:

```sh
./test-helpers.ps1     # utilidades varias
./watch-tests.ps1      # modo watch
./merge-tests.ps1      # mergea tests generados por EvoSuite
```

---

## 🔧 Notas de arquitectura

- **Catálogo duplicado a propósito**: los datos de Pokémon y la tabla de efectividad existen tanto en Java como en JS. El backend es la fuente de verdad conceptual, el frontend mantiene una copia 1:1 para evitar una llamada extra al servidor por cada cálculo de combate. Cualquier cambio (nuevo Pokémon, ajuste de daño, nueva interacción de tipos) requiere actualizar **ambos lados** (`Main.java`+`TypeChart.java` y `constants/pokemons.js`+`constants/typeChart.js`).
- **Audio**: los assets de sonido se manejan con la API `Audio` nativa de HTML5. La música persiste entre la pantalla de selección y la batalla mediante refs compartidas para evitar saltos o solapamientos.
- **Música de combate**: arranca tras el `onEnded` del vídeo de entrada, no antes. Así el audio del vídeo nunca se mezcla con la música.

---

## 🐛 Próximas mejoras (ideas)

- Convertir `lanzar.gif` a vídeo `.mp4` para poder controlar la velocidad de reproducción con `playbackRate`.
- Soporte para más Pokémon y tipos.
- Estados alterados (paralizado, quemado, dormido…).
- Movimientos con efectos secundarios (subir/bajar stats, recargar, multi-hit…).
- Modo "Versus IA" para juego en solitario.
- Persistencia de partidas y leaderboard.
