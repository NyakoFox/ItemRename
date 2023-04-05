package gay.nyako.itemrename;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.TextParserUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public final class RenameCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("rename")
                .executes(RenameCommand::clearName)
                .then(CommandManager.argument("name", StringArgumentType.greedyString())
                        .executes(RenameCommand::setName))
        );
    }

    public static int clearName(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();
        if (player == null) {
            return 0;
        }
        ItemStack heldStack = player.getMainHandStack();
        if (heldStack.isEmpty()) {
            context.getSource().sendError(Text.literal("You can't rename nothing."));
        } else {
            heldStack.removeCustomName();
            context.getSource().sendFeedback(Text.literal("Your item's name has been cleared."), false);
        }
        return 1;
    }

    public static int setName(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();
        if (player == null) {
            return 0;
        }
        ItemStack heldStack = player.getMainHandStack();
        Text newName = TextParserUtils.formatTextSafe(context.getArgument("name", String.class));
        if (heldStack.isEmpty()) {
            context.getSource().sendError(Text.literal("You can't rename nothing."));
        } else {
            heldStack.setCustomName(((MutableText)newName).styled(x -> x.withItalic(false)));
            var startingText = (MutableText) Text.literal("Your item has been renamed to ");
            context.getSource().sendFeedback(startingText.append(newName).append("."), false);
        }
        return 1;
    }
}
