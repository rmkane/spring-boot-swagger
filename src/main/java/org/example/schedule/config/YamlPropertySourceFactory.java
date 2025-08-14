package org.example.schedule.config;

import java.util.Objects;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

public final class YamlPropertySourceFactory implements PropertySourceFactory {
  @Override
  public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) {
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(resource.getResource());
    factory.afterPropertiesSet();

    Properties properties = Objects.requireNonNullElse(factory.getObject(), new Properties());

    String filename = resource.getResource().getFilename();
    String description = resource.getResource().getDescription();

    String sourceName =
        name != null && !name.isEmpty()
            ? name
            : filename != null && !filename.isEmpty()
                ? filename
                : !description.isEmpty() ? description : "yaml-property-source";

    return new PropertiesPropertySource(sourceName, properties);
  }
}
