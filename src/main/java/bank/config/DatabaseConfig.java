package bank.config;

import bank.Database;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {
    
    @Value("${spring.datasource.url}")
    private String url;
    
    @Value("${spring.datasource.username}")
    private String username;
    
    @Value("${spring.datasource.password}")
    private String password;
    
    @Bean
    public Database database() {
        // Extract database name from URL
        String dbName = "mybankuml";
        String baseUrl = url.replace("/" + dbName, "").replace("?useSSL=false&serverTimezone=UTC", "");
        
        Database db = new Database(baseUrl, username, password, dbName);
        
        // Try to connect, but don't fail if it doesn't work immediately
        // Connection will be retried on each request
        if (!db.connect()) {
            System.err.println("WARNING: Initial database connection failed. Will retry on first request.");
            System.err.println("Please ensure:");
            System.err.println("  1. MySQL server is running");
            System.err.println("  2. Database 'mybankuml' exists (run: mysql -u root -p < database/schema.sql)");
            System.err.println("  3. Username and password are correct in application.properties");
        }
        
        return db;
    }
}



