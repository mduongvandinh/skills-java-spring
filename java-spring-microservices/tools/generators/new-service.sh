#!/bin/bash
# Creates a new microservice from template
# Usage: ./new-service.sh <name> <type> [port]
# Types: core, event, gateway, bff

set -e

NAME=$1
TYPE=$2
PORT=${3:-0}

if [ -z "$NAME" ] || [ -z "$TYPE" ]; then
    echo "Usage: ./new-service.sh <name> <type> [port]"
    echo "Types: core, event, gateway, bff"
    echo ""
    echo "Examples:"
    echo "  ./new-service.sh user core"
    echo "  ./new-service.sh notification event 8084"
    exit 1
fi

# Validate type
if [[ ! "$TYPE" =~ ^(core|event|gateway|bff)$ ]]; then
    echo "Error: Invalid type '$TYPE'. Must be: core, event, gateway, bff"
    exit 1
fi

# Configuration
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(dirname "$(dirname "$SCRIPT_DIR")")"
TEMPLATE_DIR="$SCRIPT_DIR/templates/$TYPE-service"
TARGET_DIR="$PROJECT_ROOT/services/$NAME-service"

# Convert name to PascalCase
NAME_PASCAL=$(echo "$NAME" | sed -r 's/(^|-)([a-z])/\U\2/g')

# Auto-assign port if not specified
if [ "$PORT" -eq 0 ]; then
    EXISTING_PORTS=$(grep -rh "port:" "$PROJECT_ROOT/services/*/src/main/resources/application.yml" 2>/dev/null | grep -oP '\d+' | sort -n | tail -1)
    PORT=${EXISTING_PORTS:-8080}
    PORT=$((PORT + 1))
fi

echo "Creating $NAME-service from $TYPE template..."
echo "  Target: $TARGET_DIR"
echo "  Port: $PORT"

# Check template exists
if [ ! -d "$TEMPLATE_DIR" ]; then
    echo "Error: Template not found: $TEMPLATE_DIR"
    exit 1
fi

# Check service doesn't exist
if [ -d "$TARGET_DIR" ]; then
    echo "Error: Service already exists: $TARGET_DIR"
    exit 1
fi

# Copy template
echo "Copying template..."
cp -r "$TEMPLATE_DIR" "$TARGET_DIR"

# Replace placeholders
echo "Replacing placeholders..."
find "$TARGET_DIR" -type f -exec sed -i "s/{{SERVICE_NAME}}/$NAME/g" {} \;
find "$TARGET_DIR" -type f -exec sed -i "s/{{SERVICE_NAME_PASCAL}}/$NAME_PASCAL/g" {} \;
find "$TARGET_DIR" -type f -exec sed -i "s/808X/$PORT/g" {} \;

# Rename package directory
OLD_PACKAGE="$TARGET_DIR/src/main/java/com/company/template"
NEW_PACKAGE="$TARGET_DIR/src/main/java/com/company/$NAME"
if [ -d "$OLD_PACKAGE" ]; then
    mv "$OLD_PACKAGE" "$NEW_PACKAGE"
fi

# Rename Application class
OLD_APP="$NEW_PACKAGE/TemplateServiceApplication.java"
NEW_APP="$NEW_PACKAGE/${NAME_PASCAL}ServiceApplication.java"
if [ -f "$OLD_APP" ]; then
    mv "$OLD_APP" "$NEW_APP"
fi

# Update parent pom.xml
PARENT_POM="$PROJECT_ROOT/pom.xml"
if ! grep -q "services/$NAME-service" "$PARENT_POM"; then
    sed -i "s|</modules>|        <module>services/$NAME-service</module>\n    </modules>|" "$PARENT_POM"
    echo "Added module to parent pom.xml"
fi

echo ""
echo "Service created successfully!"
echo ""
echo "Next steps:"
echo "  1. cd services/$NAME-service"
echo "  2. Review and update pom.xml dependencies"
echo "  3. Create database: ${NAME}_db"
echo "  4. Run: mvn spring-boot:run"
echo ""
