/*
 * */
package com.synectiks.process.server.alarmcallbacks;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.database.CollectionName;

import org.mongojack.ObjectId;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Map;

@AutoValue
@JsonAutoDetect
@CollectionName("alarmcallbackconfigurations")
public abstract class AlarmCallbackConfigurationImpl implements AlarmCallbackConfiguration {
    static final String FIELD_ID = "_id";
    static final String FIELD_STREAM_ID = "stream_id";
    static final String FIELD_TYPE = "type";
    static final String FIELD_TITLE = "title";
    static final String FIELD_CONFIGURATION = "configuration";
    static final String FIELD_CREATED_AT = "created_at";
    static final String FIELD_CREATOR_USER_ID = "creator_user_id";

    @JsonProperty(FIELD_ID)
    @ObjectId
    @Override
    public abstract String getId();

    @JsonProperty(FIELD_STREAM_ID)
    @Override
    public abstract String getStreamId();

    @JsonProperty(FIELD_TYPE)
    @Override
    public abstract String getType();

    @JsonProperty(FIELD_TITLE)
    @Override
    @Nullable
    public abstract String getTitle();

    @JsonProperty(FIELD_CONFIGURATION)
    @Override
    public abstract Map<String, Object> getConfiguration();

    @JsonProperty(FIELD_CREATED_AT)
    @Override
    public abstract Date getCreatedAt();

    @JsonProperty(FIELD_CREATOR_USER_ID)
    @Override
    public abstract String getCreatorUserId();

    public abstract Builder toBuilder();

    @JsonCreator
    public static AlarmCallbackConfigurationImpl create(@JsonProperty(FIELD_ID) String id,
                                                        @JsonProperty(FIELD_STREAM_ID) String streamId,
                                                        @JsonProperty(FIELD_TYPE) String type,
                                                        @JsonProperty(FIELD_TITLE) @Nullable String title,
                                                        @JsonProperty(FIELD_CONFIGURATION) Map<String, Object> configuration,
                                                        @JsonProperty(FIELD_CREATED_AT) Date createdAt,
                                                        @JsonProperty(FIELD_CREATOR_USER_ID) String creatorUserId,
                                                        @Nullable @JsonProperty("id") String redundantId) {
        return create(id, streamId, type, title, configuration, createdAt, creatorUserId);
    }

    public static AlarmCallbackConfigurationImpl create(@JsonProperty(FIELD_ID) String id,
                                                        @JsonProperty(FIELD_STREAM_ID) String streamId,
                                                        @JsonProperty(FIELD_TYPE) String type,
                                                        @JsonProperty(FIELD_TITLE) @Nullable String title,
                                                        @JsonProperty(FIELD_CONFIGURATION) Map<String, Object> configuration,
                                                        @JsonProperty(FIELD_CREATED_AT) Date createdAt,
                                                        @JsonProperty(FIELD_CREATOR_USER_ID) String creatorUserId) {
        return new AutoValue_AlarmCallbackConfigurationImpl.Builder()
                .setId(id)
                .setStreamId(streamId)
                .setType(type)
                .setTitle(title)
                .setConfiguration(configuration)
                .setCreatedAt(createdAt)
                .setCreatorUserId(creatorUserId)
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setId(String id);

        public abstract Builder setStreamId(String streamId);

        public abstract Builder setType(String type);

        public abstract Builder setTitle(String title);

        public abstract Builder setConfiguration(Map<String, Object> configuration);

        public abstract Builder setCreatedAt(Date createdAt);

        public abstract Builder setCreatorUserId(String creatorUserId);

        public abstract AlarmCallbackConfigurationImpl build();
    }
}
