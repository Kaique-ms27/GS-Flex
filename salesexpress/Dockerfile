# ---------- Etapa 1: compilar com Maven ----------
# As imagens abaixo são multi-arquitetura (funcionam em ARM64 da Oracle e em x86).
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Baixa as dependências primeiro (aproveita o cache do Docker)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Etapa 2: imagem final, só com o JRE ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Fuso horário do Brasil (datas/horas do rodapé do PDF)
ENV TZ=America/Sao_Paulo

# Usuário sem privilégios
RUN useradd --system --create-home appuser
COPY --from=build /app/target/*.jar app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=60", "-jar", "app.jar"]
