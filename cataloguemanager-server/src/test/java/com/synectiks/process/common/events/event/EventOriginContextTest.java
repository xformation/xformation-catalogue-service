/*
 * */
package com.synectiks.process.common.events.event;

import org.junit.Test;

import com.synectiks.process.common.events.event.EventOriginContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class EventOriginContextTest {
    @Test
    public void elasticsearchMessage() {
        assertThat(EventOriginContext.elasticsearchMessage("cataloguemanager_0", "b5e53442-12bb-4374-90ed-c325c0d979ce"))
                .isEqualTo("urn:cataloguemanager:message:es:cataloguemanager_0:b5e53442-12bb-4374-90ed-c325c0d979ce");

        assertThatCode(() -> EventOriginContext.elasticsearchMessage("", "b5e53442-12bb-4374-90ed-c325c0d979ce"))
                .hasMessageContaining("indexName")
                .isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> EventOriginContext.elasticsearchMessage(null, "b5e53442-12bb-4374-90ed-c325c0d979ce"))
                .hasMessageContaining("indexName")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatCode(() -> EventOriginContext.elasticsearchMessage("cataloguemanager_0", ""))
                .hasMessageContaining("messageId")
                .isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> EventOriginContext.elasticsearchMessage("cataloguemanager_0", null))
                .hasMessageContaining("messageId")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void elasticsearchEvent() {
        assertThat(EventOriginContext.elasticsearchEvent("xfcatlg-events_0", "01DF13GB094MT6390TYQB2Q73Q"))
                .isEqualTo("urn:cataloguemanager:event:es:xfcatlg-events_0:01DF13GB094MT6390TYQB2Q73Q");

        assertThatCode(() -> EventOriginContext.elasticsearchEvent("", "01DF13GB094MT6390TYQB2Q73Q"))
                .hasMessageContaining("indexName")
                .isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> EventOriginContext.elasticsearchEvent(null, "01DF13GB094MT6390TYQB2Q73Q"))
                .hasMessageContaining("indexName")
                .isInstanceOf(IllegalArgumentException.class);

        assertThatCode(() -> EventOriginContext.elasticsearchEvent("xfcatlg-events_0", ""))
                .hasMessageContaining("eventId")
                .isInstanceOf(IllegalArgumentException.class);
        assertThatCode(() -> EventOriginContext.elasticsearchEvent("xfcatlg-events_0", null))
                .hasMessageContaining("eventId")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parseWrongESContext() {
        assertThat(EventOriginContext.parseESContext("urn:moo")).isEmpty();
    }

    @Test
    public void parseShortESContext() {
        assertThat(EventOriginContext.parseESContext("urn:cataloguemanager:message:es:ind")).isEmpty();
    }

    @Test
    public void parseMessageESContext() {
        assertThat(EventOriginContext.parseESContext("urn:cataloguemanager:message:es:index-42:01DF13GB094MT6390TYQB2Q73Q"))
                .isPresent()
                .get()
                .satisfies(context -> {
                    assertThat(context.indexName()).isEqualTo("index-42");
                    assertThat(context.messageId()).isEqualTo("01DF13GB094MT6390TYQB2Q73Q");
                });
    }

    @Test
    public void parseEventESContext() {
        assertThat(EventOriginContext.parseESContext("urn:cataloguemanager:event:es:index-42:01DF13GB094MT6390TYQB2Q73Q"))
                .isPresent()
                .get()
                .satisfies(context -> {
                    assertThat(context.indexName()).isEqualTo("index-42");
                    assertThat(context.messageId()).isEqualTo("01DF13GB094MT6390TYQB2Q73Q");
                });
    }
}