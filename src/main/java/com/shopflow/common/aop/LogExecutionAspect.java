package com.shopflow.common.aop;

import com.shopflow.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Chỉ ghi payload ở hàm được đánh dấu vì request có thể chứa dữ liệu tài khoản hoặc thanh toán.
@Aspect
@Component
@RequiredArgsConstructor
public class LogExecutionAspect {

    private static final String MASK = "***";
    private static final List<String> SENSITIVE_KEYS = List.of(
            "password", "secret", "token", "credential", "authorization",
            "accountnumber", "bankaccount", "otp");

    private final ObjectMapper objectMapper;

    @Value("${app.log.max-payload-length:1000}")
    private int maxPayloadLength;

    @Around("@annotation(com.shopflow.common.aop.LogExecution)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());
        String requestName = currentRequest()
                .map(request -> request.getMethod() + " " + request.getRequestURI())
                .orElse(method.getName());

        logger.info("[YÊU CẦU] {} - người_gọi={} - nội_dung={}",
                requestName,
                SecurityUtil.getCurrentUsername(),
                truncate(argumentsToJson(signature.getParameterNames(), joinPoint.getArgs())));

        try {
            Object result = joinPoint.proceed();
            logger.info("[PHẢN HỒI] {} - {}ms - nội_dung={}",
                    requestName, System.currentTimeMillis() - startTime, truncate(toJson(result)));
            return result;
        } catch (Throwable exception) {
            logger.warn("[LỖI PHẢN HỒI] {} - {}ms - {}",
                    requestName, System.currentTimeMillis() - startTime, exception.getClass().getSimpleName());
            throw exception;
        }
    }

    private Optional<jakarta.servlet.http.HttpServletRequest> currentRequest() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    private String argumentsToJson(String[] names, Object[] arguments) {
        if (arguments == null || arguments.length == 0) {
            return "[]";
        }
        return IntStream.range(0, arguments.length)
                .mapToObj(index -> names != null && index < names.length && isSensitive(names[index])
                        ? jsonString(MASK)
                        : toJson(arguments[index]))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String toJson(Object value) {
        if (value == null) {
            return "null";
        }

        String binarySummary = describeBinary(value);
        if (binarySummary != null) {
            return binarySummary;
        }
        if (value instanceof Collection<?> items
                && items.stream().anyMatch(item -> describeBinary(item) != null)) {
            return items.stream().map(this::toJson).collect(Collectors.joining(", ", "[", "]"));
        }

        try {
            JsonNode node = objectMapper.readTree(objectMapper.writeValueAsString(value));
            maskSensitive(node);
            return objectMapper.writeValueAsString(node);
        } catch (Exception exception) {
            return jsonString(value.getClass().getSimpleName());
        }
    }

    private String describeBinary(Object value) {
        if (value instanceof ResponseEntity<?> response
                && response.getBody() instanceof Resource resource) {
            return jsonString("ResponseEntity[status=" + response.getStatusCode()
                    + ", resource=" + resource.getFilename() + "]");
        }
        if (value instanceof ResponseEntity<?> response
                && response.getBody() instanceof byte[] bytes) {
            return jsonString("ResponseEntity[status=" + response.getStatusCode()
                    + ", bytes=" + bytes.length + "]");
        }
        if (value instanceof Resource resource) {
            return jsonString("Resource[" + resource.getFilename() + "]");
        }
        if (value instanceof MultipartFile file) {
            return jsonString("MultipartFile[" + file.getOriginalFilename()
                    + ", size=" + file.getSize() + "]");
        }
        if (value instanceof InputStream || value instanceof OutputStream) {
            return jsonString("Stream");
        }
        if (value instanceof byte[] bytes) {
            return jsonString("bytes[" + bytes.length + "]");
        }
        return null;
    }

    private void maskSensitive(JsonNode node) {
        if (node instanceof ObjectNode objectNode) {
            for (String name : new ArrayList<>(objectNode.propertyNames())) {
                if (isSensitive(name)) {
                    objectNode.put(name, MASK);
                } else {
                    maskSensitive(objectNode.get(name));
                }
            }
        } else if (node != null && node.isArray()) {
            for (JsonNode child : node) {
                maskSensitive(child);
            }
        }
    }

    private boolean isSensitive(String name) {
        if (name == null) {
            return false;
        }
        String normalizedName = name.toLowerCase(Locale.ROOT).replace("_", "");
        return SENSITIVE_KEYS.stream()
                .map(key -> key.toLowerCase(Locale.ROOT).replace("_", ""))
                .anyMatch(normalizedName::contains);
    }

    private String jsonString(String value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception exception) {
            return "\"[value]\"";
        }
    }

    private String truncate(String value) {
        int limit = Math.max(maxPayloadLength, 0);
        if (value == null || value.length() <= limit) {
            return value;
        }
        return value.substring(0, limit) + "...(truncated; length=" + value.length() + ")";
    }
}
