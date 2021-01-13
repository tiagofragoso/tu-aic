package group3.aic_middleware.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashingService {

    MessageDigest hasher = MessageDigest.getInstance("SHA-256");

    public HashingService() throws NoSuchAlgorithmException {
    }

    /**
     * Function computes the hash value of the image
     *
     * @param base64EncodedImage Image to be hashed
     *
     * @return hashed value of the image
     */
    protected String getHash(String base64EncodedImage) {
        return Base64.getEncoder().encodeToString(this.hasher.digest(base64EncodedImage.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Function compares two hash values
     *
     * @param newImageHashed Hash value of new/updated/etc image
     * @param oldImageHashed Hash value of stored image
     *
     * @return true when newImageHashed == oldImageHashed, false otherwise
     */
    protected boolean compareHash(String newImageHashed, String oldImageHashed) {
        return newImageHashed.equals(oldImageHashed);
    }

}
