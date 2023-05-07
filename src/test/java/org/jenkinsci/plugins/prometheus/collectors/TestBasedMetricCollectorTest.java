package org.jenkinsci.plugins.prometheus.collectors;

import hudson.tasks.test.AbstractTestResultAction;
import io.prometheus.client.Collector;
import org.jenkinsci.plugins.prometheus.collectors.testutils.MockedRunCollectorTest;
import org.jenkinsci.plugins.prometheus.config.PrometheusConfiguration;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

public class TestBasedMetricCollectorTest extends MockedRunCollectorTest {

    @Test
    public void testCannotBeExecuted() {


        PrometheusConfiguration config = mock(PrometheusConfiguration.class);
        try (MockedStatic<PrometheusConfiguration> configStatic = mockStatic(PrometheusConfiguration.class)) {
            configStatic.when(() -> PrometheusConfiguration.get()).thenReturn(config);

            TestBasedMetricCollector sut = new TestBasedMetricCollector(getLabelNames(), getNamespace(), getSubSystem(), "") {
                @Override
                protected Collector initCollector() {
                    return null;
                }

                @Override
                public void calculateMetric(Object jenkinsObject, String[] labelValues) {
                    // do nothing
                }
            };

            Assertions.assertFalse(sut.canBeCalculated(null), "Cannot be calculated when run is null");

            when(mock.isBuilding()).thenReturn(false);
            Assertions.assertFalse(sut.canBeCalculated(mock), "Cannot be calculated if build is running");

            when(mock.isBuilding()).thenReturn(true);
            Assertions.assertFalse(sut.canBeCalculated(mock), "Cannot be calculated if build is running");

            when(mock.isBuilding()).thenReturn(false);
            Assertions.assertFalse(sut.canBeCalculated(mock), "Cannot be calculated when isFetchTestResults is false");

            when(config.isFetchTestResults()).thenReturn(true);
            Assertions.assertFalse(sut.canBeCalculated(mock), "Cannot be calculated when isFetchTestResults is true but no test results found");


            AbstractTestResultAction action = mock(AbstractTestResultAction.class);
            when(mock.getAction(AbstractTestResultAction.class)).thenReturn(action);
            Assertions.assertTrue(sut.canBeCalculated(mock), "Can be collected");
        }
    }
}