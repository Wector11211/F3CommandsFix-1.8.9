package net.labymod.addons.Wector11211;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class DummyGui extends GuiScreen {
    public boolean opened = false;
    public void updateKeyBinds(){
        for(KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
            try {
                KeyBinding.setKeyBindState(keyBinding.getKeyCode(), keyBinding.getKeyCode() < 256 && Keyboard.isKeyDown(keyBinding.getKeyCode()));
            } catch (IndexOutOfBoundsException ignored) {
                // MC 1.12 ignores this exception, but it doesn't seem it can be thrown by anything in the call stack.
                // Anyway, better safe than sorry LULW
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        this.opened = true;
        Minecraft.getMinecraft().setIngameFocus();
        this.opened = false;
        updateKeyBinds();
    }

    @Override
    public void onGuiClosed() {
        updateKeyBinds();
    }

}
