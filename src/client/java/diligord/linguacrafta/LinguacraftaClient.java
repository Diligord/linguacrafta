package diligord.linguacrafta;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class LinguacraftaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		System.out.println("Hello from init client!");
        ClientSendMessageEvents.MODIFY_CHAT.register((message) -> {
			File currentlang = new File("D:/linguacrafta/run/linguacrafta/Valfhod.json");

			if (currentlang != null) {
				System.out.println(currentlang.getName());
			}
			return translate(message);
		});
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}

	private String translate(String message) {
		return "ege tiuf ";
	}
}