package com.week1.game.Model.World;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.week1.game.TowerBuilder.BlockType;

/**
 * Tiny world for testing.
 */
public class TinyWorldBuilder implements IWorldBuilder {

    public static TinyWorldBuilder ONLY = new TinyWorldBuilder();

    @Override
    public Block[][][] terrain() {
        Block[][][] blocks = new Block[20][20][5];
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[0].length; j++) {
                blocks[i][j][0] = Block.TerrainBlock.STONE;
                for (int k = 1; k < blocks[0][0].length; k++) {
                    blocks[i][j][k] = Block.TerrainBlock.AIR;
                }
            }
        }
        
        return blocks;
    }


    @Override
    public Vector3[] startLocations(int numPlayers) {
        return new Vector3[] {
                new Vector3(10, 10, 1),
        };
    }

    @Override
    public int getNumCrystals() {
        return 0;
    }
    
    @Override
    public Vector2 nextCrystalLocation() {
        return null;
    }
    
    @Override
    public void addSeed(long mapSeed) {

    }
}
