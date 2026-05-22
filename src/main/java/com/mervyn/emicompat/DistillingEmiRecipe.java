package com.mervyn.emicompat;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.favouriteless.enchanted.common.recipes.DistillingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DistillingEmiRecipe implements EmiRecipe {
	private final DistillingRecipe recipe;
	private final ResourceLocation id;
	private final List<EmiIngredient> inputs;
	private final List<EmiStack> outputs;

	public DistillingEmiRecipe(DistillingRecipe recipe) {
		this.recipe = recipe;
		this.id = recipe.getId();

		this.inputs = new ArrayList<>();
		for (ItemStack stack : recipe.getItemsIn()) {
			inputs.add(EmiStack.of(stack));
		}

		this.outputs = new ArrayList<>();
		for (ItemStack stack : recipe.getItemsOut()) {
			outputs.add(EmiStack.of(stack));
		}
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return EnchantedEmiCompat.DISTILLING_CATEGORY;
	}

	@Override
	public ResourceLocation getId() {
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
		return 146;
	}

	@Override
	public int getDisplayHeight() {
		return 75;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		// Background
		widgets.addTexture(new ResourceLocation("enchanted", "textures/gui/distillery.png"), 0, 0, 146, 75, 4, 5);

		// Animated bubbles (vertical fill from bottom)
		int durationMs = recipe.getCookTime() * 50; // 1 tick = 50ms
		widgets.addAnimatedTexture(new ResourceLocation("enchanted", "textures/gui/distillery.png"), 88, 22, 12, 29, 176, 0, durationMs, false, true, false);

		// Animated progress arrow (horizontal fill from left to right)
		widgets.addAnimatedTexture(new ResourceLocation("enchanted", "textures/gui/distillery.png"), 65, 8, 57, 61, 176, 29, durationMs, true, false, false);

		// Inputs
		if (!inputs.isEmpty()) {
			// Primary input
			widgets.addSlot(inputs.get(0), 28, 30);
			
			// Extra inputs
			int offset = 20;
			for (int i = 1; i < inputs.size(); i++) {
				widgets.addSlot(inputs.get(i), 50, offset);
				offset += 20;
			}
		}

		// Outputs
		int outOffset = 2;
		for (EmiStack stack : outputs) {
			widgets.addSlot(stack, 123, outOffset).recipeContext(this);
			outOffset += 19;
		}

		// Altar power and time display
		if (recipe.getPower() > 0) {
			widgets.addText(Component.translatable("emi.compat.enchanted.altar_power", recipe.getPower()), 4, 4, 0x404040, false);
		}
		widgets.addText(Component.literal(recipe.getCookTime() / 20 + "s"), 4, 16, 0x404040, false);
	}
}
