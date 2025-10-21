/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriswatnee.martinis.dao;

import com.chriswatnee.martinis.dto.Block;
import com.chriswatnee.martinis.dto.Scene;
import java.util.List;

/**
 *
 * @author chris
 */
public interface BlockDao {
    
    public Block create(Block block);
    public Block createBelow(Block block);
    public Block read(Integer id);
    public void update(Block block);
    public void delete(Block block);
    public void moveUp(Block block);
    public void moveDown(Block block);
    public void updateOrder(Integer blockId, Integer newOrder);
    public List<Block> list();
    public List<Block> getBlocksByScene(Scene scene);

}
