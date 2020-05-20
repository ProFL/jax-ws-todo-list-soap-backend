package projeto_1.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

public class ConfigProvider extends AbstractModule {
    private static ConfigProvider _instance;
    private final Map<String, String> env;

    private ConfigProvider() {
        this.env = System.getenv();
    }

    public static ConfigProvider getInstance() {
        if (ConfigProvider._instance == null) {
            ConfigProvider._instance = new ConfigProvider();
        }
        return ConfigProvider._instance;
    }

    public String getPort() {
        return this.env.get("PORT");
    }

    @Provides
    @SecretKey
    public String getSecretKey() {
        return this.env.get("SECRET_KEY");
    }

    @Provides
    @PostgresDb
    public String getDbDatabase() {
        return this.env.get("POSTGRES_DB");
    }

    @Provides
    @PostgresUser
    public String getDbUser() {
        return this.env.get("POSTGRES_USER");
    }

    @Provides
    @PostgresHost
    public String getDbHost() {
        return this.env.get("POSTGRES_HOST");
    }

    @Provides
    @PostgresPort
    public String getDbPort() {
        return this.env.get("POSTGRES_PORT");
    }

    @Provides
    @PostgresPassword
    public String getDbPasswd() {
        return this.env.get("POSTGRES_PASSWORD");
    }

    @Provides
    @ConnectionString
    public String getDbConnStr(@PostgresDb String db, @PostgresHost String host, @PostgresPort String port) {
        return "jdbc:postgresql://" + host + ":" + port + "/" + db;
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface SecretKey {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface PostgresUser {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface PostgresPassword {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface PostgresHost {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface PostgresPort {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface PostgresDb {
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface ConnectionString {
    }
}