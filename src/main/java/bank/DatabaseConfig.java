package bank;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for loading database configuration from properties file
 */
public class DatabaseConfig {
    private String url;
    private String username;
    private String password;
    private String databaseName;
    
    /**
     * Load configuration from properties file
     * @param configPath Path to properties file (e.g., "db.properties")
     */
    public DatabaseConfig(String configPath) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configPath)) {
            props.load(fis);
        }
        
        this.url = props.getProperty("db.url", "jdbc:mysql://localhost:3306");
        this.username = props.getProperty("db.username", "root");
        this.password = props.getProperty("db.password", "");
        this.databaseName = props.getProperty("db.database", "mybankuml");
    }
    
    /**
     * Create Database instance with loaded configuration
     */
    public Database createDatabase() {
        return new Database(url, username, password, databaseName);
    }
    
    // Getters
    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getDatabaseName() { return databaseName; }
    
    /**
     * Create DatabaseConfig with default values
     */
    public static DatabaseConfig createDefault() {
        DatabaseConfig config = new DatabaseConfig();
        config.url = "jdbc:mysql://localhost:3306";
        config.username = "root";
        config.password = "";
        config.databaseName = "mybankuml";
        return config;
    }
    
    // Private constructor for createDefault()
    private DatabaseConfig() {
    }
}

