#!/bin/bash
echo "Setting up database..."

# Остановить и пересоздать контейнер
docker-compose down
docker volume rm portal6_postgres_data

# Запустить и подождать
docker-compose up -d postgres
sleep 5

# Выполнить файлы вручную если автоматически не сработало
docker exec -i postgres-db psql -U myuser -d myproject << EOF
DROP TABLE IF EXISTS
    ads,
    users,
    moderators,
    comments,
    moderation_messages,
    categories,
    tags,
    tag_values
CASCADE;
EOF

docker exec -i postgres-db psql -U myuser -d myproject < ./src/main/resources/sql/create_tables.sql
docker exec -i postgres-db psql -U myuser -d myproject < ./src/main/resources/sql/insert_data.sql
docker exec -i postgres-db psql -U myuser -d myproject < ./src/main/resources/sql/insert_data_ads.sql
docker exec -i postgres-db psql -U myuser -d myproject < ./src/main/resources/sql/insert_category_tables.sql
docker exec -i postgres-db psql -U myuser -d myproject < ./src/main/resources/sql/insert_data_comments.sql


echo "✅ Database setup complete!"