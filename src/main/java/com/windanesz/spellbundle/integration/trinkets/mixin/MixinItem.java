//package com.windanesz.spellbundle.integration.trinkets.mixin;
//
//import com.google.common.collect.Multimap;
//import electroblob.wizardry.registry.WizardryItems;
//import electroblob.wizardry.util.WandHelper;
//import net.minecraft.entity.ai.attributes.AttributeModifier;
//import net.minecraft.inventory.EntityEquipmentSlot;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemStack;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import xzeroair.trinkets.attributes.MagicAttributes;
//
//import java.util.UUID;
//
//@Mixin(Item.class)
//public class MixinItem {
//
//	@Unique
//	private final UUID STORAGE_UPGRADE_UUID = UUID.fromString("6be88399-59cf-43ba-bf5a-910f5bb4a419");
//
//	@Inject(method = "getAttributeModifiers", at = @At("HEAD"), remap = false)
//	private void getAttributeModifiersManaStorage(EntityEquipmentSlot slot, ItemStack stack, CallbackInfoReturnable<Multimap<String, AttributeModifier>> cir) {
//		int upgrades = WandHelper.getUpgradeLevel(stack, WizardryItems.storage_upgrade);
//		if (upgrades > 0) {
//			cir.getReturnValue().put(MagicAttributes.MAX_MANA.getName(), new AttributeModifier(STORAGE_UPGRADE_UUID, "Max Mana Modifier" , upgrades * 0.05f, 0));
//		}
//	}
//
//}