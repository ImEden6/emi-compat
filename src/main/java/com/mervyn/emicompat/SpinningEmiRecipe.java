package com.mervyn.emicompat;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.favouriteless.enchanted.common.recipes.SpinningRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpinningEmiRecipe implements EmiRecipe {
	private final SpinningRecipe recipe;
	private final ResourceLocation id;
	private final List<EmiIngredient> inputs;
	private final List<EmiStack> outputs;

	public SpinningEmiRecipe(SpinningRecipe recipe) {
		this.recipe = recipe;
		this.id = recipe.getId();

		this.inputs = new ArrayList<>();
		for (ItemStack stack : recipe.getItemsIn()) {
			if (!stack.isEmpty()) {
				this.inputs.add(EmiStack.of(stack));
			}
		}

		RegistryAccess registryAccess = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.registryAccess() : RegistryAccess.EMPTY;
		this.outputs = List.of(EmiStack.of(recipe.getResultItem(registryAccess)));
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return EnchantedEmiCompat.SPINNING_CATEGORY;
	}

	@Override
	public @Nullable ResourceLocation getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return inputs;
	}

	@Override
	public List<EmiStack> getOutputs() {
		return outputs;
	}

	@Override
	public int getDisplayWidth() {
		return 140;
	}

	@Override
	public int getDisplayHeight() {
		return 80;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		// Background
		widgets.addTexture(new ResourceLocation("enchanted", "textures/gui/spinning_wheel.png"), 0, 0, 140, 60, 20, 10);

		// Animated arrows (duration in ticks * 50ms)
		int durationMs = recipe.getDuration() * 50;
		if (durationMs <= 0) {
			durationMs = 6000; // default/fallback to 120 ticks
		}

		// Left arrow (176, 0, 15, 20) filled from bottom to top
		widgets.addAnimatedTexture(new ResourceLocation("enchanted", "textures/gui/spinning_wheel.png"), 7, 16, 15, 20, 176, 0, durationMs, false, true, false);

		// Right arrow (176, 20, 15, 20) filled from bottom to top
		widgets.addAnimatedTexture(new ResourceLocation("enchanted", "textures/gui/spinning_wheel.png"), 44, 16, 15, 20, 176, 20, durationMs, false, true, false);

		// Slots positioning
		int[][] positions = new int[][]{{25, 13}, {13, 37}, {37, 37}};
		for (int i = 0; i < inputs.size() && i < positions.length; i++) {
			widgets.addSlot(inputs.get(i), positions[i][0], positions[i][1]);
		}

		// Output Slot
		widgets.addSlot(outputs.get(0), 110, 25).recipeContext(this);

		// Altar power text
		if (recipe.getPower() > 0) {
			Component text = Component.translatable("emi.compat.enchanted.required_altar_power", recipe.getPower());
			// Center text at y = 65
			widgets.addText(text, 70 - Minecraft.getInstance().font.width(text) / 2, 65, 0x404040, false);
		}
	}
}
