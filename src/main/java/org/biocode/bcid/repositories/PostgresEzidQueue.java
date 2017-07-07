package org.biocode.bcid.repositories;

import org.biocode.bcid.EzidQueueSql;
import org.biocode.bcid.ezid.EzidRequestType;
import org.biocode.bcid.models.Bcid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author rjewing
 */
@Repository
@Transactional
public class PostgresEzidQueue implements EzidQueue {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final EzidQueueSql sql;

    @Autowired
    public PostgresEzidQueue(NamedParameterJdbcTemplate jdbcTemplate, EzidQueueSql sql) {
        this.jdbcTemplate = jdbcTemplate;
        this.sql = sql;
    }

    @Override
    public List<Bcid> getEzidRequests() {
        return jdbcTemplate.query(
                sql.getEzidRequests(),
                this::mapRow
        );
    }

    @Override
    public void delete(Set<String> identifiers) {
        jdbcTemplate.execute(
                sql.deleteEzidRequests(),
                new MapSqlParameterSource("identifiers", identifiers),
                PreparedStatement::execute
        );
    }

    @Override
    public void save(Bcid bcid) {
        MapSqlParameterSource params = new MapSqlParameterSource("identifier", bcid.identifier().toString());
        params.addValue("doi", bcid.doi());
        params.addValue("title", bcid.title());
        params.addValue("webAddress", bcid.webAddress(), Types.VARCHAR);
        params.addValue("resourceType", bcid.resourceType());
        params.addValue("creator", bcid.creator());
        params.addValue("publisher", bcid.publisher());
        params.addValue("requestType", bcid.requestType().name());

        jdbcTemplate.update(
                sql.insertEzidRequest(),
                params
        );
    }

    @Override
    public int incrementId() {
        return jdbcTemplate.queryForObject(
                sql.incrementId(),
                new MapSqlParameterSource(),
                ((rs, rowNum) -> rs.getInt("nextval"))
        );
    }

    private Bcid mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Bcid bcid = new Bcid.BcidBuilder(
                rs.getString("resource_type"),
                rs.getString("creator"),
                rs.getString("publisher")
        )
                .doi(rs.getString("doi"))
                .title(rs.getString("title"))
                .build();

        bcid.setIdentifier(URI.create(rs.getString("identifier")));
        bcid.setRequestType(EzidRequestType.valueOf(rs.getString("request_type")));
        bcid.setCreated(rs.getDate("created"));

        String webAddress = rs.getString("web_address");
        if (webAddress != null) {
            bcid.setWebAddress(URI.create(webAddress));
        }

        return bcid;
    }
}
