#!/bin/bash
# Script de surveillance et rebuild automatique
# À exécuter sur la VM : ./watch-and-rebuild.sh

cd /opt/tpp

echo "👀 Surveillance des changements dans /opt/tpp..."
echo "   Appuyez sur Ctrl+C pour arrêter"

# Installer inotify-tools si nécessaire
if ! command -v inotifywait &> /dev/null; then
    echo "Installation de inotify-tools..."
    sudo apt-get update && sudo apt-get install -y inotify-tools
fi

# Surveiller les changements
while true; do
    inotifywait -r -e modify,create,delete --exclude '(node_modules|target|\.git|dist)' /opt/tpp/back /opt/tpp/front 2>/dev/null
    
    echo ""
    echo "🔄 Changement détecté ! Rebuild en cours..."
    echo "$(date)"
    
    # Rebuild les conteneurs
    docker compose -f docker-compose-dev.yml up -d --build app-backend-dev app-frontend-dev
    
    echo "✅ Rebuild terminé !"
    echo ""
    echo "👀 En attente de nouveaux changements..."
done
