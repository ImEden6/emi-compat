package com.mervyn.emicompat;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.favouriteless.enchanted.common.recipes.WitchCauldronRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CauldronEmiRecipe implements EmiRecipe {
	private final WitchCauldronRecipe recipe;
	private final ResourceLocation id;
	private final List<EmiIngredient> inputs;
	private final List<EmiStack> outputs;

	public CauldronEmiRecipe(WitchCauldronRecipe recipe) {
		this.recipe = recipe;
		this.id = recipe.getId();

		this.inputs = new ArrayList<>();
		for (ItemStack stack : recipe.getItemsIn()) {
			this.inputs.add(EmiStack.of(stack));
		}

		RegistryAccess registryAccess = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.registryAccess() : RegistryAccess.EMPTY;
		this.outputs = List.of(EmiStack.of(recipe.getResultItem(registryAccess)));
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return EnchantedEmiCompat.CAULDRON_CATEGORY;
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
		return 140;
	}

	@Override
	public int getDisplayHeight() {
		return 70;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		// Background from witch_cauldron.png
		widgets.addTexture(new ResourceLocation("enchanted", "textures/gui/jei/witch_cauldron.png"), 0, 0, 140, 70, 4, 4);

		// Animated progress arrow (120 ticks = 6000ms duration)
		widgets.addAnimatedTexture(new ResourceLocation("enchanted", "textures/gui/witch_oven.png"), 85, 29, 24, 17, 176, 14, 6000, true, false, false);

		// Inputs
		int offset = 0;
		for (int i = 0; i < inputs.size(); i++) {
			widgets.addSlot(inputs.get(i), 5 + offset, 5);
			offset += 20;
		}

		// Output
		if (!outputs.isEmpty()) {
			widgets.addSlot(outputs.get(0), 110, 30).recipeContext(this);
		}

		// Altar power text
		if (recipe.getPower() > 0) {
			Component powerComponent = Component.translatable("emi.compat.enchanted.required_altar_power", recipe.getPower());
			int width = Minecraft.getInstance().font.width(powerComponent);
			widgets.addText(powerComponent, 70 - width / 2, 55, 0x404040, false);
		}
	}
}
