package com.shopflow.common.persistence;

import com.shopflow.common.exception.ResourceNotFoundException;
import com.shopflow.common.persistence.dto.PageDto;
import com.shopflow.common.util.Const;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class NativeQuerySupport {

    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    private static final AtomicLong KEY_SEQUENCE = new AtomicLong();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public static <T> String genSqlWhereIn(String field, List<T> values, Map<String, Object> params) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        String parameterName = "param_" + field.replaceAll("[^A-Za-z0-9_]", "_")
                + "_" + KEY_SEQUENCE.incrementAndGet();
        params.put(parameterName, values);
        return " AND " + quoteIdentifierPath(field) + " IN (:" + parameterName + ")";
    }

    public Map<String, Object> callProcedure(String procedureName, SqlParameterSource params) {
        return new SimpleJdbcCall(jdbcTemplate).withProcedureName(procedureName).execute(params);
    }

    public PageRequest getPage(PageDto pageDto) {
        if (pageDto == null) {
            throw new IllegalArgumentException("Page request is required");
        }

        int page = pageDto.getPage() == null ? Const.PAGE_DEFAULT : pageDto.getPage();
        int size = pageDto.getSize() == null ? Const.SIZE_DEFAULT : pageDto.getSize();
        return PageRequest.of(
                Math.max(page, Const.PAGE_DEFAULT),
                Math.min(Math.max(size, 1), Const.SIZE_MAX));
    }

    public String appendLikeExpressionById(String value) {
        return "%," + value.trim() + ",%";
    }

    public String appendLikeExpression(String value) {
        return "%" + value.trim() + "%";
    }

    public String appendLikeEscapee(String value) {
        if (StringUtils.isBlank(value)) {
            return "%%";
        }
        return "%" + value.trim()
                .replace("[", "[[]")
                .replace("%", "[%]")
                .replace("_", "[_]") + "%";
    }

    public <T> T getFirstData(String sql, Map<String, Object> params, Class<T> clazz) {
        List<T> results = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(clazz));
        if (results.isEmpty()) {
            throw new ResourceNotFoundException(clazz.getSimpleName());
        }
        return results.get(0);
    }

    public <T> List<T> getList(String sql, Map<String, Object> params, Class<T> clazz) {
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(clazz));
    }

    public static Set<String> getAllowedColumns(Class<?> clazz) {
        Set<String> fields = new HashSet<>();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                fields.add(field.getName());
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static String camelToSnake(String value) {
        return value.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }

    public static String buildSafeOrder(String sort, String direction, List<String> sorts, Class<?> clazz) {
        Set<String> allowedColumns = getAllowedColumns(clazz);
        List<String> orderClauses = new ArrayList<>();

        if (sorts != null) {
            for (String item : sorts) {
                if (item == null) {
                    continue;
                }
                String[] parts = item.split(",", 2);
                if (parts.length == 2) {
                    addOrderClause(orderClauses, allowedColumns, parts[0].trim(), parts[1].trim());
                }
            }
        }

        if (orderClauses.isEmpty() && StringUtils.isNotBlank(sort)) {
            addOrderClause(orderClauses, allowedColumns, sort.trim(), direction);
        }

        return orderClauses.isEmpty() ? null : String.join(", ", orderClauses);
    }

    private static void addOrderClause(List<String> clauses, Set<String> allowedColumns,
                                      String column, String direction) {
        if (allowedColumns.contains(column)
                && ("ASC".equalsIgnoreCase(direction) || "DESC".equalsIgnoreCase(direction))) {
            clauses.add(quoteIdentifier(camelToSnake(column)) + " " + direction.toUpperCase());
        }
    }

    public <T> Page<T> getListPagination(String sql, Map<String, Object> params,
                                         PageDto pageDto, Class<T> clazz) {
        return getListPagination(sql, params, pageDto, clazz, null);
    }

    public <T> Page<T> getListPagination(String sql, Map<String, Object> params,
                                         PageDto pageDto, Class<T> clazz, String defaultOrderBy) {
        PageRequest pageRequest = getPage(pageDto);
        String orderBy = buildSafeOrder(
                pageDto.getSort(), pageDto.getDirection(), pageDto.getSorts(), clazz);

        if (StringUtils.isBlank(orderBy)) {
            orderBy = validateDefaultOrder(defaultOrderBy, clazz);
        }
        if (StringUtils.isBlank(orderBy)) {
            throw new IllegalArgumentException("SQL Server pagination requires a valid sort order");
        }

        String query = sql.stripTrailing();
        if (query.endsWith(";")) {
            query = query.substring(0, query.length() - 1);
        }
        String pagedSql = query + " ORDER BY " + orderBy
                + " OFFSET " + pageRequest.getOffset() + " ROWS FETCH NEXT "
                + pageRequest.getPageSize() + " ROWS ONLY";

        long total = getTotalRow(query, params);
        List<T> rows = namedParameterJdbcTemplate.query(
                pagedSql, params, new BeanPropertyRowMapper<>(clazz));
        return new PageImpl<>(rows, pageRequest, total);
    }

    private static String validateDefaultOrder(String defaultOrderBy, Class<?> clazz) {
        if (StringUtils.isBlank(defaultOrderBy)) {
            return null;
        }

        List<String> clauses = new ArrayList<>();
        Set<String> allowedColumns = getAllowedColumns(clazz);
        for (String item : defaultOrderBy.split(",")) {
            String[] parts = item.trim().split("\\s+");
            if (parts.length != 2) {
                return null;
            }
            String column = parts[0].replace("[", "").replace("]", "");
            String direction = parts[1];
            if (!allowedColumns.contains(column)
                    || !("ASC".equalsIgnoreCase(direction) || "DESC".equalsIgnoreCase(direction))) {
                return null;
            }
            clauses.add(quoteIdentifier(camelToSnake(column)) + " " + direction.toUpperCase());
        }
        return clauses.isEmpty() ? null : String.join(", ", clauses);
    }

    public <T> List<T> getResultList(String sql, MapSqlParameterSource params, Class<T> clazz) {
        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(clazz));
    }

    public <T> T getFirstResult(String sql, MapSqlParameterSource params, Class<T> clazz) {
        List<T> results = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(clazz));
        if (results.isEmpty()) {
            throw new ResourceNotFoundException(clazz.getSimpleName());
        }
        return results.get(0);
    }

    public Long getTotalRow(String sql, Map<String, Object> params) {
        String query = sql.stripTrailing();
        if (query.endsWith(";")) {
            query = query.substring(0, query.length() - 1);
        }
        String countSql = "SELECT COUNT(*) FROM (" + query + ") AS sub";
        return namedParameterJdbcTemplate.queryForObject(countSql, params, Long.class);
    }

    public int updateList(String sql, Map<String, Object> params) {
        return namedParameterJdbcTemplate.update(sql, params);
    }

    public void batchInsert(String tableName, List<String> columns, List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty() || columns == null || columns.isEmpty()) {
            return;
        }

        String columnList = columns.stream()
                .map(NativeQuerySupport::quoteIdentifierPath)
                .collect(Collectors.joining(", "));
        String parameters = columns.stream()
                .map(column -> ":" + parameterName(column))
                .collect(Collectors.joining(", "));
        String query = "INSERT INTO " + quoteIdentifierPath(tableName)
                + " (" + columnList + ") VALUES (" + parameters + ")";
        namedParameterJdbcTemplate.batchUpdate(query, rows.toArray(Map[]::new));
    }

    public void batchUpsert(String tableName, List<String> columns, List<String> conflictColumns,
                            List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty() || columns == null || columns.isEmpty()
                || conflictColumns == null || conflictColumns.isEmpty()) {
            return;
        }

        List<String> quotedColumns = columns.stream()
                .map(NativeQuerySupport::quoteIdentifierPath)
                .toList();
        if (!columns.containsAll(conflictColumns)) {
            throw new IllegalArgumentException("Conflict columns must be included in columns");
        }

        List<String> sourceValues = columns.stream()
                .map(column -> ":" + parameterName(column))
                .toList();
        String joinCondition = conflictColumns.stream()
                .map(column -> "target." + quoteIdentifierPath(column)
                        + " = source." + quoteIdentifierPath(column))
                .collect(Collectors.joining(" AND "));
        String updateSet = columns.stream()
                .filter(column -> !conflictColumns.contains(column))
                .map(column -> "target." + quoteIdentifierPath(column)
                        + " = source." + quoteIdentifierPath(column))
                .collect(Collectors.joining(", "));

        // HOLDLOCK giữ việc kiểm tra khóa duy nhất an toàn khi có nhiều upsert đồng thời.
        StringBuilder query = new StringBuilder("MERGE INTO ")
                .append(quoteIdentifierPath(tableName))
                .append(" WITH (HOLDLOCK) AS target USING (VALUES (")
                .append(String.join(", ", sourceValues))
                .append(")) AS source (")
                .append(String.join(", ", quotedColumns))
                .append(") ON ")
                .append(joinCondition)
                .append(' ');
        if (!updateSet.isEmpty()) {
            query.append("WHEN MATCHED THEN UPDATE SET ").append(updateSet).append(' ');
        }
        query.append("WHEN NOT MATCHED THEN INSERT (")
                .append(String.join(", ", quotedColumns))
                .append(") VALUES (")
                .append(columns.stream()
                        .map(column -> "source." + quoteIdentifierPath(column))
                        .collect(Collectors.joining(", ")))
                .append(");");

        namedParameterJdbcTemplate.batchUpdate(query.toString(), rows.toArray(Map[]::new));
    }

    private static String parameterName(String identifier) {
        String leaf = identifier.substring(identifier.lastIndexOf('.') + 1);
        if (!IDENTIFIER.matcher(leaf).matches()) {
            throw new IllegalArgumentException("Invalid SQL identifier: " + identifier);
        }
        return leaf;
    }

    private static String quoteIdentifierPath(String identifierPath) {
        if (StringUtils.isBlank(identifierPath)) {
            throw new IllegalArgumentException("SQL identifier is required");
        }
        return java.util.Arrays.stream(identifierPath.split("\\."))
                .map(NativeQuerySupport::quoteIdentifier)
                .collect(Collectors.joining("."));
    }

    private static String quoteIdentifier(String identifier) {
        if (!IDENTIFIER.matcher(identifier).matches()) {
            throw new IllegalArgumentException("Invalid SQL identifier: " + identifier);
        }
        return "[" + identifier + "]";
    }
}
