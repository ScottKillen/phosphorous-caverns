// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;

import java.util.Random;


// Referenced classes of package net.minecraft.src:
//            Block, Material

public class BlockCavern extends Block
{
	public static int renderTypeID;
    protected BlockCavern(int i, int j)
    {
        super(i, j, Material.ground);
        //setTickOnLoad(true);
    }
    
    //public boolean renderAsNormalBlock() {
    //	return true;
    //}
    
    //public float getAmbientOcclusionLightValue(IBlockAccess iblockaccess, int i, int j, int k) {
    //	return 1f;//1;
    //}
    
    public int getRenderType() {
    	return mod_Caverns.cavernRenderID;
    }
    
    public static boolean fixedBrightness = false;
    //public static float brightness = 1.0f;
    
    public float getBlockBrightness(IBlockAccess iblockaccess, int i, int j, int k)
    {
    	if (fixedBrightness) return 15;
        return iblockaccess.getBrightness(i, j, k, lightValue[blockID]);
    }

    public int getMixedBrightnessForBlock(IBlockAccess iblockaccess, int i, int j, int k)
    {
    	//if (fixedBrightness) return 15;
        return iblockaccess.getLightBrightnessForSkyBlocks(i, j, k, lightValue[blockID]);
    }
    
    public boolean isBlockSurface(IBlockAccess iblockaccess, int i, int j, int k) {
    	if (iblockaccess.isAirBlock(i+1, j, k)) return true;
    	if (iblockaccess.isAirBlock(i-1, j, k)) return true;
    	if (iblockaccess.isAirBlock(i, j+1, k)) return true;
    	if (iblockaccess.isAirBlock(i, j-1, k)) return true;
    	if (iblockaccess.isAirBlock(i, j, k+1)) return true;
    	if (iblockaccess.isAirBlock(i, j, k-1)) return true;
    	
		return false;
    }
    
    // Don't allow the blocks to exist when not on the surface!
    public void onNeighborBlockChange(World world, int i, int j, int k, int l)
    {
    	if (world.getBlockId(i, j, k) != mod_Caverns.cavernBlock.blockID) return;
    	if (!isBlockSurface(world,i,j,k)) {
    		world.setBlockWithNotify(i, j, k, Block.stone.blockID);
    		System.out.print("Cavern Smothered at " + i + "," + j + "," + k);
    	}
    }
    
    //public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
    //	updateTick(world,i,j,k,world.rand);
    //}
    
    // Infrequent ticks
    public int tickRate() {
    	return 10;
    }
    
    public void onBlockPlaced(World world, int i, int j, int k, int l)
    {
    	// Newly placed cavern blocks 
    	System.out.print("Player placing block, replacing with metadata");
    	world.setBlockAndMetadataWithNotify(i, j, k, mod_Caverns.cavernBlock.blockID, 15);
    	world.scheduleBlockUpdate(i,j,k,blockID,1); // Setup to retick immediately
    }
    
    public static int countBiolumeInRegion(IBlockAccess iblockaccess, int i, int j, int k, int size) {
		int count = 0;
		
		int sx = i-size,
			sy = j-size,
			sz = k-size;
		
		size *= 2;
		for (int ci = 0; ci < size; ++ci)
			for (int cj = 0; cj < size; ++cj)
				for (int ck = 0; ck < size; ++ck)
					count += iblockaccess.getBlockId(ci+sx, cj+sy, ck+sz) == mod_Caverns.cavernBlock.blockID ? 1 : 0;
		
		return count;
	}
    
    public int idDropped(int i, Random random, int j)
    {
        return Block.cobblestone.blockID;
    }
    
    public void onBlockDestroyedByPlayer(World world, int i, int j, int k, int l)
    {
    	if(world.multiplayerWorld)
            return;
    	int amt = world.rand.nextInt(4);
    	for (int c = 0; c < amt; ++c) { // Can't seem to get it to drop four times?
    		float f = 0.7F;
            double d = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double)i + d, (double)j + d1, (double)k + d2,  new ItemStack(mod_Caverns.phosphorus,1));
            entityitem.delayBeforeCanPickup = 10;
            world.entityJoinedWorld(entityitem);
    	}
    }
    
    // Spreading!
    public void updateTick(World world, int i, int j, int k, Random random) {
    	int spread = world.getBlockMetadata(i, j, k);
    	
    	if (spread == 0) return; // No point if we are out of spreadability
    	//if(random.nextInt(5) == 0)
    	//System.out.print("Performing Tick on " + i + "," + j + "," + k + " (Metadata " + spread + ")\n");
        	boolean didCreate = false;
    		
    		int range = spread > 10 ? 8 : 6;
    		int half = range/2;
            for(int l = 0; l < 70 ; l++)
            {
                int i1 = (i + random.nextInt(range)) - half;
                int j1 = (j + random.nextInt(range)) - half;
                int k1 = (k + random.nextInt(range)) - half;
                if(world.getBlockId(i1, j1, k1) == Block.stone.blockID) {
                	//System.out.print("Stoneblock found, attempting replace " + i1 + "," + j1 + "," + k1);
                	if (isBlockSurface(world,i1,j1,k1))
                		if (countBiolumeInRegion(world,i1,j1,k1,5) < 12) {
                			world.setBlockAndMetadataWithNotify(i1, j1, k1, mod_Caverns.cavernBlock.blockID,
                					MathHelper.clamp_int(spread - random.nextInt(5) - 1, 0, 13));
                			world.scheduleBlockUpdate(i1,j1,k1,blockID,1);//15-spread);
                			didCreate = true;
                		}
                }
            }
            
            didCreate = true;
            //world.scheduleBlockUpdate(i,j,k,blockID,1);
            if (didCreate) world.scheduleBlockUpdate(i,j,k,blockID,20); // setup for a slightly delayed tick
            world.setBlockAndMetadata(i, j, k, blockID, spread-1); // On failure to propegate, decrement spreading
            // (This is intended to decrease the cost of ticks over time)
    }
}
