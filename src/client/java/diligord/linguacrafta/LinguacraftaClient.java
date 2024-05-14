package diligord.linguacrafta;

import com.google.gson.*;
import com.sun.jna.platform.win32.WinDef;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class LinguacraftaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		System.out.println("Hello from init client!");
        ClientSendMessageEvents.MODIFY_CHAT.register((message) -> {
			File currentlang = new File("D:/linguacrafta/run/linguacrafta/Valfhod.json");

			return translate(message);
		});
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}

	public static JsonArray readArray(String filePath) {
		JsonArray result = null;
		try{
			Gson gson = new Gson();
			FileReader reader = new FileReader(filePath);
			result = gson.fromJson(reader, JsonArray.class);
			System.out.println("Reading array file");
		} catch (JsonSyntaxException e) {
			System.out.println("Got and EDF error");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

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

	private String translate(String message) {
		String[] s = message.split(" ");
		System.out.println(Arrays.toString(s));
		JsonObject langfile = readObject("D:/linguacrafta/run/linguacrafta/Valfhod.json");
		JsonObject lwords = langfile.getAsJsonObject("words");
		System.out.println(lwords.toString());
		String construct = "";
		for (String i : s) {
			try {
				JsonElement lword = lwords.get(i);
				JsonArray larray = lword.getAsJsonArray();
				System.out.println("lword is: " + lword.toString());
				String lworrrd = larray.get(0).toString();
				char bwuh = '"';
				String bwuhs = String.valueOf(bwuh);
				String[] lworrrrd = lworrrd.split(bwuhs);
				String lworrd = lworrrrd[1];
				if (construct.length() == 0) {
					construct += lworrd;
				} else {
					construct += " " + lworrd;
				}
			} catch (Exception e) {
				if (construct.length() == 0) {
					construct += i;
				} else {
					construct += " " + i;
				}
			}
		}
		return construct;
	}
}