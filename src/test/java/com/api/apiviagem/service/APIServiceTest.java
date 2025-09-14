package com.api.apiviagem.service;

import com.api.apiviagem.config.GeminiConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class APIServiceTest {

    @Mock
    private GeminiConfig geminiConfig;

    @InjectMocks
    private APIService apiService;

    @BeforeEach
    void setUp() {
        when(geminiConfig.getMaxPromptSize()).thenReturn(1000);
        when(geminiConfig.getOverlapSize()).thenReturn(100);
        when(geminiConfig.getDelayBetweenChunks()).thenReturn(100);
        when(geminiConfig.getModel()).thenReturn("gemini-2.0-flash");
    }

    @Test
    void testSplitPromptIntoChunks_SmallPrompt() throws Exception {
        // Arrange
        String smallPrompt = "Este é um prompt pequeno que não deve ser dividido.";
        
        // Act
        Method method = APIService.class.getDeclaredMethod("splitPromptIntoChunks", String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<String> result = (java.util.List<String>) method.invoke(apiService, smallPrompt);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals(smallPrompt, result.get(0));
    }

    @Test
    void testSplitPromptIntoChunks_LargePrompt() throws Exception {
        // Arrange
        StringBuilder largePrompt = new StringBuilder();
        for (int i = 0; i < 1500; i++) {
            largePrompt.append("Esta é uma frase de teste para criar um prompt grande. ");
        }
        
        // Act
        Method method = APIService.class.getDeclaredMethod("splitPromptIntoChunks", String.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.List<String> result = (java.util.List<String>) method.invoke(apiService, largePrompt.toString());
        
        // Assert
        assertTrue(result.size() > 1, "Prompt grande deve ser dividido em múltiplas partes");
        
        // Verifica se cada parte não excede o tamanho máximo
        for (String chunk : result) {
            assertTrue(chunk.length() <= 1000, "Cada parte deve ter no máximo 1000 caracteres");
        }
    }

    @Test
    void testFindNaturalBreakPoint() throws Exception {
        // Arrange
        String text = "Primeira frase. Segunda frase! Terceira frase? Quarta frase; Quinta frase.";
        
        // Act
        Method method = APIService.class.getDeclaredMethod("findNaturalBreakPoint", String.class, int.class, int.class);
        method.setAccessible(true);
        int result = (int) method.invoke(apiService, text, 0, 50);
        
        // Assert
        assertTrue(result > 0, "Deve encontrar um ponto de quebra natural");
        assertTrue(result <= 50, "O ponto de quebra deve estar dentro do intervalo especificado");
    }

    @Test
    void testCombineResponses() throws Exception {
        // Arrange
        java.util.List<String> responses = java.util.List.of(
            "Primeira resposta",
            "Segunda resposta",
            "Terceira resposta"
        );
        
        // Act
        Method method = APIService.class.getDeclaredMethod("combineResponses", java.util.List.class);
        method.setAccessible(true);
        String result = (String) method.invoke(apiService, responses);
        
        // Assert
        assertTrue(result.contains("Primeira resposta"));
        assertTrue(result.contains("Segunda resposta"));
        assertTrue(result.contains("Terceira resposta"));
        assertTrue(result.contains("--- Continuação da Resposta ---"));
    }
}
