import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Generate a new BCrypt hash for testing
        String testPassword = "mySecurePassword123";
        String newHash = encoder.encode(testPassword);

        System.out.println("BCrypt Password Hash Generator and Tester");
        System.out.println("==========================================\n");

        System.out.println("Generating new hash for password: " + testPassword);
        System.out.println("Generated hash: " + newHash);
        System.out.println("Verification: " + (encoder.matches(testPassword, newHash) ? "✓ VALID" : "✗ INVALID"));

        System.out.println("\nNote: Each time you run this, a different hash will be generated.");
        System.out.println("This is normal BCrypt behavior - it uses random salt for each hash.");
        System.out.println("\nTo create a hash for your password, modify 'testPassword' variable above.");
    }
}
