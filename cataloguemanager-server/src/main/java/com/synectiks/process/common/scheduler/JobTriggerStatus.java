/*
 * */
package com.synectiks.process.common.scheduler;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Job triggers can be in different lifecycle statuses.
 */
public enum JobTriggerStatus {
    /**
     * The trigger is ready to be locked by a node to execute the job.
     */
    @JsonProperty("runnable")
    RUNNABLE,

    /**
     * The trigger has been locked by a node and the job is executing.
     */
    @JsonProperty("running")
    RUNNING,

    /**
     * The trigger is complete will not be fired anymore. This will mostly be used for one-off jobs that should only run once.
     */
    @JsonProperty("complete")
    COMPLETE,

    /**
     * The trigger has been temporarily paused to avoid further executions. (e.g. maintenance periods)
     */
    @JsonProperty("paused")
    PAUSED,

    /**
     * The trigger cannot be fired because of an error. Triggers with this status will most probably need some human
     * intervention to fix the underlying issue. (e.g. too many unsuccessful retries, missing Job class)
     */
    @JsonProperty("error")
    ERROR
}
