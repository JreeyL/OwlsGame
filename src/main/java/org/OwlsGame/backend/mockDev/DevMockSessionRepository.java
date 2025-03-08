package org.OwlsGame.backend.mockDev;

import org.OwlsGame.backend.dao.SessionRepository;
import org.OwlsGame.backend.models.Session;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

@Profile("dev")
public class DevMockSessionRepository implements SessionRepository {
    private final Map<Long, Session> sessions = new HashMap<>();
    private Long idSequence = 1L;

    // 核心业务方法实现
    @Override
    public Optional<Session> findBySessionId(String sessionId) {
        return sessions.values().stream()
                .filter(s -> s.getSessionId().equals(sessionId))
                .findFirst();
    }

    @Override
    public List<Session> findByUserId(Long userId) {
        return sessions.values().stream()
                .filter(s -> Objects.equals(s.getUserId(), userId))
                .toList();
    }

    // 自定义更新方法
    @Override
    public void updateLastPlayedGame(Long sessionId, Long gameId) {
        Optional.ofNullable(sessions.get(sessionId))
                .ifPresent(s -> s.setLastPlayedGameId(gameId));
    }

    @Override
    public void updateFavoriteGame(Long sessionId, Long gameId) {
        Optional.ofNullable(sessions.get(sessionId))
                .ifPresent(s -> s.setFavoriteGameId(gameId));
    }

    @Override
    public void updateCumulativeScore(Long sessionId, int score) {
        Optional.ofNullable(sessions.get(sessionId))
                .ifPresent(s -> s.setCumulativeScore(s.getCumulativeScore() + score));
    }

    // 基础CRUD操作
    @Override
    public <S extends Session> S save(S session) {
        if (session.getId() == null) {
            session.setId(idSequence++);
        }
        sessions.put(session.getId(), session);
        return session;
    }

    @Override
    public Optional<Session> findById(Long id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public List<Session> findAll() {
        return new ArrayList<>(sessions.values());
    }

    @Override
    public void deleteById(Long id) {
        sessions.remove(id);
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends Session> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        sessions.clear();
    }

    // 新增必须实现的JPA方法
    @Override
    public void flush() {
        // 内存实现无需刷新操作
    }

    @Override
    public <S extends Session> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends Session> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<Session> entities) {
        entities.forEach(e -> sessions.remove(e.getId()));
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAllInBatch() {
        sessions.clear();
    }

    @Override
    public Session getOne(Long id) {
        return findById(id).orElseThrow();
    }

    @Override
    public Session getById(Long id) {
        return getOne(id);
    }

    @Override
    public Session getReferenceById(Long id) {
        return getOne(id);
    }

    // 其他必须实现的通用方法
    @Override
    public boolean existsById(Long id) {
        return sessions.containsKey(id);
    }

    @Override
    public long count() {
        return sessions.size();
    }

    @Override
    public <S extends Session> List<S> saveAll(Iterable<S> entities) {
        List<S> savedEntities = new ArrayList<>();
        entities.forEach(e -> savedEntities.add(save(e)));
        return savedEntities;
    }

    @Override
    public void delete(Session entity) {
        deleteById(entity.getId());
    }

    // 以下方法根据实际需要实现，暂提供空实现
    @Override
    public List<Session> findAllById(Iterable<Long> ids) {
        List<Session> result = new ArrayList<>();
        ids.forEach(id -> findById(id).ifPresent(result::add));
        return result;
    }

    @Override
    public <S extends Session> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Session> List<S> findAll(Example<S> example) {
        return Collections.emptyList();
    }

    @Override
    public <S extends Session> List<S> findAll(Example<S> example, Sort sort) {
        return Collections.emptyList();
    }

    @Override
    public <S extends Session> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public <S extends Session> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Session> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Session, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Session> findAll(Sort sort) {
        return new ArrayList<>(sessions.values());
    }

    @Override
    public Page<Session> findAll(Pageable pageable) {
        return new PageImpl<>(new ArrayList<>(sessions.values()));
    }
}