package com.windanesz.spellbundle.integration.trinkets.mixin;

import com.windanesz.spellbundle.integration.trinkets.TrinketsIntegration;
import electroblob.wizardry.constants.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xzeroair.trinkets.client.gui.hud.mana.ManaGui;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.TrinketsConfig;

import java.util.HashMap;
import java.util.Map;

@Mixin(ManaGui.class)
public class MixinManaGui {

	@Unique
	private Element lastElement = Element.MAGIC;

	@Unique
	Map<Element, ResourceLocation> manaBars = new HashMap<Element, ResourceLocation>() {{
		put(Element.MAGIC, new ResourceLocation(Reference.RESOURCE_PREFIX + "textures/gui/mana_bar.png"));
		put(Element.FIRE, new ResourceLocation(Reference.RESOURCE_PREFIX + "textures/gui/mana_bar_fire.png"));
		put(Element.ICE, new ResourceLocation(Reference.RESOURCE_PREFIX + "textures/gui/mana_bar_ice.png"));
		put(Element.LIGHTNING, new ResourceLocation(Reference.RESOURCE_PREFIX + "textures/gui/mana_bar_lightning.png"));
		put(Element.HEALING, new ResourceLocation(Reference.RESOURCE_PREFIX + "textures/gui/mana_bar_healing.png"));
		put(Element.NECROMANCY, new ResourceLocation(Reference.RESOURCE_PREFIX + "textures/gui/mana_bar_necromancy.png"));
		put(Element.EARTH, new ResourceLocation(Reference.RESOURCE_PREFIX + "textures/gui/mana_bar_earth.png"));
		put(Element.SORCERY, new ResourceLocation(Reference.RESOURCE_PREFIX + "textures/gui/mana_bar_sorcery.png"));
	}};

	@Inject(method = "renderManaGui", at = @At("HEAD"), remap = false, cancellable = true)
	private void renderManaGuiCancel(RenderGameOverlayEvent event, int x, int y, int tick, float mana, float maxMana, float cost, CallbackInfo ci) {
		Minecraft mc = Minecraft.getMinecraft();
		//		GlStateManager.pushMatrix();
		//		final EntityPlayerSP player = mc.player;
		final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		//		if (TrinketsConfig.CLIENT.MPBar.mana_horizontal) {
		// Actual Size of the Bar
		final int barWidth = TrinketsConfig.CLIENT.MPBar.width;
		final int barHeight = TrinketsConfig.CLIENT.MPBar.height;

		// Texture Section Size
		final int texWidth = barWidth;
		final int texHeight = barHeight;

		// UV Wrapped Tex Size
		final int texUVWidth = barWidth;
		final int texUVHeight = barHeight * 2;

		if (mana > maxMana) {
			mana = maxMana;
		}
		if (cost > mana) {
			cost = mana;
		}

		//		final int m = (int) (((mana) * 100) / maxMana);//(int) ((mana * 100) / maxMana); // This turns into a value between 0 and 100
		//		final float percent = (m / (100 * 1F)); // this turns into a decimal between 0 and 1
		//		final int barMana = m;//this.percentValue(barWidth, percent); // Initial Bar Width is 104
		//		final int texMana = this.percentValue(texWidth, percent);
		final float test = ((((mana - cost) * 100) / maxMana) * 0.01F);
		final int currentManaWithCost = (int) (barWidth * test);//(int) ((cost * 100) / maxMana);
		//		final float percentCost = costM / (100 * 1F);

			//		final int barCost = MathHelper.clamp(this.percentValue(barMana, percentCost), 0, barMana);
		//		final int texCost = MathHelper.clamp(this.percentValue(texMana, percentCost), 0, texMana);
		//		final int currentManaWithCost = costM;//(int) (barCost * percentCost);
		//		System.out.println(currentManaWithCost);
		//		if (cost > 0) {
		//			final int testCost = (int) ((barWidth * percent) * percentCost);//(int) MathHelper.clamp(barMana * percent, 0, barMana);
		//			System.out.println(cost + " | " + percentCost + " | " + barCost + " | " + barMana + " | " + percent + " | " + costM);
		//		}

		//		final int d = (barMana <= (barWidth - 2) ? +2 : 0);
		//		final int xPos = (x + d) + barMana;

		if (mc.player.ticksExisted % 22 == 0) {
			lastElement = TrinketsIntegration.getFullSetElement(mc.player).orElse(Element.MAGIC);
		}
		mc.getTextureManager().bindTexture(manaBars.get(lastElement));
		GlStateManager.translate(x, y, 0);
		//		System.out.println(scale);
		try {
			final int hs = mc.displayHeight;
			final int ws = mc.displayWidth;
			final int hsf = event.getResolution().getScaledHeight();
			final int wsf = event.getResolution().getScaledWidth();
			final int sF = event.getResolution().getScaleFactor();
			final float scaleH = ((hsf * 1F) / hs);
			final float scaleW = ((wsf * 1F) / ws);
			GlStateManager.scale(scaleW * sF, scaleH * sF, 0);
			//						GlStateManager.scale(0.5, 0.5, 0.5);
		} catch (final Exception e) {
		}
		if (!TrinketsConfig.CLIENT.MPBar.mana_horizontal) {
			GlStateManager.rotate(-90, 0, 0, 1);
		}
		GlStateManager.translate(-x, -y, 0);
		GlStateManager.enableBlend();
		// Bar Background
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, barWidth, barHeight, texUVWidth, texUVHeight);
		if (mana != 0) {
			Gui.drawModalRectWithCustomSizedTexture(x, y, 0, texHeight, currentManaWithCost, barHeight, texUVWidth, texUVHeight);
		}
		// Bar Foreground - Cost
		//		if (barCost != 0) {
		//			//			this.drawImage(
		//			//					xPos, 0,
		//			//					barCost, barHeight,
		//			//					texUVWidth, texUVHeight,
		//			//					0, texHeight * 2,
		//			//					texCost, texHeight,
		//			//					true, false, true, false
		//			//			);
		//			//			drawModalRectWithCustomSizedTexture(xPos - barCost, y, 0, texHeight * 2, barCost, barHeight, texCost, texUVHeight);
		//		}
		if (!TrinketsConfig.CLIENT.MPBar.hide_text) {
			GlStateManager.pushMatrix();
			// TEXT is 7 high, 5 wide in pixels
			//			y -= 11; // barHeight / 2
			//			y += (barHeight / 4) + 3;
			mana = Math.round(mana * 100) / 100;
			maxMana = Math.round(MathHelper.clamp(maxMana, 0, maxMana) * 100) / 100;
			final String txt = (int) mana + "/" + (int) maxMana;
			//			x -= txt.length() * 6;
			x += (barWidth / 2) - ((txt.length() * 6) / 2);
			x += 1;
			y += (barHeight / 2) - 4;
			x = fontRenderer.drawStringWithShadow(txt, x, y, 0xffffffff);
			//			GlStateManager.disableAlpha();
			GlStateManager.popMatrix();
			//			mc.getTextureManager().bindTexture(manaBar);
			//			y -= (barHeight / 4) + 3;
			x -= (barWidth / 2) - ((txt.length() * 6) / 2);
			x -= 1;
			y -= (barHeight / 2) - 4;
		}
		ci.cancel();
		GlStateManager.disableBlend();
		//		GlStateManager.popMatrix();
	}

}