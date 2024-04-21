package com.windanesz.spellbundle.integration.trinkets.command;

import com.windanesz.spellbundle.integration.trinkets.ManaProgression;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.util.TrinketsConfig;

import java.util.Collections;
import java.util.List;

import static com.windanesz.spellbundle.integration.trinkets.TrinketsEventHandler.calculateCurrentMana;

public class CommandSetMagicLevel extends CommandBase {

	public static final String COMMAND = "setmagiclevel";

	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public boolean checkPermission(final MinecraftServer server, final ICommandSender sender) {
		return true;
	}

	@Override
	public String getName() {
		return COMMAND;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/setmagiclevel <player> level";
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] arguments, BlockPos pos){

		switch(arguments.length){
			case 1:
				return getListOfStringsMatchingLastWord(arguments, server.getOnlinePlayerNames());
			case 2:
				return Collections.singletonList(String.valueOf(ManaProgression.getMaxLevel()));
		}
		return super.getTabCompletions(server, sender, arguments, pos);
	}
	public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
		if (args.length != 2) {
			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
		}
		if (!(sender instanceof EntityPlayerMP)) {
			throw new WrongUsageException(this.getUsage(sender), new Object[0]);
		}
		final EntityPlayer player = getPlayer(server, sender, args[0]);
		int level = Integer.parseInt(args[1]);
		int maxLevel = ManaProgression.getMaxLevel();
		if (level > maxLevel) {
			throw new WrongUsageException("The maximum allowed level is set to " + maxLevel);
		}

		if (player != null) {
			double xp = ManaProgression.getXpForLevel(level);
			ManaProgression.setTotalXP(player, (int) xp + 1);
			double nextBonus = calculateCurrentMana(ManaProgression.getMaxLevel(), level, (int) TrinketsConfig.SERVER.mana.mana_max) * 0.1;
			//int actualIncrease = nextBonus - currentManaBasedOnLevel;
			//double newBonusMana = (stats.getBonusMana() + actualIncrease);
			MagicStats stats = Capabilities.getMagicStats(player);
			if (stats == null) {
				return;
			}
			stats.setBonusMana(nextBonus);
		}
	}

	public boolean isUsernameIndex(final String[] args, final int index) {
		return index == 0;
	}
}
