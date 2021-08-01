package io.github.yangziwen.logreplay.annotation;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import io.github.yangziwen.logreplay.config.AuditConfig;

public class AuditConfigurationSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[] {AuditConfig.class.getName()};
    }

}
