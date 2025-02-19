package greymerk.roguelike.citadel;

import java.util.List;
import java.util.Random;

import greymerk.roguelike.dungeon.towers.ITower;
import greymerk.roguelike.dungeon.towers.Tower;
import greymerk.roguelike.theme.ITheme;
import greymerk.roguelike.theme.Theme;
import greymerk.roguelike.util.mst.Edge;
import greymerk.roguelike.util.mst.MinimumSpanningTree;
import greymerk.roguelike.worldgen.Cardinal;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.IWorldEditor;
import greymerk.roguelike.worldgen.blocks.BlockType;
import greymerk.roguelike.worldgen.shapes.RectSolid;

public class CityGrounds {

    public static void generate(IWorldEditor editor, Random rand, MinimumSpanningTree mst, ITheme theme, Coord pos) {

        Coord start;
        Coord end;

        start = new Coord(pos);
        start.add(new Coord(Citadel.EDGE_LENGTH * -3, 10, Citadel.EDGE_LENGTH * -3));
        end = new Coord(pos);
        end.add(new Coord(Citadel.EDGE_LENGTH * 3, 40, Citadel.EDGE_LENGTH * 3));
        RectSolid.fill(editor, rand, start, end, BlockType.get(BlockType.AIR), true, true);

        start = new Coord(pos);
        start.add(new Coord(Citadel.EDGE_LENGTH * -3, 10, Citadel.EDGE_LENGTH * -3));
        end = new Coord(pos);
        end.add(new Coord(Citadel.EDGE_LENGTH * 3, 20, Citadel.EDGE_LENGTH * 3));
        RectSolid.fill(editor, rand, start, end, theme.getPrimaryWall(), true, true);

        start = new Coord(pos);
        start.add(new Coord(Citadel.EDGE_LENGTH * -2, 20, Citadel.EDGE_LENGTH * -2));
        end = new Coord(pos);
        end.add(new Coord(Citadel.EDGE_LENGTH * 2, 30, Citadel.EDGE_LENGTH * 2));
        RectSolid.fill(editor, rand, start, end, theme.getPrimaryWall(), true, true);

        start = new Coord(pos);
        start.add(new Coord(Citadel.EDGE_LENGTH * -1, 30, Citadel.EDGE_LENGTH * -1));
        end = new Coord(pos);
        end.add(new Coord(Citadel.EDGE_LENGTH, 40, Citadel.EDGE_LENGTH));
        RectSolid.fill(editor, rand, start, end, theme.getPrimaryWall(), true, true);

        Coord cursor = new Coord(pos);
        cursor.add(Cardinal.UP, 20);

        for (Edge e : mst.getEdges()) {
            start = e.getPoints()[0].getPosition();
            start.add(cursor);
            end = e.getPoints()[1].getPosition();
            end.add(cursor);
            end.add(Cardinal.DOWN, 20);
            RectSolid.fill(editor, rand, start, end, theme.getPrimaryWall(), true, true);
        }

        List<Coord> towers = mst.getPointPositions();
        for (Coord c : towers) {
            c.add(pos);
            rand = Citadel.getRandom(editor, c.getX(), c.getZ());
            ITower tower = Tower.get(Tower.values()[rand.nextInt(Tower.values().length)]);
            tower.generate(
                    editor,
                    rand,
                    Theme.getTheme(Theme.values()[rand.nextInt(Theme.values().length)]),
                    new Coord(c.getX(), 50, c.getZ()));
        }

    }

}
