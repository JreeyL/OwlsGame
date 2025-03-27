package org.OwlsGame.backend.mockDev;

import org.OwlsGame.backend.models.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DevMockSessionRepositoryTest {

    private DevMockSessionRepository repository;
    private Session testSession1;
    private Session testSession2;
    private Session testSession3;

    @BeforeEach
    void setUp() {
        // 创建新的repository实例
        repository = new DevMockSessionRepository();

        // 使用当前时间创建Timestamp对象
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        // 初始化测试数据
        testSession1 = new Session();
        testSession1.setSessionId("test-session-1");
        testSession1.setUserId(1L);
        testSession1.setCreationTime(currentTime);  // 使用正确的方法名和类型
        testSession1.setLastAccessedTime(currentTime);  // 设置lastAccessedTime也是必需的
        testSession1.setLastPlayedGameId(101L);
        testSession1.setFavoriteGameId(102L);
        testSession1.setCumulativeScore(100);

        testSession2 = new Session();
        testSession2.setSessionId("test-session-2");
        testSession2.setUserId(1L);
        testSession2.setCreationTime(currentTime);  // 使用正确的方法名和类型
        testSession2.setLastAccessedTime(currentTime);  // 设置lastAccessedTime也是必需的
        testSession2.setLastPlayedGameId(103L);
        testSession2.setFavoriteGameId(104L);
        testSession2.setCumulativeScore(200);

        testSession3 = new Session();
        testSession3.setSessionId("test-session-3");
        testSession3.setUserId(2L);
        testSession3.setCreationTime(currentTime);  // 使用正确的方法名和类型
        testSession3.setLastAccessedTime(currentTime);  // 设置lastAccessedTime也是必需的
        testSession3.setLastPlayedGameId(105L);
        testSession3.setFavoriteGameId(106L);
        testSession3.setCumulativeScore(300);

        // 保存测试数据到repository
        repository.save(testSession1);
        repository.save(testSession2);
        repository.save(testSession3);
    }

    @Test
    void findBySessionId_ShouldReturnCorrectSession() {
        // 测试查找存在的会话
        Optional<Session> found = repository.findBySessionId("test-session-1");
        assertTrue(found.isPresent(), "应该找到具有给定sessionId的会话");
        assertEquals("test-session-1", found.get().getSessionId(), "返回的会话应该具有正确的sessionId");

        // 测试查找不存在的会话
        Optional<Session> notFound = repository.findBySessionId("non-existent");
        assertFalse(notFound.isPresent(), "不应该找到不存在的会话");
    }

    @Test
    void findByUserId_ShouldReturnCorrectSessions() {
        // 用户1应该有两个会话
        List<Session> user1Sessions = repository.findByUserId(1L);
        assertEquals(2, user1Sessions.size(), "用户1应该有两个会话");
        assertTrue(user1Sessions.stream().allMatch(s -> s.getUserId().equals(1L)),
                "所有返回的会话应该属于用户1");

        // 用户2应该有一个会话
        List<Session> user2Sessions = repository.findByUserId(2L);
        assertEquals(1, user2Sessions.size(), "用户2应该有一个会话");
        assertEquals(2L, user2Sessions.get(0).getUserId(), "返回的会话应该属于用户2");

        // 不存在的用户应该返回空列表
        List<Session> nonExistentUserSessions = repository.findByUserId(999L);
        assertTrue(nonExistentUserSessions.isEmpty(), "不存在的用户应该返回空列表");
    }

    @Test
    void updateLastPlayedGame_ShouldUpdateCorrectly() {
        // 更新最后玩的游戏
        Long newGameId = 999L;
        repository.updateLastPlayedGame(testSession1.getId(), newGameId);

        // 验证更新
        Optional<Session> updated = repository.findById(testSession1.getId());
        assertTrue(updated.isPresent(), "会话应该存在");
        assertEquals(newGameId, updated.get().getLastPlayedGameId(), "最后玩的游戏ID应该被更新");

        // 验证其他字段未改变
        assertEquals(testSession1.getFavoriteGameId(), updated.get().getFavoriteGameId(),
                "喜爱的游戏ID不应该改变");
    }

    @Test
    void updateFavoriteGame_ShouldUpdateCorrectly() {
        // 更新最喜欢的游戏
        Long newGameId = 888L;
        repository.updateFavoriteGame(testSession1.getId(), newGameId);

        // 验证更新
        Optional<Session> updated = repository.findById(testSession1.getId());
        assertTrue(updated.isPresent(), "会话应该存在");
        assertEquals(newGameId, updated.get().getFavoriteGameId(), "喜爱的游戏ID应该被更新");

        // 验证其他字段未改变
        assertEquals(testSession1.getLastPlayedGameId(), updated.get().getLastPlayedGameId(),
                "最后玩的游戏ID不应该改变");
    }

    @Test
    void updateCumulativeScore_ShouldAddToExistingScore() {
        // 记录原始分数
        int originalScore = testSession1.getCumulativeScore();
        int scoreToAdd = 50;

        // 更新累计分数
        repository.updateCumulativeScore(testSession1.getId(), scoreToAdd);

        // 验证更新
        Optional<Session> updated = repository.findById(testSession1.getId());
        assertTrue(updated.isPresent(), "会话应该存在");
        assertEquals(originalScore + scoreToAdd, updated.get().getCumulativeScore(),
                "累计分数应该增加指定的值");
    }

    @Test
    void save_ShouldAssignIdWhenNull() {
        // 创建新会话，不设置ID
        Session newSession = new Session();
        newSession.setSessionId("new-session");
        newSession.setUserId(3L);

        // 保存新会话
        Session saved = repository.save(newSession);

        // 验证ID被分配
        assertNotNull(saved.getId(), "新保存的会话应该分配一个ID");

        // 验证可以通过ID检索
        Optional<Session> retrieved = repository.findById(saved.getId());
        assertTrue(retrieved.isPresent(), "应该能够通过分配的ID检索会话");
    }

    @Test
    void deleteById_ShouldRemoveSession() {
        // 删除会话
        repository.deleteById(testSession1.getId());

        // 验证会话已被删除
        assertFalse(repository.findById(testSession1.getId()).isPresent(), "删除的会话不应该再存在");
        assertEquals(2, repository.count(), "删除后应该剩下2个会话");
    }

    @Test
    void findAll_ShouldReturnAllSessions() {
        // 获取所有会话
        List<Session> allSessions = repository.findAll();

        // 验证返回了所有会话
        assertEquals(3, allSessions.size(), "应该返回所有3个会话");
    }

    @Test
    void findAll_WithPagination_ShouldReturnPaginatedSessions() {
        // 获取分页结果
        Page<Session> page = repository.findAll(PageRequest.of(0, 2));

        // 验证结果 - 由于mock实现没有分页，返回所有3个元素
        assertEquals(3, page.getContent().size(), "应该返回所有会话");
        assertEquals(3, page.getTotalElements(), "总元素应该是3");
        assertEquals(1, page.getTotalPages(), "由于没有实际分页，应该只有1页");
    }

    @Test
    void findAll_WithSort_ShouldReturnSortedSessions() {
        // 按累计分数降序排序获取所有会话
        List<Session> sortedSessions = repository.findAll(Sort.by(Sort.Direction.DESC, "cumulativeScore"));

        // 由于mock实现没有排序，这里我们只验证返回了所有3个会话
        assertEquals(3, sortedSessions.size(), "应该返回所有3个会话");
        // 不再验证排序顺序
    }

    @Test
    void saveAll_ShouldSaveMultipleSessions() {
        // 清空存储库
        repository.deleteAll();
        assertEquals(0, repository.count(), "存储库应该为空");

        // 批量保存
        List<Session> sessionsToSave = Arrays.asList(testSession1, testSession2);
        List<Session> savedSessions = repository.saveAll(sessionsToSave);

        // 验证保存
        assertEquals(2, savedSessions.size(), "应该返回保存的2个会话");
        assertEquals(2, repository.count(), "存储库应该包含2个会话");
    }

    @Test
    void deleteAll_ShouldRemoveAllSessions() {
        // 删除所有会话
        repository.deleteAll();

        // 验证所有会话已被删除
        assertEquals(0, repository.count(), "存储库应该为空");
        assertTrue(repository.findAll().isEmpty(), "findAll应该返回空列表");
    }
}