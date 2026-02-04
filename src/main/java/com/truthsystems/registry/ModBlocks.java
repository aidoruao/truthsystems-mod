package com.truthsystems.registry;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.truthsystems.TruthSystems;

public class ModBlocks {
    public static final DeferredRegister<Block> REGISTER = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, TruthSystems.MODID);
}
