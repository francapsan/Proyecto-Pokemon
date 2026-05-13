# Contributing to pokemonv2

Thank you for your interest in contributing to the pokemonv2 project! This document provides guidelines and setup instructions for developers.

## Development Environment Setup

### Java 25 LTS Installation

This project requires **Java 25 LTS** (Long-Term Support until September 2029).

#### Linux/macOS
```bash
# Using SDKMAN (recommended)
sdk install java 25.0.3-tem
sdk use java 25.0.3-tem

# Or download from Adoptium
# https://adoptium.net/temurin/releases/?version=25
```

#### Windows
1. Download from [Adoptium](https://adoptium.net/temurin/releases/?version=25)
2. Run the installer
3. Verify installation:
   ```powershell
   java -version
   # Should show: openjdk version "25.0.3" or later
   ```

### Maven 3.9+ Installation

#### Linux/macOS
```bash
# Using Homebrew
brew install maven

# Or download from https://maven.apache.org/download.cgi
# Verify: mvn -version
```

#### Windows
1. Download from [Apache Maven](https://maven.apache.org/download.cgi)
2. Extract to a location (e.g., `C:\Maven`)
3. Add to PATH environment variable
4. Verify: `mvn -version` (should show Maven 3.9.0 or later)

### Node.js Installation

Download from [nodejs.org](https://nodejs.org/) - LTS version recommended.

## Backend Development

### Compile and Build
```bash
# Compile source code
mvn clean compile

# Run tests
mvn clean test

# Build JAR
mvn clean package

# Run the application
mvn spring-boot:run
```

### Running Tests
```bash
# All tests
mvn clean test

# Single test class
mvn test -Dtest=com.pokemon.AppTest

# Run with coverage
mvn clean test jacoco:report
```

### Code Style Guidelines

- Follow Google Java Style Guide
- Use meaningful variable names
- Add Javadoc comments for public APIs
- Write unit tests for new functionality (aim for >80% coverage)

### Debugging

Set environment variables to enable debug output:
```bash
# Unix/macOS
export MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"

# Windows PowerShell
$env:MAVEN_OPTS = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"

mvn spring-boot:run
```

## Frontend Development

### Setup
```bash
cd frontend
npm install
```

### Development Server
```bash
npm run dev
```

### Running Tests
```bash
npm test
```

### Build for Production
```bash
npm run build
```

## Git Workflow

### Branch Naming
- Feature: `feature/description`
- Bug fix: `bugfix/description`
- Hotfix: `hotfix/description`
- Upgrade: `appmod/java-upgrade-<SESSION_ID>`

### Commit Messages
```
Type(scope): Short description (50 chars max)

Longer explanation if needed. Reference issues like #123.
```

### Pull Request Process
1. Create a feature branch
2. Make your changes with meaningful commits
3. Ensure all tests pass locally: `mvn clean test`
4. Push to your fork
5. Create a Pull Request with description of changes
6. Wait for CI pipeline to pass
7. Request code review

## CI/CD Pipeline

The project uses GitHub Actions for continuous integration:

- **Trigger**: Push to any branch or Pull Request
- **Java Version**: Java 25 LTS (Temurin)
- **Build Tool**: Maven 3.9+
- **Steps**: Compile + Test

See `.github/workflows/ci.yml` for configuration.

## Java 25 LTS Support Timeline

This project targets **Java 25 LTS** which receives:
- ✅ Bug fixes until September 2027
- ✅ Security updates until September 2029
- ✅ 5-year support window

For information about future Java releases, see [Oracle Java SE Support Roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html).

## Known CVEs and Security

### Current Known Issues
- Spring Boot 3.4.0 has 3 HIGH severity CVEs (see summary.md for details)
- Plan to upgrade to Spring Boot 3.4.16+ in the next maintenance release

### Security Reporting
If you discover a security vulnerability, please email the maintainers privately. Do not open a public issue.

## Common Issues and Troubleshooting

### "Java version not compatible"
```bash
# Verify Java version
java -version
# Should be 25.0.3 or later

# If multiple Java versions installed, set JAVA_HOME
export JAVA_HOME=/path/to/java/25
```

### Maven build failures
```bash
# Clear Maven cache
mvn clean

# Update dependencies
mvn dependency:tree
```

### Frontend not connecting to backend
- Ensure backend is running: `mvn spring-boot:run`
- Check backend URL in `frontend/src/constants/config.js`
- Verify CORS settings are correct

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Java 25 Release Notes](https://www.oracle.com/java/technologies/javase/25all-relnotes.html)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Node.js Documentation](https://nodejs.org/en/docs/)

## Questions?

Feel free to open an issue or discussion in the repository for questions about contributing.

---

**Last Updated**: May 13, 2026  
**Java Target**: 25 LTS (support until September 2029)
