package com.edencoding.models.dogs;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class DogImages {

    private static String getRandomImageAsStringFromAPI() {
        try {
            HttpResponse<JsonNode> apiResponse = Unirest.get("https://dog.ceo/api/breeds/image/random").asJson();
            DogResponse dogResponse = new Gson().fromJson(apiResponse.getBody().toString(), DogResponse.class);
            return dogResponse.getMessage();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage getImage() {
        try {
            return ImageIO.read(new URL(getRandomImageAsStringFromAPI()));
        } catch (IOException e) {
            return null;
        }
    }

}
