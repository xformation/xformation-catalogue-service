/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.synectiks.process.common.plugins.views.search.Parameter;
import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.Search;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.views.PluginMetadataSummary;
import com.synectiks.process.server.contentpacks.NativeEntityConverter;
import com.synectiks.process.server.contentpacks.exceptions.ContentPackException;
import com.synectiks.process.server.contentpacks.model.ModelTypes;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;
import com.synectiks.process.server.plugin.streams.Stream;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableSet.of;

@AutoValue
@JsonAutoDetect
@JsonDeserialize(builder = SearchEntity.Builder.class)
public abstract class SearchEntity implements NativeEntityConverter<Search> {
    public static final String FIELD_REQUIRES = "requires";
    private static final String FIELD_CREATED_AT = "created_at";
    public static final String FIELD_OWNER = "owner";

    @JsonProperty
    public abstract ImmutableSet<QueryEntity> queries();

    @JsonProperty
    public abstract ImmutableSet<Parameter> parameters();

    @JsonProperty(FIELD_REQUIRES)
    public abstract Map<String, PluginMetadataSummary> requires();

    @JsonProperty(FIELD_OWNER)
    public abstract Optional<String> owner();

    @JsonProperty(FIELD_CREATED_AT)
    public abstract DateTime createdAt();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return Builder.create().parameters(of()).queries(ImmutableSet.<QueryEntity>builder().build());
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder {
        @JsonProperty
        public abstract Builder queries(ImmutableSet<QueryEntity> queries);

        @JsonProperty
        public abstract Builder parameters(ImmutableSet<Parameter> parameters);

        @JsonProperty(FIELD_REQUIRES)
        public abstract Builder requires(Map<String, PluginMetadataSummary> requirements);

        @JsonProperty(FIELD_OWNER)
        public abstract Builder owner(@Nullable String owner);

        @JsonProperty(FIELD_CREATED_AT)
        public abstract Builder createdAt(DateTime createdAt);

        public abstract SearchEntity build();

        @JsonCreator
        public static Builder create() {
            return new AutoValue_SearchEntity.Builder()
                    .requires(Collections.emptyMap())
                    .createdAt(DateTime.now(DateTimeZone.UTC))
                    .parameters(of());
        }

    }

    public Set<String> usedStreamIds() {
        final Set<String> queryStreamIds = queries().stream()
                .map(QueryEntity::usedStreamIds)
                .reduce(Collections.emptySet(), Sets::union);
        final Set<String> searchTypeStreamIds = queries().stream()
                .flatMap(q -> q.searchTypes().stream())
                .map(SearchTypeEntity::effectiveStreams)
                .reduce(Collections.emptySet(), Sets::union);

        return Sets.union(queryStreamIds, searchTypeStreamIds);
    }

    @Override
    public Search toNativeEntity(Map<String, ValueReference> parameters,
                                 Map<EntityDescriptor, Object> nativeEntities) {
        final Search.Builder searchBuilder = Search.builder()
                .queries(ImmutableSet.copyOf(
                        queries().stream()
                                .map(q -> q.toNativeEntity(parameters, nativeEntities))
                                .collect(Collectors.toSet())))
                .parameters(this.parameters())
                .requires(this.requires())
                .createdAt(this.createdAt());
        if (this.owner().isPresent()) {
            searchBuilder.owner(this.owner().get());
        }
        return searchBuilder.build();
    }
}
