package net.morthen.resource_config_api.client.list;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

public abstract class Row extends ContainerObjectSelectionList.Entry<Row> {
    protected final Font font = Minecraft.getInstance().font;
}
