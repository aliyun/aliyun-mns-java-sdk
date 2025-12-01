package com.aliyun.mns.unitTest.utils;

import com.aliyun.mns.client.Utils;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

/**
 * @author yuanzhi
 * @date 2025/4/24.
 */
public class UtilsTest {

    @Test
    public void testGetHttpURI() throws URISyntaxException {
        // 测试输入为 null 的情况
        NullPointerException nullPointException = assertThrows(NullPointerException.class, () -> Utils.getHttpURI(null));
        assertEquals("The endpoint parameter is null.", nullPointException.getMessage());

        // 测试输入不以 http:// 或 https:// 开头的情况
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Utils.getHttpURI("ftp://example.com"));
        assertEquals("Only HTTP protocol is supported. The endpoint must start with 'http://' or 'https://'.", exception.getMessage());

        // 测试输入包含多余尾部斜杠的情况
        assertEquals(new URI("http://example.com"), Utils.getHttpURI("http://example.com/"));

        // 测试输入为合法 URI 的情况
        assertEquals(new URI("http://example.com/path"), Utils.getHttpURI("http://example.com/path"));
        assertEquals(new URI("http://example.com:8080/path"), Utils.getHttpURI("http://example.com:8080/path"));
    }

    @Test
    public void testGetHttpURI_InvalidURISyntax() {
        // 测试输入为非法 URI 格式的情况
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Utils.getHttpURI("http://example.com/invalid space"));
        assertEquals("java.net.URISyntaxException: Illegal character in path at index 26: http://example.com/invalid space", exception.getMessage());

        // 测试包含非法字符的情况
        exception = assertThrows(IllegalArgumentException.class, () -> Utils.getHttpURI("http://example.com/<>"));
        assertEquals("java.net.URISyntaxException: Illegal character in path at index 19: http://example.com/<>", exception.getMessage());
    }

    @Test
    public void testGetHttpURI_MissingHost() {
        // 测试输入缺少主机名的情况
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Utils.getHttpURI("http:///example.com/path"));
        assertEquals("Invalid URI: Host is missing.", exception.getMessage());

        // 测试输入非法端口号的情况
        exception = assertThrows(IllegalArgumentException.class, () -> Utils.getHttpURI("http://example.com:invalidPort"));
        assertEquals("Invalid URI: Host is missing.", exception.getMessage());
    }

}
