/*
 * */
package com.synectiks.process.server.contentpacks.model.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.common.plugins.views.search.views.DisplayModeSettings;
import com.synectiks.process.common.plugins.views.search.views.FormattingSettings;
import com.synectiks.process.common.plugins.views.search.views.Titles;
import com.synectiks.process.common.plugins.views.search.views.ViewStateDTO;
import com.synectiks.process.common.plugins.views.search.views.WidgetDTO;
import com.synectiks.process.common.plugins.views.search.views.WidgetPositionDTO;
import com.synectiks.process.server.contentpacks.NativeEntityConverter;
import com.synectiks.process.server.contentpacks.model.entities.references.ValueReference;

import org.graylog.autovalue.WithBeanGetter;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AutoValue
@JsonDeserialize(builder = ViewStateEntity.Builder.class)
@WithBeanGetter
public abstract class ViewStateEntity implements NativeEntityConverter<ViewStateDTO> {
    static final String FIELD_SELECTED_FIELDS = "selected_fields";
    static final String FIELD_STATIC_MESSAGE_LIST_ID = "static_message_list_id";
    static final String FIELD_TITLES = "titles";
    static final String FIELD_WIDGETS = "widgets";
    static final String FIELD_WIDGET_MAPPING = "widget_mapping";
    static final String FIELD_WIDGET_POSITIONS = "positions";
    static final String FIELD_FORMATTING = "formatting";
    static final String FIELD_DISPLAY_MODE_SETTINGS = "display_mode_settings";

    @Nullable
    @JsonProperty(FIELD_SELECTED_FIELDS)
    public abstract Optional<Set<String>> fields();

    @Nullable
    @JsonProperty(FIELD_STATIC_MESSAGE_LIST_ID)
    public abstract Optional<String> staticMessageListId();

    @JsonProperty(FIELD_TITLES)
    public abstract Titles titles();

    @JsonProperty(FIELD_WIDGETS)
    public abstract Set<WidgetEntity> widgets();

    @JsonProperty(FIELD_WIDGET_MAPPING)
    public abstract Map<String, Set<String>> widgetMapping();

    @JsonProperty(FIELD_WIDGET_POSITIONS)
    public abstract Map<String, WidgetPositionDTO> widgetPositions();

    @JsonProperty(FIELD_FORMATTING)
    @Nullable
    public abstract FormattingSettings formatting();

    @JsonProperty(FIELD_DISPLAY_MODE_SETTINGS)
    public abstract DisplayModeSettings displayModeSettings();

    public static Builder builder() {
        return Builder.create();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        @Nullable
        @JsonProperty(FIELD_SELECTED_FIELDS)
        public abstract Builder fields(Set<String> fields);

        @Nullable
        @JsonProperty(FIELD_STATIC_MESSAGE_LIST_ID)
        public abstract Builder staticMessageListId(String staticMessageListId);

        @JsonProperty(FIELD_TITLES)
        public abstract Builder titles(Titles titles);

        @JsonProperty(FIELD_WIDGETS)
        public abstract Builder widgets(Set<WidgetEntity> widgets);

        @JsonProperty(FIELD_WIDGET_MAPPING)
        public abstract Builder widgetMapping(Map<String, Set<String>> widgetMapping);

        @JsonProperty(FIELD_WIDGET_POSITIONS)
        public abstract Builder widgetPositions(Map<String, WidgetPositionDTO> widgetPositions);

        @JsonProperty(FIELD_FORMATTING)
        public abstract Builder formatting(FormattingSettings formattingSettings);

        @JsonProperty(FIELD_DISPLAY_MODE_SETTINGS)
        public abstract Builder displayModeSettings(DisplayModeSettings displayModeSettings);

        public abstract ViewStateEntity build();

        @JsonCreator
        public static Builder create() {
            return new AutoValue_ViewStateEntity.Builder()
                    .titles(Titles.empty())
                    .displayModeSettings(DisplayModeSettings.empty());
        }
    }

    @Override
    public ViewStateDTO toNativeEntity(Map<String, ValueReference> parameters, Map<EntityDescriptor, Object> nativeEntities) {
        final ViewStateDTO.Builder viewStateBuilder = ViewStateDTO.builder()
                .displayModeSettings(this.displayModeSettings())
                .widgets(this.widgets().stream().map(w -> w.toNativeEntity(parameters, nativeEntities))
                        .collect(Collectors.toSet()))
                .widgetMapping(this.widgetMapping())
                .widgetPositions(this.widgetPositions())
                .formatting(this.formatting())
                .titles(this.titles());
        if (this.fields() != null && this.fields().isPresent()) {
            viewStateBuilder.fields(this.fields().get());
        }
        if (this.staticMessageListId() != null && this.staticMessageListId().isPresent()) {
            viewStateBuilder.staticMessageListId(this.staticMessageListId().get());
        }
        return viewStateBuilder.build();
    }
}
