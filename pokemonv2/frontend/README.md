# Campeonato Pokémon - Frontend

Frontend del simulador de batallas Pokémon. Implementa toda la experiencia de juego: configuración de los dos entrenadores, selección de equipos, vídeo de transición, intro de lanzamiento de Pokémon y arena de combate con animaciones, sonidos, panel de ataques por tipo y sistema de cambio.

Construido sobre **React + Vite**. Consume el backend de Spring Boot para registrar a los entrenadores; toda la lógica de combate (turnos, daño, efectividad, KO, cambios) vive en el cliente.

> Si buscas la documentación general del proyecto (backend + frontend, mecánicas, instalación), consulta el [README principal](../README.md).

---

## 🚀 Arranque rápido

Requisitos:
- **Node.js LTS** (incluye npm)

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

## 📜 Scripts disponibles

| Script | Acción |
|---|---|
| `npm run dev` | Arranca el servidor de desarrollo con HMR |
| `npm run build` | Build de producción en `dist/` |
| `npm run preview` | Sirve el build de producción para probarlo en local |
| `npm run lint` | Ejecuta ESLint sobre `src/` |

---

## 📂 Estructura

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
│   │   ├── pokemons.js                    # Catálogo (espejo de Main.java)
│   │   └── typeChart.js                   # Tabla de tipos (espejo de TypeChart.java)
│   ├── hooks/
│   │   └── useAudio.js                    # Hook reutilizable de audio
│   ├── services/
│   │   └── trainerService.js              # Llamadas a /api/trainers/create
│   ├── utils/
│   │   └── validators.js
│   ├── App.css
│   └── index.css
│
├── index.html
├── vite.config.js
├── eslint.config.js
└── package.json
```

---

## 🧱 Arquitectura del flujo

El árbol de componentes es **lineal**, cada componente decide cuándo cede el control al siguiente:

```
App
└── TrainerSetup           ─►  registra Jugador 1 → Jugador 2
    └── PokemonSelection   ─►  draft de 3 Pokémon por equipo → vídeo entrada.mp4
        └── BattleArena    ─►  intro de lanzamiento → combate → victoria
```

Cada componente sólo conoce a su hijo inmediato y le pasa el estado mínimo (nombres de entrenadores, equipos, referencia al `Audio` compartido…). Esto evita reflows y hace fácil seguir el orden temporal.

### Sincronización de música

- `TrainerSetup` crea la instancia de `Audio(Theme.mp3)` y la guarda en una `ref`.
- Esa `ref` se pasa por props a `PokemonSelection`, que la sigue usando.
- Al iniciar la transición a la batalla, `PokemonSelection`:
  1. Pausa la música de fondo.
  2. Reproduce `entrada.mp4` (con audio propio).
  3. Cuando el vídeo termina (`onEnded`), arranca `Audio(combate.m4a)` y monta `BattleArena`.
- `BattleArena` recibe esa instancia de música de combate y la usa para el botón 🔊/🔇.

Esto evita el clásico bug de "se solapan dos canciones" o "la música vuelve sola después de pausarla".

### Catálogo y tabla de tipos

Los archivos `constants/pokemons.js` y `constants/typeChart.js` son **una copia 1:1** del modelo del backend (`Main.java` y `TypeChart.java`). El frontend no llama al servidor para calcular daño: lo resuelve en cliente. Si tocas estos archivos, **acuérdate de actualizar el equivalente en Java** para mantener la coherencia.

---

## 🎨 Convenciones de código

- **Componentes funcionales** + hooks de React.
- **CSS modular por componente** (`Component.jsx` + `Component.css` en la misma carpeta).
- **Tipos en español** en frontend y backend (`FUEGO`, `AGUA`, `PLANTA`, `NORMAL`), traducidos a `Fuego/Agua/Planta/Normal` mediante `TYPE_LABELS` cuando hace falta mostrar al usuario.
- **Audio API nativa** (`new Audio(src)`), sin librerías externas.
- Los **refs** se usan para todo lo que no debe disparar re-renders (instancias de audio, flags de "ya ejecutado").

---

## 🧪 Linting y formateo

```sh
npm run lint
```

ESLint está configurado en `eslint.config.js`.

---

## 🐛 Notas y mejoras futuras

- `lanzar.gif` se reproduce a la velocidad nativa del gif. Para tener control real (slow-mo, sincronización con `onEnded`), conviene convertirlo a un `.mp4` y usar `<video playbackRate={0.5}>`.
- Si se añaden más Pokémon o tipos, **mantener `pokemons.js`/`typeChart.js` sincronizados con su contraparte Java**.
- Migración a TypeScript pendiente (la base ya está limpia y tipable).
- Tests con Vitest aún no implementados.
