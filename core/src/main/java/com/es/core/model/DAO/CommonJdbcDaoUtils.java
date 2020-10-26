package com.es.core.model.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CommonJdbcDaoUtils {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CommonJdbcDaoUtils(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isEntityExist(final String tableName, final Map<String, String> uniqueFields) {
        String query = "SELECT COUNT(*) FROM " + tableName + " WHERE ";
        query += uniqueFields
                .entrySet()
                .stream()
                .map(this::toSqlKeyValue)
                .collect(Collectors.joining(" AND "));
        return 0 != jdbcTemplate.queryForObject(query, Integer.class);
    }

    public Number insertAndReturnGeneratedKey(String tableName, SqlParameterSource parameters, String generatedColumnName) {
        return new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(tableName)
                .usingGeneratedKeyColumns(generatedColumnName)
                .executeAndReturnKey(parameters);
    }

    private String toSqlKeyValue(final Map.Entry<String, String> tuple) {
        return tuple.getKey() + "='" + tuple.getValue() + "'";
    }
}
