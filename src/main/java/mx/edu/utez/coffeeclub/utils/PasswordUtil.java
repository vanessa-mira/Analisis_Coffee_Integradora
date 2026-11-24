package mx.edu.utez.coffeeclub.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

/**
 * Cifra las contraseñas con PBKDF2 (viene incluido en Java, no necesita librerías extra).
 * Formato guardado en la BD:  iteraciones:sal:hash
 */
public class PasswordUtil {

    private static final int ITERACIONES = 65536;
    private static final int LONGITUD_BITS = 256;

    public static String hash(String password) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        byte[] h = pbkdf2(password, sal, ITERACIONES, LONGITUD_BITS);
        return ITERACIONES + ":" + HexFormat.of().formatHex(sal) + ":" + HexFormat.of().formatHex(h);
    }

    public static boolean verificar(String password, String guardado) {
        try {
            String[] partes = guardado.split(":");
            int iteraciones = Integer.parseInt(partes[0]);
            byte[] sal = HexFormat.of().parseHex(partes[1]);
            byte[] esperado = HexFormat.of().parseHex(partes[2]);
            byte[] actual = pbkdf2(password, sal, iteraciones, esperado.length * 8);
            return MessageDigest.isEqual(esperado, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private static byte[] pbkdf2(String password, byte[] sal, int iteraciones, int bits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), sal, iteraciones, bits);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo cifrar la contraseña", e);
        }
    }
}
