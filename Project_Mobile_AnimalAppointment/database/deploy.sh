#!/bin/bash

cd ./

git fetch origin Ignoreme > /dev/null 2>&1

LOCAL_HASH=$(git rev-parse HEAD)
REMOTE_HASH=$(git rev-parse origin/Ignoreme)

if [ "$LOCAL_HASH" != "$REMOTE_HASH" ]; then
    echo "New commits detected. Deploying..."
    
    git pull origin Ignoreme
    
    docker compose down
    docker compose up --build -d
    
    echo "Deployment complete."
fi
