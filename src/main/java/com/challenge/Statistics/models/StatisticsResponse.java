package com.challenge.Statistics.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private double sum;
    private double avg;
    private double max;
    private double min;
    private double count;
}
