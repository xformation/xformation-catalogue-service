/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding;

import com.google.common.io.BaseEncoding;

import java.nio.charset.StandardCharsets;

public class Base32Encode extends BaseEncodingSingleArgStringFunction {
    public static final String NAME = "base32_encode";
    private static final String ENCODING_NAME = "base32";

    @Override
    protected String getEncodedValue(String value, boolean omitPadding) {
        BaseEncoding encoding = BaseEncoding.base32Hex();
        encoding = omitPadding ? encoding.omitPadding() : encoding;

        return encoding.encode(value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected String getEncodingName() {
        return ENCODING_NAME;
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
