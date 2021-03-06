/*
 * */
package com.synectiks.process.server.indexer.indices;

import com.google.common.eventbus.EventBus;
import com.synectiks.process.common.testing.elasticsearch.ElasticsearchBaseTest;
import com.synectiks.process.common.testing.elasticsearch.SkipDefaultIndexTemplate;
import com.synectiks.process.server.audit.NullAuditEventSender;
import com.synectiks.process.server.indexer.IndexMappingFactory;
import com.synectiks.process.server.indexer.cluster.Node;
import com.synectiks.process.server.indexer.cluster.NodeAdapter;
import com.synectiks.process.server.indexer.indices.Indices;
import com.synectiks.process.server.indexer.indices.IndicesAdapter;
import com.synectiks.process.server.plugin.system.NodeId;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public abstract class IndicesGetAllMessageFieldsIT extends ElasticsearchBaseTest {
    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private Indices indices;

    protected abstract IndicesAdapter indicesAdapter();

    @Before
    public void setUp() throws Exception {
        final Node node = new Node(mock(NodeAdapter.class));
        //noinspection UnstableApiUsage
        indices = new Indices(
                new IndexMappingFactory(node),
                mock(NodeId.class),
                new NullAuditEventSender(),
                new EventBus(),
                indicesAdapter()
        );
    }

    @Test
    public void GetAllMessageFieldsForNonexistingIndexShouldReturnEmptySet() {
        final String[] indexNames = new String[]{"does_not_exist_" + System.nanoTime()};
        final Set<String> result = indices.getAllMessageFields(indexNames);

        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @SkipDefaultIndexTemplate
    public void GetAllMessageFieldsForEmptyIndexShouldReturnEmptySet() {
        final String indexName = client().createRandomIndex("get_all_message_fields_");

        final Set<String> result = indices.getAllMessageFields(new String[]{indexName});
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @SkipDefaultIndexTemplate
    public void GetAllMessageFieldsForSingleIndexShouldReturnCompleteList() {
        importFixture("org/graylog2/indexer/indices/IndicesGetAllMessageFieldsIT-MultipleIndices.json");

        final String[] indexNames = new String[]{"get_all_message_fields_0"};
        final Set<String> result = indices.getAllMessageFields(indexNames);

        assertThat(result)
                .isNotNull()
                .hasSize(5)
                .containsOnly("fieldonlypresenthere", "message", "n", "source", "timestamp");

        final String[] otherIndexName = new String[]{"get_all_message_fields_1"};

        final Set<String> otherResult = indices.getAllMessageFields(otherIndexName);

        assertThat(otherResult)
                .isNotNull()
                .hasSize(5)
                .containsOnly("message", "n", "source", "timestamp", "someotherfield");
    }

    @Test
    @SkipDefaultIndexTemplate
    public void GetAllMessageFieldsForMultipleIndicesShouldReturnCompleteList() {
        importFixture("org/graylog2/indexer/indices/IndicesGetAllMessageFieldsIT-MultipleIndices.json");

        final String[] indexNames = new String[]{"get_all_message_fields_0", "get_all_message_fields_1"};
        final Set<String> result = indices.getAllMessageFields(indexNames);

        assertThat(result)
                .isNotNull()
                .hasSize(6)
                .containsOnly("message", "n", "source", "timestamp", "fieldonlypresenthere", "someotherfield");
    }

    @Test
    public void GetAllMessageFieldsForIndicesForNonexistingIndexShouldReturnEmptySet() {
        final String indexName = "does_not_exist_" + System.nanoTime();
        assertThat(client().indicesExists(indexName)).isFalse();

        final Map<String, Set<String>> result = indices.getAllMessageFieldsForIndices(new String[]{indexName});

        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @SkipDefaultIndexTemplate
    public void GetAllMessageFieldsForIndicesForEmptyIndexShouldReturnEmptySet() {
        final String indexName = client().createRandomIndex("indices_it_");

        final Map<String, Set<String>> result = indices.getAllMessageFieldsForIndices(new String[]{indexName});

        assertThat(result)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @SkipDefaultIndexTemplate
    public void GetAllMessageFieldsForIndicesForSingleIndexShouldReturnCompleteList() {
        importFixture("org/graylog2/indexer/indices/IndicesGetAllMessageFieldsIT-MultipleIndices.json");

        final String indexName = "get_all_message_fields_0";
        final String[] indexNames = new String[]{indexName};
        final Map<String, Set<String>> result = indices.getAllMessageFieldsForIndices(indexNames);

        assertThat(result)
                .isNotNull()
                .hasSize(1)
                .containsOnlyKeys(indexName);

        assertThat(result.get(indexName))
                .isNotNull()
                .hasSize(5)
                .containsOnly("fieldonlypresenthere", "message", "n", "source", "timestamp");

        final String otherIndexName = "get_all_message_fields_1";
        final String[] otherIndexNames = new String[]{otherIndexName};

        final Map<String, Set<String>> otherResult = indices.getAllMessageFieldsForIndices(otherIndexNames);

        assertThat(otherResult)
                .isNotNull()
                .hasSize(1)
                .containsOnlyKeys(otherIndexName);

        assertThat(otherResult.get(otherIndexName))
                .isNotNull()
                .hasSize(5)
                .containsOnly("someotherfield", "message", "n", "source", "timestamp");
    }

    @Test
    @SkipDefaultIndexTemplate
    public void GetAllMessageFieldsForIndicesForMultipleIndicesShouldReturnCompleteList() {
        importFixture("org/graylog2/indexer/indices/IndicesGetAllMessageFieldsIT-MultipleIndices.json");

        final String[] indexNames = new String[]{"get_all_message_fields_0", "get_all_message_fields_1"};
        final Map<String, Set<String>> result = indices.getAllMessageFieldsForIndices(indexNames);

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsOnlyKeys("get_all_message_fields_0", "get_all_message_fields_1");

        assertThat(result.get("get_all_message_fields_0"))
                .isNotNull()
                .hasSize(5)
                .containsOnly("message", "n", "source", "timestamp", "fieldonlypresenthere");

        assertThat(result.get("get_all_message_fields_1"))
                .isNotNull()
                .hasSize(5)
                .containsOnly("message", "n", "source", "timestamp", "someotherfield");
    }
}
