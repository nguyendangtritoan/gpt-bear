# Use an existing image as a base image
FROM maven:3.6.3-jdk-8-slim as build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml file to the container
COPY pom.xml .

# Download the dependencies
RUN mvn dependency:go-offline

# Copy the rest of the application code to the container
COPY . .

# Build the application
RUN mvn clean package -DskipTests=true

# Use a separate image for the runtime
FROM openjdk:8-jre-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the built artifact from the build stage to the runtime image
COPY --from=build /app/target/*.jar app.jar

# Set the environment variable for the application
ARG MESSENGER_APP_SECRET
ARG MESSENGER_VERIFY_TOKEN
ARG MESSENGER_PAGE_ACCESS_TOKEN
ARG OPENAI_API_KEY

ENV MESSENGER_APP_SECRET ${MESSENGER_APP_SECRET}
ENV MESSENGER_VERIFY_TOKEN ${MESSENGER_VERIFY_TOKEN}
ENV MESSENGER_PAGE_ACCESS_TOKEN ${MESSENGER_PAGE_ACCESS_TOKEN}
ENV OPENAI_API_KEY ${OPENAI_API_KEY}

# Expose the port the application will listen on
EXPOSE 8080

# Start the application
CMD java $JAVA_OPTS -jar app.jar