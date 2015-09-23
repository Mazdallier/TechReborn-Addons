package techreborn.addons.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import techreborn.addons.api.farm.IFarmLogicContainer;
import techreborn.addons.api.farm.IFarmLogicDevice;
import techreborn.addons.farm.FarmTree;
import techreborn.client.TechRebornCreativeTabMisc;

public class ItemFarmPatten extends Item implements IFarmLogicContainer {


    public ItemFarmPatten() {
        setCreativeTab(TechRebornCreativeTabMisc.instance);
        setUnlocalizedName("techreborn.farmPatten");
    }

    @Override
    public IFarmLogicDevice getLogicFromStack(ItemStack stack) {
        return new FarmTree();
    }
}
