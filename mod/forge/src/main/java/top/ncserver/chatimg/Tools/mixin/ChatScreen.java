package top.ncserver.chatimg.Tools.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.CommandSuggestionHelper;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(net.minecraft.client.gui.screen.ChatScreen.class)
public abstract class ChatScreen extends Screen {
    @Shadow
    protected TextFieldWidget inputField;
    @Shadow
    private CommandSuggestionHelper commandSuggestionHelper;
    @Shadow
    private String defaultInputFieldText = "";
    /**
     * @author
     * @reason
     */
    public ChatScreen(String defaultText) {
        super(NarratorChatListener.EMPTY);
        this.defaultInputFieldText = defaultText;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public  void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.setListener(this.inputField);
        this.inputField.setFocused2(true);
        fill(matrixStack, 2, this.height - 14, this.width - 2, this.height - 2, this.minecraft.gameSettings.getChatBackgroundColor(Integer.MIN_VALUE));
        this.inputField.render(matrixStack, mouseX, mouseY, partialTicks);
        this.commandSuggestionHelper.drawSuggestionList(matrixStack, mouseX, mouseY);
        Style style = this.minecraft.ingameGUI.getChatGUI().func_238494_b_((double)mouseX, (double)mouseY);
        if (style != null && style.getHoverEvent() != null) {
            this.renderComponentHoverEffect(matrixStack, style, mouseX, mouseY);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
