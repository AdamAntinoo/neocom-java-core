package org.dimensinfin.eveonline.neocom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signals this class a a singleton. There is only one instance so when the new constructor is used the methods will only
 * modify the current instance but not create a new one.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Singleton {}
