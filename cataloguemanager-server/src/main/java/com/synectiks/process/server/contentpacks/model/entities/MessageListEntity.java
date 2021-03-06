/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.search.Filter;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.engine.BackendQuery;
import com.synectiks.process.common.plugins.views.search.searchtypes.MessageList;
import com.synectiks.process.common.plugins.views.search.searchtypes.Sort;
import com.synectiks.process.common.plugins.views.search.timeranges.DerivedTimeRange;
import com.synectiks.process.common.plugins.views.search.timeranges.OffsetRange;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.decorators.Decorator;
import com.synectiks.process.server.decorators.DecoratorImpl;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.KeywordRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.RelativeRange;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.TimeRange;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@AutoValue
@JsonTypeName(MessageListEntity.NAME)
@JsonDeserialize(builder = MessageListEntity.Builder.class)
public abstract class MessageListEntity implements SearchTypeEntity {
    public static final String NAME = "messages";

    @Override
    public abstract String type();

    @Override
    @Nullable
    @JsonProperty
    public abstract String id();

    @JsonProperty
    public abstract Optional<String> name();

    @Nullable
    @Override
    public abstract Filter filter();

    @JsonProperty
    public abstract int limit();

    @JsonProperty
    public abstract int offset();

    @Nullable
    public abstract List<Sort> sort();

    @JsonProperty
    public abstract List<Decorator> decorators();

    @JsonCreator
    public static Builder builder() {
        return new AutoValue_MessageListEntity.Builder()
                .type(NAME)
                .limit(150)
                .offset(0)
                .streams(Collections.emptySet())
                .decorators(Collections.emptyList());
    }

    public abstract Builder toBuilder();

    @Override
    public Builder toGenericBuilder() {
        return toBuilder();
    }

    @AutoValue.Builder
    public abstract static class Builder implements SearchTypeEntity.Builder {
        @JsonCreator
        public static Builder createDefault() {
            return builder()
                    .streams(Collections.emptySet());
        }

        @JsonProperty
        public abstract Builder type(String type);

        @JsonProperty
        public abstract Builder id(@Nullable String id);

        @JsonProperty
        public abstract Builder name(@Nullable String name);

        @JsonProperty
        public abstract Builder filter(@Nullable Filter filter);

        @JsonProperty
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
        @JsonSubTypes({
                @JsonSubTypes.Type(name = AbsoluteRange.ABSOLUTE, value = AbsoluteRange.class),
                @JsonSubTypes.Type(name = RelativeRange.RELATIVE, value = RelativeRange.class),
                @JsonSubTypes.Type(name = KeywordRange.KEYWORD, value = KeywordRange.class),
                @JsonSubTypes.Type(name = OffsetRange.OFFSET, value = OffsetRange.class)
        })
        public Builder timerange(@Nullable TimeRange timerange) {
            return timerange(timerange == null ? null : DerivedTimeRange.of(timerange));
        }
        public abstract Builder timerange(@Nullable DerivedTimeRange timerange);

        @JsonProperty
        public abstract Builder query(@Nullable BackendQuery query);

        @JsonProperty
        public abstract Builder streams(Set<String> streams);

        @JsonProperty
        public abstract Builder limit(int limit);

        @JsonProperty
        public abstract Builder offset(int offset);

        @JsonProperty
        public abstract Builder sort(@Nullable List<Sort> sort);

        @JsonProperty("decorators")
        public Builder _decorators(List<DecoratorImpl> decorators) {
            return decorators(new ArrayList<>(decorators));
        }

        public abstract Builder decorators(List<Decorator> decorators);

        public abstract MessageListEntity build();
    }

    @Override
    public SearchType toNativeEntity(Map<String, ValueReference> parameters, Map<EntityDescriptor, Object> nativeEntities) {
       return MessageList.builder()
       .limit(limit())
       .streams(mappedStreams(nativeEntities))
       .id(id())
       .offset(offset())
       .decorators(decorators())
       .timerange(timerange().orElse(null))
       .filter(filter())
       .name(name().orElse(null))
       .type(type())
       .query(query().orElse(null))
       .sort(sort())
       .build();
    }
}
