package db.migration;

import java.sql.Statement;
import java.util.Map;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public abstract class FlywayMigration extends BaseJavaMigration {
  protected abstract Map<String, String> getMigrations();

  @Override
  public void migrate(Context context) throws Exception {
    try (Statement statement = context.getConnection().createStatement()) {
      for (Map.Entry<String, String> migration : getMigrations().entrySet()) {
        try {
          System.out.printf("Executing migration: %s\n", migration.getKey());
          statement.execute(migration.getValue());
        } catch (Exception e) {
          System.err.printf("Failed to execute %s: %s\n", migration.getKey(), e.getMessage());
          throw e;
        }
      }
    }
  }
}
