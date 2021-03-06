package com.week1.game.Model.World;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.TowerBuilder.BlockType;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * World builder for a basic 4-player game.
 * 200x200, with plateaus in the middle.
 */
public class SmallWorldBuilder implements IWorldBuilder {

    public static SmallWorldBuilder ONLY = new SmallWorldBuilder();
    private Queue<Vector2> crystalLocs = new ConcurrentLinkedQueue<>();

    public SmallWorldBuilder() {
        crystalLocs.add(new Vector2(10, 5));
        crystalLocs.add(new Vector2(13, 5));
    }

    @Override
    public Block[][][] terrain() {
        Block[][][] blocks = new Block[40][30][15];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                blocks[i][j][0] = Block.TerrainBlock.STONE;
                for (int k = 1; k < blocks[0][0].length; k++) {
                    blocks[i][j][k] = Block.TerrainBlock.AIR;
                }
            }
        }
        makePlateau(blocks, 0, 5, 0, 5);


        blocks[30][15][1] = Block.TowerBlock.towerBlockMap.get(BlockType.EARTH);
        
        return blocks;
    }

    private void makePlateau(Block[][][] blocks, int startX, int endX, int startY, int endY) {
        for (int i = startX; i <= endX; i++) {
            for (int j = startY; j <= endY; j++) {
                blocks[i][j][1] = Block.TerrainBlock.DIRT;
            }
        }
    }

    @Override
    public Vector3[] startLocations(int numPlayers) {
        return new Vector3[] {
                new Vector3(10, 10, 1),
                new Vector3(35, 10, 1),
        };
    }

    @Override
    public Vector2 nextCrystalLocation() {
        Vector2 res = crystalLocs.remove();
        crystalLocs.add(res);
        return res;
    }

    @Override
    public int getNumCrystals() {
        return 2;
    }
    

    @Override
    public void addSeed(long mapSeed) {

    }
}
