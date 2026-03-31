#!/bin/bash
set -e

# ─────────────────────────────────────────────
# Configuration
# ─────────────────────────────────────────────
VM_HOST="192.168.74.146"
VM_USER="ubuntu"
SSH_KEY="$HOME/ggmd-etus.key"
REMOTE_DIR="/opt/tpp"
SCRIPT_DIR="$(cd "$(dirname "$0")/.." && pwd)"  # racine du projet

SSH_CMD="ssh -i $SSH_KEY -o StrictHostKeyChecking=no"
RSYNC_CMD="rsync -avz -e \"ssh -i $SSH_KEY -o StrictHostKeyChecking=no\""

# ─────────────────────────────────────────────
# Pré-requis
# ─────────────────────────────────────────────
if [ ! -f "$SSH_KEY" ]; then
  echo "Erreur : clé SSH introuvable : $SSH_KEY"
  exit 1
fi
chmod 600 "$SSH_KEY"

cd "$SCRIPT_DIR"

# ─────────────────────────────────────────────
# 1. Envoi du code backend
# ─────────────────────────────────────────────
echo ">>> Envoi du code backend..."
rsync -avz --delete --exclude='target' \
  -e "ssh -i $SSH_KEY -o StrictHostKeyChecking=no" \
  back/ $VM_USER@$VM_HOST:$REMOTE_DIR/back/

# ─────────────────────────────────────────────
# 2. Envoi du code frontend
# ─────────────────────────────────────────────
echo ">>> Envoi du code frontend..."
rsync -avz --delete --exclude='node_modules' --exclude='dist' \
  -e "ssh -i $SSH_KEY -o StrictHostKeyChecking=no" \
  front/ $VM_USER@$VM_HOST:$REMOTE_DIR/front/

# ─────────────────────────────────────────────
# 3. Envoi des fichiers de configuration
# ─────────────────────────────────────────────
echo ">>> Envoi des fichiers de configuration..."
rsync -avz -e "ssh -i $SSH_KEY -o StrictHostKeyChecking=no" \
  docker-compose-prod.yml $VM_USER@$VM_HOST:$REMOTE_DIR/docker-compose-prod.yml

if [ -f ".env.prod" ]; then
  rsync -avz -e "ssh -i $SSH_KEY -o StrictHostKeyChecking=no" \
    .env.prod $VM_USER@$VM_HOST:$REMOTE_DIR/.env
else
  echo "Avertissement : .env.prod introuvable, ignoré."
fi

rsync -avz -e "ssh -i $SSH_KEY -o StrictHostKeyChecking=no" \
  back/maven-settings.xml $VM_USER@$VM_HOST:$REMOTE_DIR/back/maven-settings.xml

# Copier les settings Maven dans ~/.m2/settings.xml de l'user ubuntu (proxy pour mvn en ligne de commande)
echo ">>> Envoi des settings Maven (~/.m2/settings.xml)..."
$SSH_CMD $VM_USER@$VM_HOST "mkdir -p ~/.m2"
rsync -avz -e "ssh -i $SSH_KEY -o StrictHostKeyChecking=no" \
  back/maven-settings.xml $VM_USER@$VM_HOST:~/.m2/settings.xml

# ─────────────────────────────────────────────
# 4. Rebuild et redémarrage des conteneurs
# ─────────────────────────────────────────────
echo ">>> Rebuild et redémarrage des conteneurs..."
$SSH_CMD $VM_USER@$VM_HOST bash << 'DEPLOY_SCRIPT'
set -e
cd /opt/tpp

export DOCKER_BUILDKIT=1
export COMPOSE_DOCKER_CLI_BUILD=1

  docker compose -f docker-compose-prod.yml up -d --build

echo "--- Status des services ---"
docker compose -f docker-compose-prod.yml ps
DEPLOY_SCRIPT

echo ">>> Déploiement terminé !"
