package com.example.config;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.example.repository")
@EnableScheduling
public class AppConfig {

    @Value("${spark.master}")
    private String sparkMaster;

    @Value("${spark.executor.memory}")
    private String sparkExecutorMemory;

    @Value("${spark.executor.cores}")
    private String sparkExecutorCores;

    @Value("${spark.driver.memory}")
    private String sparkDriverMemory;

    @Value("${spark.driver.cores}")
    private String sparkDriverCores;

    @Value("${spark.sql.shuffle.partitions}")
    private String sparkSqlShufflePartitions;

    @Value("${spark.driver.port}")
    private String sparkDriverPort;

    @Value("${spark.blockManager.port}")
    private String sparkBlockManagerPort;

    @Bean
    public SparkSession sparkSession() {

        SparkConf sparkConf = new SparkConf()
                .setAppName("Spark Demo")
                .setMaster(sparkMaster)
                .set("spark.executor.memory", sparkExecutorMemory)
                .set("spark.executor.cores", sparkExecutorCores)
                .set("spark.driver.memory", sparkDriverMemory)
                .set("spark.driver.cores", sparkDriverCores)
//                .set("spark.driver.port", sparkDriverPort)
//                .set("spark.blockManager.port", sparkBlockManagerPort)
                .set("spark.sql.shuffle.partitions", sparkSqlShufflePartitions);

        return SparkSession.builder()
                .appName("Spark Demo")
                .master(sparkMaster)
                .config(sparkConf)
                .getOrCreate();
    }
}

