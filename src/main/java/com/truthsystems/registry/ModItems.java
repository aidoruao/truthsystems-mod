package com.truthsystems.registry;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.truthsystems.TruthSystems;

public class ModItems {
    public static final DeferredRegister<Item> REGISTER = 
        DeferredRegister.create(ForgeRegistries.ITEMS, TruthSystems.MODID);
}
