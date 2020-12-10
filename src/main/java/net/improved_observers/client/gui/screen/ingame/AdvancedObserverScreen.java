package net.improved_observers.client.gui.screen.ingame;

import net.improved_observers.block.entity.AdvancedObserverBlockEntity;
import net.improved_observers.network.packet.c2s.play.UpdateAdvancedObserverC2SPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class AdvancedObserverScreen extends Screen {
    private final Text NORTH_BUTTON_TEXT = new TranslatableText("advanced_observer.north");
    private final Text SOUTH_BUTTON_TEXT = new TranslatableText("advanced_observer.south");
    private final Text EAST_BUTTON_TEXT = new TranslatableText("advanced_observer.east");
    private final Text WEST_BUTTON_TEXT = new TranslatableText("advanced_observer.west");
    private final Text UP_BUTTON_TEXT = new TranslatableText("advanced_observer.up");
    private final Text DOWN_BUTTON_TEXT = new TranslatableText("advanced_observer.down");
    private final Text OUTPUT_DIRECTION_TEXT = new TranslatableText("advanced_observer.output_direction");
    private final Text DELAY_TEXT = new TranslatableText("advanced_observer.delay");
    private final Text PULSE_LENGTH_TEXT = new TranslatableText("advanced_observer.pulse_length");
    private final Text REPEATER_MODE_TEXT = new TranslatableText("advanced_observer.repeater_mode");
    private final Text REPEATER_MODE_ON_TEXT = new TranslatableText("advanced_observer.repeater_mode_on");
    private final Text REPEATER_MODE_OFF_TEXT = new TranslatableText("advanced_observer.repeater_mode_off");
    private final int TEXT_COLOR = 0xFFd0d0d0;
    private final int INPUT_COLOR = 0xFFd48448;
    private final int OUTPUT_COLOR = 0xFF456789;

    private final AdvancedObserverBlockEntity advancedObserver;
    private final AdvancedObserverBlockEntity.FacingDirection inputDirection;
    private ButtonWidget northButton;
    private ButtonWidget southButton;
    private ButtonWidget eastButton;
    private ButtonWidget westButton;
    private ButtonWidget upButton;
    private ButtonWidget downButton;
    private AdvancedObserverBlockEntity.FacingDirection outputDirection;
    private int delay;
    private int pulseLength;
    private boolean repeaterMode;
    private ButtonWidget selectedOutputButton;
    private ButtonWidget inputButton;

    public AdvancedObserverScreen(AdvancedObserverBlockEntity advancedObserver) {
        super(NarratorManager.EMPTY);
        this.advancedObserver = advancedObserver;
        inputDirection = advancedObserver.getInputDirection();
        outputDirection = advancedObserver.getOutputDirection();
        delay = advancedObserver.getDelay();
        pulseLength = advancedObserver.getPulseLength();
        repeaterMode = advancedObserver.getRepeaterMode();
    }

    @Override
    protected void init() {
        initDirectionButtons();
        initDelaySlider();
        initPulseLengthSlider();
        initRepeaterModeToggle();
    }

    protected void initDirectionButtons() {
        int buttonY = 50;
        int buttonWidth = 40;
        int buttonHeight = 20;
        this.northButton = new ButtonWidget(this.width / 2 - 120 - buttonWidth / 2, buttonY, buttonWidth, buttonHeight, NORTH_BUTTON_TEXT, (button) -> {
            this.setOutputDirection(AdvancedObserverBlockEntity.FacingDirection.NORTH, button);
        });
        this.addButton(this.northButton);
        this.southButton = new ButtonWidget(this.width / 2 - 72 - buttonWidth / 2, buttonY, buttonWidth, buttonHeight, SOUTH_BUTTON_TEXT, (button) -> {
            this.setOutputDirection(AdvancedObserverBlockEntity.FacingDirection.SOUTH, button);
        });
        this.addButton(this.southButton);
        this.eastButton = new ButtonWidget(this.width / 2 - 24 - buttonWidth / 2, buttonY, buttonWidth, buttonHeight, EAST_BUTTON_TEXT, (button) -> {
            this.setOutputDirection(AdvancedObserverBlockEntity.FacingDirection.EAST, button);
        });
        this.addButton(this.eastButton);
        this.westButton = new ButtonWidget(this.width / 2 + 24 - buttonWidth / 2, buttonY, buttonWidth, buttonHeight, WEST_BUTTON_TEXT, (button) -> {
            this.setOutputDirection(AdvancedObserverBlockEntity.FacingDirection.WEST, button);
        });
        this.addButton(this.westButton);
        this.upButton = new ButtonWidget(this.width / 2 + 72 - buttonWidth / 2, buttonY, buttonWidth, buttonHeight, UP_BUTTON_TEXT, (button) -> {
            this.setOutputDirection(AdvancedObserverBlockEntity.FacingDirection.UP, button);
        });
        this.addButton(this.upButton);
        this.downButton = new ButtonWidget(this.width / 2 + 120 - buttonWidth / 2, buttonY, buttonWidth, buttonHeight, DOWN_BUTTON_TEXT, (button) -> {
            this.setOutputDirection(AdvancedObserverBlockEntity.FacingDirection.DOWN, button);
        });
        this.addButton(this.downButton);
        this.disableInputButton();
        this.setOutputDirection(this.outputDirection, getButtonOfDirection(this.outputDirection));
    }

    protected void initDelaySlider() {
        SliderWidget delaySlider = new SliderWidget(this.width / 2 - 100, 80, 200, 20, LiteralText.EMPTY, ((double) this.delay - 1) / 19) {
            {
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Text.of(DELAY_TEXT.getString() + ": " + AdvancedObserverScreen.this.delay));
            }

            @Override
            protected void applyValue() {
                AdvancedObserverScreen.this.delay = MathHelper.floor(this.value * 19 + 1);
                updateMessage();
            }
        };
        this.addButton(delaySlider);
    }

    protected void initPulseLengthSlider() {
        SliderWidget pulseLengthSlider = new SliderWidget(this.width / 2 - 100, 110, 200, 20, LiteralText.EMPTY, ((double) this.pulseLength - 1) / 19) {
            {
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(Text.of(PULSE_LENGTH_TEXT.getString() + ": " + AdvancedObserverScreen.this.pulseLength));
            }

            @Override
            protected void applyValue() {
                AdvancedObserverScreen.this.pulseLength = MathHelper.floor(this.value * 19 + 1);
                updateMessage();
            }
        };
        this.addButton(pulseLengthSlider);
    }

    private void initRepeaterModeToggle() {
        ButtonWidget repeaterModeToggleButton = new ButtonWidget(this.width / 2 - 20, 150, 40, 20, getRepeaterModeStatusText(), (button) -> {
            AdvancedObserverScreen.this.repeaterMode = !AdvancedObserverScreen.this.repeaterMode;
            button.setMessage(getRepeaterModeStatusText());
        });
        this.addButton(repeaterModeToggleButton);
    }

    @Override
    public void onClose() {
        updateServer();
        super.onClose();
    }

    private void updateServer() {
        if (this.client != null) {
            ClientPlayNetworkHandler networkHandler = this.client.getNetworkHandler();
            if (networkHandler != null) {
                networkHandler.sendPacket(new UpdateAdvancedObserverC2SPacket(this.advancedObserver.getPos(),
                        this.outputDirection.getIndex(), this.delay, this.pulseLength, this.repeaterMode));
            }
        }
    }

    protected void setOutputDirection(AdvancedObserverBlockEntity.FacingDirection direction, ButtonWidget button) {
        outputDirection = direction;
        if (selectedOutputButton != null) {
            this.selectedOutputButton.active = true;
        }
        selectedOutputButton = button;
        selectedOutputButton.active = false;
    }

    protected void disableInputButton() {
        inputButton = getButtonOfDirection(this.inputDirection);
        inputButton.active = false;
    }

    protected ButtonWidget getButtonOfDirection(AdvancedObserverBlockEntity.FacingDirection direction) {
        switch(direction) {
            case NORTH:
                return northButton;
            case SOUTH:
                return southButton;
            case EAST:
                return eastButton;
            case WEST:
                return westButton;
            case UP:
                return upButton;
            case DOWN:
                return downButton;
        }
        return northButton;
    }

    private Text getRepeaterModeStatusText() {
        return this.repeaterMode ? REPEATER_MODE_ON_TEXT : REPEATER_MODE_OFF_TEXT;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        renderButtonOutlines(matrices);
        drawCenteredText(matrices, this.textRenderer, OUTPUT_DIRECTION_TEXT, this.width / 2, 35, TEXT_COLOR);
        drawCenteredText(matrices, this.textRenderer, REPEATER_MODE_TEXT, this.width / 2, 140, TEXT_COLOR);
        super.render(matrices, mouseX, mouseY, delta);
    }

    protected void renderButtonOutlines(MatrixStack matrices) {
        int borderWidth = 2;
        fill(matrices, selectedOutputButton.x - borderWidth, selectedOutputButton.y - borderWidth,
                selectedOutputButton.x + selectedOutputButton.getWidth() + borderWidth,
                selectedOutputButton.y + selectedOutputButton.getHeight() + borderWidth,
                OUTPUT_COLOR);
        fill(matrices, inputButton.x - borderWidth, inputButton.y - borderWidth,
                inputButton.x + inputButton.getWidth() + borderWidth,
                inputButton.y + inputButton.getHeight() + borderWidth,
                INPUT_COLOR);
    }
}
