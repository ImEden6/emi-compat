package com.mervyn.emicompat;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import earth.terrarium.chipped.common.recipes.ChippedRecipe;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EmiEntrypoint
public class ChippedEmiCompat implements EmiPlugin {
	private static final Map<ResourceLocation, EmiRecipeCategory> CATEGORIES = new HashMap<>();

	@Override
	public void register(EmiRegistry registry) {
		RecipeManager manager = registry.getRecipeManager();

		// Register categories for all chipped workbenches
		registerCategory(registry, "alchemy_bench");
		registerCategory(registry, "botanist_workbench");
		registerCategory(registry, "carpenters_table");
		registerCategory(registry, "glassblower");
		registerCategory(registry, "loom_table");
		registerCategory(registry, "mason_table");
		registerCategory(registry, "mechanist_workbench");
		registerCategory(registry, "tinkering_table");

		for (Recipe<?> recipe : manager.getRecipes()) {
			if (recipe instanceof ChippedRecipe chippedRecipe) {
				ResourceLocation typeId = BuiltInRegistries.RECIPE_TYPE.getKey(chippedRecipe.getType());
				if (typeId != null && typeId.getNamespace().equals("chipped")) {
					EmiRecipeCategory category = CATEGORIES.get(typeId);
					if (category != null) {
						for (HolderSet<Item> tag : chippedRecipe.tags()) {
							List<Item> items = tag.stream()
									.map(holder -> holder.value())
									.collect(Collectors.toList());

							if (!items.isEmpty()) {
								ResourceLocation tagId = tag.unwrap().left().map(t -> t.location()).orElse(null);
								registry.addRecipe(new ChippedEmiRecipe(category, recipe.getId(), tagId, items));
							}
						}
					}
				}
			}
		}
	}

	private void registerCategory(EmiRegistry registry, String name) {
		ResourceLocation id = new ResourceLocation("chipped", name);
		EmiRecipeCategory category = new EmiRecipeCategory(id, EmiStack.of(BuiltInRegistries.BLOCK.get(id)));
		CATEGORIES.put(id, category);
		registry.addCategory(category);
		registry.addWorkstation(category, EmiStack.of(BuiltInRegistries.BLOCK.get(id)));
	}
}
