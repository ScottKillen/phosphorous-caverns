// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;


// Referenced classes of package net.minecraft.src:
//            Item, World, Block, EntityPlayer, 
//            ItemStack

public class ItemPhosphorus extends Item
{

    public ItemPhosphorus(int i)
    {
        super(i);
    }

    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int l)
    {
        if(world.isBlockNormalCube(i, j, k))
        {
        	// Something with facing?
            if(l == 0)
            {
                j--;
            }
            if(l == 1)
            {
                j++;
            }
            if(l == 2)
            {
                k--;
            }
            if(l == 3)
            {
                k++;
            }
            if(l == 4)
            {
                i--;
            }
            if(l == 5)
            {
                i++;
            }
            if(!world.isAirBlock(i, j, k))
            {
                return false;
            }
        }
        if(!entityplayer.func_35190_e(i, j, k))
        {
            return false;
        }
        if(mod_Caverns.phosphorusBlock.canPlaceBlockAt(world, i, j, k))
        {
            itemstack.stackSize--;
            world.setBlockAndMetadataWithNotify(i, j, k, mod_Caverns.phosphorusBlock.blockID,15 - world.rand.nextInt(5));
            world.scheduleBlockUpdate(i, j, k, mod_Caverns.phosphorusBlock.blockID, 1);
            world.playSoundEffect(i, j, k, "random.fuse", 1.0f, 1.0f);
        }
        return true;
    }
}
