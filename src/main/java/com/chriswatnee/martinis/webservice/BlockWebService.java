/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chriswatnee.martinis.webservice;

import com.chriswatnee.martinis.commandmodel.block.createblock.CreateBlockCommandModel;
import com.chriswatnee.martinis.commandmodel.block.createblockbelow.CreateBlockBelowCommandModel;
import com.chriswatnee.martinis.commandmodel.block.editblock.EditBlockCommandModel;
import com.chriswatnee.martinis.dto.Block;
import com.chriswatnee.martinis.viewmodel.block.createblock.CreateBlockViewModel;
import com.chriswatnee.martinis.viewmodel.block.createblockbelow.CreateBlockBelowViewModel;
import com.chriswatnee.martinis.viewmodel.block.editblock.EditBlockViewModel;

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
    public Block moveBlockUp(Integer id);
    public Block moveBlockDown(Integer id);
    
}
