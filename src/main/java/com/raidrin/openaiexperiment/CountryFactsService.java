package com.raidrin.openaiexperiment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryFactsService {
    @Autowired
    private OpenAiService openAiService;
    private static final String SYSTEM_TASK_MESSAGE = """
            You are are an API server that provides information about countries in JSON format.
            Don't say anything else. Respond only with the JSON.
                            
            The user will send you a country name and you will respond with information about that country.
                            
            Respond in JSON format, including the following fields:
            - name: string
            - capital: string
            - continent: string
            - population: string
            - area: string
            - currency: string
            - languages: string[]
                            
            Don't add anything else in the end after you respond with the JSON.
            """;

    public CountryInfo getFact(String countryName) throws NoCountryFoundException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        CountryInfo countryInfo = objectMapper.readValue(
                getCountryInfo(countryName),
                CountryInfo.class
        );
        return countryInfo;
    }

    private String getCountryInfo(String countryName) throws NoCountryFoundException {
        ChatMessage systemMessage = new ChatMessage();
        systemMessage.setRole("system");
        systemMessage.setContent(SYSTEM_TASK_MESSAGE);

        ChatMessage userMessage = new ChatMessage();
        userMessage.setRole("user");
        userMessage.setContent("Show me facts for " + countryName);

        ChatCompletionRequest chatCompletionRequest =
                ChatCompletionRequest.builder()
                        .model("gpt-3.5-turbo")
                        .messages(
                                List.of(
                                        systemMessage,
                                        userMessage
                                )
                        )
                        .temperature(0.0)
                        .build();
        ChatCompletionResult chatCompletionResult = openAiService.createChatCompletion(chatCompletionRequest);

        List<ChatCompletionChoice> chatCompletionChoices = chatCompletionResult.getChoices();
        if (chatCompletionChoices.size() != 1) {
            throw new NoCountryFoundException("Expected 1 choice, got " + chatCompletionChoices.size());
        }
        ChatCompletionChoice chatCompletionChoice = chatCompletionChoices.get(0);
        return chatCompletionChoice.getMessage().getContent();
    }
}
