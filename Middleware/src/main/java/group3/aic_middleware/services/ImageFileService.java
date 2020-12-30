package group3.aic_middleware.services;

import com.dropbox.core.*;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;
import group3.aic_middleware.exceptions.EventNotCreatedException;
import group3.aic_middleware.exceptions.EventNotFoundException;
import group3.aic_middleware.entities.ImageEntity;
import org.apache.commons.codec.binary.Base64;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;


public class ImageFileService {

    DbxRequestConfig config;
    DbxClientV2 client;

    public ImageFileService(){
        this.config = DbxRequestConfig.newBuilder("dropBoxAppFolderName").build();
        this.client = new DbxClientV2(config, new DbxCredential("accessTokenForDropBoxApp"));
    }

    public ImageEntity readImage(String imageName) throws EventNotFoundException {
        if (!imageName.endsWith(".jpg")) {
            imageName = imageName + ".jpg";
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
                this.client.files().downloadBuilder("/" + imageName).download(out);
            return new ImageEntity(DatatypeConverter.printBase64Binary((out.toByteArray())));
    }
         catch (DbxException | IOException ex) {
            throw new EventNotFoundException("Could not read image:\n" + ex.getMessage());
        }
    }

    public void saveImage(String filename, String imageBase64Enc) throws EventNotCreatedException {
        //  Get current account info
        //  FullAccount account = client.users().getCurrentAccount();
        try {
            byte[] bytes = Base64.decodeBase64(imageBase64Enc.getBytes(StandardCharsets.UTF_8));

            InputStream in = new ByteArrayInputStream(bytes);
            this.client.files().uploadBuilder("/" + filename).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
        } catch (DbxException | IOException ex) {
            throw new EventNotCreatedException("Could not save image:\n" + ex.getMessage());
        }
    }

    public void deleteImage (String imageName) throws EventNotFoundException {
        if (!imageName.endsWith(".jpg")) {
            imageName = imageName + ".jpg";
        }
        try {
            this.client.files().deleteV2("/" + imageName);
        } catch (DbxException ex) {
            throw new EventNotFoundException("Could not delete image:\n" + ex.getMessage());
        }
    }
}
