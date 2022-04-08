package com.abshekh.kafkastreampoc.model.kafka;

import lombok.Data;

@Data
public class Sensor {
    private String id;
    private Float acceleration;
    private Float velocity;
    private Float temperature;
}
