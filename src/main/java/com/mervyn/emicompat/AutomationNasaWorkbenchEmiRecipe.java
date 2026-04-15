package com.mervyn.emicompat;

import java.util.List;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import earth.terrarium.adastra.common.recipes.machines.NasaWorkbenchRecipe;
import net.minecraft.resources.ResourceLocation;

public class AutomationNasaWorkbenchEmiRecipe implements EmiRecipe {
	private final ResourceLocation id;
	private final List<EmiIngredient> input;
	private final List<EmiStack> output;

	public AutomationNasaWorkbenchEmiRecipe(ResourceLocation id, NasaWorkbenchRecipe recipe) {
		this.id = id;
		this.input = recipe.ingredients().stream().map(EmiIngredient::of).toList();
		this.output = List.of(EmiStack.of(recipe.result()));
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return AdAstraEmiCompat.AUTOMATION_NASA_WORKBENCH_CATEGORY;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return input;
	}

	@Override
	public List<EmiStack> getOutputs() {
		return output;
	}

	@Override
	public int getDisplayWidth() {
		// Matches the regular NASA Workbench width
		return 150;
	}

	@Override
	public int getDisplayHeight() {
		// Matches the regular NASA Workbench height
		return 128;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		// Nose
		widgets.addSlot(input.get(0), 56, 20);

		// Middle sections
		widgets.addSlot(input.get(1), 47, 38);
		widgets.addSlot(input.get(2), 65, 38);
		widgets.addSlot(input.get(3), 47, 56);
		widgets.addSlot(input.get(4), 65, 56);
		widgets.addSlot(input.get(5), 47, 74);
		widgets.addSlot(input.get(6), 65, 74);

		// Wings
		widgets.addSlot(input.get(7), 29, 92);
		widgets.addSlot(input.get(8), 47, 92);
		widgets.addSlot(input.get(9), 65, 92);
		widgets.addSlot(input.get(10), 83, 92);
		widgets.addSlot(input.get(11), 29, 110);
		widgets.addSlot(input.get(12), 56, 110);
		widgets.addSlot(input.get(13), 83, 110);

		// Arrow / Progress
		// Shifted to match the line in the regular background (99, 56)
		widgets.addFillingArrow(99, 56, 10000); // 10s default

		// Output
		widgets.addSlot(output.get(0), 129, 56).recipeContext(this);
	}
}
