package gay.nyako.itemrename;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ItemRenameMod implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LoreCommand.register(dispatcher);
            RenameCommand.register(dispatcher);
        });
    }
}
