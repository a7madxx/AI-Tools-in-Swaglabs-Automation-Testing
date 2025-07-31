package Utilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class DataUtils {

    private static final String TestDataPath = "src/test/resources/TestData/";
    //TODO : reading data from JSON file

    public static String getJsonData(String FileName, String Field) {
        try {
            // Define object of file reader
            FileReader reader = new FileReader(TestDataPath + FileName + ".json");
            // Result : src/test/resources/TestData/validLogin.json

            // Parse the JSON directory into JSON element
            JsonElement jsonElement = JsonParser.parseReader(reader);
            return jsonElement.getAsJsonObject().get(Field).getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return " ";
    }

    //TODO : reading data from properties file
    public static String getPropertyData(String FileName, String key) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(TestDataPath + FileName + ".properties"));
        return properties.getProperty(key);
    }
}
