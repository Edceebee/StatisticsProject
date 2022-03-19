package com.challenge.Statistics;

import com.challenge.Statistics.models.builders.StatisticsRequest;
import com.challenge.Statistics.models.StatisticsResponse;
import com.challenge.Statistics.models.Transaction;
import com.challenge.Statistics.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class ApplicationTests {

	@Autowired
	TransactionService transactionService;

	@Before
	public void init() {
		transactionService.clearCache();
	}

	@Order(5)
	@Test
	void testToCreateTransactionWithAmountAndTime(){
		long current = Instant.now().toEpochMilli();
		Transaction request = StatisticsRequest.createTransactions().withAmount(1.1).withTimestamp(current).build();
		log.info("request --> {}", request);
		boolean added = transactionService.createTransaction(request, current);
		log.info("added --> {}", added);
		Assertions.assertTrue(added);
	}

	@Order(4)
	@Test
	 void testToCreateTransactionWithAMinuteSubtracted(){
		long current = Instant.now().toEpochMilli();
		Transaction request = StatisticsRequest.createTransactions().withAmount(2.1).withTimestamp(current-60000).build();
		log.info("request --> {}", request);
		boolean added = transactionService.createTransaction(request, current);
		log.info("added --> {}", added);
		Assertions.assertFalse(added);
	}

	@Order(3)
	@Test
	void testToCreateTransactionWithinPastTimestampWithinTheMinute_created(){
		long current = Instant.now().toEpochMilli();
		Transaction request = StatisticsRequest.createTransactions().withAmount(2.1).withTimestamp(current-50000).build();
		log.info("request --> {}", request);
		boolean added = transactionService.createTransaction(request, current);
		log.info("added --> {}", added);
		Assertions.assertFalse(added);
	}

	@Order(1)
	@Test
	void testGetStatistics_withAnyData_success() throws Exception{
		long timestamp = Instant.now().toEpochMilli();
		StatisticsResponse response = transactionService.getStatistics(timestamp);
		Assertions.assertEquals(0, response.getCount());
		Assertions.assertEquals(0, response.getMax(), 0);
		Assertions.assertEquals(0, response.getMin(), 0);
		Assertions.assertEquals(0, response.getAvg(), 0);
	}

	@Order(2)
	@Test
	void testAddAndGetStatistics_withValidTimestampMultipleThread_success() throws Exception{
		ExecutorService executorService = Executors.newFixedThreadPool(3);
		int n = 0;
		double amount = 1.0;
		int count = 5800;
		long timestamp = Instant.now().toEpochMilli();
		long requestTime = timestamp;
		while(n<count) {
			// Time frame is managed from 0 to 59, for cache size 60.
			if(timestamp - requestTime >= 59000) {
				requestTime = timestamp;
			}
			Transaction request = StatisticsRequest.createTransactions().withAmount(amount).withTimestamp(requestTime).build();
			executorService.submit(() -> transactionService.createTransaction(request, timestamp));
			n++;
			amount++;
			requestTime -= 1;
		}

		executorService.shutdown();
		Thread.sleep(1000);
		StatisticsResponse response = transactionService.getStatistics(timestamp);
		Assertions.assertEquals(count, response.getCount());
		Assertions.assertEquals(count, response.getMax(), 0);
		Assertions.assertEquals(1, response.getMin(), 0);
		Assertions.assertEquals(2900.5, response.getAvg(), 0);
	}



}
