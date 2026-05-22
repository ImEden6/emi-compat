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

		// Register permutations for Speed Upgrades
		for (boolean jumpy : new boolean[]{false, true}) {
			registerSockUpgrade(registry, 0, 1, jumpy);
			registerSockUpgrade(registry, 1, 2, jumpy);
		}

		// Register permutations for Jumpy Upgrades
		for (int speed = 0; speed <= 2; speed++) {
			registerJumpyUpgrade(registry, speed);
		}
	}

	private void registerSockUpgrade(EmiRegistry registry, int currentSpeed, int targetSpeed, boolean jumpy) {
		ItemStack inputSocks = new ItemStack(ThingsItems.SOCKS);
		if (currentSpeed > 0) {
			inputSocks.getOrCreateTag().putInt("Speed", currentSpeed);
		}
		if (jumpy) {
			inputSocks.getOrCreateTag().putBoolean("Jumpy", true);
		}

		ItemStack swiftnessPotion = new ItemStack(Items.POTION);
		PotionUtils.setPotion(swiftnessPotion, Potions.STRONG_SWIFTNESS);

		ItemStack outputSocks = new ItemStack(ThingsItems.SOCKS);
		outputSocks.getOrCreateTag().putInt("Speed", targetSpeed);
		if (jumpy) {
			outputSocks.getOrCreateTag().putBoolean("Jumpy", true);
		}

		ResourceLocation id = new ResourceLocation("emicompat", "things/sock_upgrade_speed_" + targetSpeed + "_jumpy_" + jumpy);
		registry.addRecipe(new dev.emi.emi.api.recipe.EmiCraftingRecipe(
				List.of(
						EmiStack.of(inputSocks),
						EmiStack.of(ThingsItems.GLEAMING_POWDER),
						EmiStack.of(swiftnessPotion)
				),
				EmiStack.of(outputSocks),
				id,
				true // shapeless
		));
	}

	private void registerJumpyUpgrade(EmiRegistry registry, int speed) {
		ItemStack inputSocks = new ItemStack(ThingsItems.SOCKS);
		if (speed > 0) {
			inputSocks.getOrCreateTag().putInt("Speed", speed);
		}

		ItemStack outputSocks = new ItemStack(ThingsItems.SOCKS);
		if (speed > 0) {
			outputSocks.getOrCreateTag().putInt("Speed", speed);
		}
		outputSocks.getOrCreateTag().putBoolean("Jumpy", true);

		ResourceLocation id = new ResourceLocation("emicompat", "things/sock_upgrade_jumpy_speed_" + speed);
		registry.addRecipe(new dev.emi.emi.api.recipe.EmiCraftingRecipe(
				List.of(
						EmiStack.of(inputSocks),
						EmiStack.of(ThingsItems.GLEAMING_COMPOUND),
						EmiStack.of(ThingsItems.RABBIT_FOOT_CHARM)
				),
				EmiStack.of(outputSocks),
				id,
				true // shapeless
		));
	}
}
