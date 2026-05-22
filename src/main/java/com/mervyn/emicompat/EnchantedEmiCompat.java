package com.mervyn.emicompat;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.favouriteless.enchanted.common.circle_magic.RiteType;
import net.favouriteless.enchanted.common.init.EData;
import net.favouriteless.enchanted.common.init.EnchantedTags;
import net.favouriteless.enchanted.common.init.registry.EBlocks;
import net.favouriteless.enchanted.common.init.registry.EItems;
import net.favouriteless.enchanted.common.init.registry.ERecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.ArrayList;
import java.util.List;

@EmiEntrypoint
public class EnchantedEmiCompat implements EmiPlugin {
	public static final EmiRecipeCategory BYPRODUCT_CATEGORY = new EmiRecipeCategory(
			new ResourceLocation("enchanted", "byproduct"),
			EmiStack.of(EBlocks.WITCH_OVEN.get())
	);
	public static final EmiRecipeCategory DISTILLING_CATEGORY = new EmiRecipeCategory(
			new ResourceLocation("enchanted", "distilling"),
			EmiStack.of(EBlocks.DISTILLERY.get())
	);
	public static final EmiRecipeCategory SPINNING_CATEGORY = new EmiRecipeCategory(
			new ResourceLocation("enchanted", "spinning_"),
			EmiStack.of(EBlocks.SPINNING_WHEEL.get())
	);
	public static final EmiRecipeCategory CAULDRON_CATEGORY = new EmiRecipeCategory(
			new ResourceLocation("enchanted", "witch_cauldron"),
			EmiStack.of(EBlocks.WITCH_CAULDRON.get())
	);
	public static final EmiRecipeCategory KETTLE_CATEGORY = new EmiRecipeCategory(
			new ResourceLocation("enchanted", "kettle"),
			EmiStack.of(EBlocks.KETTLE.get())
	);
	public static final EmiRecipeCategory RITE_CATEGORY = new EmiRecipeCategory(
			new ResourceLocation("enchanted", "rite"),
			EmiStack.of(EItems.RITUAL_CHALK.get())
	);
	public static final EmiRecipeCategory MUTANDIS_CATEGORY = new EmiRecipeCategory(
			new ResourceLocation("enchanted", "mutandis"),
			EmiStack.of(EItems.MUTANDIS.get())
	);
	public static final EmiRecipeCategory MUTANDIS_EXTREMIS_CATEGORY = new EmiRecipeCategory(
			new ResourceLocation("enchanted", "mutandis_extremis"),
			EmiStack.of(EItems.MUTANDIS_EXTREMIS.get())
	);

	@Override
	public void register(EmiRegistry registry) {
		// Register Categories
		registry.addCategory(BYPRODUCT_CATEGORY);
		registry.addCategory(DISTILLING_CATEGORY);
		registry.addCategory(SPINNING_CATEGORY);
		registry.addCategory(CAULDRON_CATEGORY);
		registry.addCategory(KETTLE_CATEGORY);
		registry.addCategory(RITE_CATEGORY);
		registry.addCategory(MUTANDIS_CATEGORY);
		registry.addCategory(MUTANDIS_EXTREMIS_CATEGORY);

		// Register Workstations
		registry.addWorkstation(BYPRODUCT_CATEGORY, EmiStack.of(EBlocks.WITCH_OVEN.get()));
		registry.addWorkstation(DISTILLING_CATEGORY, EmiStack.of(EBlocks.DISTILLERY.get()));
		registry.addWorkstation(SPINNING_CATEGORY, EmiStack.of(EBlocks.SPINNING_WHEEL.get()));
		registry.addWorkstation(CAULDRON_CATEGORY, EmiStack.of(EBlocks.WITCH_CAULDRON.get()));
		registry.addWorkstation(KETTLE_CATEGORY, EmiStack.of(EBlocks.KETTLE.get()));

		registry.addWorkstation(RITE_CATEGORY, EmiStack.of(EItems.RITUAL_CHALK.get()));
		registry.addWorkstation(RITE_CATEGORY, EmiStack.of(EItems.GOLDEN_CHALK.get()));
		registry.addWorkstation(RITE_CATEGORY, EmiStack.of(EItems.NETHER_CHALK.get()));
		registry.addWorkstation(RITE_CATEGORY, EmiStack.of(EItems.OTHERWHERE_CHALK.get()));

		registry.addWorkstation(MUTANDIS_CATEGORY, EmiStack.of(EItems.MUTANDIS.get()));
		registry.addWorkstation(MUTANDIS_EXTREMIS_CATEGORY, EmiStack.of(EItems.MUTANDIS_EXTREMIS.get()));

		RecipeManager manager = registry.getRecipeManager();

		// Add Recipes from RecipeManager
		for (var recipe : manager.getAllRecipesFor(ERecipeTypes.BYPRODUCT.get())) {
			registry.addRecipe(new ByproductEmiRecipe(recipe));
		}
		for (var recipe : manager.getAllRecipesFor(ERecipeTypes.DISTILLING.get())) {
			registry.addRecipe(new DistillingEmiRecipe(recipe));
		}
		for (var recipe : manager.getAllRecipesFor(ERecipeTypes.SPINNING.get())) {
			registry.addRecipe(new SpinningEmiRecipe(recipe));
		}
		for (var recipe : manager.getAllRecipesFor(ERecipeTypes.WITCH_CAULDRON.get())) {
			registry.addRecipe(new CauldronEmiRecipe(recipe));
		}
		for (var recipe : manager.getAllRecipesFor(ERecipeTypes.KETTLE.get())) {
			registry.addRecipe(new KettleEmiRecipe(recipe));
		}

		// Rites Registration
		RegistryAccess registryAccess = null;
		if (Minecraft.getInstance().level != null) {
			registryAccess = Minecraft.getInstance().level.registryAccess();
		} else if (Minecraft.getInstance().getConnection() != null) {
			registryAccess = Minecraft.getInstance().getConnection().registryAccess();
		}

		if (registryAccess != null) {
			var riteRegistry = registryAccess.registry(EData.RITE_TYPES_REGISTRY).orElse(null);
			if (riteRegistry != null) {
				for (var entry : riteRegistry.entrySet()) {
					RiteType rite = entry.getValue();
					if (rite.getOutputs() != null) {
						registry.addRecipe(new RiteEmiRecipe(entry.getKey().location(), rite));
					}
				}
			}
		}

		// Mutandis Recipes
		registerMutandisRecipes(registry);
	}

	private void registerMutandisRecipes(EmiRegistry registry) {
		// Mutandis
		BuiltInRegistries.BLOCK.getTag(EnchantedTags.Blocks.MUTANDIS).ifPresent(tag -> {
			List<ItemStack> allInputs = tag.stream()
					.map(holder -> new ItemStack(holder.value()))
					.toList();

			var blacklistOpt = BuiltInRegistries.BLOCK.getTag(EnchantedTags.Blocks.MUTANDIS_BLACKLIST);

			tag.stream().forEach(holder -> {
				boolean inBlacklist = blacklistOpt.map(t -> t.contains(holder)).orElse(false);
				if (!inBlacklist) {
					ItemStack output = new ItemStack(holder.value());
					registry.addRecipe(new MutandisEmiRecipe(
							MUTANDIS_CATEGORY,
							new ResourceLocation("enchanted", "mutandis/" + BuiltInRegistries.BLOCK.getKey(holder.value()).getPath()),
							allInputs,
							output,
							Component.translatable("jei.enchanted.mutandis.description")
					));
				}
			});
		});

		// Mutandis Extremis
		BuiltInRegistries.BLOCK.getTag(EnchantedTags.Blocks.MUTANDIS_EXTREMIS).ifPresent(tag -> {
			List<ItemStack> allInputs = tag.stream()
					.map(holder -> new ItemStack(holder.value()))
					.toList();

			var blacklistOpt = BuiltInRegistries.BLOCK.getTag(EnchantedTags.Blocks.MUTANDIS_EXTREMIS_BLACKLIST);

			tag.stream().forEach(holder -> {
				boolean inBlacklist = blacklistOpt.map(t -> t.contains(holder)).orElse(false);
				if (!inBlacklist) {
					ItemStack output = new ItemStack(holder.value());
					registry.addRecipe(new MutandisEmiRecipe(
							MUTANDIS_EXTREMIS_CATEGORY,
							new ResourceLocation("enchanted", "mutandis_extremis/" + BuiltInRegistries.BLOCK.getKey(holder.value()).getPath()),
							allInputs,
							output,
							Component.translatable("jei.enchanted.mutandis.description")
					));
				}
			});
		});
	}
}
