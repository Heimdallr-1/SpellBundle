package com.windanesz.spellbundle.integration.trinkets;

import com.windanesz.spellbundle.Settings;
import com.windanesz.spellbundle.integration.Integration;
import com.windanesz.spellbundle.registry.SBItems;
import com.windanesz.spellbundle.registry.SBLoot;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemWizardArmour;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TrinketsIntegration extends Integration {

	// singleton instance
	private static final TrinketsIntegration instance = new TrinketsIntegration();
	private static final String modId = "xat";

	private static final List<Spell> SPELL_LIST = new ArrayList<>();
	private static final List<Item> ARTEFACT_LIST = new ArrayList<>();

	private boolean isLoaded;

	private TrinketsIntegration() {}

	public static Integration getInstance() { return instance; }

	/************* overrides *************/

	@Override
	public String getModid() { return modId; }

	@Override
	public void init() {
		// register compat instance
		Integration.register(getModid(), getInstance());

		isLoaded = Loader.isModLoaded(getModid());

		if (!isEnabled()) { return; }

		// init stuff
		//initCustom();
		MinecraftForge.EVENT_BUS.register(TrinketsEventHandler.INSTANCE);
		ManaProgression.register();
	}

	@Override
	public boolean isEnabled() {
		return Settings.generalSettings.quark_integration && isLoaded;
	}

	/**
	 * List used to track which spells belong to this supported mod. Used for spell disabling in postInit in {@link Integration#setDisables()}.
	 *
	 * @return The list of this integration's spells.
	 */
	@Override
	public List<Spell> getSpells() {
		return SPELL_LIST;
	}

	/**
	 * Adds a spell to this integration's list of spells.
	 *
	 * @param spell spell to add.
	 * @return the passed in spell for method chaining.
	 */
	@Override
	public Spell addSpell(Spell spell) {
		if (!SPELL_LIST.contains(spell)) {
			SPELL_LIST.add(spell);
		}
		return spell;
	}

	/**
	 * List used for loot injection in {@link SBLoot#onLootTableLoadEvent(net.minecraftforge.event.LootTableLoadEvent)} and to disable the artefact in
	 * {@link Integration#setDisables()} if the supported mod is not present.
	 *
	 * @return list of all registered ItemArtefacts for this supported mod
	 */
	@Override
	public List<Item> getArtefacts() {
		return ARTEFACT_LIST;
	}

	@Override
	public void addArtefact(Item item) {
		ARTEFACT_LIST.add(item);
	}

	public void initCustom() {
		if (!isEnabled()) { return; }
		WandHelper.registerSpecialUpgrade(SBItems.rejuvenation_upgrade, "rejuvenation_upgrade");
	}


	public static Optional<Element> getFullSetElementForClassOptional(EntityLivingBase entity, ItemWizardArmour.ArmourClass armourClass) {
		if (isWearingFullSet(entity, null, armourClass)) {
			// if it's a full set, we can just check any of the armour pieces
			ItemStack helmet = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
			return Optional.of(((ItemWizardArmour) helmet.getItem()).element != null ? ((ItemWizardArmour) helmet.getItem()).element : Element.MAGIC);
		}
		return Optional.empty();
	}

	public static Optional<Element> getFullSetElement(EntityLivingBase entity) {
		for (ItemWizardArmour.ArmourClass armourClass : ItemWizardArmour.ArmourClass.values()) {
			if (isWearingFullSet(entity, null, armourClass)) {
				// if it's a full set, we can just check any of the armour pieces
				ItemStack helmet = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
				return Optional.of(((ItemWizardArmour) helmet.getItem()).element != null ? ((ItemWizardArmour) helmet.getItem()).element : Element.MAGIC);
			}
		}
		return Optional.empty();
	}

	public static boolean isWearingFullSet(EntityLivingBase entity, @Nullable Element element, @Nullable ItemWizardArmour.ArmourClass armourClass) {
		ItemStack helmet = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
		if (!(helmet.getItem() instanceof ItemWizardArmour)) { return false; }
		Element e = element == null ? ((ItemWizardArmour) helmet.getItem()).element : element;
		ItemWizardArmour.ArmourClass ac = armourClass == null ? ((ItemWizardArmour) helmet.getItem()).armourClass : armourClass;
		return Arrays.stream(InventoryUtils.ARMOUR_SLOTS)
				.allMatch(s -> entity.getItemStackFromSlot(s).getItem() instanceof ItemWizardArmour
						&& ((ItemWizardArmour) entity.getItemStackFromSlot(s).getItem()).element == e
						&& ((ItemWizardArmour) entity.getItemStackFromSlot(s).getItem()).armourClass == ac);
	}
}
