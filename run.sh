#!/bin/bash

echo "Chat Client-Server Application"
echo "============================"
echo
echo "1. Run both client and server"
echo "2. Run only server"
echo "3. Run only client"
echo "4. Build the application"
echo "5. Exit"
echo

read -p "Enter your choice (1-5): " choice

case $choice in
    1)
        echo "Starting both client and server..."
        java -jar app/build/libs/app.jar both
        ;;
    2)
        echo "Starting server..."
        java -cp app/build/libs/app.jar chatclientserver.ltm.server.ServerMain
        ;;
    3)
        echo "Starting client..."
        java -cp app/build/libs/app.jar chatclientserver.ltm.client.ClientMain
        ;;
    4)
        echo "Building the application..."
        ./gradlew clean build jar
        echo "Build completed. JAR file created at app/build/libs/app.jar"
        ;;
    5)
        echo "Exiting..."
        exit 0
        ;;
    *)
        echo "Invalid choice. Please try again."
        sleep 2
        exec $0
        ;;
esac
