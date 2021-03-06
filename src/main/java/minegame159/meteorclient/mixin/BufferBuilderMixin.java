package minegame159.meteorclient.mixin;

import minegame159.meteorclient.mixininterface.IBufferBuilder;
import net.minecraft.client.render.BufferBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BufferBuilder.class)
public class BufferBuilderMixin implements IBufferBuilder {
    @Shadow private boolean building;

    @Override
    public boolean isBuilding() {
        return building;
    }
}
