package com.chatbot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;
import java.util.Map;

@Service
public class BedrockService {

    @Autowired
    private BedrockRuntimeClient bedrockRuntimeClient;

    private final Map<String, String> modelMappings = Map.of(
        "claude-3-5-sonnet", "anthropic.claude-3-5-sonnet-20241022-v2:0",
        "claude-3-haiku", "anthropic.claude-3-haiku-20240307-v1:0", 
        "cohere-command-r", "cohere.command-r-v1:0",
        "cohere-command-r-plus", "cohere.command-r-plus-v1:0"
    );

    public String generateResponse(String userMessage, String model) {
        try {
            String modelId = getModelId(model);
            
            ConverseRequest request = ConverseRequest.builder()
                .modelId(modelId)
                .messages(Message.builder()
                    .role(ConversationRole.USER)
                    .content(ContentBlock.fromText(userMessage))
                    .build())
                .inferenceConfig(InferenceConfiguration.builder()
                    .maxTokens(255)
                    .temperature(0.7f)
                    .build())
                .build();

            ConverseResponse response = bedrockRuntimeClient.converse(request);
            
            return response.output().message().content().get(0).text();
            
        } catch (Exception e) {
            System.err.println("Bedrock error: " + e.getMessage());
            return "Sorry, I'm having trouble connecting to the AI service. Please try again.";
        }
    }

    private String getModelId(String modelKey) {
        return modelMappings.getOrDefault(modelKey, "anthropic.claude-3-5-sonnet-20241022-v2:0");
    }

    public boolean isServiceAvailable() {
        try {
            ConverseRequest testRequest = ConverseRequest.builder()
                .modelId("anthropic.claude-3-5-sonnet-20241022-v2:0")
                .messages(Message.builder()
                    .role(ConversationRole.USER)
                    .content(ContentBlock.fromText("test"))
                    .build())
                .build();
            bedrockRuntimeClient.converse(testRequest);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String[] getAvailableModels() {
        return modelMappings.keySet().toArray(new String[0]);
    }
}