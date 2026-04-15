package com.mervyn.emicompat;

import java.util.List;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import earth.terrarium.adastra.common.recipes.machines.NasaWorkbenchRecipe;
import earth.terrarium.adastra.common.registry.ModRecipeTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdAstraEmiCompat implements EmiPlugin {
	public static final String MOD_ID = "emicompat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ResourceLocation NASA_WORKBENCH_ID = new ResourceLocation("ad_astra", "nasa_workbench");
	public static final EmiRecipeCategory NASA_WORKBENCH_CATEGORY = new EmiRecipeCategory(NASA_WORKBENCH_ID,
			EmiStack.of(BuiltInRegistries.ITEM.get(NASA_WORKBENCH_ID)));

	public static final ResourceLocation AUTOMATION_NASA_WORKBENCH_ID = new ResourceLocation("ad_astra_giselle_addon",
			"automation_nasa_workbench");
	public static final EmiRecipeCategory AUTOMATION_NASA_WORKBENCH_CATEGORY = new EmiRecipeCategory(
			AUTOMATION_NASA_WORKBENCH_ID, EmiStack.of(BuiltInRegistries.ITEM.get(AUTOMATION_NASA_WORKBENCH_ID)));

	@Override
	public void register(EmiRegistry registry) {
		LOGGER.info("Registering Ad Astra x EMI Compatibility");

		// NASA Workbench
		registry.addCategory(NASA_WORKBENCH_CATEGORY);
		registry.addWorkstation(NASA_WORKBENCH_CATEGORY, EmiStack.of(BuiltInRegistries.ITEM.get(NASA_WORKBENCH_ID)));

		RecipeManager manager = registry.getRecipeManager();
		List<NasaWorkbenchRecipe> recipes = manager.getAllRecipesFor(ModRecipeTypes.NASA_WORKBENCH.get());

		for (NasaWorkbenchRecipe recipe : recipes) {
			ResourceLocation id = recipe.id();
			registry.addRecipe(new NasaWorkbenchEmiRecipe(id, recipe));
		}

		// Automated NASA Workbench (Soft Dep)
		if (FabricLoader.getInstance().isModLoaded("ad_astra_giselle_addon")) {
			LOGGER.info("Detected Ad Astra: Giselle Addon, registering Automated Workbench compatibility");
			registry.addCategory(AUTOMATION_NASA_WORKBENCH_CATEGORY);
			registry.addWorkstation(AUTOMATION_NASA_WORKBENCH_CATEGORY,
					EmiStack.of(BuiltInRegistries.ITEM.get(AUTOMATION_NASA_WORKBENCH_ID)));

			for (NasaWorkbenchRecipe recipe : recipes) {
				ResourceLocation id = recipe.id();
				// Use a different ID for the automated version to avoid duplicates
				ResourceLocation automatedId = new ResourceLocation(MOD_ID,
						"automated/" + id.getNamespace() + "/" + id.getPath());
				registry.addRecipe(new AutomationNasaWorkbenchEmiRecipe(automatedId, recipe));
			}
		}
	}
}
