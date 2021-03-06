/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.syslog;

import com.google.common.primitives.Ints;
import com.synectiks.process.common.plugins.pipelineprocessor.EvaluationContext;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;

public class SyslogFacilityConversion extends AbstractFunction<String> {
    public static final String NAME = "syslog_facility";

    private final ParameterDescriptor<Object, Object> valueParam = object("value").description("Value to convert").build();

    @Override
    public String evaluate(FunctionArgs args, EvaluationContext context) {
        final String s = String.valueOf(valueParam.required(args, context));
        final Integer facility = firstNonNull(Ints.tryParse(s), -1);

        return SyslogUtils.facilityToString(facility);
    }

    @Override
    public FunctionDescriptor<String> descriptor() {
        return FunctionDescriptor.<String>builder()
                .name(NAME)
                .returnType(String.class)
                .params(valueParam)
                .description("Converts a syslog facility number to its string representation")
                .build();
    }
}
