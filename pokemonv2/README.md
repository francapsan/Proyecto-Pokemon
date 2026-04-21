# Campeonato Pokémon - Simulador de Batallas en Consola

Este proyecto es un simulador de batallas Pokémon por turnos desarrollado en Java, que se ejecuta en la consola. Dos jugadores pueden crear sus entrenadores, seleccionar un equipo de tres Pokémon en una fase de 'draft' y luego enfrentarse en un combate estratégico hasta que uno de los dos se quede sin Pokémon capaces de luchar.
---

## 🚀 Características

- **Creación de Entrenadores**: Personaliza a los dos jugadores con su propio nombre y género.
- **Fase de Selección (Draft)**: Cada jugador elige por turnos un equipo de 3 Pokémon.
- **Selección Única por Entrenador**: Un jugador no puede elegir el mismo Pokémon dos veces, pero el oponente sí puede seleccionarlo.
- **Sistema de Combate por Turnos**: El orden de ataque en cada ronda se decide por el atributo de velocidad de los Pokémon.
- **Tabla de Tipos**: Implementada la lógica de efectividad elemental (Agua, Fuego, Planta) que modifica el daño:
  - `Súper efectivo` (x2 de daño)
  - `No es muy efectivo` (x0.5 de daño)
- **Gestión de Equipo**: Si un Pokémon es derrotado, el entrenador debe elegir otro de su equipo para continuar la batalla.
- **Condición de Victoria**: El combate termina cuando un entrenador se queda sin Pokémon sanos, declarando al otro como ganador.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Java 
- **Gestión de Proyecto**: Apache Maven
- **Testing (Entorno de desarrollo)**:
  - EvoSuite (Generación automática de tests)
  - PowerShell (Scripts para automatizar el flujo de trabajo de testing)

---

## 📋 Prerrequisitos

Asegúrate de tener instalado lo siguiente en tu sistema:

- **Java Development Kit (JDK)** - Versión 25.
- **Apache Maven** - Para compilar y ejecutar el proyecto.

---

## ⚙️ Instalación y Ejecución

1.  **Clona el repositorio:**
    ```sh
    git clone <URL_DEL_REPOSITORIO>
    ```

2.  **Navega al directorio del proyecto:**
    ```sh
    cd pokemonv2
    ```

3.  **Compila el proyecto usando Maven:**
    ```sh
    mvn compile
    ```

4.  **Ejecuta la aplicación:**
    ```sh
    mvn exec:java -Dexec.mainClass="com.pokemon.Main"
    ```

---

## 🎮 Cómo Jugar

1.  Al iniciar la aplicación, sigue las instrucciones para introducir el **nombre** y **género** de ambos jugadores.
2.  En la **fase de selección**, elige por turnos los Pokémon para tu equipo introduciendo el número correspondiente.
3.  Una vez que ambos equipos estén listos, ¡comienza la batalla!
4.  En tu turno, elige un ataque de la lista para que tu Pokémon lo use.
5.  La batalla continúa hasta que un jugador se quede sin Pokémon. ¡Que gane el mejor entrenador!