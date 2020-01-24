package minegame159.meteorclient.modules;

import minegame159.meteorclient.MeteorClient;
import minegame159.meteorclient.events.EventStore;
import minegame159.meteorclient.modules.combat.Criticals;
import minegame159.meteorclient.modules.misc.AutoSign;
import minegame159.meteorclient.modules.misc.LongerChat;
import minegame159.meteorclient.modules.movement.*;
import minegame159.meteorclient.modules.player.AutoFish;
import minegame159.meteorclient.modules.player.DeathPosition;
import minegame159.meteorclient.modules.player.FastUse;
import minegame159.meteorclient.modules.render.*;
import minegame159.meteorclient.utils.Utils;

import java.util.*;

public class ModuleManager {
    private static List<Module> modules = new ArrayList<>();
    private static Map<Category, List<Module>> groups = new HashMap<>();

    private static List<Module> active = new ArrayList<>();

    public static Module moduleToBind;

    public static void init() {
        initCombat();
        initPlayer();
        initMovement();
        initRender();
        initMisc();

        System.out.println("Meteor Client loaded " + modules.size() + " modules.");
    }

    public static List<Module> getGroup(Category category) {
        return groups.computeIfAbsent(category, k -> new ArrayList<>());
    }

    public static Set<Category> getCategories() {
        return groups.keySet();
    }

    public static Module get(String name) {
        name = name.toLowerCase();
        for (Module module : modules) {
            if (module.name.equals(name)) return module;
        }

        return null;
    }

    public static List<Module> getActive() {
        return active;
    }

    public static List<Module> getAll() {
        return modules;
    }

    public static void deactivateAll() {
        List<Module> active2 = new ArrayList<>(active);
        for (Module module : active2) module.toggle();
    }

    public static boolean onKeyPress(int key) {
        // Check if binding module
        if (moduleToBind != null) {
            moduleToBind.key = key;
            moduleToBind = null;
            Utils.sendMessage("#yellowModule bound.");
            return true;
        }

        // Find module bound to that key
        for (Module module : modules) {
            if (module.key == key) {
                module.toggle();
                return true;
            }
        }

        return false;
    }

    static void addActive(Module module) {
        active.add(module);
        MeteorClient.eventBus.post(EventStore.activeModulesChangedEvent());
    }
    static void removeActive(Module module) {
        active.remove(module);
        MeteorClient.eventBus.post(EventStore.activeModulesChangedEvent());
    }

    private static void addModule(Module module) {
        modules.add(module);
        getGroup(module.category).add(module);
    }

    private static void initCombat() {
        addModule(new Criticals());
    }

    private static void initPlayer() {
        addModule(new AutoFish());
        addModule(new DeathPosition());
        addModule(new FastUse());
    }

    private static void initMovement() {
        addModule(new AutoSprint());
        addModule(new AutoWalk());
        addModule(new Blink());
        addModule(new FastLadder());
        addModule(new NoFall());
        addModule(new Spider());
        addModule(new AutoJump());
        addModule(new Flight());
    }

    private static void initRender() {
        addModule(new ActiveModules());
        addModule(new FullBright());
        addModule(new Info());
        addModule(new Position());
        addModule(new StorageESP());
        addModule(new XRay());
        addModule(new AntiFog());
        addModule(new NoHurtCam());
        addModule(new ESP());
        addModule(new Freecam());
        addModule(new Tracers());
    }

    private static void initMisc() {
        addModule(new LongerChat());
        addModule(new AutoSign());
    }
}