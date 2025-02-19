package greymerk.roguelike.worldgen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import greymerk.roguelike.treasure.ITreasureChest;
import greymerk.roguelike.treasure.TreasureManager;
import greymerk.roguelike.worldgen.shapes.RectSolid;

public class WorldEditor implements IWorldEditor {

    World world;
    private Map<Block, Integer> stats;
    private TreasureManager chests;

    public WorldEditor(World world) {
        this.world = world;
        stats = new HashMap<Block, Integer>();
        this.chests = new TreasureManager();
    }

    public boolean setBlock(Coord pos, MetaBlock block, boolean fillAir, boolean replaceSolid) {

        MetaBlock currentBlock = getBlock(pos);

        if (currentBlock.getBlock() == Blocks.chest) return false;
        if (currentBlock.getBlock() == Blocks.trapped_chest) return false;
        if (currentBlock.getBlock() == Blocks.mob_spawner) return false;

        boolean isAir = world.isAirBlock(pos.getX(), pos.getY(), pos.getZ());

        if (!fillAir && isAir) return false;
        if (!replaceSolid && !isAir) return false;

        try {
            world.setBlock(pos.getX(), pos.getY(), pos.getZ(), block.getBlock(), block.getMeta(), block.getFlag());
        } catch (NullPointerException npe) {
            // ignore it.
        }

        Block type = block.getBlock();
        Integer count = stats.get(type);
        if (count == null) {
            stats.put(type, 1);
        } else {
            stats.put(type, count + 1);
        }

        return true;

    }

    @Override
    public boolean isAirBlock(Coord pos) {
        return world.isAirBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public long getSeed() {
        return this.world.getSeed();
    }

    @Override
    public BiomeGenBase getBiome(Coord pos) {
        return world.getBiomeGenForCoords(pos.getX(), pos.getZ());
    }

    @Override
    public int getDimension() {
        return world.provider.dimensionId;
    }

    @Override
    public Random getSeededRandom(int a, int b, int c) {
        return world.setRandomSeed(a, b, c);
    }

    @Override
    public void spiralStairStep(Random rand, Coord origin, IStair stair, IBlockFactory fill) {

        MetaBlock air = new MetaBlock(Blocks.air);
        Coord cursor;
        Coord start;
        Coord end;

        start = new Coord(origin);
        start.add(new Coord(-1, 0, -1));
        end = new Coord(origin);
        end.add(new Coord(1, 0, 1));

        RectSolid.fill(this, rand, start, end, air);
        fill.set(this, rand, origin);

        Cardinal dir = Cardinal.directions[origin.getY() % 4];
        cursor = new Coord(origin);
        cursor.add(dir);
        stair.setOrientation(Cardinal.left(dir), false).set(this, cursor);
        cursor.add(Cardinal.right(dir));
        stair.setOrientation(Cardinal.right(dir), true).set(this, cursor);
        cursor.add(Cardinal.reverse(dir));
        stair.setOrientation(Cardinal.reverse(dir), true).set(this, cursor);
    }

    @Override
    public void fillDown(Random rand, Coord origin, IBlockFactory blocks) {

        Coord cursor = new Coord(origin);

        while (!getBlock(cursor).getBlock().isOpaqueCube() && cursor.getY() > 1) {
            blocks.set(this, rand, cursor);
            cursor.add(Cardinal.DOWN);
        }
    }

    @Override
    public MetaBlock getBlock(Coord pos) {
        return new MetaBlock(world.getBlock(pos.getX(), pos.getY(), pos.getZ()));
    }

    @Override
    public TileEntity getTileEntity(Coord pos) {
        return world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public boolean validGroundBlock(Coord pos) {

        if (isAirBlock(pos)) return false;

        MetaBlock block = this.getBlock(pos);

        if (block.getMaterial() == Material.wood) return false;
        if (block.getMaterial() == Material.water) return false;
        if (block.getMaterial() == Material.cactus) return false;
        if (block.getMaterial() == Material.snow) return false;
        if (block.getMaterial() == Material.grass) return false;
        if (block.getMaterial() == Material.gourd) return false;
        if (block.getMaterial() == Material.leaves) return false;
        if (block.getMaterial() == Material.plants) return false;

        return true;
    }

    @Override
    public String toString() {
        String toReturn = "";

        for (Map.Entry<Block, Integer> pair : stats.entrySet()) {
            toReturn += pair.getKey().getLocalizedName() + ": " + pair.getValue() + "\n";
        }

        return toReturn;
    }

    @Override
    public int getStat(Block type) {
        if (!this.stats.containsKey(type)) return 0;
        return this.stats.get(type);
    }

    @Override
    public void addChest(ITreasureChest toAdd) {
        this.chests.add(toAdd);
    }

    @Override
    public TreasureManager getTreasure() {
        return this.chests;
    }

    @Override
    public boolean canPlace(MetaBlock block, Coord pos, Cardinal dir) {
        if (!this.isAirBlock(pos)) return false;
        Coord cursor = new Coord(pos);
        cursor.add(dir);
        Material m = this.getBlock(cursor).getMaterial();
        return !m.isReplaceable();
    }

    @Override
    public void setBlockMetadata(Coord pos, int meta) {
        world.setBlockMetadataWithNotify(pos.getX(), pos.getY(), pos.getZ(), meta, 2);
    }
}
