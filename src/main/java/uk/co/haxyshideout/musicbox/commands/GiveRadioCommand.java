package uk.co.haxyshideout.musicbox.commands;

import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import uk.co.haxyshideout.musicbox.MusicBox;

public class GiveRadioCommand implements CommandExecutor {

    @Override public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player))
            return CommandResult.success();
        Player player = (Player) src;
        @SuppressWarnings("ConstantConditions")
        ItemStack radio = MusicBox.getInstance().game.getRegistry().createBuilder(ItemStack.Builder.class).itemType(ItemTypes.JUKEBOX).quantity(1)
                .build();
        //noinspection ConstantConditions
        radio.offer(Keys.DISPLAY_NAME, Texts.of("Radio"));
        ((EntityPlayerMP)player).inventory.addItemStackToInventory((net.minecraft.item.ItemStack) radio);

        return CommandResult.success();
    }
}
