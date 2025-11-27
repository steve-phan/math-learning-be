package com.mathlearning.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mathlearning.dto.GradingResult;
import com.mathlearning.exception.AIGradingException;
import com.mathlearning.service.IAIGradingService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIGradingServiceImpl implements IAIGradingService {

    @Value("${app.ai.openai.api-key}")
    private String openaiApiKey;

    @Value("${app.ai.openai.model:gpt-4o}")
    private String model;

    private final ObjectMapper objectMapper;

    @Override
    public GradingResult gradeSubmission(String imageUrl, String questionText, String correctAnswer,
            Integer gradeLevel) {
        long startTime = System.currentTimeMillis();

        log.debug("Starting AI grading for question: {}, grade level: {}", questionText, gradeLevel);

        try {
            OpenAiService service = new OpenAiService(openaiApiKey, Duration.ofSeconds(60));

            // Create structured prompt for grading
            String prompt = buildGradingPrompt(questionText, correctAnswer, gradeLevel);

            ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(),
                    "You are an expert math teacher for Grade " + gradeLevel + " students. " +
                            "You grade student work strictly but fairly. " +
                            "Always respond with valid JSON only, no additional text.");

            ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), prompt);

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(List.of(systemMessage, userMessage))
                    .temperature(0.3)
                    .maxTokens(500)
                    .build();

            String response = service.createChatCompletion(completionRequest)
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            log.debug("Received AI response, parsing JSON");

            // Parse JSON response
            GradingResult result = parseGradingResponse(response);
            result.setProcessingTimeMs((int) (System.currentTimeMillis() - startTime));
            result.setAiProvider("GPT4O");

            log.info("AI grading completed successfully in {}ms, score: {}",
                    result.getProcessingTimeMs(), result.getScore());

            return result;

        } catch (Exception e) {
            log.error("Error during AI grading", e);
            throw new AIGradingException("Failed to grade submission with AI: " + e.getMessage(), e);
        }
    }

    private String buildGradingPrompt(String questionText, String correctAnswer, Integer gradeLevel) {
        return String.format("""
                Grade this Grade %d math problem:

                Question: %s
                Correct Answer: %s

                Analyze the student's handwritten work in the image and provide:
                1. A score out of 10
                2. Whether the answer is correct (true/false)
                3. Detailed feedback on what they did right or wrong
                4. Step-by-step correct solution
                5. Topic tags (e.g., ["algebra", "equations"])

                Respond ONLY with valid JSON in this exact format:
                {
                  "score": 8.5,
                  "correct": true,
                  "feedback": "Your work is mostly correct...",
                  "correctSteps": ["Step 1: ...", "Step 2: ..."],
                  "topicTags": ["algebra", "linear equations"]
                }
                """, gradeLevel, questionText, correctAnswer);
    }

    private GradingResult parseGradingResponse(String jsonResponse) {
        try {
            // Clean response if it contains markdown code blocks
            String cleaned = jsonResponse.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            }
            if (cleaned.startsWith("```")) {
                cleaned = cleaned.substring(3);
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }
            cleaned = cleaned.trim();

            JsonNode node = objectMapper.readTree(cleaned);

            GradingResult result = new GradingResult();
            result.setScore(BigDecimal.valueOf(node.get("score").asDouble()));
            result.setCorrect(node.get("correct").asBoolean());
            result.setFeedback(node.get("feedback").asText());

            // Parse correctSteps array
            JsonNode stepsNode = node.get("correctSteps");
            if (stepsNode != null && stepsNode.isArray()) {
                List<String> steps = new java.util.ArrayList<>();
                stepsNode.forEach(step -> steps.add(step.asText()));
                result.setCorrectSteps(steps);
            }

            // Parse topicTags array
            JsonNode tagsNode = node.get("topicTags");
            if (tagsNode != null && tagsNode.isArray()) {
                List<String> tags = new java.util.ArrayList<>();
                tagsNode.forEach(tag -> tags.add(tag.asText()));
                result.setTopicTags(tags);
            }

            return result;
        } catch (Exception e) {
            log.error("Failed to parse AI grading response", e);
            throw new AIGradingException("Failed to parse AI response: " + e.getMessage(), e);
        }
    }
}
