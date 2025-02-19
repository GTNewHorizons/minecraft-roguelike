package greymerk.roguelike.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class BlockColumns extends BlockBase {

    private List<IBlockFactory> blocks;

    public BlockColumns() {
        blocks = new ArrayList<IBlockFactory>();
    }

    public BlockColumns(JsonElement data) {
        this();
        for (JsonElement entry : (JsonArray) data) {
            this.addBlock(BlockProvider.create(entry.getAsJsonObject()));
        }
    }

    public void addBlock(IBlockFactory toAdd) {
        blocks.add(toAdd);
    }

    @Override
    public boolean set(IWorldEditor editor, Random rand, Coord pos, boolean fillAir, boolean replaceSolid) {
        int size = blocks.size();
        int choice = Math.abs((pos.getX() % size + pos.getZ() % size)) % size;
        IBlockFactory block = blocks.get(choice);
        return block.set(editor, rand, pos, fillAir, replaceSolid);
    }

}
