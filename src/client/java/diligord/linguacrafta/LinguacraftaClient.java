package diligord.linguacrafta;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

public class LinguacraftaClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		System.out.println("Hello from init client!");

		ClientSendMessageEvents.MODIFY_CHAT.register((message) -> {
			return "Hello World!";
		});
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
	}
}