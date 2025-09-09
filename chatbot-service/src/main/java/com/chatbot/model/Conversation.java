package com.chatbot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * Conversation Entity - Represents a chat conversation/session
 * 
 * A conversation is a collection of messages between user and AI.
 * Think of it like a WhatsApp chat thread or email conversation.
 * 
 * Relationship with Message:
 * - One Conversation has many Messages
 * - Each Message belongs to one Conversation
 * 
 * @Entity tells Spring to store this in H2 table "conversations"
 */
@Entity
@Table(name = "conversations")
public class Conversation {

    /**
     * Unique identifier for this conversation
     * H2 automatically generates this when saving
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the user who owns this conversation
     * Links to user data from your existing User Management Service
     * In Phase 3, this will come from OAuth/JWT token
     */
    private Long userId;

    /**
     * Human-readable title for the conversation
     * Usually auto-generated from first user message
     * e.g., "React Best Practices", "JavaScript Help"
     */
    private String title;

    /**
     * Which AI model is used for this conversation
     * e.g., "claude-3-5-sonnet", "claude-3-haiku"
     * User can switch models per conversation
     */
    private String model;

    /**
     * When this conversation was created
     */
    private LocalDateTime createdAt;

    /**
     * When this conversation was last updated
     * Updates when new messages are added
     */
    private LocalDateTime updatedAt;

    /**
     * Whether this conversation is currently active
     * Users can archive old conversations
     */
    private boolean isActive;

    /**
     * List of message IDs belonging to this conversation
     * We store IDs instead of full Message objects to avoid large documents
     * 
     * Alternative approach: Store full messages here
     * Trade-off: Simpler queries vs. document size limits
     */
    @ElementCollection
    private List<Long> messageIds;

    /**
     * Default constructor required by Spring Data MongoDB
     * 
     * How Spring Data works:
     * 1. Fetches data from MongoDB as JSON
     * 2. Creates empty object using this constructor
     * 3. Uses setter methods to populate fields
     * 4. Returns populated object to your code
     */
    public Conversation() {
        this.messageIds = new ArrayList<>(); // Initialize empty list
        this.isActive = true; // New conversations are active by default
    }

    /**
     * Constructor for creating new conversations
     * 
     * @param userId ID of user creating this conversation
     * @param title Display name for the conversation
     * @param model AI model to use (e.g., "claude-3-5-sonnet")
     * 
     * Usage example:
     * Conversation conv = new Conversation(123L, "Help with React", "claude-3-5-sonnet");
     */
    public Conversation(Long userId, String title, String model) {
        this(); // Call default constructor first
        this.userId = userId;
        this.title = title;
        this.model = model;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Helper method to add a message ID to this conversation
     * 
     * @param messageId ID of message to add
     * 
     * This method demonstrates business logic in model classes:
     * - Adds message ID to list
     * - Updates the "updatedAt" timestamp
     * - Maintains data consistency
     */
    public void addMessageId(Long messageId) {
        if (this.messageIds == null) {
            this.messageIds = new ArrayList<>();
        }
        this.messageIds.add(messageId);
        this.updatedAt = LocalDateTime.now(); // Mark conversation as recently updated
    }

    /**
     * Helper method to get total number of messages
     * @return int count of messages in this conversation
     */
    public int getMessageCount() {
        return this.messageIds != null ? this.messageIds.size() : 0;
    }

    // ============= GETTER AND SETTER METHODS =============
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the user ID who owns this conversation
     * @return userId Long ID from User Management Service
     */
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<Long> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(List<Long> messageIds) {
        this.messageIds = messageIds;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", model='" + model + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isActive=" + isActive +
                ", messageCount=" + getMessageCount() +
                '}';
    }
}