#!/usr/bin/env bash
echo "remove current sde database..."
rm sde.db
echo "create new sde database..."
sqlite3 sde.db < db-ddl-0.16.0.ddl
echo "database creation completed."
