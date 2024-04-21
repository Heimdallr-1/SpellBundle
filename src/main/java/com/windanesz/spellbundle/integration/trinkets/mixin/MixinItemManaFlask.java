package com.windanesz.spellbundle.integration.trinkets.mixin;

import electroblob.wizardry.item.ItemManaFlask;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xzeroair.trinkets.capabilities.Capabilities;

@Mixin(ItemManaFlask.class)
public class MixinItemManaFlask {

	@Inject(method = "onItemUseFinish", at = @At("HEAD"), cancellable = true)
	private void onItemUseFinishMixin(ItemStack stack, World worldIn, EntityLivingBase entityLiving, CallbackInfoReturnable<ItemStack> cir) {
		EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer) entityLiving : null;

		if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
			stack.shrink(1);
		}

		if (entityplayer instanceof EntityPlayerMP) {
			CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) entityplayer, stack);
		}

		if (entityplayer != null) {
			Capabilities.getMagicStats(entityplayer, playerMana -> {
				playerMana.addMana(((ItemManaFlask) (Object) this).size.capacity / 2f);
			});
		}

		if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
			if (stack.isEmpty()) {
				cir.setReturnValue(ItemStack.EMPTY);
			}

			if (entityplayer != null) {
				entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
			}
		}

		cir.setReturnValue(stack);
	}

	@Inject(method = "onItemRightClick", at = @At("HEAD"), cancellable = true)
	private void onItemRightClickMixin(World world, EntityPlayer player, EnumHand hand, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
		player.setActiveHand(hand);

		ItemStack flask = player.getHeldItem(hand);
		cir.setReturnValue(new ActionResult<>(EnumActionResult.SUCCESS, flask));
	}
}

