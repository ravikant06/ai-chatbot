// MongoDB Initialization Script
// This script runs when MongoDB container first starts
// Creates database, collections, and initial data

// Switch to chatbot database (creates it if doesn't exist)
db = db.getSiblingDB('chatbot');

// Create collections with validation rules
// MongoDB collections are like SQL tables

/**
 * Messages Collection
 * Stores individual chat messages
 */
db.createCollection('messages', {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["conversationId", "role", "content", "timestamp"],
      properties: {
        conversationId: {
          bsonType: "string",
          description: "ID of conversation this message belongs to"
        },
        role: {
          bsonType: "string", 
          enum: ["user", "assistant", "system"],
          description: "Who sent this message"
        },
        content: {
          bsonType: "string",
          description: "Message text content"
        },
        model: {
          bsonType: "string",
          description: "AI model used (for assistant messages)"
        },
        timestamp: {
          bsonType: "date",
          description: "When message was created"
        }
      }
    }
  }
});

/**
 * Conversations Collection  
 * Stores conversation metadata
 */
db.createCollection('conversations', {
  validator: {
    $jsonSchema: {
      bsonType: "object",
      required: ["userId", "title", "model", "createdAt", "updatedAt"],
      properties: {
        userId: {
          bsonType: "long",
          description: "ID of user who owns this conversation"
        },
        title: {
          bsonType: "string",
          description: "Human-readable conversation title"
        },
        model: {
          bsonType: "string", 
          description: "Default AI model for this conversation"
        },
        isActive: {
          bsonType: "bool",
          description: "Whether conversation is active or archived"
        },
        messageIds: {
          bsonType: "array",
          description: "Array of message IDs in this conversation"
        }
      }
    }
  }
});

// Create indexes for better query performance
// Indexes are like database "bookmarks" that make searches faster

/**
 * Message Indexes
 * Speed up common queries
 */
// Index for finding messages by conversation (most common query)
db.messages.createIndex({ "conversationId": 1, "timestamp": 1 });

// Index for finding messages by timestamp (for recent messages)
db.messages.createIndex({ "timestamp": -1 });

/**
 * Conversation Indexes
 * Speed up conversation queries
 */
// Index for finding user's conversations (most common query)
db.conversations.createIndex({ "userId": 1, "updatedAt": -1 });

// Index for finding active conversations
db.conversations.createIndex({ "userId": 1, "isActive": 1, "updatedAt": -1 });

// Index for conversation title search
db.conversations.createIndex({ "userId": 1, "title": "text" });

/**
 * Insert sample data for development
 * This gives us test data to work with
 */

// Sample conversation
var sampleConversationId = ObjectId();
db.conversations.insertOne({
  _id: sampleConversationId,
  userId: NumberLong(1),                    // Test user ID
  title: "Welcome to AI Chatbot",
  model: "claude-3-5-sonnet", 
  createdAt: new Date(),
  updatedAt: new Date(),
  isActive: true,
  messageIds: []
});

// Sample messages
var message1Id = ObjectId();
var message2Id = ObjectId();

db.messages.insertMany([
  {
    _id: message1Id,
    conversationId: sampleConversationId.toString(),
    role: "user",
    content: "Hello! Can you help me understand Spring Boot?",
    timestamp: new Date()
  },
  {
    _id: message2Id, 
    conversationId: sampleConversationId.toString(),
    role: "assistant",
    content: "Hello! I'd be happy to help you understand Spring Boot. Spring Boot is a framework that makes it easy to create Java web applications with minimal configuration. What specific aspect would you like to learn about?",
    model: "claude-3-5-sonnet",
    timestamp: new Date()
  }
]);

// Update conversation with message IDs
db.conversations.updateOne(
  { _id: sampleConversationId },
  { 
    $push: { 
      messageIds: { 
        $each: [message1Id.toString(), message2Id.toString()] 
      } 
    }
  }
);

print("✅ Chatbot database initialized successfully!");
print("📊 Collections created: messages, conversations");
print("🗂️  Indexes created for optimal query performance");
print("💬 Sample conversation and messages inserted");
print("🌐 Access Mongo Express at http://localhost:8082");
print("   Username: admin, Password: admin123");