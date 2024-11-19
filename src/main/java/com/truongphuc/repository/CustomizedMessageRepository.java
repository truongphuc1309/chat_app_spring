package com.truongphuc.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;

@Component
public class CustomizedMessageRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public long getTotalMessagesByConversationId(String conversationId) {
        String sqlQuery = "SELECT COUNT(*) FROM message m "
                + "INNER JOIN conversation c ON m.conversation.id = c.id "
                + "WHERE c.id = :conversationId";

        Query query = entityManager.createQuery(sqlQuery);
        query.setParameter("conversationId", conversationId);

        return (long) query.getSingleResult();
    }
}
