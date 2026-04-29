package com.mervyn.emicompat;

import java.util.List;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ThingsCraftingEmiRecipe implements EmiRecipe {
	private final ResourceLocation id;
	private final List<EmiIngredient> inputs;
	private final EmiStack output;

	public ThingsCraftingEmiRecipe(ResourceLocation id, List<EmiIngredient> inputs, EmiStack output) {
		this.id = id;
		this.inputs = inputs;
		this.output = output;
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return VanillaEmiRecipeCategories.CRAFTING;
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
		return List.of(output);
	}

	@Override
	public int getDisplayWidth() {
		return 118;
	}

	@Override
	public int getDisplayHeight() {
		return 54;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		// 3x3 Crafting Grid Background
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 60, 18);

		// Inputs (Shapeless-ish layout)
		for (int i = 0; i < inputs.size(); i++) {
			widgets.addSlot(inputs.get(i), (i % 3) * 18, (i / 3) * 18);
		}

		// Output
		widgets.addSlot(output, 92, 14).large(true).recipeContext(this);
	}
}
