package com.truthsystems.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.truthsystems.TruthSystems;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTER = 
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TruthSystems.MODID);
}
