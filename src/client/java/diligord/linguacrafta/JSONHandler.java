package diligord.linguacrafta;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class JSONHandler {
    public static JsonObject readObject(String filePath) {
        JsonObject result = null;
        try{
            Gson gson = new Gson();
            FileReader reader = new FileReader(filePath);
            result = gson.fromJson(reader, JsonObject.class);
            System.out.println("Reading object file");
        } catch (JsonSyntaxException e) {
            System.out.println("Got and EDF error");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
