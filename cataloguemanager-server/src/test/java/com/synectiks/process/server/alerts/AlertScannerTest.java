/*
 * */
package com.synectiks.process.server.alerts;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.synectiks.process.server.alerts.AbstractAlertCondition;
import com.synectiks.process.server.alerts.Alert;
import com.synectiks.process.server.alerts.AlertNotificationsSender;
import com.synectiks.process.server.alerts.AlertScanner;
import com.synectiks.process.server.alerts.AlertService;
import com.synectiks.process.server.plugin.Tools;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.streams.Stream;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AlertScannerTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private AlertScanner alertScanner;
    @Mock
    private AlertService alertService;
    @Mock
    private AlertNotificationsSender alertNotificationsSender;

    @Before
    public void setUp() throws Exception {
        this.alertScanner = new AlertScanner(alertService, alertNotificationsSender);
    }

    @Test
    public void testNoCheckWhileInGracePeriod() throws Exception {
        final AlertCondition alertCondition = mock(AlertCondition.class);
        final Stream stream = mock(Stream.class);
        when(alertService.inGracePeriod(eq(alertCondition))).thenReturn(true);

        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isFalse();
        verify(alertCondition, never()).runCheck();
    }

    @Test
    public void testCheckWithNegativeResult() throws Exception {
        final AlertCondition alertCondition = mock(AlertCondition.class);
        final Stream stream = mock(Stream.class);
        when(alertService.inGracePeriod(eq(alertCondition))).thenReturn(false);
        when(alertCondition.runCheck()).thenReturn(new AbstractAlertCondition.NegativeCheckResult());

        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isFalse();
        verify(alertCondition, times(1)).runCheck();
    }

    @Test
    public void testCheckTriggersFirstAlert() throws Exception {
        final Stream stream = mock(Stream.class);
        final AlertCondition alertCondition = mock(AlertCondition.class);
        when(alertService.inGracePeriod(eq(alertCondition))).thenReturn(false);
        final AlertCondition.CheckResult positiveCheckResult = new AbstractAlertCondition.CheckResult(true, alertCondition, "Mocked positive CheckResult", Tools.nowUTC(), Collections.emptyList());
        when(alertCondition.runCheck()).thenReturn(positiveCheckResult);

        final Alert alert = mock(Alert.class);
        when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.empty());
        when(alertService.factory(eq(positiveCheckResult))).thenReturn(alert);

        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();

        when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.of(alert));
        when(alertService.isResolved(alert)).thenReturn(false);
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();

        verify(alertCondition, times(2)).runCheck();
        verify(alertNotificationsSender, times(1)).send(positiveCheckResult, stream, alert, alertCondition);
        verify(alertService, never()).resolveAlert(alert);
    }

    @Test
    public void testCheckTriggersAlertIfPreviousIsResolved() throws Exception {
        final Stream stream = mock(Stream.class);
        final AlertCondition alertCondition = mock(AlertCondition.class);
        when(alertService.inGracePeriod(eq(alertCondition))).thenReturn(false);
        final AlertCondition.CheckResult positiveCheckResult = new AbstractAlertCondition.CheckResult(true, alertCondition, "Mocked positive CheckResult", Tools.nowUTC(), Collections.emptyList());
        when(alertCondition.runCheck()).thenReturn(positiveCheckResult);

        final Alert alert = mock(Alert.class);
        final Alert previousAlert = mock(Alert.class);
        when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.of(previousAlert));
        when(alertService.isResolved(previousAlert)).thenReturn(true);
        when(alertService.factory(eq(positiveCheckResult))).thenReturn(alert);

        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();

        verify(alertCondition, times(1)).runCheck();
        verify(alertNotificationsSender, times(1)).send(positiveCheckResult, stream, alert, alertCondition);
        verify(alertService, never()).resolveAlert(alert);
    }

    @Test
    public void testCheckStatefulAlertNotifications() throws Exception {
        final Stream stream = mock(Stream.class);
        final AlertCondition alertCondition = mock(AlertCondition.class);
        when(alertCondition.shouldRepeatNotifications()).thenReturn(false);
        when(alertService.inGracePeriod(eq(alertCondition))).thenReturn(false);
        final AlertCondition.CheckResult positiveCheckResult = new AbstractAlertCondition.CheckResult(true, alertCondition, "Mocked positive CheckResult", Tools.nowUTC(), Collections.emptyList());
        when(alertCondition.runCheck()).thenReturn(positiveCheckResult);

        final Alert alert = mock(Alert.class);
        when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.empty());
        when(alertService.factory(eq(positiveCheckResult))).thenReturn(alert);

        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();

        when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.of(alert));
        when(alertService.isResolved(alert)).thenReturn(false);
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();

        verify(alertCondition, times(2)).runCheck();
        verify(alertNotificationsSender, times(1)).send(positiveCheckResult, stream, alert, alertCondition);
        verify(alertService, never()).resolveAlert(alert);
    }

    @Test
    public void testCheckRepeatedAlertNotifications() throws Exception {
        final Stream stream = mock(Stream.class);
        final AlertCondition alertCondition = mock(AlertCondition.class);
        when(alertCondition.shouldRepeatNotifications()).thenReturn(true);
        when(alertService.inGracePeriod(eq(alertCondition))).thenReturn(false);
        final AlertCondition.CheckResult positiveCheckResult = new AbstractAlertCondition.CheckResult(true, alertCondition, "Mocked positive CheckResult", Tools.nowUTC(), Collections.emptyList());
        when(alertCondition.runCheck()).thenReturn(positiveCheckResult);

        final Alert alert = mock(Alert.class);
        when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.empty());
        when(alertService.factory(eq(positiveCheckResult))).thenReturn(alert);

        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();

        when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.of(alert));
        when(alertService.isResolved(alert)).thenReturn(false);
        when(alertService.shouldRepeatNotifications(alertCondition, alert)).thenReturn(true);
        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();

        verify(alertCondition, times(2)).runCheck();
        verify(alertNotificationsSender, times(2)).send(positiveCheckResult, stream, alert, alertCondition);
        verify(alertService, never()).resolveAlert(alert);
    }

    @Test
    public void testAlertIsResolved() throws Exception {
        final AlertCondition alertCondition = mock(AlertCondition.class);
        final Stream stream = mock(Stream.class);
        when(alertService.inGracePeriod(eq(alertCondition))).thenReturn(false);
        final AlertCondition.CheckResult positiveCheckResult = new AbstractAlertCondition.CheckResult(true, alertCondition, "Mocked positive CheckResult", Tools.nowUTC(), Collections.emptyList());
        when(alertCondition.runCheck()).thenReturn(positiveCheckResult);

        final Alert alert = mock(Alert.class);
        when(alertService.factory(eq(positiveCheckResult))).thenReturn(alert);

        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isTrue();
        verify(alertCondition, times(1)).runCheck();
        verify(alertService, never()).resolveAlert(alert);

        when(alertCondition.runCheck()).thenReturn(new AbstractAlertCondition.NegativeCheckResult());
        when(alertService.getLastTriggeredAlert(stream.getId(), alertCondition.getId())).thenReturn(Optional.of(alert));

        assertThat(this.alertScanner.checkAlertCondition(stream, alertCondition)).isFalse();
        verify(alertCondition, times(2)).runCheck();
        verify(alertService, times(1)).resolveAlert(alert);
    }
}
