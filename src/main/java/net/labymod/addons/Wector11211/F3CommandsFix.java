package net.labymod.addons.Wector11211;

import net.labymod.api.LabyModAddon;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;
import java.util.List;

public class F3CommandsFix extends LabyModAddon {
    private boolean addonEnabled;

    @Override
    public void onEnable() {
        getApi().registerForgeListener(this);
    }

    @Override
    public void loadConfig() {
        this.addonEnabled = getConfig().has( "enabled" ) ? getConfig().get("enabled").getAsBoolean() : true;
    }

    @Override
    protected void fillSettings(List<SettingsElement> options) {

        BooleanElement addonEnabledElement = new BooleanElement(
                "Enabled",
                this,
                new ControlElement.IconData(Material.LEVER),
                "enabled", this.addonEnabled);

        options.add( addonEnabledElement );
    }


    HashMap keyFlags = new HashMap();
    boolean onKeyPress(int keyCode, Runnable callback){
        if(Keyboard.isKeyDown(keyCode)){
            if(!(boolean)keyFlags.get(keyCode)) callback.run();
            keyFlags.put(keyCode, true);
        } else {
            keyFlags.put(keyCode, false);
        }
        return (boolean)keyFlags.get(keyCode);
    }

    private void debugFeedbackTranslated(String untranslatedTemplate, Object... objs){
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(
                (new ChatComponentText("")).appendSibling((new ChatComponentTranslation("debug.prefix", new Object[0]))
                        .setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.YELLOW)
                        .setBold(Boolean.valueOf(true)))).appendText(" ")
                        .appendSibling(new ChatComponentTranslation(untranslatedTemplate, objs)));
    }

    private long debugCrashKeyPressTime = -1L;
    private boolean f3Flag = false;
    private boolean modKeyPressed = false;
    private DummyGui dummyGui = new DummyGui();

    @SubscribeEvent
    public void onKeyInput(TickEvent.ClientTickEvent e) throws IllegalAccessException {
        if(this.addonEnabled){
            if(Minecraft.getMinecraft().currentScreen == null || Minecraft.getMinecraft().currentScreen == dummyGui) {
                if (Keyboard.isKeyDown(61)) {
                    if (!f3Flag) {
                        Minecraft.getMinecraft().gameSettings.showDebugInfo ^= true; // build NEEDS that
                        Minecraft.getMinecraft().displayGuiScreen(dummyGui);
                    }
                    f3Flag = true;
                    modKeyPressed |= onKeyPress(16, () -> {
                        System.out.println("F3+Q - show all commands");
                    }) || onKeyPress(20, () -> {
                        System.out.println("F3+T - textures reload");
                        Minecraft.getMinecraft().refreshResources();
                    }) || onKeyPress(25, () -> {
                        System.out.println("F3+P - pause");
                        Minecraft.getMinecraft().gameSettings.pauseOnLostFocus ^= true;
                        Minecraft.getMinecraft().gameSettings.saveOptions();
                    }) || onKeyPress(30, () -> {
                        System.out.println("F3+A - chunk reloading");
                        Minecraft.getMinecraft().renderGlobal.loadRenderers();
                    }) || onKeyPress(31, () -> {
                        System.out.println("F3+S - textures reload");
                        Minecraft.getMinecraft().refreshResources();
                    }) || onKeyPress(32, () -> {
                        System.out.println("F3+D - clear chat");
                        Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages();
                    }) || onKeyPress(33, () -> {
                        System.out.println("F3+F - change render distance");
                        Minecraft.getMinecraft().gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
                    }) || onKeyPress(35, () -> {
                        System.out.println("F3+H - show tooltips");
                        Minecraft.getMinecraft().gameSettings.advancedItemTooltips ^= true;
                        Minecraft.getMinecraft().gameSettings.saveOptions();
                    }) || onKeyPress(48, () -> {
                        System.out.println("F3+B - show hitboxes");
                        Minecraft.getMinecraft().getRenderManager().setDebugBoundingBox(!Minecraft.getMinecraft().getRenderManager().isDebugBoundingBox());
                    });

                } else {
                    if (f3Flag) {
                        Minecraft.getMinecraft().displayGuiScreen(null);
                        Minecraft.getMinecraft().gameSettings.showDebugInfo ^= true && !modKeyPressed;
                        Minecraft.getMinecraft().gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                        Minecraft.getMinecraft().gameSettings.showLagometer = Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
                    }
                    f3Flag = false;
                    modKeyPressed = false;
                }
            }
        }
    }

    @SubscribeEvent
    public void guiOpening(GuiOpenEvent event) {
        if (dummyGui.opened)
            event.setCanceled(true);
    }
}