/*
 * */
package com.synectiks.process.server.filters.events;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class FilterDescriptionUpdateEvent {
    private static final String FIELD_ID = "id";

    @JsonProperty(FIELD_ID)
    public abstract String id();

    @JsonCreator
    public static FilterDescriptionUpdateEvent create(@JsonProperty(FIELD_ID) String id) {
        return new AutoValue_FilterDescriptionUpdateEvent(id);
    }
}