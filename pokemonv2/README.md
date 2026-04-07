# Pokemon Project Test Generation

This project includes an automated system for generating JUnit tests using EvoSuite Maven plugin.

## Setup

No additional downloads required. EvoSuite is handled via Maven.

## How it works

- The system detects changes to Java files in `src/main/java/com/pokemon/` using `git diff`.
- For each changed class, it generates tests using EvoSuite Maven plugin.
- The generated test methods are extracted and appended to `AppTest.java`.

## Automation

- **Pre-commit hook**: Runs automatically on `git commit` via `.git/hooks/pre-commit`.
- **Maven profile**: Run manually with `mvn clean compile -Pgenerate-tests`.

## Running Tests

```bash
mvn test
```

## Manual Generation

To generate tests manually for a specific class:

```bash
mvn evosuite:generate -DtargetClass=com.pokemon.Pokemon
```

Then run the PowerShell script:

```powershell
.\merge-tests.ps1
```

## Incremental Updates

The system only generates tests for modified classes, ensuring incremental updates without regenerating all tests.