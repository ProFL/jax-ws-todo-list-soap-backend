package projeto_1.config;

import java.util.Map;

public class ConfigProvider {
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

    public String getDbUser() {
        return this.env.get("POSTGRES_USER");
    }

    public String getDbPasswd() {
        return this.env.get("POSTGRES_PASSWORD");
    }

    public String getDbConnStr() {
        String db = this.env.get("POSTGRES_DB");
        String host = this.env.get("POSTGRES_HOST");
        String port = this.env.get("POSTGRES_PORT");
        return "jdbc:postgresql://" + host + ":" + port + "/" + db;
    }
}