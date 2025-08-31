# Chat Bot Spring Boot Service Project Plan

## Overview
This project implements a modern AI chatbot system using microservices architecture with Single Sign-On (SSO) integration. The system consists of a React frontend, Spring Boot backend, and integration with existing User Management Service via OAuth 2.0.

## Architecture Overview

```
┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────────┐
│   User Management   │    │    Chat Bot UI      │    │   Chat Bot Service  │
│   Service (Existing)│    │   (React + Vite)    │    │   (Spring Boot)     │
│   Port: 8080        │    │   Port: 3001        │    │   Port: 8081        │
└─────────────────────┘    └─────────────────────┘    └─────────────────────┘
         │                          │                          │
         │                          │                          │
         ▼                          ▼                          ▼
┌─────────────────────┐    ┌─────────────────────┐    ┌─────────────────────┐
│   PostgreSQL        │    │   Static Assets     │    │     MongoDB         │
│   (User Data)       │    │   (Build Output)    │    │   (Chat History)    │
│   Port: 5432        │    │                     │    │   Port: 27017       │
└─────────────────────┘    └─────────────────────┘    └─────────────────────┘
                                                              │
                                                              ▼
                                                   ┌─────────────────────┐
                                                   │   AWS Bedrock       │
                                                   │   (AI Models)       │
                                                   │                     │
                                                   └─────────────────────┘
```

## Development Strategy: 3-Phase Approach

### Phase 1: Modern Chat UI (Week 1)
- Create React frontend with beautiful UX
- Implement mock data for development
- Build responsive, animated interface
- No backend dependency

### Phase 2: Spring Boot Backend (Week 2)
- Create Spring Boot microservice
- Integrate with MongoDB and AWS Bedrock
- Replace frontend mock data with real APIs
- Add WebSocket for real-time communication

### Phase 3: SSO Integration (Week 3)
- Implement OAuth 2.0 authorization server
- Add SSO client configuration
- Integrate with existing User Management Service
- Enable seamless cross-service authentication

---

## Chat Bot UI Requirements

### Core UI Components
- **Chat Container**: Main chat interface with message display area
- **Message Bubbles**: User and AI message bubbles with distinct styling
- **Input Area**: Message input with send button and model selection
- **Sidebar**: Conversation history and management
- **Header**: User info, logout, and navigation
- **Modal Dialogs**: New conversation, settings, help

### UX Features
- **Real-time Messaging**: WebSocket integration for live responses
- **Typing Indicators**: Show when AI is processing
- **Message Status**: Sent, delivered, error states
- **Auto-scroll**: Automatic scroll to latest messages
- **Message Actions**: Copy, delete, react to messages
- **Conversation Management**: Create, rename, delete conversations
- **Search**: Search within conversation history
- **Export**: Download conversation as text/PDF

### Design Requirements
- **Responsive Design**: Mobile-first approach, works on all devices
- **Dark/Light Theme**: User preference with system detection
- **Accessibility**: WCAG 2.1 AA compliance
- **Performance**: Smooth animations, lazy loading for long conversations
- **Modern Aesthetics**: Clean, minimalist design with subtle animations

### Technical Features
- **Code Highlighting**: Syntax highlighting for code blocks
- **Markdown Support**: Rich text formatting in messages
- **File Upload**: Support for images and documents
- **Keyboard Shortcuts**: Power user features (Ctrl+Enter, Ctrl+K)
- **Offline Support**: Service worker for basic offline functionality

---

## Project Structure

### Frontend Structure (React + Vite)
```
chatbot-frontend/
├── public/
│   ├── index.html
│   └── favicon.ico
├── src/
│   ├── components/
│   │   ├── Chat/
│   │   │   ├── ChatContainer.jsx
│   │   │   ├── MessageList.jsx
│   │   │   ├── MessageInput.jsx
│   │   │   ├── MessageBubble.jsx
│   │   │   ├── TypingIndicator.jsx
│   │   │   └── ModelSelector.jsx
│   │   ├── Sidebar/
│   │   │   ├── ConversationList.jsx
│   │   │   ├── ConversationItem.jsx
│   │   │   ├── SearchBar.jsx
│   │   │   └── UserProfile.jsx
│   │   ├── Layout/
│   │   │   ├── Header.jsx
│   │   │   ├── Sidebar.jsx
│   │   │   ├── MainLayout.jsx
│   │   │   └── MobileLayout.jsx
│   │   ├── ui/
│   │   │   ├── Button.jsx
│   │   │   ├── Input.jsx
│   │   │   ├── Modal.jsx
│   │   │   ├── Dropdown.jsx
│   │   │   └── Toast.jsx
│   │   └── auth/
│   │       ├── AuthProvider.jsx
│   │       ├── ProtectedRoute.jsx
│   │       └── LoginCallback.jsx
│   ├── hooks/
│   │   ├── useChat.js
│   │   ├── useAuth.js
│   │   ├── useWebSocket.js
│   │   ├── useConversations.js
│   │   └── useTheme.js
│   ├── services/
│   │   ├── api.js
│   │   ├── authService.js
│   │   ├── chatService.js
│   │   ├── websocketService.js
│   │   └── mockData.js
│   ├── context/
│   │   ├── AuthContext.jsx
│   │   ├── ChatContext.jsx
│   │   └── ThemeContext.jsx
│   ├── utils/
│   │   ├── constants.js
│   │   ├── helpers.js
│   │   ├── dateUtils.js
│   │   └── validators.js
│   ├── styles/
│   │   ├── globals.css
│   │   └── components.css
│   ├── App.jsx
│   └── main.jsx
├── package.json
├── vite.config.js
├── tailwind.config.js
└── README.md
```

### Backend Structure (Spring Boot)
```
chatbot-service/
├── src/main/java/com/yourname/chatbot/
│   ├── ChatbotApplication.java
│   ├── config/
│   │   ├── WebConfig.java
│   │   ├── SecurityConfig.java
│   │   ├── MongoConfig.java
│   │   ├── WebSocketConfig.java
│   │   ├── BedrockConfig.java
│   │   └── OAuth2ResourceServerConfig.java
│   ├── controller/
│   │   ├── ChatController.java
│   │   ├── ConversationController.java
│   │   ├── ModelController.java
│   │   ├── WebSocketController.java
│   │   └── HealthController.java
│   ├── service/
│   │   ├── ChatService.java
│   │   ├── ConversationService.java
│   │   ├── BedrockService.java
│   │   ├── UserManagementClient.java
│   │   └── MessageProcessingService.java
│   ├── repository/
│   │   ├── ConversationRepository.java
│   │   ├── MessageRepository.java
│   │   └── UsageAnalyticsRepository.java
│   ├── model/
│   │   ├── Conversation.java
│   │   ├── Message.java
│   │   ├── AIModel.java
│   │   ├── UserInfo.java
│   │   └── UsageAnalytics.java
│   ├── dto/
│   │   ├── ChatRequestDto.java
│   │   ├── ChatResponseDto.java
│   │   ├── ConversationDto.java
│   │   ├── MessageDto.java
│   │   └── ModelInfoDto.java
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── OAuth2UserService.java
│   │   └── SecurityUtils.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── ChatbotException.java
│   │   └── BedrockException.java
│   └── util/
│       ├── MessageFormatter.java
│       ├── TokenCounter.java
│       └── DateTimeUtils.java
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   └── static/ (React build output served here)
├── src/test/java/
│   ├── integration/
│   ├── unit/
│   └── ChatbotApplicationTests.java
├── pom.xml
├── Dockerfile
└── README.md
```

---

## Prerequisites and Environment Setup

### System Requirements
- **Java**: OpenJDK 17 or higher
- **Node.js**: Version 18+ with npm
- **MongoDB**: Version 6.0+
- **Docker**: For containerized deployment
- **Git**: Version control
- **AWS Account**: For Bedrock access
- **IDE**: IntelliJ IDEA or VS Code

### Development Tools
```bash
# Check Java version
java -version  # Should be 17+

# Check Node.js version  
node --version  # Should be 18+

# Check Docker
docker --version

# Check MongoDB (if local)
mongosh --version
```

### AWS Setup
1. **AWS Account** with Bedrock access
2. **IAM User** with Bedrock permissions
3. **AWS CLI** configured with credentials
4. **Enable Models** in Bedrock console:
   - Claude 3.5 Sonnet
   - Claude 3 Haiku
   - Titan Text (optional)

### Environment Variables
```bash
# Create .env file in project root
AWS_ACCESS_KEY_ID=your_access_key
AWS_SECRET_ACCESS_KEY=your_secret_key
AWS_REGION=us-east-1
JWT_SECRET=your-jwt-secret-key
MONGODB_URI=mongodb://localhost:27017/chatbot
USER_SERVICE_URL=http://localhost:8080
```

---

## Local Deployment Steps

### Step 1: Setup Infrastructure
```bash
# 1. Start existing User Management Service
cd user-management-system
./start-services.sh  # Assuming this exists from your current setup

# 2. Start MongoDB and Redis
docker-compose up -d mongodb redis

# 3. Verify services are running
docker ps
curl http://localhost:8080/health  # User Management Service
mongosh --eval "db.adminCommand('ping')"  # MongoDB
```

### Step 2: Frontend Development Setup
```bash
# 1. Create React frontend
npm create vite@latest chatbot-frontend -- --template react
cd chatbot-frontend

# 2. Install dependencies
npm install
npm install -D tailwindcss postcss autoprefixer
npm install @headlessui/react lucide-react framer-motion
npm install axios socket.io-client react-markdown
npm install @tailwindcss/typography react-router-dom
npm install react-hot-toast zustand

# 3. Initialize Tailwind CSS
npx tailwindcss init -p

# 4. Start development server
npm run dev
# Frontend will be available at http://localhost:3001
```

### Step 3: Backend Development Setup
```bash
# 1. Create Spring Boot project
mkdir chatbot-service
cd chatbot-service

# 2. Initialize Spring Boot project (using Spring Initializr)
curl https://start.spring.io/starter.tgz \
  -d dependencies=web,websocket,data-mongodb,security,oauth2-client,oauth2-resource-server,actuator \
  -d language=java \
  -d platformVersion=3.2.0 \
  -d packaging=jar \
  -d jvmVersion=17 \
  -d artifactId=chatbot-service \
  -d name=ChatbotService \
  -d description="AI Chatbot Service with AWS Bedrock" \
  -d packageName=com.yourname.chatbot | tar -xzf -

# 3. Add AWS Bedrock dependency to pom.xml
# (Manual step - edit pom.xml)

# 4. Build and run
./mvnw spring-boot:run
# Backend will be available at http://localhost:8081
```

### Step 4: Integration and Testing
```bash
# 1. Build frontend for production
cd chatbot-frontend
npm run build

# 2. Copy build to Spring Boot static folder
cp -r dist/* ../chatbot-service/src/main/resources/static/

# 3. Rebuild Spring Boot with frontend
cd ../chatbot-service
./mvnw clean package

# 4. Run integrated application
./mvnw spring-boot:run

# 5. Test full integration
curl http://localhost:8081/health
curl http://localhost:8081/api/models
```

### Step 5: SSO Configuration
```bash
# 1. Update User Management Service with OAuth
# (Add OAuth dependencies and configuration)

# 2. Configure OAuth client in Chat Service
# (Update application.yml with OAuth settings)

# 3. Test SSO flow
# Visit http://localhost:3001 → Should redirect to User Management for auth
```

---

## Technology Stack

### Frontend Stack
- **React 18**: Component-based UI framework
- **Vite**: Fast build tool and dev server
- **Tailwind CSS**: Utility-first CSS framework
- **Headless UI**: Accessible component primitives
- **Framer Motion**: Animation library
- **React Router**: Client-side routing
- **Zustand**: Lightweight state management
- **React Markdown**: Markdown rendering
- **Socket.IO Client**: WebSocket communication
- **Lucide React**: Icon library

### Backend Stack
- **Spring Boot 3.2**: Main framework
- **Spring Security**: Authentication and authorization
- **Spring Data MongoDB**: Database operations
- **Spring WebSocket**: Real-time communication
- **Spring Cloud OpenFeign**: Service communication
- **Spring Boot Actuator**: Monitoring and health checks
- **Spring OAuth2**: SSO implementation

### Databases and Storage
- **MongoDB**: Chat conversations and messages
- **Redis**: Session caching and rate limiting
- **PostgreSQL**: User data (existing User Management Service)

### Cloud and AI Integration
- **AWS Bedrock**: AI model access
- **AWS SDK for Java v2**: AWS service integration
- **Claude 3.5 Sonnet**: Primary AI model
- **Claude 3 Haiku**: Fast response model

### Development and Testing
- **JUnit 5**: Unit testing
- **TestContainers**: Integration testing
- **WireMock**: API mocking
- **Docker**: Containerization
- **Docker Compose**: Multi-service orchestration

---

## API Endpoints Design

### Chat Management Endpoints
```
GET    /api/conversations              # Get user's conversations
POST   /api/conversations              # Create new conversation
GET    /api/conversations/{id}         # Get specific conversation
DELETE /api/conversations/{id}         # Delete conversation
PATCH  /api/conversations/{id}         # Update conversation settings

POST   /api/conversations/{id}/messages # Send message
GET    /api/conversations/{id}/messages # Get conversation messages
DELETE /api/messages/{messageId}       # Delete specific message
```

### AI Model Endpoints
```
GET    /api/models                     # Get available AI models
POST   /api/models/{model}/chat        # Chat with specific model
GET    /api/models/{model}/info        # Get model information and limits
```

### User and Authentication
```
GET    /api/user/profile               # Get current user info
GET    /api/user/usage                 # Get usage statistics
POST   /api/auth/logout                # Logout from chat service
```

### Health and Monitoring
```
GET    /health                         # Application health
GET    /api/health/bedrock             # AWS Bedrock connectivity
GET    /api/health/mongodb             # MongoDB connectivity
GET    /api/health/user-service        # User service connectivity
GET    /metrics                        # Application metrics
```

### WebSocket Endpoints
```
/ws/chat                              # WebSocket connection for real-time chat
```

---

## Database Schema

### MongoDB Collections

#### Conversations Collection
```json
{
  "_id": "ObjectId",
  "userId": "Long",
  "title": "String",
  "model": "claude-3-5-sonnet",
  "systemPrompt": "String",
  "settings": {
    "temperature": 0.7,
    "maxTokens": 1000,
    "topP": 0.9
  },
  "createdAt": "DateTime",
  "updatedAt": "DateTime",
  "messageCount": "Integer",
  "isActive": "Boolean",
  "tags": ["String"]
}
```

#### Messages Collection
```json
{
  "_id": "ObjectId",
  "conversationId": "ObjectId",
  "role": "user|assistant|system",
  "content": "String",
  "model": "String",
  "metadata": {
    "tokensUsed": "Integer",
    "responseTime": "Long",
    "cost": "Double",
    "modelVersion": "String"
  },
  "timestamp": "DateTime",
  "edited": "Boolean",
  "parentMessageId": "ObjectId",
  "reactions": ["String"]
}
```

#### Usage Analytics Collection
```json
{
  "_id": "ObjectId",
  "userId": "Long",
  "model": "String",
  "tokensUsed": "Integer",
  "cost": "Double",
  "responseTime": "Long",
  "date": "DateTime",
  "conversationId": "ObjectId",
  "apiCalls": "Integer"
}
```

---

## Configuration Files

### Frontend Configuration (vite.config.js)
```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3001,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      '/ws': {
        target: 'http://localhost:8081',
        ws: true
      }
    }
  },
  build: {
    outDir: '../chatbot-service/src/main/resources/static',
    emptyOutDir: true
  }
})
```

### Backend Configuration (application.yml)
```yaml
server:
  port: 8081

spring:
  application:
    name: chatbot-service
  
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/chatbot}
      
  security:
    oauth2:
      client:
        registration:
          user-management:
            client-id: chatbot-service
            client-secret: chatbot-secret
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope: read,write,profile
        provider:
          user-management:
            authorization-uri: http://localhost:8080/oauth2/authorize
            token-uri: http://localhost:8080/oauth2/token
            user-info-uri: http://localhost:8080/userinfo
            jwk-set-uri: http://localhost:8080/.well-known/jwks.json
            user-name-attribute: sub
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8080/.well-known/jwks.json

aws:
  region: ${AWS_REGION:us-east-1}
  bedrock:
    models:
      claude-3-5-sonnet: anthropic.claude-3-5-sonnet-20241022-v2:0
      claude-3-haiku: anthropic.claude-3-haiku-20240307-v1:0
      
user-service:
  base-url: ${USER_SERVICE_URL:http://localhost:8080}
  
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.yourname.chatbot: DEBUG
    org.springframework.security: DEBUG
```

### Docker Compose Configuration
```yaml
version: '3.8'

services:
  # Add to existing docker-compose.yml
  
  mongodb:
    image: mongo:7
    container_name: chatbot-mongodb
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: password123
      MONGO_INITDB_DATABASE: chatbot
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
    networks:
      - app-network

  redis:
    image: redis:7-alpine
    container_name: chatbot-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - app-network

  chatbot-service:
    build:
      context: ./chatbot-service
      dockerfile: Dockerfile
    container_name: chatbot-service
    ports:
      - "8081:8081"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://admin:password123@mongodb:27017/chatbot?authSource=admin
      SPRING_REDIS_HOST: redis
      USER_SERVICE_BASE_URL: http://user-management:8080
      AWS_REGION: us-east-1
    depends_on:
      - mongodb
      - redis
      - user-management
    networks:
      - app-network

volumes:
  mongodb_data:
  redis_data:

networks:
  app-network:
    external: true
```

---

## Security Implementation

### OAuth 2.0 Configuration

#### User Management Service (Authorization Server)
```java
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig {
    
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient chatbotClient = RegisteredClient.withId("chatbot-service")
            .clientId("chatbot-service")
            .clientSecret("{noop}chatbot-secret")
            .clientAuthenticationMethods(Set.of(
                ClientAuthenticationMethod.CLIENT_SECRET_POST,
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC
            ))
            .authorizationGrantTypes(Set.of(
                AuthorizationGrantType.AUTHORIZATION_CODE,
                AuthorizationGrantType.REFRESH_TOKEN
            ))
            .redirectUri("http://localhost:3001/login/oauth2/code/user-management")
            .scopes(Set.of("read", "write", "profile"))
            .clientSettings(ClientSettings.builder()
                .requireAuthorizationConsent(false)
                .build())
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(1))
                .refreshTokenTimeToLive(Duration.ofHours(8))
                .build())
            .build();

        return new InMemoryRegisteredClientRepository(chatbotClient);
    }
}
```

#### Chatbot Service (Resource Server)
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login/**", "/error", "/static/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            );

        return http.build();
    }
}
```

### CORS Configuration
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3001", "http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
```

---

## AWS Bedrock Integration

### Dependencies (pom.xml)
```xml
<dependencies>
    <!-- AWS Bedrock Runtime -->
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>bedrock-runtime</artifactId>
        <version>2.21.0</version>
    </dependency>
    
    <!-- AWS SDK Core -->
    <dependency>
        <groupId>software.amazon.awssdk</groupId>
        <artifactId>aws-core</artifactId>
        <version>2.21.0</version>
    </dependency>
</dependencies>
```

### Bedrock Service Implementation
```java
@Service
public class BedrockService {
    
    private final BedrockRuntimeClient bedrockClient;
    
    public BedrockService() {
        this.bedrockClient = BedrockRuntimeClient.builder()
            .region(Region.US_EAST_1)
            .build();
    }
    
    public String chatWithClaude(String message, String model) {
        // Implementation for Claude model interaction
        // Handle streaming responses
        // Manage conversation context
        // Track token usage
    }
}
```

---

## Testing Strategy

### Frontend Testing
```bash
# Install testing dependencies
npm install -D @testing-library/react @testing-library/jest-dom
npm install -D @testing-library/user-event vitest jsdom

# Run tests
npm run test
npm run test:coverage
```

### Backend Testing
```java
// Integration tests with TestContainers
@SpringBootTest
@Testcontainers
class ChatServiceIntegrationTest {
    
    @Container
    static MongoDBContainer mongodb = new MongoDBContainer("mongo:7");
    
    @Test
    void shouldProcessChatMessage() {
        // Test complete chat flow
    }
}
```

---

## Monitoring and Observability

### Application Metrics
- **Custom metrics**: Message count, response time, token usage
- **Spring Actuator**: Health, info, metrics endpoints
- **MongoDB metrics**: Connection pool, query performance
- **AWS Bedrock metrics**: API calls, costs, latency

### Logging Configuration
```yaml
logging:
  level:
    com.yourname.chatbot: INFO
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/chatbot-service.log
```

---

## Development Workflow

### Phase 1: Frontend Development (Days 1-7)
1. **Day 1-2**: Setup React project, basic layout, routing
2. **Day 3-4**: Chat components with mock data
3. **Day 5-6**: Conversation management, sidebar
4. **Day 7**: Polish UI, animations, responsive design

### Phase 2: Backend Integration (Days 8-14)
1. **Day 8-9**: Spring Boot setup, MongoDB integration
2. **Day 10-11**: Chat APIs, conversation management
3. **Day 12-13**: AWS Bedrock integration, AI responses
4. **Day 14**: WebSocket implementation, real-time features

### Phase 3: SSO Integration (Days 15-21)
1. **Day 15-16**: OAuth authorization server in User Management
2. **Day 17-18**: OAuth client configuration in Chat Service
3. **Day 19-20**: Frontend authentication integration
4. **Day 21**: Testing, bug fixes, documentation

---

## Deployment Architecture

### Local Development
```
Frontend Dev Server (Vite) → Backend API (Spring Boot) → MongoDB + Redis
http://localhost:3001     →  http://localhost:8081    →  Docker containers
```

### Integrated Deployment
```
Spring Boot Static Resources → Spring Boot API → MongoDB + Redis
http://localhost:8081         →  Internal       →  Docker containers
```

### Production Considerations
- **Container orchestration**: Docker Compose or Kubernetes
- **Load balancing**: Nginx or AWS Application Load Balancer
- **Database clustering**: MongoDB replica set
- **Caching layer**: Redis cluster
- **Monitoring**: Prometheus + Grafana
- **Log aggregation**: ELK stack or AWS CloudWatch

---

## Quick Start Commands

### 1. Clone and Setup
```bash
# Setup workspace
mkdir chatbot-project
cd chatbot-project

# Initialize Git repository
git init
git remote add origin <your-repo-url>
```

### 2. Frontend Development
```bash
# Create and start React frontend
npm create vite@latest chatbot-frontend -- --template react
cd chatbot-frontend
npm install
npm run dev
```

### 3. Backend Development
```bash
# Create Spring Boot backend
mkdir chatbot-service
cd chatbot-service
# Use Spring Initializr or provided setup
```

### 4. Infrastructure
```bash
# Start databases
docker-compose up -d mongodb redis

# Verify setup
docker ps
curl http://localhost:8081/health
```

---

## Success Criteria

### Phase 1 Success (Frontend)
- [ ] Beautiful, responsive chat interface
- [ ] Smooth animations and interactions
- [ ] Working conversation management with mock data
- [ ] Model selection and settings
- [ ] Mobile-responsive design

### Phase 2 Success (Backend Integration)
- [ ] All API endpoints functional
- [ ] Real AI responses from AWS Bedrock
- [ ] MongoDB storing conversations and messages
- [ ] WebSocket real-time communication
- [ ] Comprehensive error handling

### Phase 3 Success (SSO Integration)
- [ ] Single sign-on working between services
- [ ] No duplicate login screens
- [ ] Seamless navigation between User Management and Chat
- [ ] Secure token validation
- [ ] Session management across services

---

## Learning Outcomes

### Technical Skills
- **Modern React development** with hooks, context, and state management
- **Spring Boot microservices** architecture and best practices
- **OAuth 2.0 and JWT** authentication and authorization
- **MongoDB** document database design and operations
- **AWS Bedrock** AI service integration
- **WebSocket** real-time communication
- **Docker** containerization and orchestration

### Architectural Skills
- **Microservices design** patterns and communication
- **Authentication flows** in distributed systems
- **API design** and RESTful service development
- **Database modeling** for chat applications
- **Security implementation** in web applications
- **Service integration** and error handling

### Modern Development Practices
- **Component-driven development** with React
- **Test-driven development** with comprehensive testing
- **Configuration management** and environment setup
- **Monitoring and observability** implementation
- **Documentation** and project organization

---

This project plan provides a comprehensive roadmap for building a modern, professional chat bot system that integrates with your existing User Management Service through industry-standard SSO patterns.