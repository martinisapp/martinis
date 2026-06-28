package com.chriswatnee.martinis.service;

import com.chriswatnee.martinis.dao.BlockDao;
import com.chriswatnee.martinis.dto.Block;
import com.chriswatnee.martinis.dto.Scene;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockServiceImplTest {

    @Mock
    private BlockDao blockDao;

    private BlockServiceImpl blockService;

    @BeforeEach
    void setUp() {
        blockService = new BlockServiceImpl(blockDao);
    }

    @Test
    void createDelegatesToDao() {
        Block block = new Block();
        block.setContent("Line 1");

        Block saved = new Block();
        saved.setId(1);
        saved.setContent("Line 1");

        when(blockDao.create(block)).thenReturn(saved);

        Block result = blockService.create(block);

        assertEquals(1, result.getId());
        assertEquals("Line 1", result.getContent());
        verify(blockDao).create(block);
    }

    @Test
    void createBelowDelegatesToDao() {
        Block block = new Block();
        block.setContent("Line Below");
        block.setOrder(2);

        Block saved = new Block();
        saved.setId(3);
        saved.setContent("Line Below");

        when(blockDao.createBelow(block)).thenReturn(saved);

        Block result = blockService.createBelow(block);

        assertEquals(3, result.getId());
        verify(blockDao).createBelow(block);
    }

    @Test
    void readDelegatesToDao() {
        Block block = new Block();
        block.setId(1);
        block.setContent("Test");

        when(blockDao.read(1)).thenReturn(block);

        Block result = blockService.read(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(blockDao).read(1);
    }

    @Test
    void readReturnsNullForNonExistentId() {
        when(blockDao.read(999)).thenReturn(null);

        assertNull(blockService.read(999));
        verify(blockDao).read(999);
    }

    @Test
    void updateDelegatesToDao() {
        Block block = new Block();
        block.setId(1);
        block.setContent("Updated");

        blockService.update(block);

        verify(blockDao).update(block);
    }

    @Test
    void deleteDelegatesToDao() {
        Block block = new Block();
        block.setId(1);

        blockService.delete(block);

        verify(blockDao).delete(block);
    }

    @Test
    void restoreDelegatesToDao() {
        Block block = new Block();
        block.setId(1);

        blockService.restore(block);

        verify(blockDao).restore(block);
    }

    @Test
    void moveUpDelegatesToDao() {
        Block block = new Block();
        block.setId(1);

        blockService.moveUp(block);

        verify(blockDao).moveUp(block);
    }

    @Test
    void moveDownDelegatesToDao() {
        Block block = new Block();
        block.setId(1);

        blockService.moveDown(block);

        verify(blockDao).moveDown(block);
    }

    @Test
    void reorderBlocksUpdatesOrderSequentially() {
        List<Integer> blockIds = Arrays.asList(3, 1, 2);

        blockService.reorderBlocks(blockIds);

        verify(blockDao).updateOrder(3, 1);
        verify(blockDao).updateOrder(1, 2);
        verify(blockDao).updateOrder(2, 3);
        verifyNoMoreInteractions(blockDao);
    }

    @Test
    void reorderBlocksWithEmptyList() {
        blockService.reorderBlocks(Collections.emptyList());

        verifyNoInteractions(blockDao);
    }

    @Test
    void reorderBlocksWithSingleElement() {
        blockService.reorderBlocks(List.of(5));

        verify(blockDao).updateOrder(5, 1);
        verifyNoMoreInteractions(blockDao);
    }

    @Test
    void listDelegatesToDao() {
        Block b1 = new Block();
        b1.setId(1);

        Block b2 = new Block();
        b2.setId(2);

        when(blockDao.list()).thenReturn(Arrays.asList(b1, b2));

        List<Block> result = blockService.list();

        assertEquals(2, result.size());
        verify(blockDao).list();
    }

    @Test
    void listReturnsEmptyList() {
        when(blockDao.list()).thenReturn(Collections.emptyList());

        assertTrue(blockService.list().isEmpty());
        verify(blockDao).list();
    }

    @Test
    void getBlocksBySceneDelegatesToDao() {
        Scene scene = new Scene();
        scene.setId(1);

        Block b1 = new Block();
        b1.setId(1);

        when(blockDao.getBlocksByScene(scene)).thenReturn(List.of(b1));

        List<Block> result = blockService.getBlocksByScene(scene);

        assertEquals(1, result.size());
        verify(blockDao).getBlocksByScene(scene);
    }
}
