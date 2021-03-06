/*
 * */
package com.synectiks.process.server.system.urlwhitelist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.collect.ImmutableList;
import com.synectiks.process.server.system.urlwhitelist.LiteralWhitelistEntry;
import com.synectiks.process.server.system.urlwhitelist.RegexWhitelistEntry;
import com.synectiks.process.server.system.urlwhitelist.UrlWhitelist;
import com.synectiks.process.server.system.urlwhitelist.WhitelistEntry;

import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlWhitelistTest {

    @Test
    public void isWhitelisted() {
        assertThat(UrlWhitelist.createEnabled(ImmutableList.of(LiteralWhitelistEntry.create("a", "title", "foo")))
                .isWhitelisted(".foo")).isFalse();
        assertThat(UrlWhitelist.createEnabled(ImmutableList.of(RegexWhitelistEntry.create("b", "title", "foo")))
                .isWhitelisted(".foo")).isTrue();
        assertThat(UrlWhitelist.createEnabled(ImmutableList.of(RegexWhitelistEntry.create("c", "title", "^foo$")))
                .isWhitelisted(".foo")).isFalse();
        assertThat(UrlWhitelist.createEnabled(ImmutableList.of(LiteralWhitelistEntry.create("d", "title", ".foo"
                )))
                .isWhitelisted(".foo")).isTrue();
    }

    @Test
    public void isDisabled() {
        assertThat(UrlWhitelist.create(Collections.emptyList(), false).isWhitelisted("test")).isFalse();
        assertThat(UrlWhitelist.create(Collections.emptyList(), true).isWhitelisted("test")).isTrue();
    }

    @Test
    public void serializationRoundtrip() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());

        List<WhitelistEntry> entries =
                ImmutableList.of(LiteralWhitelistEntry.create("a", "title", "https://www.graylog.com"),
                        RegexWhitelistEntry.create("b", "regex test title", "https://www\\.graylog\\.com/.*"));
        UrlWhitelist orig = UrlWhitelist.createEnabled(entries);
        String json = objectMapper.writeValueAsString(orig);
        UrlWhitelist read = objectMapper.readValue(json, UrlWhitelist.class);
        assertThat(read).isEqualTo(orig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicateIds() {
        UrlWhitelist.createEnabled(ImmutableList.of(LiteralWhitelistEntry.create("a", "a", "a"),
                RegexWhitelistEntry.create("a", "b", "b")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidRegex() {
        UrlWhitelist.createEnabled(ImmutableList.of(RegexWhitelistEntry.create("a", "b", "${")));
    }
}
