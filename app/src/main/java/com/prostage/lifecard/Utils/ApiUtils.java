package com.prostage.lifecard.Utils;

import com.prostage.lifecard.remote.APIService;
import com.prostage.lifecard.remote.RetrofitClient;

/**
 * Created by PHP Team on 3/20/2017.
 *
 */

public class ApiUtils {

    private ApiUtils(){}

    public static final String BASE_URL = "https://lifecard-omatome-api.net:11443/customers/";

    public static APIService getAPIService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
