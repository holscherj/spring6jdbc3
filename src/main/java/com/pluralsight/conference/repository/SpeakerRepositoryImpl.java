package com.pluralsight.conference.repository;

import com.pluralsight.conference.model.Speaker;
import com.pluralsight.conference.repository.util.SpeakerRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("speakerRepository")
public class SpeakerRepositoryImpl implements SpeakerRepository {

    private JdbcTemplate jdbcTemplate;

    public SpeakerRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Speaker> findAll() {
        List<Speaker> speakers = jdbcTemplate.query("select * from speaker", new SpeakerRowMapper());
        return speakers;
    }

    @Override
    public Speaker create(Speaker speaker) {
        //jdbcTemplate.update("INSERT INTO speaker (name) VALUES (?)", speaker.getName());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("INSERT INTO speaker (name) VALUES (?)", new String[] {"id"});
                ps.setString(1, speaker.getName());
                return ps;
            }
        }, keyHolder);

        Number id = keyHolder.getKey();

        return getSpeaker(id.intValue());
    }

    @Override
    public Speaker getSpeaker(int id) {
        return jdbcTemplate.queryForObject("select * from speaker where id = ?", new SpeakerRowMapper(), id);
    }

    @Override
    public Speaker update(Speaker speaker) {
        jdbcTemplate.update("update speaker set name = ? where id = ?", speaker.getName(), speaker.getId());
        return speaker;
    }

    @Override
    public void update(List<Object[]> pairs) {
        jdbcTemplate.batchUpdate("update speaker set skill = ? where id = ?", pairs);
    }

    @Override
    public void delete(int id) {
        // jdbcTemplate.update("delete from speaker where id = ?", id);
        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);

        namedTemplate.update("delete from speaker where id = :id", paramMap);
    }
}
