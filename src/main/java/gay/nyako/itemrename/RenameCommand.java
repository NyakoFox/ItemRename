package gay.nyako.itemrename;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.pb4.placeholders.api.TextParserUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static net.minecraft.commands.Commands.literal;

public final class RenameCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("rename")
                .executes(RenameCommand::clearName)
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(RenameCommand::setName))
        );
    }

    public static int clearName(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();
        if (player == null) {
            return 0;
        }
        ItemStack heldStack = player.getMainHandItem();
        if (heldStack.isEmpty()) {
            context.getSource().sendFailure(Component.literal("You can't rename nothing."));
        } else {
            heldStack.set(DataComponents.CUSTOM_NAME, null);
            context.getSource().sendSuccess(() -> Component.literal("Your item's name has been cleared."), false);
        }
        return 1;
    }

    public static int setName(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Player player = source.getPlayer();
        if (player == null) {
            return 0;
        }
        ItemStack heldStack = player.getMainHandItem();
        Component newName = TextParserUtils.formatTextSafe(context.getArgument("name", String.class));
        if (heldStack.isEmpty()) {
            context.getSource().sendFailure(Component.literal("You can't rename nothing."));
        } else {
            heldStack.set(DataComponents.CUSTOM_NAME, ((MutableComponent)newName).withStyle(x -> x.withItalic(false)));
            var startingText = (MutableComponent) Component.literal("Your item has been renamed to ");
            context.getSource().sendSuccess(() -> startingText.append(newName).append("."), false);
        }
        return 1;
    }
}
