package com.doc.tardisflightcore.registry;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModItemTags {
    public static final TagKey<Item> COMPONENTS = TagKey.of(RegistryKeys.ITEM, new Identifier("tardisflightcore", "components"));
}
