package com.teoresi.staff.shared.services;

import com.google.gson.*;

import java.io.*;
import java.util.Objects;

public class FileManagementService {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final GsonBuilder gsonBuilder = new GsonBuilder();

    public JsonObject retrieveFileInfoFromJsonObject (String filename) throws IOException {
        File config = new File(filename);
        JsonObject settings;
        if(config.exists()){
            Reader reader = new FileReader(config);
            settings = gson.fromJson(reader, JsonObject.class);
        }
        else{
            Reader reader = new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("/"+filename)));
            settings = gson.fromJson(reader, JsonObject.class);
            Writer writer = new FileWriter(filename);
            JsonObject jsonObject = new JsonParser().parse(settings.toString()).getAsJsonObject();
            gsonBuilder.setPrettyPrinting().create();
            gson.toJson(jsonObject, writer);
            writer.flush();
            writer.close();
        }
        return settings;
    }

    public JsonArray retrieveFileInfoFromJsonArray(String filename) throws IOException {
        File configFile = new File(filename);
        JsonArray settings;
        if (configFile.exists()) {
            try (Reader reader = new FileReader(configFile)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element.isJsonArray()) {
                    settings = element.getAsJsonArray();
                } else {
                    throw new IllegalStateException("Expected JSON array in file: " + filename);
                }
            }
        } else {
            settings = new JsonArray();
        }
        return settings;
    }
}
