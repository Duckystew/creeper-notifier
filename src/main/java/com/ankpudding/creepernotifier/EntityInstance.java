package com.ankpudding.creepernotifier;

import net.minecraft.world.entity.Entity;

public class EntityInstance<T extends Entity> {
    T entity;
    Float distance;

    public EntityInstance(T e, Float d){
        entity = e;
        distance = d;
    }
}
