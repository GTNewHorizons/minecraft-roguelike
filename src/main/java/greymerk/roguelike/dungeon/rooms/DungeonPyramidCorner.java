package greymerk.roguelike.dungeon.rooms;

import java.util.Random;

import greymerk.roguelike.dungeon.base.DungeonBase;
import greymerk.roguelike.dungeon.settings.LevelSettings;
import greymerk.roguelike.theme.ITheme;
import greymerk.roguelike.worldgen.Cardinal;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IBlockFactory;
import greymerk.roguelike.worldgen.MetaBlock;
import greymerk.roguelike.worldgen.IWorldEditor;
import greymerk.roguelike.worldgen.blocks.BlockType;

public class DungeonPyramidCorner extends DungeonBase {

	@Override
	public boolean generate(IWorldEditor editor, Random rand, LevelSettings settings, Cardinal[] entrances, Coord origin) {
		
		int x = origin.getX();
		int y = origin.getY();
		int z = origin.getZ();
		ITheme theme = settings.getTheme();
		
		IBlockFactory blocks = theme.getPrimaryWall();
		IBlockFactory pillar = theme.getPrimaryPillar();
		MetaBlock air = BlockType.get(BlockType.AIR);
		
		editor.fillRectHollow(rand, new Coord(x - 3, y - 1, z - 3), new Coord(x + 3, y + 3, z + 3), blocks, false, true);
		editor.fillRectSolid(rand, new Coord(x - 2, y + 3, z - 2), new Coord(x + 2, y + 5, z + 2), blocks, false, true);
		editor.fillRectSolid(rand, new Coord(x - 1, y + 3, z - 1), new Coord(x + 1, y + 3, z + 1), air, true, true);
		
		// floor
		editor.fillRectSolid(rand, new Coord(x - 3, y - 1, z - 3), new Coord(x + 3, y - 1, z + 3), theme.getPrimaryFloor(), false, true);
		
		Coord start;
		Coord end;
		Coord cursor;
		
		cursor = new Coord(x, y, z);
		cursor.add(Cardinal.UP, 4);
		air.setBlock(editor, cursor);
		cursor.add(Cardinal.UP, 1);
		air.setBlock(editor, cursor);
		
		
		
		for(Cardinal dir : Cardinal.directions){
			
			cursor = new Coord(x, y, z);
			cursor.add(Cardinal.UP, 4);
			cursor.add(dir);
			air.setBlock(editor, rand, cursor);
			
			cursor = new Coord(x, y, z);
			cursor.add(dir, 2);
			cursor.add(Cardinal.getOrthogonal(dir)[0], 2);
			start = new Coord(cursor);
			cursor.add(Cardinal.UP, 2);
			end = new Coord(cursor);
			editor.fillRectSolid(rand, start, end, pillar, true, true);
		}
		
		return true;
	}
	
	public int getSize(){
		return 4;
	}

}
