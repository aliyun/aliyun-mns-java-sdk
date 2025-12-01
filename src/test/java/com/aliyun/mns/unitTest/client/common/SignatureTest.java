package com.aliyun.mns.unitTest.client.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for all signature-related tests.
 * This class is kept for backward compatibility.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    ServiceSignatureTest.class,
    MNSV2SignerTest.class,
    MNSV4SignerTest.class
})
public class SignatureTest {
    // This class is intentionally left empty.
    // It serves as a test suite to run all signature-related tests.
}