package dan200.computercraft.shared.common;

import net.minecraft.item.ItemStack;

public interface IColouredItem
{
    int getColour( ItemStack stack );

    ItemStack setColour( ItemStack stack, int colour );
}
