// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode fieldsfirst 

package net.minecraft.src;

import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")
public class mod_Caverns extends BaseMod
{
	public static int cavernRenderID;
	// Reistence mimics Stone
	public static Block cavernBlock = 
			new BlockCavern(160,1)
				.setBlockName("Cavern")
				.setLightValue(0.4f)
				.setRequiresSelfNotify()
				.setResistance(10F)
				.setHardness(1.5F)
				.disableStats()
				.setStepSound(new StepSound("stone", 1.0F, 0.8F));//.setResistance(0.5f).setHardness(5f);
	
	public static Block phosphorusBlock =
			new BlockPhosphorus(161,3)
				.setHardness(0.0F)
				.setStepSound(new StepSound("stone",1.0f,1.0f))
				.setBlockName("Phosphorus")
				.disableStats()
				.setLightValue(1f)
				.setRequiresSelfNotify();
	
	public static Item phosphorus =
			new ItemPhosphorus(891).setIconCoord(14, 11).setItemName("Phosphorus");
	
	public static int lumTexID; // Luminance texture ID, for cavern second pass
	public static int phosphorusRenderID; // Render ID for phosphorus
	
	public String getVersion() {
		return "0.30(1)";
	}
	
	// Only insert in 1/20 chunks, and then only an average of columns four times per chunk
	// For each column, each ceiling has a 1/3 chance of getting a cavern block
	// Essentially, cavern blocks will fill most caves in cavern chunks
	public void GenerateSurface(World world, Random random, int px, int pz)
    {
		if (random.nextInt(20) != 0) return;
		// Propegation! Cavern blocks will be placed only on cave roofs, so we march vertically up the chains
		
		for (int i = 0; i < 16; ++i) {
			if (random.nextInt(8) != 0) continue;
			for (int k = 0; k < 16; ++k) {
				if (random.nextInt(8) != 0) continue;
				boolean didCreate = false;
				for (int j = 0; j < 140; ++j) {
					if ((world.getBlockId(px+i, j, pz+k) == 0) && world.getBlockId(px+i, j+1, pz+k) == Block.stone.blockID) {
						// We are directly below a cave roof
						if (random.nextInt(3) == 0) {
							world.setBlockAndMetadataWithNotify(px+i, j+1, pz+k, cavernBlock.blockID, 15);
							world.scheduleBlockUpdate(px+i,j+1,pz+k,cavernBlock.blockID,1); // Kickoff propegation
							System.out.print("Cavern Injected at ("+px+i+","+j+","+pz+k+")\n");
							didCreate = true;
						}
					}
				}
				// For debugging, create a glass pillar to mark caverns
				/*if (didCreate) {
					int height = world.getHeightValue(px+i, pz+k);
					for (int c = height; c < 120; ++c) {
						world.setBlock(px+i, c, pz+k, Block.glass.blockID);
					}
				}*/
			}
		}
    }
	
	public void load() {
		// Grab two render ID's
		cavernRenderID = ModLoader.getUniqueBlockModelID(this, false);
		phosphorusRenderID = ModLoader.getUniqueBlockModelID(this,false);
		
		ModLoader.RegisterBlock(cavernBlock);
		ModLoader.AddName(cavernBlock, "Phosphorus Rock");
		
		ModLoader.RegisterBlock(phosphorusBlock);
		ModLoader.AddName(phosphorusBlock, "Phosphorus");
		
		ModLoader.AddName(phosphorus, "Phosphorus");
		
		lumTexID = ModLoader.addOverride("/terrain.png", "/caverns/block.png");
		
		ModLoader.AddRecipe(new ItemStack(Block.torchWood,3), new Object[]{
	        "Y","X", Character.valueOf('X'), Item.stick, Character.valueOf('Y'), phosphorus
	        });
	}
	
	
	public static int countBiolumeInRegion(IBlockAccess iblockaccess, int i, int j, int k, int size) {
		int count = 0;
		
		int sx = i-size,
			sy = j-size,
			sz = k-size;
		
		size *= 2;
		for (int ci = 0; ci < size; ++ci)
			for (int cj = 0; cj < size; ++cj)
				for (int ck = 0; ck < size; ++ck) {
					int id = iblockaccess.getBlockId(ci+sx, cj+sy, ck+sz);
					
					count += ((id == cavernBlock.blockID) || (id == phosphorusBlock.blockID)) ? 1 : 0;
				}
		
		return count;
	}
	
	public static Minecraft mc = ModLoader.getMinecraftInstance();
	
	void DrawAuraBlock(RenderBlocks renderblocks, Block block, int i, int j, int k) {
		renderblocks.renderStandardBlockWithColorMultiplier(cavernBlock, i, j, k, 0.8f, 0.8f, 0.8f);
		float expand = 0.02f;
		cavernBlock.maxX += expand;
		cavernBlock.maxY += expand;
		cavernBlock.maxZ += expand;
		cavernBlock.minX -= expand;
		cavernBlock.minY -= expand;
		cavernBlock.minZ -= expand;
		int oldVal = Block.lightValue[block.blockID];
		Block.lightValue[block.blockID] = 8;
		renderblocks.overrideBlockTexture = lumTexID;
		renderblocks.renderStandardBlockWithColorMultiplier(cavernBlock, i, j, k, auraRed, auraGreen, auraBlue);
		renderblocks.overrideBlockTexture = -1;
		Block.lightValue[block.blockID] = oldVal;
	}
	
	void RenderTopCubes(RenderBlocks renderblocks, IBlockAccess iblockaccess, int i, int j, int k, Block block, int l) {
		boolean gridAlternate = ((i%2+j)%2+k)%2 == 0; // Checkboard time!
		
		if (gridAlternate) {
			cavernBlock.setBlockBounds(0.05f, -0.8f, 0.11f, 0.35f, 0.03f, 0.45f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j+1, k);
			cavernBlock.setBlockBounds(0.62f, -0.8f, 0.32f, 0.95f, 0.03f, 0.76f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j+1, k);
			cavernBlock.setBlockBounds(0.08f, -0.8f, 0.78f, 0.43f, 0.03f, 0.92f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j+1, k);
		} else {
			cavernBlock.setBlockBounds(0.05f, -0.8f, 0.54f, 0.62f, 0.03f, 0.74f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j+1, k);
			cavernBlock.setBlockBounds(0.45f, -0.8f, 0.08f, 0.74f, 0.03f, 0.45f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j+1, k);
		}
	}
	
	
	void RenderBottemCubes(RenderBlocks renderblocks, IBlockAccess iblockaccess, int i, int j, int k, Block block, int l) {
		// An alternate drawstyle if there is only one block in between
		if (iblockaccess.isAirBlock(i, j-1, k)) {
			int id = iblockaccess.getBlockId(i, j-2, k);
			if (id == cavernBlock.blockID) {
				// Stalactites, complete
				cavernBlock.setBlockBounds(0.05f, 0.0f, 0.11f, 0.15f, 1f, 0.21f);
				DrawAuraBlock(renderblocks,cavernBlock, i, j-1, k);
				cavernBlock.setBlockBounds(0.62f, 0.0f, 0.32f, 0.72f, 1f, 0.42f);
				DrawAuraBlock(renderblocks,cavernBlock, i, j-1, k);
				cavernBlock.setBlockBounds(0.08f, 0.0f, 0.78f, 0.18f, 1f, 0.88f);
				DrawAuraBlock(renderblocks,cavernBlock, i, j-1, k);
				return;
			}
		}
		
		boolean gridAlternate = ((i%2+j)%2+k)%2 == 0; // Checkboard time!
		
		if (gridAlternate) {
			// Stalactite, incomplete
			cavernBlock.setBlockBounds(0.05f, 0.6f, 0.11f, 0.15f, 1.8f, 0.31f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j-1, k);
			cavernBlock.setBlockBounds(0.62f, 0.4f, 0.32f, 0.73f, 1.8f, 0.42f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j-1, k);
			cavernBlock.setBlockBounds(0.08f, 0.2f, 0.78f, 0.18f, 1.8f, 0.88f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j-1, k);
		} else {
			cavernBlock.setBlockBounds(0.05f, 0.97f, 0.54f, 0.62f, 1.8f, 0.74f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j-1, k);
			cavernBlock.setBlockBounds(0.45f, 0.97f, 0.08f, 0.74f,1.8f, 0.45f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j-1, k);
		}
	}
	
	void RenderXCubes(RenderBlocks renderblocks, IBlockAccess iblockaccess, int i, int j, int k, Block block, int l) {
		boolean gridAlternate = ((i%2+j)%2+k)%2 == 0; // Checkboard time!
		
		if (gridAlternate) {
			cavernBlock.setBlockBounds(-0.05f, 0.05f, 0.11f, 1.05f, 0.35f, 0.45f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j, k);
			cavernBlock.setBlockBounds(-0.05f, 0.62f, 0.32f, 1.05f, 0.95f, 0.76f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j, k);
			cavernBlock.setBlockBounds(-0.05f, 0.08f, 0.78f, 1.05f, 0.43f, 0.92f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j, k);
		} else {
			cavernBlock.setBlockBounds(-0.05f, 0.05f, 0.54f, 1.05f, 0.62f, 0.74f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j, k);
			cavernBlock.setBlockBounds(-0.05f, 0.45f, 0.08f, 1.05f, 0.74f, 0.45f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j, k);
		}
	}
	
	void RenderZCubes(RenderBlocks renderblocks, IBlockAccess iblockaccess, int i, int j, int k, Block block, int l) {
		boolean gridAlternate = ((i%2+j)%2+k)%2 == 0; // Checkboard time!
		
		if (gridAlternate) {
			cavernBlock.setBlockBounds(0.11f, 0.05f,-0.05f, 0.45f, 0.35f, 1.05f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j, k);
			cavernBlock.setBlockBounds(0.32f, 0.62f,-0.05f, 0.76f, 0.95f, 1.05f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j, k);
			cavernBlock.setBlockBounds(0.78f, 0.08f,-0.05f, 0.92f, 0.43f, 1.05f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j, k);
		} else {
			cavernBlock.setBlockBounds(0.54f, 0.05f,-0.05f, 0.74f, 0.62f, 1.05f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j, k);
			cavernBlock.setBlockBounds(0.08f, 0.45f,-0.05f, 0.45f, 0.74f, 1.05f);
			DrawAuraBlock(renderblocks,cavernBlock, i, j, k);
		}
	}
	
	public static float auraRed = 0f, auraBlue = 0f, auraGreen = 0f;
	
	
	public boolean RenderWorldBlock(RenderBlocks renderblocks, IBlockAccess iblockaccess, int i, int j, int k, Block block, int l) {
		if (l == phosphorusRenderID) {
			// Firstly, figure out our boundry conditions
			boolean
				px = iblockaccess.isBlockNormalCube(i+1, j, k),
				py = iblockaccess.isBlockNormalCube(i, j+1, k),
				pz = iblockaccess.isBlockNormalCube(i, j, k+1),
				nx = iblockaccess.isBlockNormalCube(i-1, j, k),
				ny = iblockaccess.isBlockNormalCube(i, j-1, k),
				nz = iblockaccess.isBlockNormalCube(i, j, k-1);
			
			// Next, solve our color
			
			float max = iblockaccess.getBlockMetadata(i, j, k);
			auraRed = max/4f; auraBlue = max/13f; auraGreen = max/8f;
			auraRed += 0.1f;   auraBlue += 0.1f;  auraGreen += 0.3f;
			
			Tessellator t = Tessellator.instance;
			t.setBrightness(block.getMixedBrightnessForBlock(iblockaccess, i, j, k));
			
			int i1 = lumTexID;
			int j1 = (i1 & 0xf) << 4;
	        int k1 = i1 & 0xf0;
	        double d = (float)j1 / 256F;
	        double d2 = ((float)j1 + 15.99F) / 256F;
	        double d4 = (float)k1 / 256F;
	        double d6 = ((float)k1 + 15.99F) / 256F;
			
			float r = 1/16f;
			float ir = 1f-r;
			float u = r * 4;
			float v = r * 10;
			
			if(ny) {
				t.setColorOpaque_F(auraRed, auraBlue, auraGreen);
				t.addVertexWithUV(i, j+r, k,     d2, d6);
				t.addVertexWithUV(i, j+r, k+1,   d2, d4);
				t.addVertexWithUV(i+1, j+r, k+1, d, d4);
				t.addVertexWithUV(i+1, j+r, k,   d, d6);
			}
			
			if(nx) {
				t.setColorOpaque_F(auraRed, auraBlue, auraGreen);
				t.addVertexWithUV(i+r, j, k,     d2, d6);
				t.addVertexWithUV(i+r, j, k+1,   d2, d4);
				t.addVertexWithUV(i+r, j+1, k+1, d, d4);
				t.addVertexWithUV(i+r, j+1, k,   d, d6);
			}
			
			if(nz) {
				t.setColorOpaque_F(auraRed, auraBlue, auraGreen);
				t.addVertexWithUV(i, j, k+r,     d2, d6);
				t.addVertexWithUV(i+1, j, k+r,   d2, d4);
				t.addVertexWithUV(i+1, j+1, k+r, d, d4);
				t.addVertexWithUV(i, j+1, k+r,   d, d6);
			}
			
			if(pz) {
				t.setColorOpaque_F(auraRed, auraBlue, auraGreen);
				t.addVertexWithUV(i, j+1, k+ir,   d, d6);
				t.addVertexWithUV(i+1, j+1, k+ir, d, d4);
				t.addVertexWithUV(i+1, j, k+ir,   d2, d4);
				t.addVertexWithUV(i, j, k+ir,     d2, d6);
			}
			if(px) {
				t.setColorOpaque_F(auraRed, auraBlue, auraGreen);
				t.addVertexWithUV(i+ir, j+1, k,   d, d6);
				t.addVertexWithUV(i+ir, j+1, k+1, d, d4);
				t.addVertexWithUV(i+ir, j, k+1,   d2, d4);
				t.addVertexWithUV(i+ir, j, k,     d2, d6);
			}
			if(py) {
				t.setColorOpaque_F(auraRed, auraBlue, auraGreen);
				t.addVertexWithUV(i+1, j+ir, k,   d, d6);
				t.addVertexWithUV(i+1, j+ir, k+1, d, d4);
				t.addVertexWithUV(i, j+ir, k+1,   d2, d4);
				t.addVertexWithUV(i, j+ir, k,     d2, d6);
			}
			
			return true;
		}
		
		if (l == cavernRenderID) {
			int oldLight = Block.lightValue[cavernBlock.blockID];
			Block.lightValue[cavernBlock.blockID] = 0;
			//BlockWithAmbientOcclusion
			renderblocks.renderStandardBlockWithAmbientOcclusion(cavernBlock, i, j, k, 1, 1, 1);
			
			// Find the maximum brightness of a neighboring cell
			/*float 	fax = iblockaccess.getLightBrightness(i+1, j, k),
					fay = iblockaccess.getLightBrightness(i+1,j+1,k),
					faz = iblockaccess.getLightBrightness(i, j, k+1),
					fnx = iblockaccess.getLightBrightness(i-1, j, k),
					fny = iblockaccess.getLightBrightness(i ,j-1, k),
					fnz = iblockaccess.getLightBrightness(i, j, k-1);
			float max = fax;
			if (fay > max) max = fay;
			if (faz > max) max = faz;
			if (fnx > max) max = fnx;
			if (fny > max) max = fny;
			if (fnz > max) max = fnz;
			
			max = 1-max; // Invert
			max /= 8;*/
			float max = countBiolumeInRegion(iblockaccess,i,j,k,4);
			max /= 25f;
			max *= max;// * max;
			max *= 25f;
			//max *= dist; 
			//float max = iblockaccess.getBlockMetadata(i, j, k);
			//float red = max / 4f, green = max / 8f, blue = max / 16f;
			auraRed = max/13f; auraBlue = max/6f; auraGreen = max/4f;
			auraRed += 0.1f;   auraBlue += 0.1f;  auraGreen += 0.3f;
			
			//float rcp = iblockaccess.getBlockMetadata(i, j, k) * (1f/16f);
			//auraRed = rcp; auraBlue = rcp; auraGreen = rcp;
			
			boolean 
				nx = !iblockaccess.isBlockOpaqueCube(i - 1, j, k),
				ny = !iblockaccess.isBlockOpaqueCube(i, j - 1, k),
				nz = !iblockaccess.isBlockOpaqueCube(i, j, k - 1),
				ax = !iblockaccess.isBlockOpaqueCube(i + 1, j, k),
				ay = !iblockaccess.isBlockOpaqueCube(i, j + 1, k),
				az = !iblockaccess.isBlockOpaqueCube(i, j, k + 1);
			
			if (ay) RenderTopCubes	 (renderblocks, iblockaccess,  i,  j,  k,  block,  l);
			if (ny) RenderBottemCubes(renderblocks, iblockaccess,  i,  j,  k,  block,  l);
			if ((nx || ax) && !(ay || ny)) RenderXCubes(renderblocks,iblockaccess,  i,  j,  k,  block,  l);
			if ((nz || az) && !(ay || ny)) RenderZCubes(renderblocks,iblockaccess,  i,  j,  k,  block,  l);
			
			//BlockCavern.fixedBrightness = true;
			/*
			Block.lightValue[cavernBlock.blockID] = 15;
			
			float amt = 0.08f;// + MathHelper.sin((float)dist);// + (max / 10);
			
			float nScale = -amt;
			float pScale = 1 + amt;
			
			renderblocks.overrideBlockTexture = lumTexID;
			cavernBlock.setBlockBounds(
					nx ? nScale : 0, 
					ny ? nScale : 0, 
					nz ? nScale : 0, 
					ax ? pScale : 1, 
					ay ? pScale : 1, 
					az ? pScale : 1);
			//if (!(ay || ny)) renderblocks.renderStandardBlockWithColorMultiplier(cavernBlock, i, j, k, auraRed, auraBlue, auraGreen); */
			cavernBlock.setBlockBounds(0, 0, 0, 1, 1, 1);
			//renderblocks.overrideBlockTexture = -1; // Clear the override 
			//BlockCavern.fixedBrightness = false;
			Block.lightValue[cavernBlock.blockID] = oldLight; // The actual light value
			
			return false;
		}
		return false;
	}
}
