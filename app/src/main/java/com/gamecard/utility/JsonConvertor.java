package com.gamecard.utility;

import com.gamecard.model.GameResponseModel;

import org.json.JSONObject;

/**
 * Created by bridgeit on 22/8/16.
 */

public class JsonConvertor {

    public static GameResponseModel getGameResponseFromJson(String message){
        GameResponseModel gameResponseModel=new GameResponseModel();
        try {
            JSONObject jsonObject = new JSONObject(message);
            gameResponseModel.setId(jsonObject.getInt("id"));
            gameResponseModel.setGametittle(jsonObject.getString("gametittle"));
            gameResponseModel.setCategory(jsonObject.getString("category"));
            gameResponseModel.setVersion(jsonObject.getString("version"));
            gameResponseModel.setSize(jsonObject.getString("Size"));
            gameResponseModel.setGamedate(jsonObject.getString("gamedate"));
            gameResponseModel.setPackagename(jsonObject.getString("packagename"));
            gameResponseModel.setIsgame(jsonObject.getBoolean("isgame"));
            gameResponseModel.setIconLink(jsonObject.getString("iconLink"));
            gameResponseModel.setJsonImageVedioLink(jsonObject.getString("jsonImageVedioLink"));
            gameResponseModel.setSuggestion(jsonObject.getBoolean("suggestion"));
            return gameResponseModel;
        }catch (Exception exception){
            return null;
        }
    }
}
