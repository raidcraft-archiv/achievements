package de.raidcraft;

import de.raidcraft.achievements.database.TAchievement;
import de.raidcraft.achievements.database.TAchievementHolder;
import de.raidcraft.achievements.database.TAchievementTemplate;
import io.ebean.annotation.Platform;
import io.ebean.config.ServerConfig;
import io.ebean.dbmigration.DbMigration;
import io.ebeaninternal.dbmigration.DefaultDbMigration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenerateDbMigration {

    /**
     * Generate the next "DB schema DIFF" migration.
     * <p>
     * These migration are typically run using FlywayDB, Liquibase
     * or Ebean's own built in migration runner.
     * </p>
     */
    public static void main(String[] args) throws IOException {

        List<Class<?>> tables = new ArrayList<>();
        tables.add(TAchievement.class);
        tables.add(TAchievementHolder.class);
        tables.add(TAchievementTemplate.class);

        DbMigration dbMigration = new DefaultDbMigration();
        dbMigration.setPlatform(Platform.MYSQL);
        ServerConfig config = new ServerConfig();
        config.setClasses(tables);
        dbMigration.setServerConfig(config);
        // generate the migration ddl and xml
        // ... with EbeanServer in "offline" mode
        dbMigration.generateMigration();
    }
}