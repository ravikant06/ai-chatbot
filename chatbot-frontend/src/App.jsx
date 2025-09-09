import { useState, useRef, useEffect } from 'react'
import axios from 'axios'

// API configuration
const API_BASE_URL = 'http://localhost:8081/api/chat'

// Available AI models from your backend
const AVAILABLE_MODELS = [
  { id: 'claude-3-5-sonnet', name: 'Claude 3.5 Sonnet' },
  { id: 'claude-3-haiku', name: 'Claude 3 Haiku' },
  { id: 'cohere-command-r', name: 'Cohere Command R' },
  { id: 'cohere-command-r-plus', name: 'Cohere Command R Plus' }
]

function App() {
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const [selectedModel, setSelectedModel] = useState('claude-3-5-sonnet')
  const [conversationId, setConversationId] = useState(null)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState(null)
  const fileInputRef = useRef(null)

  // Create a new conversation when component mounts
  useEffect(() => {
    createNewConversation()
  }, [])

  // Create a new conversation
  const createNewConversation = async () => {
    try {
      const response = await axios.post(`${API_BASE_URL}/conversations`, {
        userId: 1, // Default user ID
        title: 'New Chat',
        model: selectedModel
      })
      setConversationId(response.data.id)
      setMessages([])
      setError(null)
    } catch (err) {
      console.error('Error creating conversation:', err)
      setError('Failed to create conversation')
    }
  }

  // Load messages for current conversation
  const loadMessages = async (convId) => {
    try {
      const response = await axios.get(`${API_BASE_URL}/conversations/${convId}/messages`)
      setMessages(response.data)
    } catch (err) {
      console.error('Error loading messages:', err)
      setError('Failed to load messages')
    }
  }

  // Send message to backend
  const sendMessage = async () => {
    if (!input.trim() || !conversationId) return
    
    const userMessage = {
      id: Date.now(),
      conversationId: conversationId,
      role: 'user',
      content: input,
      timestamp: new Date().toISOString()
    }
    
    // Add user message immediately
    setMessages(prev => [...prev, userMessage])
    setInput('')
    setIsLoading(true)
    setError(null)
    
    try {
      const response = await axios.post(`${API_BASE_URL}/send`, {
        conversationId: conversationId,
        message: input,
        model: selectedModel
      })
      
      // Add AI response
      setMessages(prev => [...prev, response.data])
      
    } catch (err) {
      console.error('Error sending message:', err)
      setError('Failed to send message. Please try again.')
      
      // Remove the user message if sending failed
      setMessages(prev => prev.filter(msg => msg.id !== userMessage.id))
    } finally {
      setIsLoading(false)
    }
  }

  // Handle file upload
  const handleFileUpload = (e) => {
    const file = e.target.files[0]
    if (file) {
      const fileMessage = { 
        id: Date.now(), 
        role: 'user', 
        content: `📎 Uploaded file: ${file.name}`,
        timestamp: new Date().toISOString()
      }
      setMessages(prev => [...prev, fileMessage])
    }
  }

  // Start new conversation
  const startNewChat = () => {
    createNewConversation()
  }

  // Handle Enter key press
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      sendMessage()
    }
  }

  return (
    <div style={{ 
      display: 'flex', 
      flexDirection: 'column', 
      height: '100vh', 
      maxWidth: '800px', 
      margin: '0 auto',
      backgroundColor: '#ffffff',
      fontFamily: 'system-ui, -apple-system, sans-serif'
    }}>
      {/* Header */}
      <div style={{ 
        backgroundColor: '#2563eb', 
        color: 'white', 
        padding: '20px',
        borderBottom: '1px solid #e5e7eb',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <h1 style={{ margin: 0, fontSize: '24px', fontWeight: '600' }}>AI Chatbot</h1>
        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <select
            value={selectedModel}
            onChange={(e) => setSelectedModel(e.target.value)}
            style={{
              padding: '8px 12px',
              borderRadius: '6px',
              border: '1px solid #e5e7eb',
              backgroundColor: 'white',
              color: '#374151',
              fontSize: '14px'
            }}
          >
            {AVAILABLE_MODELS.map(model => (
              <option key={model.id} value={model.id}>
                {model.name}
              </option>
            ))}
          </select>
          <button
            onClick={startNewChat}
            style={{
              backgroundColor: 'rgba(255,255,255,0.2)',
              color: 'white',
              padding: '8px 16px',
              borderRadius: '6px',
              border: '1px solid rgba(255,255,255,0.3)',
              cursor: 'pointer',
              fontSize: '14px'
            }}
          >
            New Chat
          </button>
        </div>
      </div>

      {/* Error Message */}
      {error && (
        <div style={{
          backgroundColor: '#fef2f2',
          color: '#dc2626',
          padding: '12px 20px',
          borderBottom: '1px solid #fecaca',
          fontSize: '14px'
        }}>
          {error}
        </div>
      )}

      {/* Messages */}
      <div style={{ 
        flex: 1, 
        overflowY: 'auto', 
        padding: '20px',
        backgroundColor: '#f9fafb'
      }}>
        {messages.length === 0 && !isLoading && (
          <div style={{
            textAlign: 'center',
            color: '#6b7280',
            marginTop: '40px',
            fontSize: '16px'
          }}>
            Start a conversation by typing a message below
          </div>
        )}
        
        {messages.map(msg => (
          <div key={msg.id} style={{ 
            display: 'flex', 
            justifyContent: msg.role === 'user' ? 'flex-end' : 'flex-start',
            marginBottom: '16px'
          }}>
            <div style={{
              maxWidth: '70%',
              padding: '12px 16px',
              borderRadius: '18px',
              backgroundColor: msg.role === 'user' ? '#2563eb' : '#e5e7eb',
              color: msg.role === 'user' ? 'white' : '#374151',
              position: 'relative'
            }}>
              {msg.content}
              {msg.role === 'assistant' && msg.model && (
                <div style={{
                  fontSize: '10px',
                  color: '#9ca3af',
                  marginTop: '4px',
                  fontStyle: 'italic'
                }}>
                  {AVAILABLE_MODELS.find(m => m.id === msg.model)?.name || msg.model}
                </div>
              )}
            </div>
          </div>
        ))}
        
        {/* Loading indicator */}
        {isLoading && (
          <div style={{ 
            display: 'flex', 
            justifyContent: 'flex-start',
            marginBottom: '16px'
          }}>
            <div style={{
              padding: '12px 16px',
              borderRadius: '18px',
              backgroundColor: '#e5e7eb',
              color: '#6b7280',
              display: 'flex',
              alignItems: 'center',
              gap: '8px'
            }}>
              <div style={{
                width: '12px',
                height: '12px',
                border: '2px solid #9ca3af',
                borderTop: '2px solid transparent',
                borderRadius: '50%',
                animation: 'spin 1s linear infinite'
              }}></div>
              AI is thinking...
            </div>
          </div>
        )}
      </div>

      {/* Input */}
      <div style={{ 
        borderTop: '1px solid #e5e7eb', 
        padding: '20px',
        backgroundColor: 'white'
      }}>
        <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-end' }}>
          <input
            ref={fileInputRef}
            type="file"
            onChange={handleFileUpload}
            style={{ display: 'none' }}
            accept="image/*,application/pdf,.doc,.docx,.txt"
          />
          
          <button
            onClick={() => fileInputRef.current?.click()}
            disabled={isLoading}
            style={{
              padding: '10px',
              border: '1px solid #d1d5db',
              borderRadius: '8px',
              backgroundColor: isLoading ? '#f3f4f6' : '#f9fafb',
              cursor: isLoading ? 'not-allowed' : 'pointer',
              fontSize: '18px',
              opacity: isLoading ? 0.5 : 1
            }}
          >
            📎
          </button>
          
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder={isLoading ? "AI is responding..." : "Type your message..."}
            disabled={isLoading}
            style={{
              flex: 1,
              border: '1px solid #d1d5db',
              borderRadius: '12px',
              padding: '12px 16px',
              fontSize: '16px',
              outline: 'none',
              backgroundColor: isLoading ? '#f9fafb' : 'white',
              opacity: isLoading ? 0.7 : 1
            }}
          />
          
          <button
            onClick={sendMessage}
            disabled={isLoading || !input.trim()}
            style={{
              backgroundColor: isLoading || !input.trim() ? '#9ca3af' : '#2563eb',
              color: 'white',
              padding: '12px 24px',
              borderRadius: '12px',
              border: 'none',
              cursor: isLoading || !input.trim() ? 'not-allowed' : 'pointer',
              fontSize: '16px',
              fontWeight: '500'
            }}
          >
            {isLoading ? 'Sending...' : 'Send'}
          </button>
        </div>
      </div>

      {/* CSS for loading animation */}
      <style>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  )
}

export default App
