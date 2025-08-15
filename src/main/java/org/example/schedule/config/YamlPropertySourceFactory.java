package org.example.schedule.config;

import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public final class YamlPropertySourceFactory implements PropertySourceFactory {

  @Override
  @NonNull
  public PropertySource<?> createPropertySource(
      @Nullable String name, @NonNull EncodedResource resource) {

    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(resource.getResource());
    factory.afterPropertiesSet();

    Properties properties = factory.getObject();
    if (properties == null) {
      properties = new Properties();
    }

    String sourceName = determineSourceName(name, resource);
    return new PropertiesPropertySource(sourceName, properties);
  }

  private String determineSourceName(@Nullable String name, @NonNull EncodedResource resource) {
    if (name != null && !name.isEmpty()) {
      return name;
    }

    String filename = resource.getResource().getFilename();
    if (filename != null && !filename.isEmpty()) {
      return filename;
    }

    String description = resource.getResource().getDescription();
    return description.isEmpty() ? "yaml-property-source" : description;
  }
}
