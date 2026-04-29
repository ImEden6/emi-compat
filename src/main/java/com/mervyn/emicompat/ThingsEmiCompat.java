package com.mervyn.emicompat;

import java.util.List;

import com.glisco.things.items.ThingsItems;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EmiEntrypoint
public class ThingsEmiCompat implements EmiPlugin {
	public static final String MOD_ID = "emicompat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void register(EmiRegistry registry) {
		LOGGER.info("Registering Things x EMI Compatibility");

		// Sock Speed Upgrades
		registerSockUpgrade(registry, 0, 1);
		registerSockUpgrade(registry, 1, 2);

		// Jumpy Socks Upgrade
		registerJumpyUpgrade(registry);
	}

	private void registerSockUpgrade(EmiRegistry registry, int currentSpeed, int targetSpeed) {
		ItemStack inputSocks = new ItemStack(ThingsItems.SOCKS);
		if (currentSpeed > 0) {
			inputSocks.getOrCreateTag().putInt("Speed", currentSpeed);
		}

		ItemStack swiftnessPotion = new ItemStack(Items.POTION);
		PotionUtils.setPotion(swiftnessPotion, Potions.SWIFTNESS);

		ItemStack outputSocks = new ItemStack(ThingsItems.SOCKS);
		outputSocks.getOrCreateTag().putInt("Speed", targetSpeed);
		if (currentSpeed > 0) {
			// If it was already jumpy, keep it (optional, but good for completeness)
			// However, the recipe in Things doesn't explicitly handle jumpy state during speed upgrade, 
			// it just increments Speed.
		}

		ResourceLocation id = new ResourceLocation("emicompat", "things/sock_upgrade_" + targetSpeed);
		registry.addRecipe(new ThingsCraftingEmiRecipe(
				id,
				List.of(
						EmiStack.of(inputSocks),
						EmiStack.of(ThingsItems.GLEAMING_POWDER),
						EmiStack.of(swiftnessPotion)
				),
				EmiStack.of(outputSocks)
		));
	}

	private void registerJumpyUpgrade(EmiRegistry registry) {
		ItemStack inputSocks = new ItemStack(ThingsItems.SOCKS);
		ItemStack outputSocks = new ItemStack(ThingsItems.SOCKS);
		outputSocks.getOrCreateTag().putBoolean("Jumpy", true);

		ResourceLocation id = new ResourceLocation("emicompat", "things/sock_upgrade_jumpy");
		registry.addRecipe(new ThingsCraftingEmiRecipe(
				id,
				List.of(
						EmiStack.of(inputSocks),
						EmiStack.of(ThingsItems.GLEAMING_COMPOUND),
						EmiStack.of(ThingsItems.RABBIT_FOOT_CHARM)
				),
				EmiStack.of(outputSocks)
		));
	}
}
