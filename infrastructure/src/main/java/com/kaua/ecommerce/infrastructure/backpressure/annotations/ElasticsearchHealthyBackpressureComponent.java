package com.kaua.ecommerce.infrastructure.backpressure.annotations;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Qualifier("elasticsearchHealthyBackpressure")
public @interface ElasticsearchHealthyBackpressureComponent {
}
