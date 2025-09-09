package com.chatbot.repository;

import com.chatbot.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Message Repository - Database Access Layer
 * 
 * Repository Pattern Explanation:
 * - Repository = Class that handles database operations
 * - Separates business logic from database code
 * - Makes testing easier (can mock database operations)
 * - Provides standard CRUD operations (Create, Read, Update, Delete)
 * 
 * Spring Data JPA automatically implements this interface!
 * You just define method signatures, Spring provides implementation.
 * 
 * @Repository annotation tells Spring this is a data access component
 * Spring will create a bean (instance) of this interface automatically
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /*
     * JpaRepository<Message, Long> explanation:
     * - Message: The entity type this repository manages
     * - Long: The type of the ID field (@Id in Message class)
     * 
     * JpaRepository provides these methods automatically:
     * - save(Message) - Insert or update message
     * - findById(Long) - Find message by ID
     * - findAll() - Get all messages
     * - deleteById(Long) - Delete message by ID
     * - count() - Count total messages
     * - existsById(Long) - Check if message exists
     */

    /**
     * Find all messages in a specific conversation, ordered by timestamp
     * 
     * Method Naming Convention:
     * - "findBy" tells Spring this is a query method
     * - "ConversationId" matches the field name in Message class
     * - "OrderBy" sorts the results
     * - "TimestampAsc" sorts by timestamp field in ascending order
     * 
     * Spring automatically generates SQL query:
     * SELECT * FROM messages WHERE conversation_id = ? ORDER BY timestamp ASC
     * 
     * @param conversationId ID of the conversation to get messages for
     * @return List<Message> all messages in chronological order
     * 
     * Usage example:
     * List<Message> messages = messageRepository.findByConversationIdOrderByTimestampAsc("conv123");
     */
    List<Message> findByConversationIdOrderByTimestampAsc(String conversationId);

    /**
     * Find messages by role (user/assistant/system) in a conversation
     * 
     * Multiple field query:
     * - "ConversationIdAndRole" means find by BOTH fields
     * - Spring generates: db.messages.find({conversationId: id, role: role})
     * 
     * @param conversationId Which conversation to search in
     * @param role What type of messages to find (user/assistant/system)
     * @return List<Message> matching messages
     * 
     * Usage example:
     * List<Message> userMessages = messageRepository.findByConversationIdAndRole("conv123", "user");
     */
    List<Message> findByConversationIdAndRole(String conversationId, String role);

    /**
     * Count total messages in a conversation
     * 
     * "countBy" prefix tells Spring to return a number instead of objects
     * 
     * @param conversationId ID of conversation to count messages for
     * @return long number of messages in the conversation
     * 
     * Usage example:
     * long totalMessages = messageRepository.countByConversationId("conv123");
     */
    long countByConversationId(String conversationId);

    /**
     * Custom query using MongoDB query syntax
     * 
     * @Query annotation allows writing custom MongoDB queries
     * This is useful when Spring's naming convention isn't enough
     * 
     * Query explanation:
     * - "{'conversationId': ?0}" - Find documents where conversationId equals first parameter (?0)
     * - "{'timestamp': -1}" - Sort by timestamp descending (-1 = newest first)
     * 
     * @param conversationId ID of conversation
     * @param limit Maximum number of messages to return
     * @return List<Message> recent messages (newest first)
     * 
     * Usage example:
     * List<Message> recent = messageRepository.findRecentMessagesInConversation("conv123", 10);
     */
    @Query("SELECT m FROM Message m WHERE m.conversationId = ?1 ORDER BY m.timestamp DESC")
    List<Message> findRecentMessagesInConversation(String conversationId);

    /**
     * Delete all messages in a conversation
     * 
     * "deleteBy" prefix tells Spring to remove matching records
     * Useful when user deletes an entire conversation
     * 
     * @param conversationId ID of conversation to clear
     * 
     * Usage example:
     * messageRepository.deleteByConversationId("conv123"); // Removes all messages
     */
    void deleteByConversationId(String conversationId);

    /*
     * LAMBDA FUNCTION EQUIVALENT:
     * If you were to write these operations using Java 8 lambda functions manually:
     * 
     * // Instead of: findByConversationIdOrderByTimestampAsc()
     * messages.stream()
     *     .filter(msg -> msg.getConversationId().equals(conversationId))
     *     .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
     *     .collect(Collectors.toList());
     * 
     * // Instead of: countByConversationId()
     * messages.stream()
     *     .filter(msg -> msg.getConversationId().equals(conversationId))
     *     .count();
     * 
     * But Spring Data JPA does all this database work for you automatically!
     */
}