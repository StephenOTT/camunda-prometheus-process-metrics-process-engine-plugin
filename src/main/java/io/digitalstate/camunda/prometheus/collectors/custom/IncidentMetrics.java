package io.digitalstate.camunda.prometheus.collectors.custom;

import io.digitalstate.camunda.prometheus.collectors.SimpleGaugeMetric;
import org.camunda.bpm.engine.ProcessEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class IncidentMetrics {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncidentMetrics.class);

    public IncidentMetrics(ProcessEngine processEngine, long startDelayMills, long frequencyMills){
        String timerName = this.getClass().getName() + " timer";
        new Timer(timerName, true).schedule(new TimerTask() {
            @Override
            public void run() {
                collectAll(processEngine);
            }
        }, startDelayMills, frequencyMills);
    }

    /**
     * Collect Count of open incidents.
     * @param processEngine
     * @param engineName
     */
    public static void collectOpenIncidentCount(ProcessEngine processEngine, String engineName){
        SimpleGaugeMetric gauge = new SimpleGaugeMetric(
                "open_incidents_count",
                "The number of incidents currently open.",
                Arrays.asList("engine_name"));

        long count = processEngine.getHistoryService()
                .createHistoricIncidentQuery()
                .open()
                .count();

        LOGGER.debug("Collecting Metric Number of open incidents " + count);

        gauge.setValue(count, Arrays.asList(engineName));
    }

    /**
     * Collect Count of resolved incidents.
     * @param processEngine
     * @param engineName
     */
    public static void collectResolvedIncidentCount(ProcessEngine processEngine, String engineName){
        SimpleGaugeMetric gauge = new SimpleGaugeMetric(
                "resolved_incidents_count",
                "The number of incidents resolved.",
                Arrays.asList("engine_name"));

        long count = processEngine.getHistoryService()
                .createHistoricIncidentQuery()
                .resolved()
                .count();

        LOGGER.debug("Collecting Metric Number of resolved incidents " + count);

        gauge.setValue(count, Arrays.asList(engineName));
    }

    /**
     * Collect Count of deleted incidents.
     * @param processEngine
     * @param engineName
     */
    public static void collectDeletedIncidentCount(ProcessEngine processEngine, String engineName){
        SimpleGaugeMetric gauge = new SimpleGaugeMetric(
                "deleted_incidents_count",
                "The number of incidents deleted.",
                Arrays.asList("engine_name"));

        long count = processEngine.getHistoryService()
                .createHistoricIncidentQuery()
                .deleted()
                .count();

        LOGGER.debug("Collecting Metric Number of deleted incidents " + count);

        gauge.setValue(count, Arrays.asList(engineName));
    }


    /**
     * Collects all collectors defined in this class.
     * @param processEngine
     */
    public static void collectAll(ProcessEngine processEngine){
        String engineName = processEngine.getName();

        collectOpenIncidentCount(processEngine, engineName);
        collectDeletedIncidentCount(processEngine, engineName);
        collectResolvedIncidentCount(processEngine, engineName);
    }
}
