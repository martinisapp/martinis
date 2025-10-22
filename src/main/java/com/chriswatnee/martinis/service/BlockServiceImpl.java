/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriswatnee.martinis.service;

import com.chriswatnee.martinis.dao.BlockDao;
import com.chriswatnee.martinis.dto.Block;
import com.chriswatnee.martinis.dto.Scene;
import java.util.List;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;

/**
 *
 * @author chris
 */
@Service
public class BlockServiceImpl implements BlockService {

    BlockDao blockDao;

    @Inject
    public BlockServiceImpl(BlockDao blockDao) {
        this.blockDao = blockDao;
    }

    @Override
    public Block create(Block block) {
        return blockDao.create(block);
    }

    @Override
    public Block createBelow(Block block) {
        return blockDao.createBelow(block);
    }

    @Override
    public Block read(Integer id) {
        return blockDao.read(id);
    }

    @Override
    public void update(Block block) {
        blockDao.update(block);
    }

    @Override
    public void delete(Block block) {
        blockDao.delete(block);
    }

    @Override
    public void restore(Block block) {
        blockDao.restore(block);
    }

    @Override
    public void moveUp(Block block) {
        blockDao.moveUp(block);
    }

    @Override
    public void moveDown(Block block) {
        blockDao.moveDown(block);
    }

    @Override
    public void reorderBlocks(List<Integer> blockIds) {
        // Update the order of each block based on its position in the list
        for (int i = 0; i < blockIds.size(); i++) {
            Integer blockId = blockIds.get(i);
            Integer newOrder = i + 1; // Order starts at 1
            blockDao.updateOrder(blockId, newOrder);
        }
    }

    @Override
    public List<Block> list() {
        return blockDao.list();
    }

    @Override
    public List<Block> getBlocksByScene(Scene scene) {
        return blockDao.getBlocksByScene(scene);
    }
    
}
