package at.fhtw.swen.tourplanner.persistence.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class SearchRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SearchRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SearchResultRow> search(UUID userId, String tsQuery, boolean includeTours, boolean includeTourLogs) {
        String sql = """
                WITH search_query AS (
                    SELECT to_tsquery('simple', :query) AS query
                )
                SELECT
                    'TOUR' AS type,
                    t.id::text AS id,
                    t.id::text AS tour_id,
                    t.name AS title,
                    concat_ws(' ', t.description, t.start_name, t.end_name, t.tour_type) AS snippet
                FROM tours t, search_query sq
                WHERE :includeTours = true
                  AND t.created_by = :userId
                  AND to_tsvector(
                        'simple',
                        concat_ws(' ', t.name, t.description, t.start_name, t.end_name, t.tour_type)
                      ) @@ sq.query

                UNION ALL

                SELECT
                    'TOUR_LOG' AS type,
                    l.id::text AS id,
                    l.tour_id::text AS tour_id,
                    t.name AS title,
                    concat_ws(
                        ' ',
                        l.comment,
                        l.rating::text,
                        l.difficulty::text,
                        l.total_distance::text,
                        l.total_time::text,
                        l.date_time::text
                    ) AS snippet
                FROM tour_logs l
                JOIN tours t ON t.id = l.tour_id
                CROSS JOIN search_query sq
                WHERE :includeTourLogs = true
                  AND l.created_by = :userId
                  AND t.created_by = :userId
                  AND to_tsvector(
                        'simple',
                        concat_ws(
                            ' ',
                            t.name,
                            l.comment,
                            l.rating::text,
                            l.difficulty::text,
                            l.total_distance::text,
                            l.total_time::text,
                            l.date_time::text
                        )
                      ) @@ sq.query
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("query", tsQuery)
                .addValue("includeTours", includeTours)
                .addValue("includeTourLogs", includeTourLogs);

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> new SearchResultRow(
                rs.getString("type"),
                rs.getString("id"),
                rs.getString("tour_id"),
                rs.getString("title"),
                rs.getString("snippet")
        ));
    }
}
