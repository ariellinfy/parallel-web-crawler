package com.udacity.webcrawler;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A binding annotation for the number of CPU cores specified in the crawler configuration.
 *
 * <p>The value bound to this annotation is the value of the {@code "parallelism"} option from the
 * crawler configuration JSON.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetParallelism {
}
