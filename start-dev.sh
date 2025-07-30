#!/bin/bash

# AIrTicle Development Startup Script
# This script starts both the backend (Spring Boot) and frontend (SvelteKit) in separate terminals

echo "🚀 Starting AIrTicle Development Environment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Check if we're in the right directory
if [ ! -f "pom.xml" ] || [ ! -d "frontend" ]; then
    echo -e "${RED}❌ Error: Please run this script from the project root directory${NC}"
    echo -e "${YELLOW}Expected: /path/to/validationsystem/${NC}"
    exit 1
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Error: Java is not installed or not in PATH${NC}"
    exit 1
fi

# Check if Node.js is available
if ! command -v node &> /dev/null; then
    echo -e "${RED}❌ Error: Node.js is not installed or not in PATH${NC}"
    exit 1
fi

# Check if pnpm is available (fallback to npm)
if command -v pnpm &> /dev/null; then
    NPM_CMD="pnpm"
else
    NPM_CMD="npm"
    echo -e "${YELLOW}⚠️  pnpm not found, using npm instead${NC}"
fi

echo -e "${BLUE}📋 Environment Check:${NC}"
echo -e "  ✅ Java: $(java -version 2>&1 | head -n 1)"
echo -e "  ✅ Node: $(node --version)"
echo -e "  ✅ Package Manager: $NPM_CMD $(eval $NPM_CMD --version)"
echo ""

# Function to start backend
start_backend() {
    echo -e "${GREEN}🌱 Starting Spring Boot Backend...${NC}"
    
    # Check if Maven wrapper exists
    if [ -f "./mvnw" ]; then
        MAVEN_CMD="./mvnw"
    elif command -v mvn &> /dev/null; then
        MAVEN_CMD="mvn"
    else
        echo -e "${RED}❌ Error: Neither Maven wrapper nor Maven found${NC}"
        return 1
    fi
    
    # Start backend in a new tab
    if command -v gnome-terminal &> /dev/null; then
        gnome-terminal --tab --title="AIrTicle Backend" -- bash -c "
            echo -e '${GREEN}🌱 Starting Spring Boot Backend...${NC}';
            echo -e '${BLUE}📍 Backend will be available at: http://localhost:8080${NC}';
            echo -e '${BLUE}📍 API endpoints at: http://localhost:8080/api${NC}';
            echo '';
            $MAVEN_CMD spring-boot:run;
            echo -e '${RED}Backend stopped. Press any key to close...${NC}';
            read -n 1;
        "
    elif command -v konsole &> /dev/null; then
        konsole --new-tab --title="AIrTicle Backend" -e bash -c "
            echo -e '${GREEN}🌱 Starting Spring Boot Backend...${NC}';
            echo -e '${BLUE}📍 Backend will be available at: http://localhost:8080${NC}';
            echo -e '${BLUE}📍 API endpoints at: http://localhost:8080/api${NC}';
            echo '';
            $MAVEN_CMD spring-boot:run;
            echo -e '${RED}Backend stopped. Press any key to close...${NC}';
            read -n 1;
        "
    elif [[ "$TERM_PROGRAM" == "vscode" ]] || [[ "$TERMINAL_EMULATOR" == "JetBrains-JediTerm" ]]; then
        # For VS Code integrated terminal or JetBrains terminal
        echo -e "${YELLOW}🔧 Opening backend in background (use Ctrl+C to stop)...${NC}"
        echo -e "${BLUE}📍 Backend will be available at: http://localhost:8080${NC}"
        echo -e "${BLUE}📍 API endpoints at: http://localhost:8080/api${NC}"
        $MAVEN_CMD spring-boot:run &
        BACKEND_PID=$!
    else
        echo -e "${YELLOW}⚠️  Starting backend in background (use Ctrl+C to stop)...${NC}"
        echo -e "${BLUE}📍 Backend will be available at: http://localhost:8080${NC}"
        echo -e "${BLUE}📍 API endpoints at: http://localhost:8080/api${NC}"
        $MAVEN_CMD spring-boot:run &
        BACKEND_PID=$!
    fi
}

# Function to start frontend
start_frontend() {
    echo -e "${GREEN}🎨 Starting SvelteKit Frontend...${NC}"
    
    cd frontend
    
    # Install dependencies if node_modules doesn't exist
    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}📦 Installing frontend dependencies...${NC}"
        $NPM_CMD install
    fi
    
    # Start frontend in a new tab
    if command -v gnome-terminal &> /dev/null; then
        gnome-terminal --tab --title="AIrTicle Frontend" -- bash -c "
            cd frontend;
            echo -e '${GREEN}🎨 Starting SvelteKit Frontend...${NC}';
            echo -e '${BLUE}📍 Frontend will be available at: http://localhost:5173${NC}';
            echo -e '${BLUE}📍 Hot reload enabled for development${NC}';
            echo '';
            $NPM_CMD run dev -- --open;
            echo -e '${RED}Frontend stopped. Press any key to close...${NC}';
            read -n 1;
        "
    elif command -v konsole &> /dev/null; then
        konsole --new-tab --title="AIrTicle Frontend" -e bash -c "
            cd frontend;
            echo -e '${GREEN}🎨 Starting SvelteKit Frontend...${NC}';
            echo -e '${BLUE}📍 Frontend will be available at: http://localhost:5173${NC}';
            echo -e '${BLUE}📍 Hot reload enabled for development${NC}';
            echo '';
            $NPM_CMD run dev -- --open;
            echo -e '${RED}Frontend stopped. Press any key to close...${NC}';
            read -n 1;
        "
    elif [[ "$TERM_PROGRAM" == "vscode" ]] || [[ "$TERMINAL_EMULATOR" == "JetBrains-JediTerm" ]]; then
        # For VS Code integrated terminal or JetBrains terminal
        echo -e "${YELLOW}🔧 Opening frontend in background (use Ctrl+C to stop)...${NC}"
        echo -e "${BLUE}📍 Frontend will be available at: http://localhost:5173${NC}"
        $NPM_CMD run dev &
        FRONTEND_PID=$!
    else
        echo -e "${YELLOW}⚠️  Starting frontend in background (use Ctrl+C to stop)...${NC}"
        echo -e "${BLUE}📍 Frontend will be available at: http://localhost:5173${NC}"
        $NPM_CMD run dev &
        FRONTEND_PID=$!
    fi
    
    cd ..
}

# Start both services
echo -e "${YELLOW}🔄 Starting development services...${NC}"
echo ""

# Start backend first (it takes longer to start up)
start_backend

# Wait a moment before starting frontend
sleep 2

# Start frontend
start_frontend

echo ""
echo -e "${GREEN}✅ Development environment started!${NC}"
echo ""
echo -e "${BLUE}📍 Services:${NC}"
echo -e "  🌱 Backend (API): ${YELLOW}http://localhost:8080${NC}"
echo -e "  🎨 Frontend (UI): ${YELLOW}http://localhost:5173${NC}"
echo ""
echo -e "${YELLOW}💡 Tips:${NC}"
echo -e "  • Both services will auto-reload on file changes"
if command -v gnome-terminal &> /dev/null || command -v konsole &> /dev/null; then
    echo -e "  • Check the new terminal tabs for logs and output"
    echo -e "  • Use Ctrl+C in each tab to stop the services"
else
    echo -e "  • Services are running in background (PIDs: Backend=${BACKEND_PID:-N/A}, Frontend=${FRONTEND_PID:-N/A})"
    echo -e "  • Use Ctrl+C to stop this script and all services"
fi
echo -e "  • API documentation: http://localhost:8080/swagger-ui.html"
echo ""
echo -e "${GREEN}🎉 Happy coding!${NC}"

# If running in integrated terminal, wait for user input to keep script alive
if [[ "$TERM_PROGRAM" == "vscode" ]] || [[ "$TERMINAL_EMULATOR" == "JetBrains-JediTerm" ]] || [[ -z "$(command -v gnome-terminal)" && -z "$(command -v konsole)" ]]; then
    echo ""
    echo -e "${YELLOW}Press Ctrl+C to stop all services and exit${NC}"
    
    # Set up cleanup function
    cleanup() {
        echo ""
        echo -e "${YELLOW}🛑 Stopping services...${NC}"
        if [[ -n "$BACKEND_PID" ]]; then
            kill $BACKEND_PID 2>/dev/null && echo -e "  ✅ Backend stopped"
        fi
        if [[ -n "$FRONTEND_PID" ]]; then
            kill $FRONTEND_PID 2>/dev/null && echo -e "  ✅ Frontend stopped"
        fi
        echo -e "${GREEN}👋 Goodbye!${NC}"
        exit 0
    }
    
    # Trap Ctrl+C
    trap cleanup SIGINT SIGTERM
    
    # Keep script running
    while true; do
        sleep 1
    done
fi