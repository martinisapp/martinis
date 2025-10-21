import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2a$10$BGmcUaHCftiGRKB2d8XCWO2u04qbdsVt.xoq1gw1f2ILA6TaRpA2G";
        String plainPassword = "password";

        System.out.println("Testing bcrypt hash...");
        System.out.println("Hash: " + hash);
        System.out.println("Plain password: " + plainPassword);
        System.out.println("Match: " + encoder.matches(plainPassword, hash));

        // Also test variations in case there's a typo
        System.out.println("\nTesting variations:");
        System.out.println("'Password' (capital P): " + encoder.matches("Password", hash));
        System.out.println("'PASSWORD' (all caps): " + encoder.matches("PASSWORD", hash));
        System.out.println("'admin': " + encoder.matches("admin", hash));
    }
}
