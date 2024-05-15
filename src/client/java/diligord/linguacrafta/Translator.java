package diligord.linguacrafta;

// Handles translation

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Translator {

    // Translates from LANG to ENG
    public static String toLang(String message) {
        String[] wordArray = message.split(" "); // Split message into words
        String[] construct = wordArray.clone(); // This is what will become the final translation

        // Read JSON file and get the language's word list
        JsonObject langFile = JSONHandler.readObject(System.getProperty("user.dir") + "/languagepacks/Valfhod.json"); // Put in run/languagepacks


        // 1: TRANSLATE THE WORDS
        JsonObject languageDictionary = langFile.getAsJsonObject("words");

        String defaultPart = langFile.get("default_part").getAsString();
        String[] partsOfSpeech = new String[wordArray.length]; // Array of parts of speech
        Arrays.fill(partsOfSpeech, defaultPart); // Fill with default value

        directTranslate(construct, wordArray, languageDictionary, partsOfSpeech);
        System.out.println("POS: " + String.join(" | ", partsOfSpeech));


        // 2: APPLY RULES
        JsonArray rules = langFile.getAsJsonArray("rules");
        applyRules(construct, rules, wordArray, partsOfSpeech);

        System.out.println(String.join(" | ", construct));


        // 3: REMOVE NULL VALUES FROM STEP 2
        String finalConstruct = "";
        for (String word : construct) {
            if (word != null) finalConstruct += word + " ";
        }

        return finalConstruct.trim();
    }

    /**
     * Directly translate text from the dictionary (with context rules)
     * @param construct The array of untranslated words to be built on
     * @param wordArray The array of untranslated words for reference
     * @param languageDictionary Translations
     * @param partsOfSpeech The array of parts of speech to be built on
     */
    private static void directTranslate(String[] construct, String[] wordArray, JsonObject languageDictionary, String[] partsOfSpeech) {
        // Iterate through every word to find a translation
        for (int i = 0; i < wordArray.length; i++) {
            String word = wordArray[i];

            // Attempt to find a matching word
            JsonElement matchingWordElement = languageDictionary.get(word);
            if (matchingWordElement == null) continue;

            JsonArray matchingWordOptions = matchingWordElement.getAsJsonArray(); // All possible translations for the word

            // Iterate through options and find a translation
            for (JsonElement wordOptionElement : matchingWordOptions) {

                JsonObject wordOption = wordOptionElement.getAsJsonObject();

                String lang = wordOption.get("lang").getAsString();
                String part = wordOption.get("part").getAsString();

                JsonElement contextElement = wordOption.get("context");

                if (contextElement == null) { // No context, add this word
                    construct[i] = lang;
                    partsOfSpeech[i] = part;
                    break;
                }

                // String context = contextElement.getAsString();

                // TODO: Check for context
            }
        }
    }

    /**
     * Handles rule application
     * @param construct The array of words to be built on
     * @param rules The JsonArray of rules
     * @param wordArray The array of words for reference
     * @param partsOfSpeech The array of parts of speech (may be nullified)
     */
    private static void applyRules(String[] construct, JsonArray rules, String[] wordArray, String[] partsOfSpeech) {
        for (JsonElement ruleElement : rules) {
            JsonObject ruleObject = ruleElement.getAsJsonObject();

            String ruleEng = ruleObject.get("eng").getAsString();
            String ruleLang = ruleObject.get("lang").getAsString();

            String[] ruleEngParts = ruleEng.split(" ");

            // Variable storage of words to be used later
            HashMap<String, List<String>> variableStorage = new HashMap<>();

            // Iterate through every word of the sentence to check if this rule matches
            int ruleStartIndex = -1;
            int matchingLength = 0, ruleStep = 0;

            for (int i = 0; i < wordArray.length; i++) {

                // Reset values
                if (ruleStartIndex == -1) {
                    ruleStep = 0;
                    matchingLength = 0;
                    variableStorage = new HashMap<>();
                }

                // Check if the word at index i fits the rule at index ruleStep
                String thisRule = ruleEngParts[ruleStep];

                if (thisRule.startsWith("%")) { // Check if this is the correct part of speech
                    String[] insides = thisRule.split("%")[1].split(":");
                    String key = insides[0];  // The variable name
                    String typeWithFlags = insides[1]; // The part of speech

                    boolean optionalFlag = typeWithFlags.indexOf('~') > -1;

                    String type = typeWithFlags
                            .replace("~","");

                    if (!type.equals(partsOfSpeech[i])) { // Invalid rule
                        // If optional flag is used, then try again
                        if (optionalFlag) {
                            ruleStep++;
                            i -= 1;
                        } else {
                            ruleStartIndex = -1;
                            i -= ruleStep; // Reset to last unchecked word start
                        }

                        continue;
                    }


                    // Valid! Add to variable
                    variableStorage.putIfAbsent(key, new ArrayList<>());
                    variableStorage.get(key).add(construct[i]);

                } else if (!thisRule.equals(wordArray[i])) { // Check for non-matching strings
                    ruleStartIndex = -1;
                    i -= ruleStep; // Reset to last unchecked word start
                    continue;
                }

                // Rule proceeds
                ruleStep++; // Next rule step
                matchingLength++; // Add 1 to the total length of this rule match
                if (ruleStartIndex == -1) ruleStartIndex = i; // Mark this as the start of the rule

                if (ruleStep >= ruleEngParts.length) { // Rule is satisfied
                    // Nullify all words in the matching rule set & parts of speech
                    // This way the array length can stay the same while increasing amount of words
                    // This also prevents re-iteration
                    for (int j = 0; j < matchingLength; j++) {
                        construct[ruleStartIndex + j] = null;
                        partsOfSpeech[ruleStartIndex + j] = null;
                    }

                    construct[ruleStartIndex] = replaceWithVariables(ruleLang, variableStorage);

                    // Reset for another run of this rule (if there remain words)
                    ruleStartIndex = -1;
                }
            }
        }
    }

    /**
     * Replaces a string with %variables% with their associated variables
     * @param string The base string
     * @param storage The list of variables and their values
     * @return The replaced string
     */
    private static String replaceWithVariables(String string, HashMap<String, List<String>> storage) {
        // TODO: Handle < flag

        // Iterate through rule parts to find variables
        String[] ruleLangParts = string.split("%");
        String replacedString = "";

        for (int j = 0; j < ruleLangParts.length; j++) {
            List<String> matchingVariable = storage.get(ruleLangParts[j]);

            if (j % 2 == 0) { // Not a variable (string)
                replacedString += ruleLangParts[j];
                continue;
            }

            if (matchingVariable == null) {
                if (replacedString.endsWith(" ")) replacedString = removeLastChar(replacedString);
                continue;
            }

            replacedString += StringUtils.join(matchingVariable, " "); // Add all words in this variable
        }

        return replacedString; // Put new string in first word array location
    }

    private static String removeLastChar(String s) {
        return s.substring(0, s.length() - 1);
    }
}
