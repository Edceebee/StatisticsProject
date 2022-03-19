package com.challenge.Statistics.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {
    private Lock lock = new ReentrantLock();
    private double sum = 0;
    private double max = 0;
    private double min = Double.MAX_VALUE;
    private long count = 0;

    private Statistics(Statistics s) {
        this.sum = s.sum;
        this.max = s.max;
        this.min = s.min;
        this.count = s.count;
    }

    public void updateStatistics(double amount) {
        try{
            lock.lock();
            sum += amount;
            count++;
            min = Math.min(min, amount);
            max = Math.max(max, amount);
        }finally {
            lock.unlock();
        }
    }

    public Statistics getStatistics() {
        try{
            lock.lock();
            return new Statistics(this);
        }finally {
            lock.unlock();
        }
    }

    public double getSum() {
        return sum;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public long getCount() {
        return count;
    }

}
