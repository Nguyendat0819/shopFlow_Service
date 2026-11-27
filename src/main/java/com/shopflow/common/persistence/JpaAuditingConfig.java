package com.shopflow.common.persistence;

import com.shopflow.common.util.SecurityUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@ConditionalOnProperty(name = "app.database.enabled", havingValue = "true", matchIfMissing = true)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {

    // Dùng username trong JWT làm giá trị auditor cho created_by và updated_by.
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityUtil.getCurrentUsername());
    }
}
