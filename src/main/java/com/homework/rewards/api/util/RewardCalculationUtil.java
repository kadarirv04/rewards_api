package com.homework.rewards.api.util;

import java.math.BigDecimal;

public class RewardCalculationUtil {
    public static int calculateRewardPoints(BigDecimal amount) {
        BigDecimal points = BigDecimal.ZERO;
        BigDecimal hundred = new BigDecimal("100");
        BigDecimal fifty = new BigDecimal("50");
        if (amount.compareTo(hundred) > 0) {
            BigDecimal over100 = amount.subtract(hundred);
            points = points.add(over100.multiply(new BigDecimal("2")));
            points = points.add(fifty); // $50 between 50 and 100 always gets 1x points
        } else if (amount.compareTo(fifty) > 0) {
            BigDecimal over50 = amount.subtract(fifty);
            points = points.add(over50);
        }
        return points.intValue();
    }

    public static String getRewardPointsExplanation(BigDecimal amount) {
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