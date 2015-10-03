
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;
import java.security.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Shashank
 */
public class Encryption {

    /**
     * @param args the command line arguments
     */
    public final String cloudKey = "helloworld";

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, SQLException {
        // TODO code application logic here
        //new Encryption().EncryptData("Hello world!, I am testing pretty much large files.");
        //new Encryption().CloudEncryption("sdd");
        //new Utils().AuthenticateUser("", "");
    }

    public static String GeneratePublicKey() {
        String publicKey = "";
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            keygen.init(128);
            SecretKey secKey = keygen.generateKey();
            byte[] byteKey = secKey.getEncoded();
            publicKey = new BASE64Encoder().encode(byteKey);

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return publicKey;
    }

    public Song EncryptData(Song objSong) throws NoSuchAlgorithmException {
        try {
            //KeyGenerator keygen = KeyGenerator.getInstance("AES");
            //keygen.init(128); 
            //SecretKey secKey = keygen.generateKey();
            //byte[] byteKey = secKey.getEncoded();
            System.out.println("Entered ... ");
            String secKey = new DbConnect().GetPublicKey();
            System.out.println("Entered ... 1  " + secKey);
            byte[] byteKey = new BASE64Decoder().decodeBuffer(secKey);
            secKey = new String(byteKey);
            SecretKey skey = new SecretKeySpec(byteKey, 0, byteKey.length, "AES");
            System.out.println("Entered ... 2");
            Cipher cph = Cipher.getInstance("AES");
            cph.init(Cipher.ENCRYPT_MODE, skey);
            System.out.println("Entered ...3 ");
            String index = objSong.getIndexer();
            System.out.println("objsong index: " + index);

            objSong.setTitle(EncryptData(cph, objSong.getTitle()));
            objSong.setGenre(EncryptData(cph, objSong.getGenre()));
            objSong.setArtist(EncryptData(cph, objSong.getArtist()));
            objSong.setCopyright(EncryptData(cph, objSong.getCopyright()));
            objSong.setDuration(EncryptData(cph, objSong.getDuration()));
            objSong.setLyrics(EncryptData(cph, objSong.getLyrics()));
            objSong.setOwner(EncryptData(cph, objSong.getOwner()));
            //Commutative encryption code
            //byte[] byteSecKey = skey.getEncoded();
            //String cphSecKey = new BASE64Encoder().encode(byteSecKey);
            //CommutativeClientEncrypt(cphSecKey, index);
            return objSong;
        } catch (Exception e) {
            System.out.println("Exception occured : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String EncryptData(Cipher cph, String data) {
        String strEncryptedData = null;
        try {

            byte[] encryptedData = cph.doFinal(data.getBytes());
            strEncryptedData = new BASE64Encoder().encode(encryptedData);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return strEncryptedData;
    }

    public void CommutativeClientEncrypt(String ownerID, String index) throws IOException, NoSuchAlgorithmException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException, SQLException {
        String secKey = new DbConnect().GetPublicKey();

        byte[] byteKey = new BASE64Decoder().decodeBuffer(secKey);
        secKey = new String(byteKey);
        SecretKey skey = new SecretKeySpec(byteKey, 0, byteKey.length, "AES");
        byte[] byteSecKey = skey.getEncoded();
        String cipherKey = new BASE64Encoder().encode(byteSecKey);
        byte[] seckey = new BASE64Decoder().decodeBuffer(cipherKey);
        byte[] keystream = new byte[seckey.length];
        byte[] encoded = new byte[seckey.length];
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        sr.nextBytes(keystream);
        for (int i = 0; i < seckey.length; i++) {
            encoded[i] = (byte) (seckey[i] ^ keystream[i]);
        }
        String strEncoded = new BASE64Encoder().encode(encoded);
        String doubleEnc = CloudEncryption(strEncoded);
        byte[] dblbytes = new BASE64Decoder().decodeBuffer(doubleEnc);
        byte[] cldbytes = new byte[keystream.length];
        for (int i = 0; i < keystream.length; i++) {
            cldbytes[i] = (byte) (keystream[i] ^ dblbytes[i]);
        }
        String cldEncKey = new BASE64Encoder().encode(cldbytes);
        new DbConnect().InsertKeyManager(index, cldEncKey);
        //ConsumerEncryption(cldEncKey, cipherData);
    }

    public String CloudEncryption(String encKey) throws IOException, NoSuchAlgorithmException,
            NoSuchProviderException {
        byte[] encbytes = new BASE64Decoder().decodeBuffer(encKey);
        byte[] cldbytes = new byte[encbytes.length];
        byte[] combytes = new byte[encbytes.length];
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        sr.setSeed(cloudKey.getBytes());
        sr.nextBytes(cldbytes);
        for (int i = 0; i < encbytes.length; i++) {
            combytes[i] = (byte) (encbytes[i] ^ cldbytes[i]);
        }
        String strEncoded = new BASE64Encoder().encode(combytes);
        return strEncoded;
    }

    public void ConsumerEncryption(String cldEncKey, String cipherData) throws NoSuchAlgorithmException,
            NoSuchProviderException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
            BadPaddingException {
        byte[] seckey = new BASE64Decoder().decodeBuffer(cldEncKey);
        byte[] keystream = new byte[seckey.length];
        byte[] encoded = new byte[seckey.length];
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        sr.nextBytes(keystream);
        for (int i = 0; i < seckey.length; i++) {
            encoded[i] = (byte) (seckey[i] ^ keystream[i]);
        }
        String strEncoded = new BASE64Encoder().encode(encoded);
        String doubleEnc = CloudEncryption(strEncoded);
        byte[] dblbytes = new BASE64Decoder().decodeBuffer(doubleEnc);
        byte[] cldbytes = new byte[keystream.length];
        for (int i = 0; i < keystream.length; i++) {
            cldbytes[i] = (byte) (keystream[i] ^ dblbytes[i]);
        }
        String originalkey = new String(cldbytes);
        Cipher cph = Cipher.getInstance("AES");
        SecretKey skey = new SecretKeySpec(cldbytes, 0, cldbytes.length, "AES");
        cph.init(Cipher.DECRYPT_MODE, skey, cph.getParameters());
        byte[] b = new BASE64Decoder().decodeBuffer(cipherData);
        byte[] bytedec = cph.doFinal(b);
        String data = new String(bytedec);
        return;
    }

    public byte[] GetDecryptionKey(String songID) throws IOException {
        String cldEncKey = new DbConnect().GetEncryptedKey(songID);
        byte[] seckey = new BASE64Decoder().decodeBuffer(cldEncKey);
        byte[] originalkey = new byte[seckey.length];
        try {

            byte[] keystream = new byte[seckey.length];
            byte[] encoded = new byte[seckey.length];
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            sr.nextBytes(keystream);
            for (int i = 0; i < seckey.length; i++) {
                encoded[i] = (byte) (seckey[i] ^ keystream[i]);
            }
            String strEncoded = new BASE64Encoder().encode(encoded);
            String doubleEnc = CloudEncryption(strEncoded);
            byte[] dblbytes = new BASE64Decoder().decodeBuffer(doubleEnc);
            byte[] cldbytes = new byte[keystream.length];
            for (int i = 0; i < keystream.length; i++) {
                cldbytes[i] = (byte) (keystream[i] ^ dblbytes[i]);
            }
            originalkey = cldbytes;
            //originalkey = new String(cldbytes);
        } catch (IOException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return originalkey;
    }

    public Song GetDecryptedData(Song objSong) throws IOException {
        try {
            byte[] byteKey = GetDecryptionKey(objSong.getIndexer());
            Cipher cph = Cipher.getInstance("AES");
            SecretKey skey = new SecretKeySpec(byteKey, 0, byteKey.length, "AES");
            cph.init(Cipher.DECRYPT_MODE, skey, cph.getParameters());
            objSong.setTitle(DecryptData(cph, objSong.getTitle()));
            objSong.setGenre(DecryptData(cph, objSong.getGenre()));
            objSong.setArtist(DecryptData(cph, objSong.getArtist()));
            objSong.setCopyright(DecryptData(cph, objSong.getCopyright()));
            objSong.setDuration(DecryptData(cph, objSong.getDuration()));
            objSong.setLyrics(DecryptData(cph, objSong.getLyrics()));
            System.out.println("Objsong title: " + objSong.getTitle());
            } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidAlgorithmParameterException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return objSong;
    }

    public String DecryptData(Cipher cph, String data) {
        try {
            byte[] bytesData = new BASE64Decoder().decodeBuffer(data);
            data = new String(cph.doFinal(bytesData));
        } catch (IOException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Encryption.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    public static String getMD5(String input) {
        byte[] source;
        try {
            //Get byte according by specified coding.
            source = input.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            source = input.getBytes();
        }
        String result = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            //The result should be one 128 integer
            byte temp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = temp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            result = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
