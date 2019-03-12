package com.pass0word1.demo.springtest.dao;

import com.pass0word1.demo.springtest.domain.Pet;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PetDao {

  private JdbcTemplate jdbcTemplate;

  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Autowired
  public void setNamedParameterJdbcTemplate(
      NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public Pet findById(Long id) {
    try {
      return this.jdbcTemplate.queryForObject("SELECT * FROM PET WHERE ID=?", new RowMapper<Pet>() {
        @Override
        public Pet mapRow(ResultSet rs, int rowNum) throws SQLException {
          Pet pet = new Pet();
          pet.setId(rs.getLong("ID"));
          pet.setName(rs.getString("NAME"));
          pet.setStatus(rs.getString("STATUS"));
          return pet;
        }
      }, id);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  public void deleteById(Long id) {
    this.jdbcTemplate.update("DELETE FROM PET WHERE ID=?", id);
  }

  public Long insert(Pet pet) {
    MapSqlParameterSource msps = new MapSqlParameterSource();
    msps.addValue("NAME", pet.getName());
    msps.addValue("STATUS", pet.getStatus());
    KeyHolder keyHolder = new GeneratedKeyHolder();
    this.namedParameterJdbcTemplate
        .update("INSERT INTO PET (NAME,STATUS) VALUES (:NAME,:STATUS)", msps, keyHolder);
    pet.setId(keyHolder.getKey().longValue());
    return keyHolder.getKey().longValue();
  }
}
