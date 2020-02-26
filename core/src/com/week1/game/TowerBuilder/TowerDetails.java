package com.week1.game.TowerBuilder;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.TowerFootprint;

import java.util.List;

public class TowerDetails {
    private double hp = 0;
    private double atk = 0;
    private double range = 0;
    private int rawHp = 0;
    private int rawAtk = 0;
    private int armour = 1;
    private double price = 0;
    private Array<ModelInstance> model = new Array<>();
    private List<BlockSpec> layout;
//    private List<List<Integer>> footprint = new ArrayList<>();
    private TowerFootprint footprint;
    
    public List<BlockSpec> getLayout() {
        return layout;
    }

    public double getPrice() {
        return price;
    }

    public double getHp() {
        return hp;
    }

    public double getAtk() {
        return atk;
    }

    public double getRange() {
        return range;
    }

    public Array<ModelInstance> getModel() {
        return model;
    }

    //want to also generate dimensions, footprint, other multipliers
    //prog generated view

    public TowerDetails(List<BlockSpec> layout) {
        this.layout = layout;

        //generate model and stats
        populateFields();

    }
    
    /*
        Overloaded constructor to allow us to bypass automatic stat generation, if necessary for testing
     */
    public TowerDetails(TowerFootprint fp, double health, double price, double range, double damage) {
        this.footprint = fp;
        this.hp = health;
        this.price = price;
        this.range = range;
        this.atk = damage;
    }

    private void populateFields() {
        int base_blocks = 0;

        this.footprint = new TowerFootprint();

        for (BlockSpec block : layout) {
            int code = block.getBlockCode();
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            ModelInstance blockInstance = new ModelInstance(TowerMaterials.modelMap.get(code));
            blockInstance.transform.setToTranslation(x * 5f, y * 5f, z * 5f);
            this.model.add(blockInstance);
            this.footprint.setFootPrint(x + 2, z + 2, true);

            //Generate the tower stats
            rawHp += TowerMaterials.blockHp.get(code);
            rawAtk += TowerMaterials.blockAtk.get(code);
            range = Math.max(range, y + 1);
            price += TowerMaterials.blockPrice.get(code);
        }

        //We aren't calculating armour multipliers, etc
        atk = rawAtk * 0.05;
        hp = rawHp * armour;
        range = range * 3;

    }

    public TowerFootprint getFootprint() {
        return this.footprint;
    }
}