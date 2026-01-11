WITH partitions AS (
    SELECT
        c.oid,
        c.relname
    FROM pg_class c
             JOIN pg_inherits i ON i.inhrelid = c.oid
             JOIN pg_class p ON p.oid = i.inhparent
    WHERE p.relname = 'events'
)
SELECT
    relname AS partition_name,
    pg_size_pretty(pg_total_relation_size(oid)) AS total_size
FROM partitions
UNION ALL
SELECT
    'TOTAL' AS partition_name,
    pg_size_pretty(SUM(pg_total_relation_size(oid))) AS total_size
FROM partitions
ORDER BY partition_name;

SELECT
    pg_size_pretty(pg_relation_size('parkflow.snapshots')) AS table_only,
    pg_size_pretty(pg_indexes_size('parkflow.snapshots')) AS indexes,
    pg_size_pretty(pg_total_relation_size('parkflow.snapshots')) AS total,
    pg_size_pretty(
        pg_total_relation_size('parkflow.snapshots')
            - pg_relation_size('parkflow.snapshots')
            - pg_indexes_size('parkflow.snapshots')
    ) AS toast_or_external
;
