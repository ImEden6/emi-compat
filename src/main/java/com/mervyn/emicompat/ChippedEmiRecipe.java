package com.mervyn.emicompat;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class ChippedEmiRecipe implements EmiRecipe {
	private final EmiRecipeCategory category;
	private final ResourceLocation id;
	private final EmiIngredient input;
	private final List<EmiStack> outputs;

	public ChippedEmiRecipe(EmiRecipeCategory category, ResourceLocation recipeId, @Nullable ResourceLocation tagId, List<Item> items) {
		this.category = category;
		this.id = new ResourceLocation(recipeId.getNamespace(), recipeId.getPath() + "/" + (tagId != null ? tagId.getPath().replace(":", "_") : BuiltInRegistries.ITEM.getKey(items.get(0)).getPath().replace(":", "_")));
		this.outputs = items.stream().map(EmiStack::of).collect(Collectors.toList());
		if (tagId != null) {
			this.input = EmiIngredient.of(TagKey.create(BuiltInRegistries.ITEM.key(), tagId));
		} else {
			this.input = EmiIngredient.of(outputs);
		}
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return category;
	}

	@Override
	public @Nullable ResourceLocation getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(input);
	}

	@Override
	public List<EmiStack> getOutputs() {
		return outputs;
	}

	@Override
	public int getDisplayWidth() {
		// Output grid right edge: x=58 + 4*18 = 130 + 4px padding = 134
		return 134;
	}

	@Override
	public int getDisplayHeight() {
		// Output grid: 3 rows * 18px = 54 + 1px top buffer + 1px bottom for hover = 56
		return 56;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addTexture(EmiTexture.EMPTY_ARROW, 30, 18);
		widgets.addSlot(input, 6, 17);

		int x = 58;
		int y = 1; // 1px top buffer so hover outlines aren't clipped
		for (int i = 0; i < outputs.size() && i < 12; i++) {
			widgets.addSlot(outputs.get(i), x + (i % 4) * 18, y + (i / 4) * 18).recipeContext(this);
		}
	}
}
