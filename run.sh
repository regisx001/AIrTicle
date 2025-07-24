#!/bin/bash

# Load environment variables from .env file
set -a
source .env
set +a

# Run the Spring Boot application
mvn spring-boot:run
