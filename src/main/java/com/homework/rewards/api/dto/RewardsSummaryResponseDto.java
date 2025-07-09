package com.homework.rewards.api.dto;

import java.util.Map;

public class RewardsSummaryResponseDto {
    private Map<Long, ResponseDto> rewards;

    public RewardsSummaryResponseDto() {}

    public RewardsSummaryResponseDto(Map<Long, ResponseDto> rewards) {
        this.rewards = rewards;
    }

    public Map<Long, ResponseDto> getRewards() {
        return rewards;
    }

    public void setRewards(Map<Long, ResponseDto> rewards) {
        this.rewards = rewards;
    }
} 