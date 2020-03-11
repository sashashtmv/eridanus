package com.week1.game.TowerBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.week1.game.Model.TowerFootprint;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TowerDetails {
    private Array<ModelInstance> model = new Array<>();
    private List<BlockSpec> layout;
    private TowerFootprint footprint;
    private String name = "";


    //these stats are based off the raw blocks
    private int rawHeight = 0;
    private int rawHp = 0;
    private int rawAtk = 0;
    private double price = 0;

    //These stats are based off raw stats and multipliers
    private double hp = 0;
    private double atk = 0;
    private double range = 0;
    private int armour = 1;

    //For penalizing small bases
    private int baseSize = 0;

    private float BLOCKLENGTH = TowerMaterials.BLOCKLENGTH;

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

    private List<BlockSpec> parseLayoutString(String layout) {
        List<BlockSpec> blocks = new ArrayList<>();

        String blox = layout.substring(1, layout.length() - 1);
        boolean isFirst = true;
        for (String block: blox.split("\\(")) {
            if (isFirst) {
                isFirst = false;
                continue;
            }

            int i = 0;
            int x = 0,y = 0,z = 0,code = 0;
            for (String coord: block.split(", ")) {
                if (i == 0) {
                    x = Integer.parseInt(coord);
                } else if (i == 1) {
                    y = Integer.parseInt(coord);
                } else if (i == 2) {
                    z = Integer.parseInt(coord);
                } else if (i == 3) {
                    code = Integer.parseInt(coord.substring(0, 1));
                }
                i += 1;
            }

            blocks.add(new BlockSpec(BlockType.values()[code], x, y, z));
        }

        return blocks;
    }

    public TowerDetails(String filename) {

        //parse the name and block layout to use the other constructor
        try {
            List<BlockSpec> blocks = new ArrayList<>();

            File myObj = new File(filename);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                blocks = parseLayoutString(data);
            }
            myReader.close();

            String twrName = filename.substring(13, filename.length() - 11);
            layout = blocks;
            name = twrName;
            calcRawStats();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public TowerDetails(List<BlockSpec> layout, String name) {
        this.layout = layout;
        this.name = name;

        //generate model and stats
        calcRawStats();
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

    private void calcRawStats() {
        this.footprint = new TowerFootprint();

        for (int i = 0; i < layout.size(); i++) {
            BlockSpec block = layout.get(i);

            BlockType code = block.getBlockCode();
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            ModelInstance blockInstance = new ModelInstance(TowerMaterials.modelMap.get(code));
            blockInstance.transform.setToTranslation(x * BLOCKLENGTH, y * BLOCKLENGTH, z * BLOCKLENGTH);
            this.model.add(blockInstance);
            this.footprint.setFootPrint(x + 2, z + 2, true);

            //Generate the tower stats
            rawHp += TowerMaterials.blockHp.get(code);
            rawAtk += TowerMaterials.blockAtk.get(code);
            rawHeight = Integer.max(rawHeight, y + 1);
            price += TowerMaterials.blockPrice.get(code);


            //Getting the base size
            if (y == 0) {
                baseSize += 1;
            }
        }

        calcFinalStats();
    }

    private void calcFinalStats() {
        atk = rawAtk * 0.05;
        range = rawHeight * 3;

        //penalties up to basesize of 5
        hp = rawHp * Math.min(1, baseSize/5);

    }

    public TowerFootprint getFootprint() {
        return this.footprint;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void addBlock(BlockSpec bs) {
        //made it protected so that only the TowerBuilder classes are dealing with adding blocks to the
        //towers
        BlockType code = bs.getBlockCode();
        int x = bs.getX();
        int y = bs.getY();
        int z = bs.getZ();


        //add to the layout
        this.layout.add(bs);

        //add to the model
        ModelInstance blockInstance = new ModelInstance(TowerMaterials.modelMap.get(code));
        blockInstance.transform.setToTranslation(x * BLOCKLENGTH, y * BLOCKLENGTH, z * BLOCKLENGTH);
        this.model.add(blockInstance);
        this.footprint.setFootPrint(x + 2, z + 2, true);

        if (y == 0) {
            baseSize += 1;
        }

        //populate the fields
        this.rawAtk += TowerMaterials.blockAtk.get(code);
        this.rawHp += TowerMaterials.blockHp.get(code);
        rawHeight = Integer.max(rawHeight, y + 1);
//        range = rawHeight * 3;
        this.price += TowerMaterials.blockPrice.get(code);

        calcFinalStats();

    }

    private boolean checkRemovalSafety(int blockIdx) {

        boolean blockAtGroundLevel = false;

        List<BlockSpec> updatedBlocks = new ArrayList<>();
        for (int i = 0; i < layout.size(); i++) {
            if (i != blockIdx) {
                updatedBlocks.add(layout.get(i));
                if (layout.get(i).getY() == 0) {
                    blockAtGroundLevel = true;
                }
            }
        }

        if (!blockAtGroundLevel) {
            //disconnected from ground
            return false;
        }

        List<BlockSpec> q = new ArrayList<>();
        q.add(updatedBlocks.get(0));

        List<BlockSpec> seen = new ArrayList<>();
        while (q.size() != 0) {
            BlockSpec cur = q.get(0);
            q.remove(0);
            seen.add(cur);

            List<BlockSpec> nbrs = getNbrs(cur, updatedBlocks);
            for (int i = 0; i < nbrs.size(); i++) {
                if (!seen.contains(nbrs.get(i)) & !q.contains(nbrs.get(i))) {
                    q.add(nbrs.get(i));
                }
            }

        }

        if (seen.size() != updatedBlocks.size()) {
            Gdx.app.log("Unable to remove: TowerDetails expected " + updatedBlocks.size() + " blocks but only saw " + seen.size() + " blocks", "skv2");
            return false;
        }


        return true;
    }

    private List<BlockSpec> getNbrs(BlockSpec b, List<BlockSpec> blocks) {
        List<BlockSpec> nbrs = new ArrayList<>();
        int x = b.getX();
        int y = b.getY();
        int z = b.getZ();

        for (int i = 0; i < blocks.size(); i++) {
            BlockSpec blk = blocks.get(i);
            int curX = blk.getX();
            int curY = blk.getY();
            int curZ = blk.getZ();

            if (curX == x) {
                if (y == curY) {
                    if (Math.abs(z - curZ) == 1) {
                        nbrs.add(blk);
                    }
                } else if (z == curZ) {
                    if (Math.abs((y - curY)) == 1) {
                        nbrs.add(blk);
                    }
                }
            } else if (y == curY && z == curZ) {
                if (Math.abs(x - curX) == 1) {
                    nbrs.add(blk);
                }
            }

        }

        return nbrs;
    }

    protected boolean removeBlock(int x, int y, int z) {
        BlockType code = null;

        int modelIdx = -1;
        int blockIdx = -1;
        boolean shrinkFootprint = true;
        int newHt = 0;

        for (int i = 0; i < layout.size(); i++) {
            if (code == null) {
                BlockSpec b = layout.get(i);
                if (x == b.getX() & y == b.getY() & z == b.getZ()) {
                    code = b.getBlockCode();
                    blockIdx = i;
                }
            }

            ModelInstance m = model.get(i);
            Vector3 translation = new Vector3();
            m.transform.getTranslation(translation);


            if (translation.x == BLOCKLENGTH * x & translation.y == BLOCKLENGTH * y & translation.z == BLOCKLENGTH * z) {
                modelIdx = i;
            } else {
                newHt = Integer.max(newHt, ((int)translation.y/5) + 1);

            }

            if (translation.x == x & translation.z == z) {
                shrinkFootprint = false;
            }

        }

        if (!checkRemovalSafety(blockIdx)) {
            return false;
        }
        //remove from model and layout
        layout.remove(blockIdx);
        this.model.removeIndex(modelIdx);


        //update the fields
        this.rawAtk -= TowerMaterials.blockAtk.get(code);
        this.rawHp -= TowerMaterials.blockHp.get(code);
        this.price -= TowerMaterials.blockPrice.get(code);

        //updating range and footprint
        if (shrinkFootprint) {
            baseSize -= 1;
            this.footprint.setFootPrint(x + 2, z + 2, false);
        }

        rawHeight = newHt;

        calcFinalStats();
        return true;

    }

    private String getLayoutStr() {
        String towerStr = "[";
        for (int i = 0; i < layout.size(); i++) {
            BlockSpec b = layout.get(i);
            towerStr += b.toFileStr();
            towerStr += ", ";
        }
        towerStr = towerStr.substring(0, towerStr.length() - 2);
        towerStr += "]";

        return  towerStr;

    }

    public void saveTower() {
        //write tower layout to a file
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("customTowers/" +name +"_layout.txt"), "utf-8"))) {
            writer.write(getLayoutStr());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
