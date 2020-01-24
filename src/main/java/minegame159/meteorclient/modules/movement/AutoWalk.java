package minegame159.meteorclient.modules.movement;

import baritone.api.BaritoneAPI;
import minegame159.jes.SubscribeEvent;
import minegame159.meteorclient.events.TickEvent;
import minegame159.meteorclient.modules.Category;
import minegame159.meteorclient.modules.Module;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.builders.EnumSettingBuilder;
import minegame159.meteorclient.utils.GoalDirection;
import minegame159.meteorclient.utils.KeyBindings;

public class AutoWalk extends Module {
    public enum Mode {
        Simple,
        Smart
    }

    private Setting<Mode> mode = addSetting(new EnumSettingBuilder<Mode>()
            .name("mode")
            .description("Walking mode.")
            .defaultValue(Mode.Smart)
            .build()
    );

    private int timer = 0;
    private GoalDirection goal;

    public AutoWalk() {
        super(Category.Movement, "auto-walk", "Automatically walks forward.");
    }

    @Override
    public void onActivate() {
        if (mode.value() == Mode.Smart) {
            timer = 0;
            goal = new GoalDirection(mc.player.getPos(), mc.player.yaw);
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
        }
    }

    @Override
    public void onDeactivate() {
        if (mode.value() == Mode.Simple) KeyBindings.forward.setPressed(false);
        else BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
    }

    @SubscribeEvent
    private void onTick(TickEvent e) {
        if (mode.value() == Mode.Simple) {
            KeyBindings.forward.setPressed(true);
        } else {
            if (timer > 20) {
                timer = 0;
                goal.recalculate(mc.player.getPos());
            }

            timer++;
        }
    }
}