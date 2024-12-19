package com.beeja.api.accounts.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The {@code SecretsGenerator} class provides utility methods to generate secure random secrets,
 * API keys, and hash values using BCrypt encryption.
 *
 * <p>This class uses {@link SecureRandom} for cryptographically secure random string generation and
 * {@link BCryptPasswordEncoder} for hashing strings.
 *
 * <p>Constants define character sets for various secret types:
 *
 * <ul>
 *   <li>{@code UPPER_ALPHA_NUMERIC}: Uppercase letters and numbers
 *   <li>{@code LOWER_ALPHA}: Lowercase letters
 *   <li>{@code SPECIAL_CHARS}: Special characters
 *   <li>{@code ALL_CHARS}: Combination of all character sets
 * </ul>
 *
 * <p>Default lengths:
 *
 * <ul>
 *   <li>Password length: 12 characters
 *   <li>API key length: 40 characters
 * </ul>
 */
public class SecretsGenerator {
  private static final String UPPER_ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
  public static final String LOWER_ALPHA = "abcdefghijklmnopqrstuvwxyz";
  public static final String SPECIAL_CHARS = "!?%&$#+-_=";
  public static final String ALL_CHARS = LOWER_ALPHA + UPPER_ALPHA_NUMERIC + SPECIAL_CHARS;
  private static final int PASSWORD_LENGTH = 12;
  private static final int API_KEY_LENGTH = 40;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public SecretsGenerator(BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  public static String generateSecret() {
    return generateRandomString(ALL_CHARS, PASSWORD_LENGTH);
  }

  public static String generateAPIKey() {
    return generateRandomString(ALL_CHARS, API_KEY_LENGTH);
  }

  /**
   * Generates a random string of the specified length using characters from the given seed string.
   *
   * @param seed the string containing the set of characters to use for random generation
   * @param length the length of the random string to generate
   * @return a random string of the specified length
   */
  private static String generateRandomString(String seed, int length) {
    var secureRandom = new SecureRandom();

    return IntStream.range(0, length)
        .map(i -> secureRandom.nextInt(seed.length()))
        .mapToObj(index -> String.valueOf(seed.charAt(index)))
        .collect(Collectors.joining());
  }

  /**
   * Hashes the given string using the BCrypt hashing algorithm.
   *
   * @param uuidString the string to hash
   * @return the hashed string
   */
  public static String hashWithBcrypt(String uuidString) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    return encoder.encode(uuidString);
  }
}
