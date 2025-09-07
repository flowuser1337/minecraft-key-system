package dev.lvstrng.argon.gui; // Asegúrate de que el paquete sea correcto

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lvstrng.argon.managers.SecurityManager; // Asumiendo que SecurityManager está en este paquete
import dev.lvstrng.argon.utils.AuthService; // Asumiendo que AuthService está en este paquete
import net.minecraft.client.gui.DrawContext; // Importación nueva y corregida
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import javax.swing.*;

public class ActivationScreen extends Screen {

    private TextFieldWidget licenseKeyField;
    private String statusMessage = "";
    private boolean isError = false;

    public ActivationScreen() {
        super(Text.of("Flow Activation"));
    }

    @Override
    protected void init() {
        super.init();
        
        this.licenseKeyField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, this.height / 2 - 10, 200, 20, Text.of("Enter License Key"));
        this.addDrawableChild(this.licenseKeyField);

        ButtonWidget activateButton = ButtonWidget.builder(Text.of("Activate"), button -> {
            this.statusMessage = "Activating...";
            this.isError = false;
            new Thread(this::attemptActivation).start();
        }).dimensions(this.width / 2 - 100, this.height / 2 + 20, 200, 20).build();
        this.addDrawableChild(activateButton);
    }

    private void attemptActivation() {
        try {
            String licenseKey = this.licenseKeyField.getText();
            if (licenseKey.isBlank()) {
                this.statusMessage = "Please enter a key.";
                this.isError = true;
                return;
            }

            String hwid = AuthService.getHwid();
            String responseJson = AuthService.activateKey(licenseKey.trim(), hwid);
            JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

            if ("success".equals(response.get("status").getAsString())) {
                String username = response.get("username").getAsString();
                SecurityManager.saveConfig(username); // Guardamos la configuración
                // Mostramos un mensaje y pedimos reiniciar
                JOptionPane.showMessageDialog(null, "Flow activated for " + username + "!\nPlease restart your game.", "Success", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            } else {
                this.statusMessage = response.get("message").getAsString();
                this.isError = true;
            }

        } catch (Exception e) {
            this.statusMessage = "Error connecting to server.";
            this.isError = true;
            e.printStackTrace();
        }
    }
    
    // --- INICIO DE LA CORRECCIÓN ---
    // El método 'render' ahora usa 'DrawContext' en lugar de 'MatrixStack'
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // En lugar de this.renderBackground(matrices...), ahora es:
        this.renderBackground(context, mouseX, mouseY, delta);
        
        // Los métodos para dibujar texto ahora usan 'context'
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, Text.of(statusMessage), this.width / 2, this.height / 2 + 50, isError ? 0xFF5555 : 0xFFFFFF);
        
        super.render(context, mouseX, mouseY, delta);
    }
    // --- FIN DE LA CORRECCIÓN ---

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}