/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing;

import org.apache.commons.codec.digest.DigestUtils;

public class SHA256 extends SingleArgStringFunction {

    public static final String NAME = "sha256";

    @Override
    protected String getDigest(String value) {
        return DigestUtils.sha256Hex(value);
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
