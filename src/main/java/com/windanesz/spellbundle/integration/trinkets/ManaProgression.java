package com.windanesz.spellbundle.integration.trinkets;

import com.windanesz.spellbundle.Settings;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public final class ManaProgression {
	public static final IStoredVariable<Integer> MAGIC_XP = IStoredVariable.StoredVariable.ofInt("spellbundleMagicXP", Persistence.ALWAYS).setSynced();
	public static final IStoredVariable<Integer> ORIGINAL_COST = IStoredVariable.StoredVariable.ofInt("spellbundleContinuousSpellOriginalCost", Persistence.NEVER).setSynced();

	private static final int MAX_LEVEL = Settings.generalSettings.max_magic_level;
	private static final double BASE_XP = 100;
	private static final double XP_MULTIPLIER = 1.2;
	private static final double[] XP_TABLE = new double[MAX_LEVEL + 1];

	public static int getMaxLevel() {
		return 32;
	}

	protected static int getDistributedCost(int cost, int castingTick) {
		int partialCost;
		if (castingTick % 20 == 0) {
			partialCost = cost / 2 + cost % 2;
		} else if (castingTick % 10 == 0) {
			partialCost = cost / 2;
		} else {
			partialCost = 0;
		}

		return partialCost;
	}

	public static int getXpGainForSpellCast(EntityPlayer caster, World world, Spell spell, SpellModifiers modifiers, EnumHand hand, int castingTick) {
		int progression = 0;
		if (!world.isRemote) {
			if (!spell.isContinuous && spell.requiresPacket()) {
				IMessage msg = new PacketCastSpell.Message(caster.getEntityId(), hand, spell, modifiers);
				WizardryPacketHandler.net.sendToDimension(msg, world.provider.getDimension());
			}

			progression = (int) ((float) spell.getCost() * modifiers.get("cost") + 0.1F);
			if (spell.isContinuous) {
				progression = getDistributedCost(progression, castingTick);
			}

		}

		if (castingTick % 20 == 0) {
			progression = (int) ((float) spell.getCost() * modifiers.get("progression"));
			WizardData.get(caster).trackRecentSpell(spell);
		}
		return progression;
	}

	static {
		// Initialize the XP_TABLE with calculated XP values for each level
		for (int i = 0; i < MAX_LEVEL + 1; i++) {
			XP_TABLE[i] = calculateXpRequired(i + 1);
		}
	}

	private ManaProgression() {}

	private static double calculateXpRequired(int level) {
		if (level < 1 || level > MAX_LEVEL + 1) {
			return 0; // or any other default value
		}
		// Calculate the XP required for a specific level using the base XP and multiplier
		return BASE_XP * Math.pow(XP_MULTIPLIER, level - 1);
	}

	public static int calculateNextLevel(double currentXp) {
		// Find the next level based on the current XP
		for (int level = 1; level <= MAX_LEVEL; level++) {
			if (currentXp < XP_TABLE[level]) {
				return level;
			}
		}
		return MAX_LEVEL;
	}

	public static double getXpForLevel(int targetLevel) {
		double totalXp = 0;

		for (int i = 0; i < targetLevel; i++) {
			if (i >= XP_TABLE.length) {
				// Handle cases where targetLevel exceeds the highest level in XP_TABLE
				return Double.POSITIVE_INFINITY;
			}
			totalXp += XP_TABLE[i];
		}

		return totalXp;
	}

//	public static double calculateTotalXpRequired(int level) {
//		if (level < 1 || level > MAX_LEVEL) {
//			return 0; // or any other default value
//		}
//		double totalXp = 0;
//		// Calculate the total XP required to reach a specific level by summing up the XP for each level
//		for (int i = 1; i <= level; i++) {
//			totalXp += calculateXpRequired(i);
//		}
//		return totalXp;
//	}

	public static int getLevelForXp(double currentXp) {
		int level = 0;
		double totalXp = 0;

		for (double xp : XP_TABLE) {
			totalXp += xp;
			if (currentXp <= totalXp) {
				return level;
			}
			level++;
		}

		return level;
	}

	public static double calculateXpRequiredForNextLevel(double currentXp) {
		int currentLevel = getLevelForXp(currentXp);
		if (currentLevel + 1 < MAX_LEVEL) {
			// Calculate the XP required to reach the next level based on the current XP
			double xpRequiredForNextLevel = XP_TABLE[currentLevel + 1];
			return xpRequiredForNextLevel - currentXp;
		}
		return 0; // Max level reached, no XP required for next level
	}

	public static void addXP(EntityPlayer player, int amount) {

		WizardData data = WizardData.get(player);
		int xp = data.getVariable(MAGIC_XP) != null ? data.getVariable(MAGIC_XP) : 0;
		xp += amount;
		data.setVariable(MAGIC_XP, xp);
		data.sync();
	}

	public static void setTotalXP(EntityPlayer player, int amount) {

		WizardData data = WizardData.get(player);
		data.setVariable(MAGIC_XP, (int) Math.min(amount, getXpForLevel(getMaxLevel()) + 1));
		data.sync();
	}

	public static int getXP(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		return data.getVariable(MAGIC_XP) != null ? data.getVariable(MAGIC_XP) : 0;
	}

	public static int getLevel(EntityPlayer player) {

		int xp = getXP(player);
		return getLevelForXp(xp);
	}

	public static void register() {

		WizardData.registerStoredVariables(MAGIC_XP);
		WizardData.registerStoredVariables(ORIGINAL_COST);
	}

	public static void setOriginalCost(EntityPlayer player, int cost) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			data.setVariable(ORIGINAL_COST, cost);
			data.sync();
		}
	}

	public static int getOriginalCost(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		if (data != null) {
			return data.getVariable(ORIGINAL_COST) != null ? data.getVariable(ORIGINAL_COST) : 0;
		}
		return 0;
	}
}
