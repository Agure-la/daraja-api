package com.tanda.daraja_api.repository;

import com.tanda.daraja_api.entity.B2CResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface B2CResponseEntityRepository extends JpaRepository<B2CResponseEntity, Long> {
    B2CResponseEntity findByOriginatorConversationID(String originatorConversationID);
    Optional<B2CResponseEntity> findByOriginatorConversationIDAndConversationID(String originatorConversationID, String conversationId);
}
