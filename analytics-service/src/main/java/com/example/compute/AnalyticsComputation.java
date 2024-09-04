package com.example.compute;

import com.example.repository.ProjectProgressRepository;
import com.example.repository.TaskStatisticsRepository;
import com.example.repository.UserPerformanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import com.example.model.ProjectProgress;
import com.example.model.TaskStatistics;
import com.example.model.UserPerformance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsComputation {

    private final SparkSession sparkSession;
    private final TaskStatisticsRepository taskStatisticsRepository;
    private final UserPerformanceRepository userPerformanceRepository;
    private final ProjectProgressRepository projectProgressRepository;

    @Value("${spring.datasource.url}")
    private String DATABASE_URL;
    @Value("${spring.datasource.username}")
    private String DATABASE_USERNAME;
    @Value("${spring.datasource.password}")
    private String DATABASE_PASSWORD;

    private static final String ANALYTICS_DATA_TABLE = "analytics_data";

    private Dataset<Row> buildDataFrame() {
        return sparkSession.read()
                .format("jdbc")
                .option("url", DATABASE_URL)
                .option("dbtable", ANALYTICS_DATA_TABLE)
                .option("user", DATABASE_USERNAME)
                .option("password", DATABASE_PASSWORD)
                .load();
    }

    public void computeTaskStatistics() {
        log.info("Computing task statistics");
        Dataset<Row> analyticsDataDF = buildDataFrame();
        analyticsDataDF.createOrReplaceTempView(ANALYTICS_DATA_TABLE);

        String query = "SELECT "
                + "0 AS id, "
                + "SUM(CASE WHEN status = 'TO_DO' THEN 1 ELSE 0 END) AS toDo, "
                + "SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS inProgress, "
                + "SUM(CASE WHEN status = 'DONE' THEN 1 ELSE 0 END) AS done, "
                + "COUNT(*) AS totalTasks, "
                + "CONCAT("
                + "FLOOR(AVG(time_to_complete) / 3600000), ' hours ', "
                + "FLOOR((AVG(time_to_complete) % 3600000) / 60000), ' minutes ', "
                + "FLOOR((AVG(time_to_complete) % 60000) / 1000), ' seconds'"
                + ") AS averageTimeToComplete, "
                + "current_timestamp AS lastUpdatedAt "
                + "FROM " + ANALYTICS_DATA_TABLE;

        Dataset<Row> resultDF = sparkSession.sql(query);
        List<TaskStatistics> taskStatisticsList = resultDF.as(Encoders.bean(TaskStatistics.class))
                .collectAsList();

        if (!taskStatisticsList.isEmpty()) {
            taskStatisticsRepository.saveAll(taskStatisticsList);
        }
        log.info("Finished computing task statistics");
    }

    public void computeUserPerformance() {
        log.info("Computing user performance");
        Dataset<Row> analyticsDataDF = buildDataFrame();
        analyticsDataDF.createOrReplaceTempView(ANALYTICS_DATA_TABLE);

        String query = "WITH time_data AS ("
                + "    SELECT "
                + "        0 AS id, "
                + "        user_id AS userId, "
                + "        SUM(CASE WHEN status = 'TO_DO' THEN 1 ELSE 0 END) AS toDo, "
                + "        SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) AS inProgress, "
                + "        SUM(CASE WHEN status = 'DONE' THEN 1 ELSE 0 END) AS done, "
                + "        COUNT(*) AS totalTasks, "
                + "        AVG(time_to_complete) AS avgTimeToComplete "
                + "    FROM " + ANALYTICS_DATA_TABLE + " "
                + "    GROUP BY user_id "
                + ") "
                + "SELECT "
                + "    id, "
                + "    userId, "
                + "    toDo, "
                + "    inProgress, "
                + "    done, "
                + "    totalTasks, "
                + "    CONCAT("
                + "        FLOOR(avgTimeToComplete / 3600000), ' hours ', "
                + "        FLOOR((avgTimeToComplete % 3600000) / 60000), ' minutes ', "
                + "        FLOOR((avgTimeToComplete % 60000) / 1000), ' seconds'"
                + "    ) AS averageTimeToComplete, "
                + "    ROUND("
                + "        (done * 1.0 / totalTasks) * 100, "
                + "        2"
                + "    ) AS performanceScore, "
                + "    current_timestamp AS lastUpdatedAt "
                + "FROM time_data";

        Dataset<Row> resultDF = sparkSession.sql(query);
        List<UserPerformance> userPerformanceList = resultDF.as(Encoders.bean(UserPerformance.class))
                .collectAsList();

        if (!userPerformanceList.isEmpty()) {
            userPerformanceRepository.saveAll(userPerformanceList);
        }
        log.info("Finished computing user performance");
    }

    public void computeProjectProgress() {
        log.info("Computing project progress");
        Dataset<Row> analyticsDataDF = buildDataFrame();
        analyticsDataDF.createOrReplaceTempView(ANALYTICS_DATA_TABLE);

        String query = "WITH project_summary AS ("
                + "    SELECT "
                + "        0 AS id, "
                + "        project_id AS projectId, "
                + "        COUNT(*) AS totalTasks, "
                + "        SUM(CASE WHEN status = 'DONE' THEN 1 ELSE 0 END) AS tasksCompleted, "
                + "        AVG(time_to_complete) AS avgTimeToComplete, "
                + "        current_timestamp AS lastUpdatedAt "
                + "    FROM " + ANALYTICS_DATA_TABLE + " "
                + "    GROUP BY project_id "
                + ") "
                + "SELECT "
                + "    id, "
                + "    projectId, "
                + "    tasksCompleted, "
                + "    totalTasks, "
                + "    ROUND("
                + "        (tasksCompleted * 1.0 / totalTasks) * 100, "
                + "        2"
                + ") AS completionPercentage, "
                + "    NOW() + INTERVAL '1 day' * FLOOR(avgTimeToComplete / 86400000) AS estimatedCompletionDate, " // Renaming estimated_completion_date
                + "    lastUpdatedAt "
                + "FROM project_summary";

        Dataset<Row> resultDF = sparkSession.sql(query);
        List<ProjectProgress> projectProgressList = resultDF.as(Encoders.bean(ProjectProgress.class))
                .collectAsList();

        if (!projectProgressList.isEmpty()) {
            projectProgressRepository.saveAll(projectProgressList);
        }
        log.info("Finished computing project progress");
    }

}


