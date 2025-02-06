package com.truongphuc.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import com.truongphuc.entity.ConversationEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class CustomizedConversationRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<ConversationEntity> getAllConversationsByUserId(String userId, Pageable pageable) {
        String sqlQuery = "SELECT c FROM user u "
                + "INNER JOIN participant p ON p.user.id = u.id "
                + "INNER JOIN conversation c ON c.id = p.conversation.id "
                + "WHERE u.id = :userId ORDER BY c.updatedAt DESC";

        Query query = entityManager.createQuery(sqlQuery);

        query.setParameter("userId", userId);
        query.setFirstResult(pageable.getPageNumber());
        query.setMaxResults(pageable.getPageSize());
        List<?> result = query.getResultList();

        return (List<ConversationEntity>) result;
    }

    public long getTotalConversationsByUserId(String userId) {
        String sqlQuery = "SELECT COUNT(*) FROM user u "
                + "INNER JOIN participant p ON p.user.id = u.id "
                + "INNER JOIN conversation c ON c.id = p.conversation.id "
                + "WHERE u.id = :userId";

        Query query = entityManager.createQuery(sqlQuery);
        query.setParameter("userId", userId);

        return (long) query.getSingleResult();
    }
}
