package org.OwlsGame.backend.dao;

import org.OwlsGame.backend.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findBySessionId(String sessionId);

    List<Session> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE Session s SET s.lastPlayedGameId = :gameId WHERE s.id = :sessionId")
    void updateLastPlayedGame(@Param("sessionId") Long sessionId, @Param("gameId") Long gameId);

    @Modifying
    @Query("UPDATE Session s SET s.favoriteGameId = :gameId WHERE s.id = :sessionId")
    void updateFavoriteGame(@Param("sessionId") Long sessionId, @Param("gameId") Long gameId);

    @Modifying
    @Query("UPDATE Session s SET s.cumulativeScore = s.cumulativeScore + :score WHERE s.id = :sessionId")
    void updateCumulativeScore(@Param("sessionId") Long sessionId, @Param("score") int score);
}