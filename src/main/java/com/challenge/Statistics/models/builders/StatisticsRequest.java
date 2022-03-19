package com.challenge.Statistics.models.builders;

import com.challenge.Statistics.models.Transaction;

public class StatisticsRequest {
    private Transaction request;

    private StatisticsRequest() {
        request = new Transaction();
    }

    public static StatisticsRequest createTransactions(){
        return new StatisticsRequest();
    }

    public StatisticsRequest withAmount(double amount){
        request.setAmount(amount);
        return this;
    }

    public StatisticsRequest withTimestamp(long timestamp){
        request.setTransactionTime(timestamp);
        return this;
    }

    public Transaction build(){
        return request;
    }

}
