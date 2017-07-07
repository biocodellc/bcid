package org.biocode.bcid.repositories;

import org.biocode.bcid.ClientSql;
import org.biocode.bcid.models.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rjewing
 */
@Repository
@Transactional
public class PostgresClientRepository implements ClientRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ClientSql sql;

    @Autowired
    public PostgresClientRepository(NamedParameterJdbcTemplate jdbcTemplate, ClientSql sql) {
        this.jdbcTemplate = jdbcTemplate;
        this.sql = sql;
    }

    @Override
    public void save(Client client) {
        MapSqlParameterSource params = new MapSqlParameterSource("id", client.id());
        params.addValue("secret", client.secret());
        params.addValue("accessToken", client.accessToken());
        params.addValue("tokenExpiration", client.tokenExpiration());

        jdbcTemplate.update(
                sql.insert(),
                params
        );
    }

    @Override
    public Client getClientById(String id) {
        return jdbcTemplate.queryForObject(
                sql.getClientById(),
                new MapSqlParameterSource("id", id),
                this::mapRow
        );
    }

    @Override
    public Client getClientByAccessToken(String accessToken) {
        return jdbcTemplate.queryForObject(
                sql.getClientByAccessToekn(),
                new MapSqlParameterSource("accessToken", accessToken),
                this::mapRow
        );
    }

    @Override
    public void associateIdentifier(String clientId, URI identifier) {
        MapSqlParameterSource params = new MapSqlParameterSource("clientId", clientId);
        params.addValue("identifier", identifier.toString());

        jdbcTemplate.execute(
                sql.clientIdentifierAssociate(),
                params,
                PreparedStatement::execute
        );
    }

    @Override
    public boolean isAssociated(String id, URI identifier) {
        MapSqlParameterSource params = new MapSqlParameterSource("clientId", id);
        params.addValue("identifier", identifier, Types.VARCHAR);

        return jdbcTemplate.queryForObject(
                sql.clientIdentifierIsAssociated(),
                params,
                (rs, rowNum) -> rs.getBoolean("associated")
        );
    }

    private Client mapRow(ResultSet rs, int rowNum) throws SQLException {
        Client client = new Client(rs.getString("id"), rs.getString("secret"));
        client.setAccessToken(rs.getString("access_token"), rs.getDate("token_expiration"));
        return client;
    }
}
