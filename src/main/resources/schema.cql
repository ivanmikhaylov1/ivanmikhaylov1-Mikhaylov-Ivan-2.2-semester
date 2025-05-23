CREATE KEYSPACE IF NOT EXISTS mykeyspace
            WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE mykeyspace;

CREATE TABLE IF NOT EXISTS outbox
(
    id    timeuuid PRIMARY KEY,
    value text
);
