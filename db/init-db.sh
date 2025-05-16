#!/bin/sh
set -e

echo "Iniciando o init script."

until pg_isready -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER"; do
  sleep 2
done

export PGPASSWORD="$POSTGRES_PASSWORD"

until psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$POSTGRES_DB" \
  -tAc "SELECT to_regclass('public.users') IS NOT NULL" | grep -q 't'; do
  sleep 10
done

echo "Limpando dados antigos das tabelas..."

psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<EOF
DO \$\$
BEGIN
  IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'user_progresses') THEN
    EXECUTE 'DELETE FROM user_progresses';
  END IF;

  IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'module_items') THEN
    EXECUTE 'DELETE FROM module_items';
  END IF;

  IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'modules') THEN
    EXECUTE 'DELETE FROM modules';
  END IF;

  IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'users') THEN
    EXECUTE 'DELETE FROM users';
  END IF;

END
\$\$;
EOF

echo "Copiando dados para a tabela users..."
psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<EOF
\set ON_ERROR_STOP on
\copy users (id, email, password, role, is_deleted, nome, cpf, created_at, is_first_access) FROM '/users.csv' WITH (FORMAT csv, HEADER true);
EOF

echo "Copiando dados para a tabela modules..."
psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<EOF
\set ON_ERROR_STOP on
\copy modules (id, module_order, name) FROM '/modules.csv' WITH (FORMAT csv, HEADER true);
EOF

echo "Copiando dados para a tabela module_items..."
psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<EOF
\set ON_ERROR_STOP on
\copy module_items (id, MODULE_ITEM_ORDER, TEXT, TYPE, MODULE_ID, MODULE_PHASE) FROM '/module_items.csv' WITH (FORMAT csv, HEADER true);
EOF

echo "Copiando dados para a tabela user_progresses..."
psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<EOF
\set ON_ERROR_STOP on
\copy user_progresses (ID, MODULE_ITEM_ID, USER_ID, CREATED_AT, IS_DELETED) FROM '/user_progresses.csv' WITH (FORMAT csv, HEADER true);
EOF

# COMO ADICIONAR PARA OS PROXIMOS
# echo "Copiando dados para a tabela produto..."
# psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$POSTGRES_USER" -d "$POSTGRES_DB" <<EOF
# \set ON_ERROR_STOP on
# \copy produto (nome, preco, categoria) FROM '/produtos.csv' WITH (FORMAT csv, HEADER true);
# EOF


echo "Importação concluída com sucesso."