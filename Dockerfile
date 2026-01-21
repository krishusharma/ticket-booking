# Use Ubuntu-based Temurin for full ARM64 support
FROM eclipse-temurin:8-jdk

WORKDIR /app

# Install bash, curl, and GnuPG (needed for sbt repo)
RUN apt-get update && apt-get install -y bash curl gnupg && \
    # Add sbt repository
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add - && \
    apt-get update && apt-get install -y sbt && \
    rm -rf /var/lib/apt/lists/*

# 1. Copy build definition files
COPY build.sbt /app/
COPY project /app/project/

# 2. Trigger sbt to download dependencies (using system sbt)
RUN sbt update

# 3. Copy the rest of the application
COPY . /app

# 4. Build Play app
RUN sbt -J-Xmx2G -J-Xms512M clean stage

EXPOSE 9000

# 5. Run the staged Play app
# Note: The binary name usually matches your project name in build.sbt
CMD ["./target/universal/stage/bin/ticket-booking", \
     "-Dhttp.port=9000", \
     "-Dplay.http.secret.key=ticket-booking-secret", \
     "-Dplay.evolutions.db.default.autoApply=true"]