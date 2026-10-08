
# ---------- Etapa 1: Compilação com Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o arquivo de dependências primeiro para aproveitar o cache
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copia o código-fonte e compila a aplicação
COPY src ./src
RUN mvn -B clean package -DskipTests


# ---------- Etapa 2: Execução da aplicação ----------
FROM eclipse-temurin:17-jre

WORKDIR /app

# Fuso horário do Brasil
ENV TZ=America/Sao_Paulo

# Cria usuário sem privilégios administrativos
RUN useradd --system --create-home appuser

# Copia o JAR gerado na etapa de compilação
COPY --from=build /app/target/*.jar app.jar

# Executa com usuário sem privilégios
USER appuser

# Porta padrão do Render
EXPOSE 10000

# Utiliza a porta disponibilizada pelo Render
ENTRYPOINT ["sh", "-c", "exec java -XX:MaxRAMPercentage=60 -Dserver.port=${PORT:-10000} -jar app.jar"]