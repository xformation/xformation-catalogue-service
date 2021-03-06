/*
 * */
package com.synectiks.process.common.scheduler.rest.requests;

import org.joda.time.DateTime;
import org.junit.Test;

import com.synectiks.process.common.events.JobSchedulerTestClock;
import com.synectiks.process.common.scheduler.JobTriggerData;
import com.synectiks.process.common.scheduler.JobTriggerLock;
import com.synectiks.process.common.scheduler.JobTriggerStatus;
import com.synectiks.process.common.scheduler.rest.requests.CreateJobTriggerRequest;
import com.synectiks.process.common.scheduler.schedule.IntervalJobSchedule;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.joda.time.DateTimeZone.UTC;

public class CreateJobTriggerRequestTest {
    @Test
    public void toDto() {
        final DateTime now = DateTime.now(UTC);
        final JobSchedulerTestClock clock = new JobSchedulerTestClock(now);

        final IntervalJobSchedule schedule = IntervalJobSchedule.builder()
                .interval(1)
                .unit(TimeUnit.SECONDS)
                .build();

        final CreateJobTriggerRequest request = CreateJobTriggerRequest.builder()
                .jobDefinitionId("abc-123")
                .startTime(now)
                .nextTime(now)
                .schedule(schedule)
                .build();

        final JobTriggerData.FallbackData data = new JobTriggerData.FallbackData();
        final CreateJobTriggerRequest requestWithDataAndEndTime = CreateJobTriggerRequest.builder()
                .jobDefinitionId("abc-123")
                .startTime(now)
                .endTime(now.plusDays(1))
                .nextTime(now)
                .schedule(schedule)
                .data(data)
                .build();

        assertThat(request.toDto(clock)).satisfies(dto -> {
            assertThat(dto.jobDefinitionId()).isEqualTo("abc-123");
            assertThat(dto.startTime()).isEqualTo(now);
            assertThat(dto.endTime()).isNotPresent();
            assertThat(dto.nextTime()).isEqualTo(now);
            assertThat(dto.createdAt()).isEqualTo(now);
            assertThat(dto.updatedAt()).isEqualTo(now);
            assertThat(dto.triggeredAt()).isNotPresent();
            assertThat(dto.status()).isEqualTo(JobTriggerStatus.RUNNABLE);
            assertThat(dto.lock()).isEqualTo(JobTriggerLock.empty());
            assertThat(dto.schedule()).isEqualTo(schedule);
            assertThat(dto.data()).isNotPresent();
        });

        assertThat(requestWithDataAndEndTime.toDto(clock)).satisfies(dto -> {
            assertThat(dto.jobDefinitionId()).isEqualTo("abc-123");
            assertThat(dto.startTime()).isEqualTo(now);
            assertThat(dto.endTime()).isPresent().get().isEqualTo(now.plusDays(1));
            assertThat(dto.nextTime()).isEqualTo(now);
            assertThat(dto.createdAt()).isEqualTo(now);
            assertThat(dto.updatedAt()).isEqualTo(now);
            assertThat(dto.triggeredAt()).isNotPresent();
            assertThat(dto.status()).isEqualTo(JobTriggerStatus.RUNNABLE);
            assertThat(dto.lock()).isEqualTo(JobTriggerLock.empty());
            assertThat(dto.schedule()).isEqualTo(schedule);
            assertThat(dto.data()).isPresent().get().isEqualTo(data);
        });
    }
}
