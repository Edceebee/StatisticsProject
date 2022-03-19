package com.challenge.Statistics.service;

import com.challenge.Statistics.StatisticsCache.Cache;
import com.challenge.Statistics.models.builders.StatisticsResponseBuilder;
import com.challenge.Statistics.models.ClearCache;
import com.challenge.Statistics.models.Statistics;
import com.challenge.Statistics.models.StatisticsResponse;
import com.challenge.Statistics.models.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

import static com.challenge.Statistics.utils.Constants.ONE_MINUTE_IN_MS;

@Service
public class TransactionService {

    @Autowired
    Cache<Long, Statistics> cache;


    public boolean createTransaction(Transaction transaction, long timeStamp) {

    long requestTime = transaction.getTransactionTime();
    long delay = timeStamp - requestTime;

    if (delay < 0 || delay >= ONE_MINUTE_IN_MS) {
        return false;
    }
    else {
        Long key = getKeyFromTimeStamp(requestTime);
        Statistics statistics = cache.get(key);
        if (statistics == null) {
            synchronized (cache) {
                statistics = cache.get(key);
                if (statistics == null) {
                    statistics = new Statistics();
                    cache.put(key, statistics);
                }
            }
        }
        statistics.updateStatistics(transaction.getAmount());
    }
    return true;

    }

    public Long getKeyFromTimeStamp(long timeStamp) {
        return (timeStamp * cache.getCapacity()) / ONE_MINUTE_IN_MS;
    }


    public StatisticsResponse getStatisticsFromCacheCopy(Map<Long, Statistics> copy, long timeStamp) {

        double sum = 0;
        double avg = 0;
        double max = 0;
        double min = Double.MAX_VALUE;
        long count = 0;
        Long key = getKeyFromTimeStamp(timeStamp);

        for (Map.Entry<Long, Statistics> e : copy.entrySet()) {
            Long eKey = e.getKey();
            Long timeFrame = key - eKey;
            if(timeFrame >= 0 && timeFrame < cache.getCapacity()) {
                Statistics eValue = e.getValue();
                if(eValue.getCount() > 0) {
                    sum += eValue.getSum();
                    min = Math.min(min, eValue.getMin());
                    max = Math.max(max, eValue.getMax());
                    count += eValue.getCount();
                }
            }
        }
        if(count == 0) {
            min = 0;
            avg = 0;
        } else {
            avg = sum / count;
        }

        return StatisticsResponseBuilder.createStatisticsResponse().withSum(sum).withAvg(avg)
                .withMax(max).withMin(min).withCount(count).build();

    }

    public StatisticsResponse getStatistics(long timeStamp) {
        Map<Long, Statistics> copy = cache.entrySet().parallelStream().collect(Collectors
                .toMap(Map.Entry::getKey, e -> e.getValue().getStatistics()));
        return getStatisticsFromCacheCopy(copy, timeStamp);
    }

    public void clearCache() {
        cache.clear();
    }

    public boolean deleteTransactions(ClearCache cache) {
        clearCache();
        return true;
    }
}
