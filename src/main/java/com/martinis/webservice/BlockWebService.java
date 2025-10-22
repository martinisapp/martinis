/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martinis.webservice;

import com.martinis.commandmodel.block.createblock.CreateBlockCommandModel;
import com.martinis.commandmodel.block.createblockbelow.CreateBlockBelowCommandModel;
import com.martinis.commandmodel.block.editblock.EditBlockCommandModel;
import com.martinis.dto.Block;
import com.martinis.dto.Person;
import com.martinis.viewmodel.block.createblock.CreateBlockViewModel;
import com.martinis.viewmodel.block.createblockbelow.CreateBlockBelowViewModel;
import com.martinis.viewmodel.block.editblock.EditBlockViewModel;
import java.util.List;

/**
 *
 * @author chris
 */
public interface BlockWebService {

    public CreateBlockViewModel getCreateBlockViewModel(Integer sceneId);
    public CreateBlockBelowViewModel getCreateBlockBelowViewModel(Integer id);
    public EditBlockViewModel getEditBlockViewModel(Integer id);

    public Block saveCreateBlockCommandModel(CreateBlockCommandModel createBlockCommandModel);
    public Block saveCreateBlockBelowCommandModel(CreateBlockBelowCommandModel createBlockBelowCommandModel);
    public Block saveEditBlockCommandModel(EditBlockCommandModel editBlockCommandModel);

    public Block deleteBlock(Integer id);
    public void restoreBlock(Block block);
    public Block moveBlockUp(Integer id);
    public Block moveBlockDown(Integer id);
    public void reorderBlocks(List<Integer> blockIds);
    public void toggleBookmark(Integer id);

    public List<Person> getPersonsForScene(Integer sceneId);
    public Block getBlock(Integer id);

}
