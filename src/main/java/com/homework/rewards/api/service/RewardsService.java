package com.homework.rewards.api.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class RewardsService {

    public String getServiceInfo() {
        return "RewardsService - Business logic implemented in ApiController for simplicity";
    }

    public int calcPoints(BigDecimal amount) {
        int points = 0;
        if (amount.compareTo(new BigDecimal("100")) > 0) {
            points += (amount.intValue() - 100) * 2 + 50;
        } else if (amount.compareTo(new BigDecimal("50")) > 0) {
            points += (amount.intValue() - 50);
        }
        return points;
    }

    public String getPointsExplanation(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("100")) > 0) {
            int over100 = amount.intValue() - 100;
            int pointsOver100 = over100 * 2;
            int points50to100 = 50;
            return String.format("$%d over $100 = %d × 2 = %d points, $50-$100 = %d points, Total = %d points",
                over100, over100, pointsOver100, points50to100, pointsOver100 + points50to100);
        } else if (amount.compareTo(new BigDecimal("50")) > 0) {
            int over50 = amount.intValue() - 50;
            return String.format("$%d over $50 = %d × 1 = %d points", over50, over50, over50);
        } else {
            return "Amount under $50 = 0 points";
        }
    }
} 