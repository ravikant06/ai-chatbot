package com.chatbot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Message Entity - Represents a single chat message
 * 
 * This class is a "model" or "entity" that represents data structure.
 * In Spring Boot architecture:
 * - Model layer: Defines data structure (this class)
 * - Repository layer: Handles database operations (saves/retrieves messages)
 * - Service layer: Business logic (processes messages)
 * - Controller layer: HTTP endpoints (receives requests, returns responses)
 * 
 * @Entity annotation tells Spring this class should be stored in H2 database
 * H2 is a lightweight SQL database that stores data in tables
 */
@Entity
@Table(name = "messages")  // H2 table name
public class Message {

    /**
     * @Id annotation marks this field as the primary key
     * @GeneratedValue tells H2 to auto-generate unique IDs
     * Similar to auto-increment ID in SQL databases
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * conversationId links this message to a specific conversation
     * Multiple messages can belong to one conversation
     * This creates a relationship: Conversation -> has many -> Messages
     */
    private String conversationId;

    /**
     * role indicates who sent this message:
     * - "user": Message from human user
     * - "assistant": Message from AI bot
     * - "system": System messages (e.g., "User joined chat")
     */
    private String role;

    /**
     * content is the actual message text
     * Can be plain text or markdown formatted
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * model specifies which AI model generated the response
     * e.g., "claude-3-5-sonnet", "claude-3-haiku"
     * Only relevant for assistant messages
     */
    private String model;

    /**
     * timestamp records when the message was created
     * LocalDateTime is Java's modern date/time class
     */
    private LocalDateTime timestamp;

    /**
     * Default constructor
     * Required by Spring Data MongoDB for object creation
     * When MongoDB loads data, it uses this constructor then sets fields
     */
    public Message() {
        // Empty constructor - Spring will populate fields after creation
    }

    /**
     * Constructor with parameters for creating new messages
     * 
     * @param conversationId Which conversation this message belongs to
     * @param role Who sent the message (user/assistant/system)
     * @param content The actual message text
     * @param model AI model used (can be null for user messages)
     * 
     * Usage example:
     * Message userMsg = new Message("conv123", "user", "Hello!", null);
     */
    public Message(String conversationId, String role, String content, String model) {
        this.conversationId = conversationId;
        this.role = role;
        this.content = content;
        this.model = model;
        this.timestamp = LocalDateTime.now(); // Set current time
    }

    // ============= GETTER AND SETTER METHODS =============
    // These methods allow other classes to read and modify the fields
    // Spring uses these methods to:
    // 1. Convert Java objects to JSON (for API responses)
    // 2. Convert JSON to Java objects (for API requests)
    // 3. Save/load data from MongoDB

    /**
     * Gets the unique ID of this message
     * @return Long ID (H2 generates this automatically)
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the message ID (usually done by H2)
     * @param id Unique identifier for this message
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets which conversation this message belongs to
     * @return conversationId String linking to a conversation
     */
    public String getConversationId() {
        return conversationId;
    }

    /**
     * Sets which conversation this message belongs to
     * @param conversationId ID of the parent conversation
     */
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * Gets the role (user/assistant/system)
     * @return role String indicating message sender type
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the message role
     * @param role Who sent this message (user/assistant/system)
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the message content/text
     * @return content The actual message text
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the message content
     * @param content The message text to store
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the AI model that generated this message (if it's an assistant message)
     * @return model String name of AI model, null for user messages
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the AI model name
     * @param model Name of the AI model used to generate response
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets when this message was created
     * @return timestamp LocalDateTime of message creation
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the message timestamp
     * @param timestamp When this message was created
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * toString method for debugging
     * Useful for logging and debugging - shows object contents as string
     * 
     * @return String representation of this message object
     */
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", conversationId='" + conversationId + '\'' +
                ", role='" + role + '\'' +
                ", content='" + content + '\'' +
                ", model='" + model + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}