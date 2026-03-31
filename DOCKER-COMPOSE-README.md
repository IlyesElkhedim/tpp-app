# Documentation des configurations Docker Compose

Ce projet utilise 3 fichiers Docker Compose pour 3 environnements différents :

## 📁 Fichiers


### 1. `docker-compose-local.yml`
**Usage :** Développement sur ton PC local
```bash
docker compose -f docker-compose-local.yml up -d
```

**Caractéristiques :**
- Tous les ports exposés (postgres:5432, backend:8080, frontend:80, pgadmin:443)
- Logs très verbeux (DEBUG)
- PgAdmin inclus pour inspecter la base
- Profil Spring : `local`
- Volumes : `tpp-postgres-local`, `tpp-logs-local`, `tpp-pgadmin-local`


### 2. `docker-compose-dev.yml`
**Usage :** VM de développement (déploiement automatique via GitLab CI)
```bash
docker compose -f docker-compose-dev.yml up -d --build
```

**Caractéristiques :**
- Tous les ports exposés pour debug
- Logs INFO/DEBUG
- PgAdmin inclus
- Profil Spring : `dev`
- Subnet custom : `172.20.0.0/16`
- Volumes : `tpp-postgres`, `tpp-logs`, `tpp-pgadmin`


### 3. `docker-compose-prod.yml`
**Usage :** VM de production
```bash
docker compose -f docker-compose-prod.yml up -d --build
```

**Caractéristiques :**
- ❌ Backend NON exposé (sécurité)
- ❌ Postgres NON exposé (sécurité)
- ❌ PAS de PgAdmin (sécurité)
- Logs minimaux (ERROR/WARN)
- Profil Spring : `prod`
- DDL mode : `validate` (pas de modif auto du schéma)
- Secrets OBLIGATOIRES (DB_PASSWORD, JWT_SECRET)
- Limites CPU/RAM
- Subnet custom : `172.21.0.0/16`
- Volumes : `tpp-postgres-prod`, `tpp-logs-prod`

## 🔐 Variables d'environnement

Créer les fichiers `.env` :

```bash
# Local
.env.local

# Dev
.env.dev

# Prod
.env.prod
# Éditer .env.prod avec des mots de passe FORTS
```

## 💾 Persistance des données

Tous les volumes sont **nommés** et gérés par Docker :

| Environnement | Volumes |
|---------------|---------|
| **Local** | `tpp-postgres-local`, `tpp-logs-local`, `tpp-pgadmin-local` |
| **Dev** | `tpp-postgres`, `tpp-logs`, `tpp-pgadmin` |
| **Prod** | `tpp-postgres-prod`, `tpp-logs-prod` |

**Les données persistent** entre redémarrages et rebuilds ! ✅

### Commandes utiles

```bash
# Lister les volumes
docker volume ls

# Inspecter un volume
docker volume inspect tpp-postgres-prod

# Backup d'un volume
docker run --rm \
  -v tpp-postgres-prod:/data \
  -v $(pwd)/backups:/backup \
  alpine tar czf /backup/postgres-$(date +%Y%m%d-%H%M%S).tar.gz /data

# Restore d'un volume
docker run --rm \
  -v tpp-postgres-prod:/data \
  -v $(pwd)/backups:/backup \
  alpine tar xzf /backup/postgres-20260128-143000.tar.gz -C /

# Supprimer un volume (⚠️ ATTENTION : perte de données)
docker volume rm tpp-postgres-prod
```

## 📊 Différences clés

| Aspect | Local | Dev | Prod |
|--------|-------|-----|------|
| **PgAdmin** | ✅ | ✅ | ❌ |
| **Ports Backend** | ✅ 8080 | ✅ 8080 | ❌ |
| **Ports Postgres** | ✅ 5432 | ✅ 5432 | ❌ |
| **Logs** | DEBUG | INFO/DEBUG | ERROR/WARN |
| **DDL** | update | update | validate |
| **Secrets** | Optionnels | Optionnels | **OBLIGATOIRES** |
| **Resources** | ∞ | ∞ | Limités |
