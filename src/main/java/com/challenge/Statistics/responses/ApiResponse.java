package com.challenge.Statistics.responses;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse {

    /**
     * field for api message.
     */
    private String message;
    /**
     * field to check if it's successful.
     */
    private boolean isSuccessful;
    /**
     * field for the time of response.
     */
    private LocalDateTime timeStamp;


    /**
     * Constructor for class.
     * @param message represents message to be shown.
     * @param isSuccessful to check if it is successful.
     */
    public ApiResponse(String message, boolean isSuccessful) {
        this.message = message;
        this.isSuccessful = isSuccessful;
        this.timeStamp = LocalDateTime.now();
    }


    /**
     * constructor for only message.
     * @param message represents message to be shown.
     */
    public ApiResponse(String message) {
        this.message = message;
    }
}
