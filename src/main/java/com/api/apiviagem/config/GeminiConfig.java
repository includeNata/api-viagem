package com.api.apiviagem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gemini")
public class GeminiConfig {
    
    private int maxPromptSize = 30000;
    private int overlapSize = 1000;
    private int delayBetweenChunks = 200;
    private String model = "gemini-2.0-flash";
    
    public int getMaxPromptSize() {
        return maxPromptSize;
    }
    
    public void setMaxPromptSize(int maxPromptSize) {
        this.maxPromptSize = maxPromptSize;
    }
    
    public int getOverlapSize() {
        return overlapSize;
    }
    
    public void setOverlapSize(int overlapSize) {
        this.overlapSize = overlapSize;
    }
    
    public int getDelayBetweenChunks() {
        return delayBetweenChunks;
    }
    
    public void setDelayBetweenChunks(int delayBetweenChunks) {
        this.delayBetweenChunks = delayBetweenChunks;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
}
