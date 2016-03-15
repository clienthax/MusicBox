package uk.co.haxyshideout.musicbox.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

public class GiveRadioCommand implements CommandExecutor {

    @Override public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player))
            return CommandResult.success();
        Player player = (Player) src;
        @SuppressWarnings("ConstantConditions")
        ItemStack radio = ItemStack.builder().itemType(ItemTypes.JUKEBOX).quantity(1).build();
        //noinspection ConstantConditions
        radio.offer(Keys.DISPLAY_NAME, Text.of("Radio"));
        player.getInventory().offer(radio);
        return CommandResult.success();
    }
}
