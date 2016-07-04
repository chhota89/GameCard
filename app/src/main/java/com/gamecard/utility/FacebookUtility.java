package com.gamecard.utility;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.gamecard.callback.CallBackString;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by bridgeit on 26/6/16.
 */

public class FacebookUtility {

    public static void getUserName(final CallBackString callBack) {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (object != null) {
                            try {
                                callBack.onSuccess(object.getString("name"));
                            } catch (JSONException e) {
                                callBack.onError(e.getMessage());
                            }
                        } else {
                            callBack.onError("Object is null");
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
