# 1. Java 17
FROM openjdk:17-jdk-slim

# 2 netcat(nc) 설치 추가
RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*

# 3. JAR 파일 경로 설정 
COPY build/libs/Team-0.0.1-SNAPSHOT.jar /app.jar

# 4. wait-for-it.sh 복사
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# 5. 포트 개방
EXPOSE 8080

# 6. wait-for-it.sh를 통해 DB가 떠 있는지 확인하고 Spring Boot 실행
ENTRYPOINT ["/wait-for-it.sh", "db:3306", "--", "java", "-jar", "/app.jar"]
