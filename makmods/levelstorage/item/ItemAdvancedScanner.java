package makmods.levelstorage.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.Items;
import ic2.api.recipe.Recipes;

import java.util.ArrayList;
import java.util.List;

import makmods.levelstorage.LSBlockItemList;
import makmods.levelstorage.LevelStorage;
import makmods.levelstorage.LSCreativeTab;
import makmods.levelstorage.proxy.ClientProxy;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemAdvancedScanner extends Item implements IElectricItem {

	public static final int TIER = 2;
	public static final int STORAGE = 100000;
	public static final int COOLDOWN_PERIOD = 20;
	public static final int ENERGY_PER_USE = 10000;
	private static final int RADIUS = 16;

	public static final String NBT_COOLDOWN = "cooldown";

	public ItemAdvancedScanner(int id) {
		super(id);
		this.setMaxDamage(27);
		this.setNoRepair();
		if (FMLCommonHandler.instance().getSide().isClient()) {
			this.setCreativeTab(LSCreativeTab.instance);
		}
		this.setMaxStackSize(1);
	}

	public static void addCraftingRecipe() {

		ItemStack ovScanner = Items.getItem("ovScanner");
		ItemStack uum = Items.getItem("matter");
		ItemStack energyCrystal = Items.getItem("energyCrystal");
		ItemStack advCircuit = Items.getItem("advancedCircuit");
		ItemStack glassFiber = Items.getItem("glassFiberCableItem");
		ItemStack advScanner = new ItemStack(LSBlockItemList.itemAdvScanner);
		Recipes.advRecipes.addRecipe(advScanner, "ucu", "asa", "ggg",
		        Character.valueOf('u'), uum, Character.valueOf('g'),
		        glassFiber, Character.valueOf('a'), advCircuit,
		        Character.valueOf('c'), energyCrystal, Character.valueOf('s'),
		        ovScanner);

	}

	public static void verifyStack(ItemStack stack) {
		// Just in case... Whatever!
		if (stack.stackTagCompound == null) {
			stack.stackTagCompound = new NBTTagCompound();
			if (!stack.stackTagCompound.hasKey("charge")) {
				stack.stackTagCompound.setInteger("charge", 0);
			}
		}
	}

	// TODO: refactor this later with the NBTHelper.
	public static void setNBTInt(ItemStack stack, String name, int value) {
		verifyStack(stack);
		stack.stackTagCompound.setInteger(name, value);
	}

	public static int getNBTInt(ItemStack stack, String name) {
		verifyStack(stack);
		if (!stack.stackTagCompound.hasKey(name)) {
			stack.stackTagCompound.setInteger(name, 0);
		}
		return stack.stackTagCompound.getInteger(name);
	}

	public void printMessage(String message, EntityPlayer player) {
		LevelStorage.proxy.messagePlayer(player, message, new Object[0]);
	}

	@Override
	public void addInformation(ItemStack par1ItemStack,
	        EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		String[] lines = StatCollector.translateToLocal("tooltip.advScanner").split("\n");
		for (String line : lines) {
			par3List.add("\2472" + line);
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
	        EntityPlayer par3EntityPlayer) {
		if (!par2World.isRemote) {
			if (ElectricItem.manager.canUse(par1ItemStack, ENERGY_PER_USE)) {
				ElectricItem.manager.use(par1ItemStack, ENERGY_PER_USE,
				        par3EntityPlayer);
			} else
				return par1ItemStack;
			if (!(getNBTInt(par1ItemStack, NBT_COOLDOWN) == 0))
				return par1ItemStack;
			setNBTInt(par1ItemStack, NBT_COOLDOWN, COOLDOWN_PERIOD);

			ArrayList<ItemStack> blocksFound = new ArrayList<ItemStack>();

			int playerX = (int) par3EntityPlayer.posX;
			int playerY = (int) par3EntityPlayer.posY;
			int playerZ = (int) par3EntityPlayer.posZ;

			for (int y = 0; y < (int) par3EntityPlayer.posY; y++) {
				for (int x = -(RADIUS / 2); x < (RADIUS / 2); x++) {
					for (int z = -(RADIUS / 2); z < (RADIUS / 2); z++) {
						ItemStack foundStack = new ItemStack(
						        par2World.getBlockId(playerX + x, y, playerZ
						                + z), 1, par2World.getBlockMetadata(
						                playerX + x, y, playerZ + z));
						blocksFound.add(foundStack);
					}
				}
			}

			this.printMessage("", par3EntityPlayer);
			this.printMessage("", par3EntityPlayer);
			this.printMessage("Found materials in " + RADIUS + "x" + RADIUS
			        + " cubouid below you", par3EntityPlayer);
			this.printMessage("", par3EntityPlayer);
			ArrayList<String> names = new ArrayList<String>();
			ArrayList<CollectedStatInfo> info = new ArrayList<CollectedStatInfo>();
			for (ItemStack stack : blocksFound) {
				try {
					String currentName = stack.getDisplayName();
					if (!names.contains(currentName)) {
						names.add(currentName);
						info.add(new CollectedStatInfo(currentName, 1));
					} else {
						int amountAlreadyHas = 0;
						int indexAt = 0;
						for (int i = 0; i < info.size(); i++) {
							if (currentName.equals(info.get(i).name)) {
								amountAlreadyHas = info.get(i).amount;
								indexAt = i;
								break;
							}
						}
						info.remove(indexAt);
						info.add(new CollectedStatInfo(currentName,
						        amountAlreadyHas + 1));
					}
				}
				// There will be a ton of these guys, let's ignore em
				catch (NullPointerException e) {
					continue;
				}
			}
			for (CollectedStatInfo i : info) {
				this.printMessage(i.name + " - " + i.amount, par3EntityPlayer);
			}
		}
		return par1ItemStack;
	}

	@Override
	public void onUpdate(ItemStack par1ItemStack, World par2World,
	        Entity par3Entity, int par4, boolean par5) {
		if (!par2World.isRemote) {
			verifyStack(par1ItemStack);
			if (getNBTInt(par1ItemStack, NBT_COOLDOWN) > 0) {
				setNBTInt(par1ItemStack, NBT_COOLDOWN,
				        getNBTInt(par1ItemStack, NBT_COOLDOWN) - 1);
			}
		}
	}

	@Override
	public boolean canProvideEnergy(ItemStack itemStack) {
		return false;
	}

	@Override
	public int getChargedItemId(ItemStack itemStack) {
		return this.itemID;
	}

	@Override
	public int getEmptyItemId(ItemStack itemStack) {
		return this.itemID;
	}

	@Override
	public int getMaxCharge(ItemStack itemStack) {
		return STORAGE;
	}

	@Override
	public int getTier(ItemStack itemStack) {
		return TIER;
	}

	@Override
	public int getTransferLimit(ItemStack itemStack) {
		return 2000;
	}

	@Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs,
	        List par3List) {
		ItemStack var4 = new ItemStack(this, 1);
		ElectricItem.manager.charge(var4, Integer.MAX_VALUE, Integer.MAX_VALUE,
		        true, false);
		par3List.add(var4);
		par3List.add(new ItemStack(this, 1, this.getMaxDamage()));

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister
		        .registerIcon(ClientProxy.ADV_SCANNER_TEXTURE);
	}

	public class CollectedStatInfo {
		public int amount;
		public String name;

		public CollectedStatInfo(String name, Integer amount) {
			this.name = name;
			this.amount = amount;
		}
	}
}
