package com.tanda.daraja_api.repository;

import com.tanda.daraja_api.entity.B2CRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface B2CResponseEntityRepository extends JpaRepository<B2CRequestEntity, Long> {
    B2CRequestEntity findByOriginatorConversationID(String originatorConversationID);
    Optional<B2CRequestEntity> findByOriginatorConversationIDAndConversationID(String originatorConversationID, String conversationId);
}
