package org.example.library.service;

public class FinanceStats {
    private final double earnedRentals;
    private final double activeDeposits;

    public FinanceStats(double earnedRentals, double activeDeposits) {
        this.earnedRentals = earnedRentals;
        this.activeDeposits = activeDeposits;
    }

    public double getEarnedRentals() {
        return earnedRentals;
    }

    public double getActiveDeposits() {
        return activeDeposits;
    }
}
