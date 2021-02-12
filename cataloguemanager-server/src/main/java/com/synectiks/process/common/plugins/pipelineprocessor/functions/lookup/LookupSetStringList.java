/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.lookup;

import com.google.inject.Inject;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import com.synectiks.process.server.lookup.LookupTableService;

import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.string;

import java.util.List;

public class LookupSetStringList extends AbstractFunction<Object> {

    public static final String NAME = "lookup_set_string_list";

    private final ParameterDescriptor<String, LookupTableService.Function> lookupTableParam;
    private final ParameterDescriptor<Object, Object> keyParam;
    @SuppressWarnings("rawtypes")
    private final ParameterDescriptor<List, List> valueParam;

    @Inject
    public LookupSetStringList(LookupTableService lookupTableService) {
        lookupTableParam = string("lookup_table", LookupTableService.Function.class)
                .description("The existing lookup table to use to set the given list")
                .transform(tableName -> lookupTableService.newBuilder().lookupTable(tableName).build())
                .build();
        keyParam = object("key")
                .description("The key to set in the lookup table")
                .build();
        valueParam = ParameterDescriptor.type("value", List.class)
                .description("The list value that should be set into the lookup table")
                .build();
    }

    @Override
    public Object evaluate(FunctionArgs args, EvaluationContext context) {
        Object key = keyParam.required(args, context);
        if (key == null) {
            return null;
        }
        LookupTableService.Function table = lookupTableParam.required(args, context);
        if (table == null) {
            return null;
        }
        List<String> value = valueParam.required(args, context);
        if (value == null) {
            return null;
        }
        return table.setStringList(key, value).stringListValue();
    }

    @Override
    public FunctionDescriptor<Object> descriptor() {
        return FunctionDescriptor.builder()
                .name(NAME)
                .description("Set a string list in the named lookup table. Returns the new value on success, null on failure.")
                .params(lookupTableParam, keyParam, valueParam)
                .returnType(List.class)
                .build();
    }
}
