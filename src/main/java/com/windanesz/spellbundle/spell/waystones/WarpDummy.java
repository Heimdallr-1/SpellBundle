package com.windanesz.spellbundle.spell.waystones;

import com.windanesz.spellbundle.SpellBundle;
import com.windanesz.spellbundle.integration.waystones.WaystonesIntegration;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WarpDummy extends SpellRay {

	public WarpDummy() {
		super(SpellBundle.MODID, "warp", SpellActions.SUMMON, false);
	}

	protected WarpDummy(String modid, String name, EnumAction action, boolean isContinuous) {
		super(modid, name, action, isContinuous);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit,
			@Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	@Override
	protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
		return false;
	}

	/**
	 * Returns the disabled spell desc.
	 */
	protected String getDescriptionTranslationKey() { return WaystonesIntegration.getInstance().getMissingSpellDesc(); }
}
