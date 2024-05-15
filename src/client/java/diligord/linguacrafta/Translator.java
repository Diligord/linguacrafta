package diligord.linguacrafta;

// Handles translation

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Translator {

    // Translates from LANG to ENG
    public static String toLang(String message) {
        // Split message into words
        String[] wordArray = message.split(" ");

        // Read JSON file and get the language's word list
        JsonObject langFile = JSONHandler.readObject(System.getProperty("user.dir") + "/languagepacks/Valfhod.json"); // Put in run/languagepacks
        JsonObject languageWords = langFile.getAsJsonObject("words");

        StringBuilder construct = new StringBuilder(); // The translated word structure
        for (String word : wordArray) {
            // Attempt to find a matching word
            JsonElement matchingWordElement = languageWords.get(word);

            if (matchingWordElement == null) { // No matching word, skip it
                construct.append(" ").append(word);
                continue;
            }

            JsonArray matchingWordArray = matchingWordElement.getAsJsonArray(); // Array [word, pos]
            String translatedWord = matchingWordArray.get(0).getAsString(); // The translated word

            construct.append(" ").append(translatedWord);
        }

        return construct.toString().trim(); // Trim will remove the space at the start of the string
    }

}
