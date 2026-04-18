package com.mervyn.emicompat;

import java.util.List;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellRegistry;

public class SpellBindingEmiRecipe implements EmiRecipe {
	private final Item item;
	private final String spellId;
	private final ResourceLocation id;
	private final Spell spell;
	private final int tier;

	public SpellBindingEmiRecipe(Item item, String spellId) {
		this.item = item;
		this.spellId = spellId;
		ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item);
		// Create a unique ID for this recipe
		this.id = new ResourceLocation("emicompat",
				"spell_binding/" + itemKey.getNamespace() + "/" + itemKey.getPath() + "/" + spellId.replace(":", "/"));
		this.spell = SpellRegistry.getSpell(new ResourceLocation(spellId));
		this.tier = this.spell != null && this.spell.learn != null ? this.spell.learn.tier : 1;
	}

	private int getLevelRequirement() {
		int perTier = spell != null && spell.learn != null ? spell.learn.level_requirement_per_tier : 10;
		return tier * perTier;
	}

	private int getLevelCost() {
		int perTier = spell != null && spell.learn != null ? spell.learn.level_cost_per_tier : 3;
		int cost = tier * perTier;
		if (SpellEngineMod.config != null) {
			cost *= SpellEngineMod.config.spell_binding_level_cost_multiplier;
		}
		return cost;
	}

	private int getLapisCost() {
		int perTier = spell != null && spell.learn != null ? spell.learn.level_cost_per_tier : 3;
		int cost = tier * perTier;
		if (SpellEngineMod.config != null) {
			cost *= SpellEngineMod.config.spell_binding_lapis_cost_multiplier;
		}
		return cost;
	}

	private int getBookshelfRequirement() {
		int requirement = getLevelRequirement();
		if (requirement <= 10)
			return 0;
		int bookshelves = (int) Math.ceil((requirement - 10.0) / 1.5);
		return Math.min(bookshelves, 18);
	}

	private String getRomanNumeral(int tier) {
		return switch (tier) {
			case 1 -> "I";
			case 2 -> "II";
			case 3 -> "III";
			case 4 -> "IV";
			case 5 -> "V";
			default -> String.valueOf(tier);
		};
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return SpellEngineEmiCompat.SPELL_BINDING_CATEGORY;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return List.of(EmiStack.of(item), EmiStack.of(Items.LAPIS_LAZULI, getLapisCost()));
	}

	@Override
	public List<EmiStack> getOutputs() {
		ItemStack stack = new ItemStack(item);
		CompoundTag nbt = stack.getOrCreateTag();

		// Structure for Spell Engine 0.15.x on 1.20.1
		// This sets the spell on the item so it shows up in EMI with the correct
		// tooltips/info
		CompoundTag container = new CompoundTag();
		ListTag spellIds = new ListTag();
		spellIds.add(StringTag.valueOf(spellId));
		container.put("spell_ids", spellIds);
		nbt.put("spell_container", container);

		return List.of(EmiStack.of(stack));
	}

	@Override
	public int getDisplayWidth() {
		return 100;
	}

	@Override
	public int getDisplayHeight() {
		return 18;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.addSlot(getInputs().get(0), 0, 0);
		widgets.addSlot(getInputs().get(1), 18, 0);

		// Roman Numeral before the arrow
		widgets.addText(Component.literal(getRomanNumeral(tier)).withStyle(ChatFormatting.GOLD), 38, 2, 0xFFFFFF,
				false);

		widgets.addTexture(EmiTexture.EMPTY_ARROW, 46, 0);

		// XP Level cost under the arrow
		int levelCost = getLevelCost();
		int requiredLevel = getLevelRequirement();
		widgets.addText(Component.literal(String.valueOf(levelCost)).withStyle(ChatFormatting.GREEN), 52, 10, 0xFFFFFF,
				false);
		widgets.addTooltipText(List.of(Component.literal("Requires Level " + requiredLevel)), 52, 10, 10, 10);

		widgets.addSlot(getOutputs().get(0), 73, 0).recipeContext(this);

		// Bookshelf requirement in the bottom right
		int bookshelves = getBookshelfRequirement();
		if (bookshelves > 0) {
			widgets.addText(Component.literal(String.valueOf(bookshelves)).withStyle(ChatFormatting.LIGHT_PURPLE), 92,
					10, 0xFFFFFF, false);
			widgets.addTooltipText(List.of(Component.literal("Bookshelves Required")), 92, 10, 10, 10);
		}
	}
}
