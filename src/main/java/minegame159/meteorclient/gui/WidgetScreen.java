package minegame159.meteorclient.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import minegame159.meteorclient.gui.renderer.GuiRenderer;
import minegame159.meteorclient.gui.widgets.Cell;
import minegame159.meteorclient.gui.widgets.WTextBox;
import minegame159.meteorclient.gui.widgets.WWidget;
import minegame159.meteorclient.rendering.Matrices;
import minegame159.meteorclient.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import org.lwjgl.glfw.GLFW;

public class WidgetScreen extends Screen {
    public final String title;
    protected final MinecraftClient mc;

    public Screen parent;
    public final WWidget root;
    private final int prePostKeyEvents;
    private boolean renderDebug = false;

    public boolean locked;
    private boolean firstInit = true;

    public WidgetScreen(String title) {
        super(new LiteralText(title));

        this.title = title;
        this.mc = MinecraftClient.getInstance();
        this.parent = mc.currentScreen;
        this.root = new WRoot();
        this.prePostKeyEvents = GuiThings.postKeyEvents;
    }

    @Override
    protected void init() {
        if (firstInit) {
            firstInit = false;
            return;
        }

        loopWidget(root);
    }

    private void loopWidget(WWidget widget) {
        if (widget instanceof WTextBox && ((WTextBox) widget).isFocused()) GuiThings.setPostKeyEvents(true);

        for (Cell<?> cell : widget.getCells()) {
            loopWidget(cell.getWidget());
        }
    }

    public <T extends WWidget> Cell<T> add(T widget) {
        return root.add(widget);
    }

    public void clear() {
        root.clear();
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (locked) return;

        double s = mc.getWindow().getScaleFactor();
        double scale = GuiConfig.INSTANCE.guiScale;

        mouseX *= s;
        mouseY *= s;
        mouseX /= scale;
        mouseY /= scale;

        root.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (locked) return false;

        return root.mouseClicked(button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (locked) return false;

        return root.mouseReleased(button);
    }

    @Override
    public boolean mouseScrolled(double d, double e, double amount) {
        if (locked) return false;

        return root.mouseScrolled(amount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (locked) return false;

        if (modifiers == GLFW.GLFW_MOD_CONTROL && keyCode == GLFW.GLFW_KEY_9) {
            renderDebug = !renderDebug;
            return true;
        }

        return root.keyPressed(keyCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void keyRepeated(int key, int mods) {
        if (locked) return;

        root.keyRepeated(key, mods);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (locked) return false;

        return root.charTyped(chr, keyCode);
    }

    @Override
    public void tick() {
        root.tick();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (!Utils.canUpdate()) renderBackground(matrices);

        double s = mc.getWindow().getScaleFactor();
        double scale = GuiConfig.INSTANCE.guiScale;

        mouseX *= s;
        mouseY *= s;
        mouseX /= scale;
        mouseY /= scale;

        Matrices.begin(new MatrixStack());
        GlStateManager.pushMatrix();
        GlStateManager.scaled(1 / s, 1 / s, 1);
        GlStateManager.scaled(scale, scale, 1);

        GuiRenderer.INSTANCE.begin();
        root.render(GuiRenderer.INSTANCE, mouseX, mouseY, delta);
        GuiRenderer.INSTANCE.end();

        if (renderDebug) GuiRenderer.INSTANCE.renderDebug(root);

        GlStateManager.popMatrix();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        root.invalidate();
    }

    @Override
    public void onClose() {
        if (locked) return;

        GuiThings.postKeyEvents = prePostKeyEvents;
        mc.openScreen(parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !locked;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class WRoot extends WWidget {
        @Override
        protected void onCalculateSize() {
            width = Utils.getScaledWindowWidthGui();
            height = Utils.getScaledWindowHeightGui();
        }
    }
}
