package com.udacity.webcrawler.profiler;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * Concrete implementation of the {@link Profiler}.
 */
final class ProfilerImpl implements Profiler {

  private final Clock clock;
  private final ProfilingState state = new ProfilingState();
  private final ZonedDateTime startTime;

  @Inject
  ProfilerImpl(Clock clock) {
    this.clock = Objects.requireNonNull(clock);
    this.startTime = ZonedDateTime.now(clock);
  }

  private Boolean profiledClass (Class<?> klass) {
    List<Method> methodList = new ArrayList<>(Arrays.asList(klass.getDeclaredMethods()));
    if (!methodList.isEmpty()) {
      for (Method method : methodList) {
        if (method.getAnnotation(Profiled.class) != null) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public <T> T wrap(Class<T> klass, T delegate) {
    Objects.requireNonNull(klass);
    if (!profiledClass(klass)) {
      throw new IllegalArgumentException(klass.getName() + " does not contain a @Profiled method.");
    }

    Object proxy = Proxy.newProxyInstance(ProfilerImpl.class.getClassLoader(), new Class[] {klass}, new ProfilingMethodInterceptor(clock, delegate, state));
    return (T) proxy;
  }

  @Override
  public void writeData(Path path) {
    Objects.requireNonNull(path);
    try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
      writeData(writer);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void writeData(Writer writer) throws IOException {
    writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
    writer.write(System.lineSeparator());
    state.write(writer);
    writer.write(System.lineSeparator());
  }
}
