package diligord.linguacrafta;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

public class LinguacraftaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		System.out.println("Hello from init client!");

		// This event runs when a chat message is sent
        ClientSendMessageEvents.MODIFY_CHAT.register(Translator::toLang);
	}
}