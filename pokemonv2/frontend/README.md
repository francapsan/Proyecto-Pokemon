# Campeonato Pokémon - Frontend

Frontend del simulador de batallas Pokémon. Implementa toda la experiencia de juego: configuración de los dos entrenadores, selección de equipos, vídeo de transición, intro de lanzamiento de Pokémon y arena de combate con animaciones, sonidos, panel de ataques por tipo y sistema de cambio.

Construido sobre **React 19 + Vite 8**. Consume el backend de Spring Boot para registrar entrenadores y para obtener el catálogo de Pokémon; el motor de combate (turnos, daño, efectividad, KO, cambios) vive en el cliente.

> Si buscas la documentación general del proyecto (backend + frontend, mecánicas, instalación), consulta el [README principal](../README.md).

---

## Arranque rápido

Requisitos:
- **Node.js 20 LTS** o superior (incluye npm)

```sh
npm install
npm run dev
```

Vite arranca por defecto en `http://localhost:5173`.

### Variables de entorno

| Variable | Por defecto | Descripción |
|---|---|---|
| `VITE_API_URL` | `http://localhost:8080` | URL del backend Spring Boot |

Crea un `.env` en `frontend/` si necesitas apuntar a otro backend:

```env
VITE_API_URL=http://localhost:9090
```

---

## Scripts disponibles

| Script | Acción |
|---|---|
| `npm run dev` | Arranca el servidor de desarrollo con HMR |
| `npm run build` | Build de producción en `dist/` |
| `npm run preview` | Sirve el build de producción para probarlo en local |
| `npm run lint` | Ejecuta ESLint sobre `src/` |
| `npm test` | Ejecuta la suite Vitest una vez (modo CI) |
| `npm run test:watch` | Vitest en modo watch |
| `npm run test:ui` | Vitest con interfaz gráfica |

---

## Estructura

```
frontend/
├── public/
│   ├── gifs/
│   │   ├── charizard.gif / blastoise.gif / venusaur.gif   # Sprites de Pokémon
│   │   ├── trainer-boy.gif / trainer-girl.gif              # Entrenadores
│   │   ├── espera.gif                                      # Pokeball en espera (cuadro pequeño)
│   │   ├── lanzar.gif                                      # Lanzamiento de Pokémon
│   │   ├── entrada.mp4                                     # Vídeo de transición
│   │   └── carga.gif                                       # Indicador de carga
│   ├── sounds/
│   │   ├── Theme.mp3                                       # Música de setup/selección
│   │   ├── combate.m4a                                     # Música de la arena
│   │   ├── Click Button.mp3                                # Sonido de clic
│   │   └── Grito_de_{Charizard|Blastoise|Venusaur}.ogg     # Gritos por Pokémon
│   ├── favicon.svg / icons.svg
│
├── src/
│   ├── App.jsx                            # Entrada del árbol de componentes
│   ├── main.jsx                           # Bootstrap de React
│   ├── components/
│   │   ├── TrainerSetup.jsx               # Registro de los 2 jugadores
│   │   ├── TrainerSetup.css
│   │   ├── PokemonSelection.jsx           # Draft + vídeo de entrada
│   │   ├── PokemonSelection.css
│   │   ├── BattleArena.jsx                # Arena: intro de lanzamiento, combate, KO, cambios
│   │   └── Battle.css
│   ├── constants/
│   │   ├── config.js                      # API_CONFIG, AUDIO_CONFIG, GAME_CONFIG
│   │   ├── pokemons.js                    # Catálogo fallback (offline)
│   │   ├── typeChart.js                   # Tabla de tipos (mirror del backend)
│   │   └── typeChart.test.js              # Tests Vitest
│   ├── services/
│   │   ├── trainerService.js              # POST /api/trainers/create
│   │   ├── trainerService.test.js
│   │   ├── pokemonService.js              # GET /api/pokemons (con fallback local)
│   │   └── pokemonService.test.js
│   ├── utils/
│   │   ├── validators.js                  # Validación cliente
│   │   └── validators.test.js
│   ├── test/
│   │   └── setup.js                       # Bootstrap Vitest + jest-dom
│   ├── App.css
│   └── index.css
│
├── index.html
├── vite.config.js                          # Config Vite + Vitest (jsdom, globals)
├── eslint.config.js
└── package.json
```

> `hooks/useAudio.js` fue eliminado en la última pasada de limpieza porque no se usaba en ningún componente y arrastraba warnings de `react-hooks/refs`. Si vuelve a hacer falta, hay que reimplementarlo evitando escribir en `ref.current` durante el render.

---

## Arquitectura del flujo

El árbol de componentes es **lineal**, cada componente decide cuándo cede el control al siguiente:

```
App
└── TrainerSetup           ─►  registra Jugador 1 → Jugador 2
    └── PokemonSelection   ─►  fetch a /api/pokemons → draft → vídeo entrada.mp4
        └── BattleArena    ─►  intro de lanzamiento → combate → victoria
```

Cada componente sólo conoce a su hijo inmediato y le pasa el estado mínimo (nombres de entrenadores, equipos, referencia al `Audio` compartido, catálogo de Pokémon…).

### Fuente única de verdad del catálogo

- `PokemonSelection` llama a `pokemonService.listPokemons()` (endpoint `GET /api/pokemons`) al montarse.
- La respuesta se normaliza y se guarda en el state `catalog`, indexado por nombre.
- Ese `catalog` se pasa por props a `BattleArena`, que lo usa para resolver el estado inicial de cada Pokémon (tipo, velocidad, ataques, sprite, HP).
- Si el backend está caído o el fetch falla, hay **fallback automático** a `constants/pokemons.js`. Ese archivo actúa como réplica offline, no como fuente principal.
- `constants/typeChart.js` sigue siendo un mirror del `TypeChart.java` del backend (los tipos son strings idénticos al enum Java).

### Sincronización de música

- `TrainerSetup` crea la instancia de `Audio(Theme.mp3)` y la guarda en una `ref`.
- Esa `ref` se pasa por props a `PokemonSelection`, que la sigue usando.
- Al iniciar la transición a la batalla, `PokemonSelection`:
  1. Pausa la música de fondo.
  2. Reproduce `entrada.mp4` (con audio propio).
  3. Cuando el vídeo termina (`onEnded`), arranca `Audio(combate.m4a)` y monta `BattleArena`.
- `BattleArena` recibe esa instancia de música de combate y la usa para el botón 🔊/🔇.

Esto evita el clásico bug de "se solapan dos canciones" o "la música vuelve sola después de pausarla".

### Validación en dos capas

- Frontend (`utils/validators.js` → `trainerValidator.validate`): feedback inmediato al usuario antes de disparar el POST.
- Backend (Bean Validation en `TrainerDTO` + `GlobalExceptionHandler`): última barrera de seguridad, siempre presente aunque alguien salte la UI.

---

## Convenciones de código

- **Componentes funcionales** + hooks de React.
- **CSS modular por componente** (`Component.jsx` + `Component.css` en la misma carpeta).
- **Tipos en español** en frontend y backend (`FUEGO`, `AGUA`, `PLANTA`, `NORMAL`), traducidos a `Fuego/Agua/Planta/Normal` mediante `TYPE_LABELS` cuando hace falta mostrar al usuario.
- **Audio API nativa** (`new Audio(src)`), sin librerías externas.
- Los **refs** se usan para todo lo que no debe disparar re-renders (instancias de audio, flags de "ya ejecutado").
- **Servicios** en `services/` para todas las llamadas HTTP; los componentes nunca hacen `fetch` inline.

---

## Testing

Vitest + Testing Library + jsdom. Suite actual: **14 tests en 4 archivos**.

```sh
npm test              # Ejecuta todo una vez
npm run test:watch    # Modo watch
npm run test:ui       # UI navegable
```

| Archivo | Cobertura |
|---|---|
| `constants/typeChart.test.js` | Multiplicadores de efectividad y mensajes contextuales |
| `utils/validators.test.js` | Reglas de nombre, género y validación combinada |
| `services/trainerService.test.js` | POST + propagación de mensajes de error del backend (fetch mockeado) |
| `services/pokemonService.test.js` | GET catálogo + manejo de errores HTTP (fetch mockeado) |

### Configuración

- `vite.config.js` incluye el bloque `test` con `environment: 'jsdom'`, `globals: true` y `setupFiles: './src/test/setup.js'`.
- `src/test/setup.js` importa `@testing-library/jest-dom/vitest` para tener los matchers extendidos.

---

## Linting

```sh
npm run lint
```

ESLint 9 con `eslint-plugin-react-hooks` y `eslint-plugin-react-refresh`. Actualmente hay **6 errores y 3 warnings pre-existentes** en `BattleArena.jsx`, `PokemonSelection.jsx` y `TrainerSetup.jsx` — categorías `react-hooks/set-state-in-effect` y `react-hooks/refs`. El CI los ejecuta con `continue-on-error: true` para no bloquear PRs mientras se refactoriza la gestión de audio.

---

## Notas y mejoras futuras

- Refactorizar los componentes para eliminar los lints de `react-hooks/refs` (pasar refs como props) y `set-state-in-effect` (mover lógica a event handlers o a `useSyncExternalStore`).
- `lanzar.gif` se reproduce a la velocidad nativa del gif. Para tener control real (slow-mo, sincronización con `onEnded`), conviene convertirlo a un `.mp4` y usar `<video playbackRate={0.5}>`.
- Migración a **TypeScript** pendiente.
- Añadir tests de integración de componentes con Testing Library (por ejemplo `TrainerSetup` completo con `fetch` mockeado).
