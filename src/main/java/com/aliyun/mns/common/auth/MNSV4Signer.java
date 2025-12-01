package com.aliyun.mns.common.auth;

import com.aliyun.mns.common.http.RequestMessage;
import com.aliyun.mns.common.utils.CodingUtils;
import com.aliyun.mns.common.utils.HttpHeaders;
import com.aliyun.mns.common.utils.HttpUtil;
import com.aliyun.mns.common.utils.IdptEnvUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TreeMap;

import static com.aliyun.mns.common.MNSConstants.X_HEADER_MNS_PREFIX;

/**
 * 引入了时间、Region、服务(产品)三种因素后，对SK采用 HMAC-SHA256 进行多轮计算产生的派生签名，称为V4签名。
 * <p>V4签名，主要解决 {@link MNSV2Signer} V2签名 无地域限制、无时间限制、无产品限制等全局可用特性从而导致的安全风险问题。
 *
 * @author yuanzhi
 * @date 2025/6/3.
 */
public class MNSV4Signer implements RequestSigner {

    private static final Set<String> DEFAULT_SIGNED_HEADERS = new HashSet<>();

    static {
        DEFAULT_SIGNED_HEADERS.add(HttpHeaders.CONTENT_TYPE.toLowerCase());
        DEFAULT_SIGNED_HEADERS.add(HttpHeaders.CONTENT_MD5.toLowerCase());
    }

    private static final String NEW_LINE = "\n";
    private static final String ISO8601_DATETIME_FORMAT = "yyyyMMdd'T'HHmmss'Z'";
    private static final String ISO8601_DATE_FORMAT = "yyyyMMdd";
    private static final String SEPARATOR_BACKSLASH = "/";
    private static final String AUTHORIZATION_PREFIX = "MNS4-HMAC-SHA256";
    private static final String IDPT_AUTHORIZATION_PREFIX = "APSARA-MNS4-HMAC-SHA256";
    private static final String SIGN_PREFIX = "aliyun_v4";
    private static final String IDPT_SIGN_PREFIX = "apsara_v4";
    private static final String SIGN_SUFFIX = "aliyun_v4_request";
    private static final String IDPT_SIGN_SUFFIX = "apsara_v4_request";
    private static final String SIGN_SOURCE_PRODUCT = "mns";

    private ServiceSignature serviceSignature = new HmacSHA256Signature();
    private final String authorizationPrefix;
    private final String signPrefix;
    private final String signSuffix;

    public MNSV4Signer() {
        this.authorizationPrefix = IdptEnvUtil.isIdptEnv() ? IDPT_AUTHORIZATION_PREFIX : AUTHORIZATION_PREFIX;
        this.signPrefix = IdptEnvUtil.isIdptEnv() ? IDPT_SIGN_PREFIX : SIGN_PREFIX;
        this.signSuffix = IdptEnvUtil.isIdptEnv() ? IDPT_SIGN_SUFFIX : SIGN_SUFFIX;
    }

    private static DateFormat getIso8601DateTimeFormat() {
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATETIME_FORMAT, Locale.US);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df;
    }

    private static DateFormat getIso8601DateFormat() {
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT, Locale.US);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df;
    }

    @Override
    public String getAuthorization(String accessKeyId, String accessKeySecret,
                                   RequestMessage request, String region) {

        CodingUtils.assertStringNotEmpty(accessKeyId, "accessKeyId");
        CodingUtils.assertStringNotEmpty(accessKeySecret, "accessKeySecret");
        CodingUtils.assertParameterNotNull(request, "request");
        CodingUtils.assertParameterNotNull(request.getMethod(), "method");
        CodingUtils.assertParameterNotNull(request.getRequestDateTime(), "requestDateTime");
        CodingUtils.assertStringNotEmpty(region, "region");

        Date requestDateTime = request.getRequestDateTime();
        String product = SIGN_SOURCE_PRODUCT;
        //需要转换为UTC日期时间，格式为"20240805"
        String utcDate = getIso8601DateFormat().format(requestDateTime);
        //生成规范化的请求字符串（包括 HTTP method、URI、QueryString、Header）
        String canonicalString = buildCanonicalRequest(request);
        // 生成待签名字符串
        String contentToSign = buildStringToSign(requestDateTime, region, product, canonicalString);
        // 生成派生密钥
        byte[] signingKey = this.buildSigningKey(utcDate, region, product, signPrefix, accessKeySecret, signSuffix);
        // 签名
        String signature = this.buildSignature(signingKey, contentToSign);
        // 构建认证请求头
        return this.buildAuthorization(accessKeyId, utcDate, region, product, signSuffix, signature);
    }

    private String buildCanonicalRequest(RequestMessage request) {
        String method = request.getMethod().toString();
        String resourcePath = request.getResourcePath();

        // 处理resourcePath中可能包含的查询参数
        Map<String, String> additionalParameters = resourcePath != null ?
            parseQueryParametersFromResourcePath(resourcePath)
            : Collections.emptyMap();

        // 标准化resourcePath，确保以"/"开头且不包含查询参数
        resourcePath = normalizeResourcePath(resourcePath);

        StringBuilder canonicalString = new StringBuilder();

        //http method + "\n"
        canonicalString.append(method).append(NEW_LINE);

        //Canonical URI + "\n"
        canonicalString.append(HttpUtil.urlEncode(resourcePath, true)).append(NEW_LINE);

        //Canonical Query String + "\n"  。 QueryString 的key全部转换为小写（即大小写不敏感），Value是大小写敏感的。
        Map<String, String> parameters = request.getParameters();
        // 合并request中的参数和resourcePath中解析出的参数
        Map<String, String> allParameters = new HashMap<>();
        if (parameters != null) {
            allParameters.putAll(parameters);
        }
        allParameters.putAll(additionalParameters);

        TreeMap<String, String> orderMap = new TreeMap<>();
        if (!allParameters.isEmpty()) {
            for (Map.Entry<String, String> param : allParameters.entrySet()) {
                orderMap.put(HttpUtil.urlEncode(StringUtils.trim(param.getKey().toLowerCase()), false),
                    HttpUtil.urlEncode(StringUtils.trim(param.getValue()), false));
            }
        }
        String separator = "";
        StringBuilder canonicalPart = new StringBuilder();
        for (Map.Entry<String, String> param : orderMap.entrySet()) {
            canonicalPart.append(separator).append(param.getKey());
            if (param.getValue() != null && !param.getValue().isEmpty()) {
                canonicalPart.append("=").append(param.getValue());
            }
            separator = "&";
        }
        canonicalString.append(canonicalPart).append(NEW_LINE);

        //Canonical Headers + "\n" 。 Header的key全部转换为小写（即大小写不敏感），Header的Value是大小写敏感的。
        orderMap = buildSortedHeadersMap(request.getHeaders());
        canonicalPart = new StringBuilder();
        for (Map.Entry<String, String> param : orderMap.entrySet()) {
            canonicalPart.append(param.getKey()).append(":").append(param.getValue().trim()).append(NEW_LINE);
        }
        canonicalString.append(canonicalPart).append(NEW_LINE);

        return canonicalString.toString();
    }

    /**
     * 构建排序的Header头TreeMap，Header的key全部转换为小写（即大小写不敏感），Header的Value是大小写敏感的。
     */
    private TreeMap<String, String> buildSortedHeadersMap(Map<String, String> headers) {
        TreeMap<String, String> orderMap = new TreeMap<>();
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                String key = header.getKey().toLowerCase();
                if (hasSignedHeaders(key)) {
                    orderMap.put(key, header.getValue());
                }
            }
        }
        return orderMap;
    }

    private boolean hasSignedHeaders(String header) {
        return hasDefaultSignedHeaders(header);
    }

    private boolean hasDefaultSignedHeaders(String header) {
        if (DEFAULT_SIGNED_HEADERS.contains(header)) {
            return true;
        }
        return header.startsWith(X_HEADER_MNS_PREFIX);
    }

    private String buildStringToSign(Date requestDateTime, String region, String product, String canonicalString) {
        return authorizationPrefix + NEW_LINE +
            getDateTime(requestDateTime) + NEW_LINE +
            buildScope(region, product, requestDateTime) + NEW_LINE +
            canonicalString;
    }

    private String buildScope(String region, String product, Date requestDateTime) {
        return getDate(requestDateTime) + SEPARATOR_BACKSLASH +
            region + SEPARATOR_BACKSLASH +
            product + SEPARATOR_BACKSLASH +
            signSuffix;
    }

    private String getDateTime(Date requestDateTime) {
        return getIso8601DateTimeFormat().format(requestDateTime);
    }

    private String getDate(Date requestDateTime) {
        return getIso8601DateFormat().format(requestDateTime);
    }

    /**
     * 生成 V4 签名所需的派生密钥
     */
    private byte[] buildSigningKey(String date, String region, String product, String signPrefix, String sk, String signSuffix) {
        byte[] signingSecret = (signPrefix + sk).getBytes(StandardCharsets.UTF_8);
        // 一级派生密钥
        byte[] signingDate = serviceSignature.computeHash(signingSecret, date.getBytes(StandardCharsets.UTF_8));
        // 二级派生密钥
        byte[] signingRegion = serviceSignature.computeHash(signingDate, region.getBytes(StandardCharsets.UTF_8));
        // 三级派生密钥
        byte[] signingService = serviceSignature.computeHash(signingRegion, product.getBytes(StandardCharsets.UTF_8));
        // 四级派生密钥
        return serviceSignature.computeHash(signingService, signSuffix.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 V4 签名
     */
    public String buildSignature(byte[] signingKey, String stringToSign) {
        byte[] result = serviceSignature.computeHash(signingKey, stringToSign.getBytes(StandardCharsets.UTF_8));
        return Hex.encodeHexString(result);
    }

    /**
     * 构建包含 ak 和 sk密钥签名 的V4签名认证信息
     */
    private String buildAuthorization(String ak, String date, String region, String product, String signSuffix, String signature) {
        return String.format("%s Credential=%s/%s/%s/%s/%s,Signature=%s",
            authorizationPrefix,
            ak,
            date,
            region,
            product,
            signSuffix,
            signature
        );
    }

    /**
     * 从resourcePath中解析查询参数并返回参数Map
     */
    private Map<String, String> parseQueryParametersFromResourcePath(String url) {
        Map<String, String> additionalParameters = new HashMap<>();
        if (url != null && url.contains("?")) {
            int questionMarkIndex = url.indexOf("?");
            String queryString = url.substring(questionMarkIndex + 1);
            // resourcePath = resourcePath.substring(0, questionMarkIndex); // 不再需要修改resourcePath

            // 解析查询参数并添加到additionalParameters中
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    String key = pair.substring(0, idx);
                    String value = pair.substring(idx + 1);
                    additionalParameters.put(key, value);
                } else {
                    additionalParameters.put(pair, "");
                }
            }
        }
        return additionalParameters;
    }

    /**
     * 标准化resourcePath，确保以"/"开头且不包含查询参数
     */
    private String normalizeResourcePath(String url) {
        if (url != null) {
            // 移除查询参数部分（如果存在）
            if (url.contains("?")) {
                int questionMarkIndex = url.indexOf("?");
                url = url.substring(0, questionMarkIndex);
            }

            // 确保以"/"开头
            if (!url.startsWith("/")) {
                url = "/" + url;
            }
        } else {
            url = "/";
        }
        return url;
    }
}