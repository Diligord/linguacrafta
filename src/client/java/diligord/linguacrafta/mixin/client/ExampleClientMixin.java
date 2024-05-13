package diligord.linguacrafta.mixin.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.Scanner;
import java.io.File;

@Mixin(MinecraftClient.class)
public class ExampleClientMixin {
	@Inject(at = @At("HEAD"), method = "run")
	private void init(CallbackInfo info) {
		Scanner sc = new Scanner(System.in);
		Path path = FabricLoader.getInstance().getGameDir();
		System.out.println(path);
		//if () {

		//}
		// This code is injected into the start of MinecraftClient.run()V
	}
}