package group3.aic_middleware.services.authorization;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuth;
import com.dropbox.core.TokenAccessType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShortLiveTokenAuthorize {
    public DbxAuthFinish authorize(DbxAppInfo appInfo) {
        // Run through Dropbox API authorization process
        DbxRequestConfig requestConfig = new DbxRequestConfig("examples-authorize");
        DbxWebAuth webAuth = new DbxWebAuth(requestConfig, appInfo);

        // TokenAccessType.OFFLINE means refresh_token + access_token. ONLINE means access_token only.
        DbxWebAuth.Request webAuthRequest =  DbxWebAuth.newRequestBuilder()
                                                       .withNoRedirect()
                                                       .withTokenAccessType(TokenAccessType.OFFLINE)
                                                       .build();
        String code = "7eAmhScCFQAAAAAAAAAAHnbLKfGrNMva0tPXF8-EgZQ";

        try {
            return webAuth.finishFromCode(code);
        } catch (DbxException ex) {
            System.err.println("Error in DbxWebAuth.authorize: " + ex.getMessage());
            System.exit(1);
            return null;
        }
    }
}
