package com.edencoding.models.openVision;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.File;

public class OpenVision {

    public static OpenVisionResponse submitImageToAPI(File image) {
        HttpResponse<JsonNode> response;
        try {
            response = makeRequestToAPI(image);
            Gson gson = new Gson();
            return gson.fromJson(response.getBody().toString(), OpenVisionResponse.class);
        } catch (UnirestException e) {
            return null;
        }
    }

    private static HttpResponse<JsonNode> makeRequestToAPI(File image) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        return Unirest.post("https://api.openvisionapi.com/api/v1/detection")
                .field("model", "yolov4")
                .field("image", image, "image/jpeg")
                .asJson();
    }
}
