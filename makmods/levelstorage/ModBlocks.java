package makmods.levelstorage;

import ic2.api.item.Items;
import makmods.levelstorage.block.BlockWirelessConductor;
import makmods.levelstorage.block.BlockXpCharger;
import makmods.levelstorage.block.BlockXpGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ModBlocks {
	public static final ModBlocks instance = new ModBlocks();

	public BlockXpGenerator blockXpGen;
	public BlockXpCharger blockXpCharger;
	public BlockWirelessConductor blockWlessConductor;

	private ModBlocks() {
	}

	private void initBlocks() {
		blockXpGen = new BlockXpGenerator(
				BlockItemIds.instance.getIdFor("blockXpGenId"));
		blockXpCharger = new BlockXpCharger(
				BlockItemIds.instance.getIdFor("blockXpChargerId"));
		blockWlessConductor = new BlockWirelessConductor(
				BlockItemIds.instance.getIdFor("blockWirelessConductor"));
	}

	private void registerBlocks() {
		GameRegistry.registerBlock(blockXpGen, "tile.blockXpGen");
		GameRegistry.registerBlock(blockXpCharger, "tile.blockXpCharger");
		GameRegistry.registerBlock(blockWlessConductor, "tile.blockWirelessConductor");
		
	}

	private void addBlockNames() {
		LanguageRegistry.addName(blockXpGen, "XP Generator");
		LanguageRegistry.addName(blockXpCharger, "XP Charger");
		LanguageRegistry.addName(blockWlessConductor, "Wireless Conductor");
	}

	private void addRecipes() {
		ItemStack blockXpGenStack = new ItemStack(blockXpGen);
		ItemStack blockXpChargerStack = new ItemStack(blockXpCharger);
		ItemStack advMachine = Items.getItem("advancedMachine");
		ItemStack machine = Items.getItem("machine");
		ItemStack generator = Items.getItem("generator");
		ItemStack refIron = Items.getItem("refinedIronIngot");
		ItemStack goldIngot = new ItemStack(Item.ingotGold);
		GameRegistry.addRecipe(blockXpGenStack, "iai", "geg", "imi",
				Character.valueOf('i'), refIron, Character.valueOf('g'),
				goldIngot, Character.valueOf('a'), advMachine,
				Character.valueOf('m'), machine, Character.valueOf('e'),
				generator);
		GameRegistry.addRecipe(blockXpChargerStack, "iai", "geg", "imi",
				Character.valueOf('i'), refIron, Character.valueOf('g'),
				goldIngot, Character.valueOf('a'), advMachine,
				Character.valueOf('m'), machine, Character.valueOf('e'),
				blockXpGenStack);
	}

	private void setMiningLevels() {
		MinecraftForge.setBlockHarvestLevel(blockXpGen, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(blockXpCharger, "pickaxe", 1);
		MinecraftForge.setBlockHarvestLevel(blockWlessConductor, "pickaxe", 1);
		
	}

	public void init() {
		this.initBlocks();
		this.registerBlocks();
		this.addBlockNames();
		this.addRecipes();
		this.setMiningLevels();
	}
}
