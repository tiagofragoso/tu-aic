package group3.aic_middleware.services;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingService {

    MessageDigest hasher = MessageDigest.getInstance("SHA-256");

    public HashingService() throws NoSuchAlgorithmException {
    }

    /**
     * Function computes the hash value of the image
     * @param base64EncodedImage Image to be hashed
     *
     * @return hashed value of the image
     */
    protected int getHash(String base64EncodedImage) {
        return ByteBuffer.wrap(this.hasher.digest(base64EncodedImage.getBytes(StandardCharsets.UTF_8))).getInt();
    }

    /**
     * Function compares two hash values
     * @param newImageHashed Hash value of new/updated/etc image
     * @param oldImageHashed Hash value of stored image
     *
     * @return true when newImageHashed == oldImageHashed, false otherwise
     */
    protected boolean compareHash(int newImageHashed, int oldImageHashed) {
        return newImageHashed == oldImageHashed;
    }

}
