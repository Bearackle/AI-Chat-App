package com.workspace.llmsystem.security.annotation;

import java.lang.annotation.*;


@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheException {
}
