package ru.wexside.ui;

import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.DeferredRenderTask;
import ru.wexside.misc.GuiInteractionState;
import ru.wexside.misc.MovablePanel;
import ru.wexside.util.ClickGuiPanel;

public final class WexsideScreen extends class_437 {
   private static int lastFramebufferWidth;
   private static int lastFramebufferHeight;
   private final MovablePanel root;
   private final Runnable deferredRenderTaskRunner;

   public WexsideScreen() {
      this(WexSideClient.getInstance().getMiscellaneous(), DeferredRenderTask::update);
   }

   public WexsideScreen(MovablePanel root) {
      this(root, DeferredRenderTask::update);
   }

   WexsideScreen(MovablePanel root, Runnable deferredRenderTaskRunner) {
      super(class_2561.method_43473());
      this.root = root;
      this.deferredRenderTaskRunner = deferredRenderTaskRunner;
   }

   private int scaledMouseX() {
      class_310 client = class_310.method_1551();
      return (int)(client.field_1729.method_1603() / (double)client.method_22683().method_4495());
   }

   private int scaledMouseY() {
      class_310 client = class_310.method_1551();
      return (int)(client.field_1729.method_1604() / (double)client.method_22683().method_4495());
   }

   private void updateLayout(int framebufferWidth, int framebufferHeight) {
      if (framebufferWidth > 0 && framebufferHeight > 0 && (framebufferWidth != lastFramebufferWidth || framebufferHeight != lastFramebufferHeight)) {
         this.root
            .process7(
               (int)((float)framebufferWidth / 2.0F - this.root.getBounds().getWidth() / 2.0F),
               (int)((float)framebufferHeight / 2.0F - this.root.getBounds().getHeight() / 2.0F)
            );
         lastFramebufferWidth = framebufferWidth;
         lastFramebufferHeight = framebufferHeight;
      }
   }

   protected void method_25426() {
      this.refreshLayout();
      GuiInteractionState.getInstance().setRootPanel(this.root);
      MovablePanel var2 = this.root;
      if (var2 instanceof ClickGuiPanel clickGui) {
         clickGui.update3();
      }
   }

   public void method_25432() {
      this.root.update2();
      super.method_25432();
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      this.updateMouseState();
   }

   public void renderPanel(float delta) {
      this.root.update();
      float scale = (float)class_310.method_1551().method_22683().method_4495();
      Matrix4f transform = new Matrix4f().scale(scale, scale, 1.0F).translate(this.root.getBounds().getX(), this.root.getBounds().getY(), 0.0F);
      this.root.render(delta, transform);
      this.deferredRenderTaskRunner.run();
   }

   public boolean method_25404(class_11908 input) {
      return this.root.onKeyPressed(input.comp_4795()) || super.method_25404(input);
   }

   public boolean method_25400(class_11905 input) {
      return Character.isBmpCodePoint(input.comp_4793()) && this.root.onCharTyped((char)input.comp_4793()) || super.method_25400(input);
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.updateMouseState();
      this.root.onMouseScroll(this.scaledMouseX(), this.scaledMouseY(), verticalAmount);
      return super.method_25401(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      this.updateMouseState();
      int mouseX = this.scaledMouseX();
      int mouseY = this.scaledMouseY();
      return this.root.onMousePressed(mouseX, mouseY, click.method_74245()) || super.method_25402(click, doubled);
   }

   public boolean method_25406(class_11909 click) {
      this.updateMouseState();
      this.root.onMouseReleased(this.scaledMouseX(), this.scaledMouseY(), click.method_74245());
      return super.method_25406(click);
   }

   public void method_16014(double mouseX, double mouseY) {
      this.updateMouseState();
      super.method_16014(mouseX, mouseY);
   }

   public void method_25410(int width, int height) {
      super.method_25410(width, height);
      this.refreshLayout();
   }

   public boolean method_25421() {
      return false;
   }

   public MovablePanel root() {
      return this.root;
   }

   private void refreshLayout() {
      this.updateLayout(this.field_22789, this.field_22790);
   }

   private void updateMouseState() {
      class_310 client = class_310.method_1551();
      GuiInteractionState mouse = GuiInteractionState.getInstance();
      mouse.setRawMousePosition((int)client.field_1729.method_1603(), (int)client.field_1729.method_1604());
      mouse.setScaledMousePosition(this.scaledMouseX(), this.scaledMouseY());
      mouse.setRootPanel(this.root);
   }
}
