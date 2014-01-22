// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;

import java.util.ArrayList;
import java.util.Random;


// Referenced classes of package net.minecraft.src:
//            Block, Material

public class BlockPhosphorus extends Block
{

    protected BlockPhosphorus(int i, int j)
    {
        super(i, j, Material.circuits);
        setTickOnLoad(true);
    }
    
    public void updateTick(World world, int i, int j, int k, Random random)
    {
    	// Wear down and destroy self
    	int metadata = world.getBlockMetadata(i, j, k);
    	
    	if (metadata < 2) {
    		world.setBlockWithNotify(i, j, k, 0);
    		Entity entity = new EntityFlameFX(world, i + 0.5f,j + 0.5f,k + 0.5f, 0, 0, 0);
    		switch(random.nextInt(6)){
    			case 0 : // Normal explosion
    				world.createExplosion(entity, i, j, k, 2F); break;
    			case 1 : // Firey explosion
    				world.createExplosion(entity, i, j, k, 2f);
    				for (int c = 0; c < 20; ++c) {
    					double d5 = (float)i + random.nextFloat();
    		            double d6 = (double)j + 1;
    		            double d7 = (float)k + random.nextFloat();
    		            world.spawnParticle("lava", d5, d6, d7, 0.0D, 0.0D, 0.0D);
    				} break;
    			case 2 : // Potentially large explosion
    				if (random.nextInt(3) == 0)
    					world.createExplosion(entity, i, j, k, 3f);
    				else
    					world.createExplosion(entity, i, j, k, 2f);
    				break;
    			default: // Fizzle
    				for (int c = 0; c < 20; ++c) {
    					double d = random.nextDouble(), d1 = random.nextDouble(), d2 = random.nextDouble();
    					
    					world.spawnParticle("largesmoke", d+i, d1+j, d2+k, 0.0D, 0.0D, 0.0D);
    					world.playSoundEffect((float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, "fire.fire", 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F);
    				}
    		}
    		return;
    	}
    	world.setBlockAndMetadataWithNotify(i, j, k, blockID,(metadata - 1));
    	world.scheduleBlockUpdate(i, j, k, blockID, 5); // Schedule the update
    }
    
    public void onBlockDestroyedByExplosion(World world, int i, int j, int k)
    {
    	explodeBlockAt(world,i,j,k,world.rand);
    }
    
    void explodeBlockAt(World world, int i, int j, int k, Random random) {
    	world.setBlockWithNotify(i, j, k, 0);
		Entity entity = new EntityFlameFX(world, i + 0.5f,j + 0.5f,k + 0.5f, 0, 0, 0);
		switch(random.nextInt(4)){
			case 0 : // Normal explosion
				world.createExplosion(entity, i, j, k, 2F); break;
			case 1 : // Firey explosion
				world.createExplosion(entity, i, j, k, 2f);
				for (int c = 0; c < 20; ++c) {
					double d5 = (float)i + random.nextFloat();
		            double d6 = (double)j + 1;
		            double d7 = (float)k + random.nextFloat();
		            world.spawnParticle("lava", d5, d6, d7, 0.0D, 0.0D, 0.0D);
				} break;
			case 2 : // Potentially large explosion
				if (random.nextInt(3) == 0)
					world.createExplosion(entity, i, j, k, 3f);
				else
					world.createExplosion(entity, i, j, k, 2f);
				break;
			default: // Fizzle
				for (int c = 0; c < 20; ++c) {
					double d = random.nextDouble(), d1 = random.nextDouble(), d2 = random.nextDouble();
					
					world.spawnParticle("largesmoke", d+i, d1+j, d2+k, 0.0D, 0.0D, 0.0D);
					world.playSoundEffect((float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, "fire.fire", 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F);
				}
		}
    }
    
    public void randomDisplayTick(World world, int i, int j, int k, Random random)
    {
    	if(random.nextInt(24) == 0)
        {
            world.playSoundEffect((float)i + 0.5F, (float)j + 0.5F, (float)k + 0.5F, "fire.fire", 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F);
        }
    	
        for (int t = 0; t < 5; ++t)
        {
            double d = (double)i + 0.5D + ((double)random.nextFloat() - 0.5D);
            double d1 = (float)j + 0.0625F + random.nextGaussian();
            double d2 = (double)k + 0.5D + ((double)random.nextFloat() - 0.5D);
            float f = (float)15 / 15F;
            float f1 = f * 0.6F + 0.4F;
            float f2 = f * f * 0.7F - 0.5F;
            float f3 = f * f * 0.6F - 0.7F;
            if(f2 < 0.0F)
            {
                f2 = 0.0F;
            }
            if(f3 < 0.0F)
            {
                f3 = 0.0F;
            }
            world.spawnParticle("largesmoke", d, d1, d2, 0.0D, 0.0D, 0.0D);
            world.spawnParticle("reddust", d, d1, d2, f1, f2, f3);
            double d5 = (float)i + random.nextFloat();
            double d6 = (double)j + 1;
            double d7 = (float)k + random.nextFloat();
            if (random.nextInt(2) == 0)
            	world.spawnParticle("lava", d5, d6, d7, 0.0D, 0.0D, 0.0D);
        }
    }
    
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public int getRenderType()
    {
        return mod_Caverns.phosphorusRenderID;
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k)
    {
        return null;
    }
    
    public int idDropped(int i, Random random, int j)
    {
        return mod_Caverns.phosphorus.shiftedIndex;
    }
    
    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k)
    {
    	setBlockBounds(0,0,0,1,1,1);
    	
    	boolean
		px = iblockaccess.isBlockNormalCube(i+1, j, k),
		py = iblockaccess.isBlockNormalCube(i, j+1, k),
		pz = iblockaccess.isBlockNormalCube(i, j, k+1),
		nx = iblockaccess.isBlockNormalCube(i-1, j, k),
		ny = iblockaccess.isBlockNormalCube(i, j-1, k),
		nz = iblockaccess.isBlockNormalCube(i, j, k-1);
    	
    	int sum = (px ? 1 : 0) + (py ? 1 : 0) + (pz ? 1 : 0) + (nx ? 1 : 0) + (ny ? 1 : 0) + (nz ? 1 : 0);
    	if (sum > 1) return;
    	
    	float f = 1/3f;
    	
    	if (nx) setBlockBounds(1-f,1,1,1,1,1);
    	if (ny) setBlockBounds(1,1-f,1,1,1,1);
    	if (nz) setBlockBounds(1,1,1-f,1,1,1);
    	if (px) setBlockBounds(0,0,0,0+f,0,0);
    	if (py) setBlockBounds(0,0,0,0,0+f,0);
    	if (pz) setBlockBounds(0,0,0,0,0,0+f);
    }
    
    
    // Can only be placed next to normal cubes
    public boolean canPlaceBlockAt(World world, int i, int j, int k)
    {
    	return	world.isBlockNormalCube(i+1, j, k) ||
    			world.isBlockNormalCube(i-1, j, k) ||
    			world.isBlockNormalCube(i, j+1, k) ||
    			world.isBlockNormalCube(i, j-1, k) ||
    			world.isBlockNormalCube(i, j, k+1) ||
    			world.isBlockNormalCube(i, j, k-1);
    }
    
    public boolean canBlockStay(World world, int i, int j, int k)
    {
    	return!(world.isBlockNormalCube(i+1, j, k) ||
    			world.isBlockNormalCube(i-1, j, k) ||
    			world.isBlockNormalCube(i, j+1, k) ||
    			world.isBlockNormalCube(i, j-1, k) ||
    			world.isBlockNormalCube(i, j, k+1) ||
    			world.isBlockNormalCube(i, j, k-1));
    }
}
