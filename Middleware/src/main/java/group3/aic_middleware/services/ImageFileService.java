package group3.aic_middleware.services;

import com.dropbox.core.*;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import group3.aic_middleware.exceptions.DropboxLoginException;
import group3.aic_middleware.exceptions.ImageNotCreatedException;
import group3.aic_middleware.exceptions.ImageNotFoundException;
import group3.aic_middleware.entities.ImageEntity;
import com.dropbox.core.json.JsonReader;
import group3.aic_middleware.restData.ImageObjectDTO;
import group3.aic_middleware.services.authorization.ShortLiveTokenAuthorize;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ImageFileService {

    String password = "aicW$2020_Group3";
    String assetsPath = "/tmp/src/main/resources/config/";
    String tokenFile = assetsPath + "token-information.json";
    DbxRequestConfig config;
    DbxClientV2 client;

    public ImageFileService() throws    DropboxLoginException, IOException, JSONException {
//        if (!Files.exists(Paths.get(tokenFile))) {
//            this.login();
//        }
//        this.config = DbxRequestConfig.newBuilder("aic_2020").build();
//        this.client = new DbxClientV2(config, parseJSONFile(tokenFile).get("access_token").toString());
        /*
        ImageEntity testpic = new ImageEntity("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");
        MetaDataEntity testdata = new MetaDataEntity();
        testdata.setFilename("test1.jpg");
        ImageObjectDTO dto = new ImageObjectDTO(1, testpic, testdata);
        saveImage(dto);
        System.out.println(readImage("test1.jpg").getBase64EncodedImage());
        deleteImage("test1.jpg");
        */
    }

    private void login() throws DropboxLoginException, IOException {
        // Read app info file (contains app key and app secret)
        DbxAppInfo appInfo = new DbxAppInfo("5neyyqy1vy93ydk","xf3vcha9m6ckq3z");

        // Run through Dropbox API authorization process
        DbxAuthFinish authFinish = new ShortLiveTokenAuthorize().authorize(appInfo);

        // Save auth information the new DbxCredential instance.
        // It also contains app_key and app_secret which is required to do refresh call.
        DbxCredential credential = new DbxCredential(authFinish.getAccessToken(),
            authFinish.getExpiresAt(), authFinish.getRefreshToken(), appInfo.getKey(), appInfo.getSecret());
        try {
            File output = new File(tokenFile);
            DbxCredential.Writer.writeToFile(credential, output);
            System.out.println("Saved authorization information to: " + output.getCanonicalPath());
        } catch (IOException ex) {
            throw new DropboxLoginException("Could not save credentials:\n"+ex.getMessage());
        }
    }

    public ImageEntity readImage(String imageName) throws ImageNotFoundException {
//        try {
//            Path destinationFile = Paths.get(assetsPath, imageName);
//            try (OutputStream downloadFile = new FileOutputStream(destinationFile.toFile())) {
//                // FileMetadata metadata =
//                    client.files().downloadBuilder("/"+imageName)
//                                              .download(downloadFile);
//            }
//            ImageEntity imageEntity = new ImageEntity(DatatypeConverter.printBase64Binary((Files.readAllBytes(destinationFile))));
//            Files.delete(destinationFile);
//            return imageEntity;
//        } catch (DbxException | IOException ex) {
//            throw new ImageNotFoundException("Could not read image:\n"+ex.getMessage());
//        }
        return new ImageEntity();
    }

    public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)));
        return new JSONObject(content);
    }

    public void saveImage(ImageObjectDTO imageObjectDTO) throws ImageNotCreatedException {
        //  Get current account info
        //  FullAccount account = client.users().getCurrentAccount();

        /*
        ListFolderResult result = client.files().listFolder("/");
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println("test: " + metadata.getPathLower());
            }
            if (!result.getHasMore()) {
                break;
            }
            result = client.files().listFolderContinue(result.getCursor());
        }
        */
//        String filename = imageObjectDTO.getMetaData().getSeqId() + ".jpg";
//        byte[] bytes = Base64.decodeBase64(imageObjectDTO.getImage().getBase64EncodedImage().getBytes(StandardCharsets.UTF_8));
//        Path destinationFile = Paths.get(assetsPath, filename);
//        try {
//            Files.write(destinationFile, bytes);
//
//            try (InputStream in = new FileInputStream(destinationFile.toString())) {
//                FileMetadata metadata = client.files().uploadBuilder("/" + filename).withMode(WriteMode.OVERWRITE).uploadAndFinish(in);
//            }
//
//            Files.delete(destinationFile);
//        } catch (IOException | DbxException ex) {
//            throw new ImageNotCreatedException("Could not save image:\n"+ex.getMessage());
//        }
    }

    public void deleteImage(String imageName) throws ImageNotFoundException {
//        try {
//            client.files().deleteV2("/" + imageName);
//        } catch (DbxException ex) {
//            throw new ImageNotFoundException("Could not delete image:\n"+ex.getMessage());
//        }
    }
}
