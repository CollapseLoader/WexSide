package ru.wexside.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.SearchQueryState;
import ru.wexside.misc.ThemeColors;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleManager;
import ru.wexside.setting.Setting;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public final class ModuleSearchResults
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   static final int slot = 3;
   static final int slot2 = 6;
   private String string4 = null;
   private final String string5;
   private final ModuleManager moduleManager;
   private final String string6;
   private final String string7;
   private final ScrollController scrollController = new ScrollController(18.0F, 30.0F);
   static final int slot3 = 9;
   private final Consumer<Module> consumer;
   private final SearchQueryState searchQueryState;
   private final Function<Module, String> function;

   public ModuleSearchResults(
      GuiBounds bounds2, ModuleManager moduleManager, SearchQueryState searchQueryState, Consumer<Module> consumer, Function<Module, String> function
   ) {
      super(bounds2);
      this.string5 = "Введите название функции или модуля в поисковую";
      this.string7 = "строку, что-бы найти нужный вам элемент";
      this.string6 = "Мы обыскали всё, но не нашли ни одной функции";
      this.moduleManager = moduleManager;
      this.searchQueryState = searchQueryState;
      this.consumer = consumer;
      this.function = function;
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      if (this.getBounds().contains((float)n, (float)n2)) {
         this.scrollController.scrollByWheel(d, this.getBounds().getHeight());
      }
   }

   @Override
   public void update() {
      for(GuiElement element2 : this.children) {
         element2.update();
      }
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      this.update3();
      return this.getBounds().contains((float)n, (float)n2) && super.onMousePressed(n, n2, n3);
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
      this.update3();
      if (!this.searchQueryState.hasQuery()) {
         this.process5(matrix4f, drawApi, bounds2, "Введите название функции или модуля в поисковую", "строку, что-бы найти нужный вам элемент");
         return bounds2.getY() + bounds2.getHeight();
      } else if (this.children.isEmpty()) {
         this.scrollController.update(bounds2.getHeight(), 0.0F);
         this.scrollController.setContentHeight(bounds2.getHeight(), 0.0F);
         String string = this.searchQueryState.getQuery();
         String string2 = "или модуля похожих на «" + string + "»";
         this.process5(matrix4f, drawApi, bounds2, "Мы обыскали всё, но не нашли ни одной функции", string2);
         return bounds2.getY() + bounds2.getHeight();
      } else {
         float f2 = this.getFloatType();
         this.scrollController.update(bounds2.getHeight(), f2);
         drawApi.beginStencil(1);
         drawApi.drawRoundedRectangleRadii(
            matrix4f,
            bounds2.getX() + 1.0F,
            bounds2.getY() + 1.0F,
            bounds2.getWidth() - 1.5F,
            bounds2.getHeight() - 1.5F,
            10.5F,
            0.0F,
            0.0F,
            0.0F,
            ColorUtils.rgba(0, 0, 0, 45)
         );
         drawApi.applyStencilMask(1);
         float f3 = this.process4(f, matrix4f);
         drawApi.endStencil();
         this.scrollController.setContentHeight(bounds2.getHeight(), f3);
         return bounds2.getY() + bounds2.getHeight();
      }
   }

   @Override
   public void update2() {
      this.scrollController.scrollToTop();
   }

   private float process4(float f, Matrix4f matrix4f) {
      GuiBounds bounds2 = this.getBounds();
      float f3 = this.scrollController.getOffset();
      float f4 = bounds2.getX() + 8.0F;
      float f2;
      float f5 = f2 = bounds2.getY() + 8.0F + f3;
      float f6 = bounds2.getY() - 1.0F;
      float f7 = bounds2.getY() + bounds2.getHeight() + 1.0F;

      for(GuiElement element2 : this.children) {
         element2.getBounds().setPosition(f4, f2);
         float f8 = element2.getBounds().getHeight();
         f5 = !(f2 + f8 < f6) && !(f2 > f7) ? element2.render(f, matrix4f) : f2 + f8;
         f2 = f5 + 4.0F;
      }

      float f9 = f5 - f3;
      return f9 <= bounds2.getY() + 8.0F ? 0.0F : f9 - bounds2.getY() + 8.0F;
   }

   private void update3() {
      String string = this.searchQueryState.getQuery();
      if (!Objects.equals(string, this.string4)) {
         this.string4 = string;
         this.children.clear();
         if (string != null && !string.isBlank()) {
            String string2 = this.process6(string);
            if (string2.isEmpty()) {
               this.scrollController.scrollToTop();
            } else {
               ArrayList<ModuleSearchResults.SearchResult> results = new ArrayList<>();
               ArrayList<Module> arrayList2 = new ArrayList<>(this.moduleManager.getModules());
               EspFeatureRegistry espFeatures = WexSideClient.getEspFeatureRegistry();
               if (espFeatures != null) {
                  arrayList2.addAll(espFeatures.getDefaultModules());
               }

               for(Module module : arrayList2) {
                  int n = this.process7(module, string2);
                  if (n >= 0) {
                     ModuleSearchResults.MatchKind matchKind = n < 6 ? ModuleSearchResults.MatchKind.MODULE : ModuleSearchResults.MatchKind.SETTING;
                     String string3 = this.function == null ? "" : this.function.apply(module);
                     results.add(new ModuleSearchResults.SearchResult(n, new ModuleSearchResults.SearchResultRow(module, string3, matchKind, this.consumer)));
                  }
               }

               results.sort(Comparator.comparingInt(ModuleSearchResults.SearchResult::score));

               for(ModuleSearchResults.SearchResult result : results) {
                  this.addChild(result.row());
               }

               this.scrollController.scrollToTop();
            }
         } else {
            this.scrollController.scrollToTop();
         }
      }
   }

   private void process5(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2, String string, String string2) {
      int n = ThemeColors.textPlaceholder();
      float f = bounds2.getX() + 87.5F;
      float f2 = bounds2.getY() + 119.0F;
      float f3 = FontRegistry.font2.process3(string, 6.5F);
      float f4 = FontRegistry.font2.process4(string, 6.5F);
      float f5 = f + f3 / 2.0F;
      float f6 = FontRegistry.font2.process3(string2, 6.5F);
      float f7 = f5 - f6 / 2.0F;
      float f8 = f2 + f4 + 3.0F - 1.0F;
      FontRegistry.font2.process2(matrix4f, drawApi, string, f, f2, 6.5F, n);
      FontRegistry.font2.process2(matrix4f, drawApi, string2, f7, f8, 6.5F, n);
   }

   private String process6(String string) {
      if (string == null) {
         return "";
      } else {
         StringBuilder stringBuilder = new StringBuilder(string.length());

         for(int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c != '-' && c != '_' && !Character.isWhitespace(c)) {
               stringBuilder.append(Character.toLowerCase(c));
            }
         }

         return stringBuilder.toString();
      }
   }

   private int process7(Module module, String string) {
      int n = Integer.MAX_VALUE;
      n = Math.min(n, this.process8(module.getDisplayName(), string, 0));

      for(String object : module.getAliases()) {
         n = Math.min(n, this.process8(object, string, 3));
      }

      for(Setting setting : module.getSettings()) {
         n = Math.min(n, this.process8(setting.getDisplayName(), string, 6));

         for(String string2 : setting.getAliases()) {
            n = Math.min(n, this.process8(string2, string, 9));
         }
      }

      return n == Integer.MAX_VALUE ? -1 : n;
   }

   private int process8(String string, String string2, int n) {
      if (string == null) {
         return Integer.MAX_VALUE;
      } else {
         String string3 = this.process6(string);
         if (string3.isEmpty()) {
            return Integer.MAX_VALUE;
         } else if (string3.equals(string2)) {
            return n;
         } else if (string3.startsWith(string2)) {
            return n + 1;
         } else {
            return string3.contains(string2) ? n + 2 : Integer.MAX_VALUE;
         }
      }
   }

   private float getFloatType() {
      float f = 8.0F;

      for(GuiElement element2 : this.children) {
         f += element2.getBounds().getHeight() + 4.0F;
      }

      return f - 4.0F + 8.0F;
   }

   public boolean isActive() {
      this.update3();
      if (this.searchQueryState.hasQuery() && !this.children.isEmpty()) {
         Object e = this.children.get(0);
         if (e instanceof ModuleSearchResults.SearchResultRow row && this.consumer != null) {
            this.consumer.accept(row.module());
            return true;
         }

         return false;
      } else {
         return false;
      }
   }

   private static enum MatchKind {
      MODULE,
      SETTING;
   }

   private static record SearchResult(int score, ModuleSearchResults.SearchResultRow row) {
   }

   private static final class SearchResultRow extends GuiElement {
      private static final float HEIGHT = 31.0F;
      private final Module module;
      private final String context;
      private final ModuleSearchResults.MatchKind matchKind;
      private final Consumer<Module> onSelected;

      private SearchResultRow(Module module, String context, ModuleSearchResults.MatchKind matchKind, Consumer<Module> onSelected) {
         super(new GuiBounds(0.0F, 0.0F, 0.0F, 31.0F));
         this.module = module;
         this.context = context == null ? "" : context;
         this.matchKind = matchKind;
         this.onSelected = onSelected;
      }

      private Module module() {
         return this.module;
      }

      @Override
      public void onMouseScroll(int mouseX, int mouseY, double amount) {
      }

      @Override
      public void update() {
      }

      @Override
      public boolean onMousePressed(int mouseX, int mouseY, int button) {
         if (button == 0 && this.getBounds().contains((float)mouseX, (float)mouseY)) {
            if (this.onSelected != null) {
               this.onSelected.accept(this.module);
            }

            return true;
         } else {
            return false;
         }
      }

      @Override
      public float render(float delta, Matrix4f matrix) {
         GuiBounds bounds = this.getBounds();
         bounds.setSize(bounds.getWidth(), 31.0F);
         GuiDrawApi renderer = WexSideClient.getGuiRenderer();
         renderer.drawRoundedRectangle(matrix, bounds.getX(), bounds.getY(), bounds.getWidth(), 31.0F, 7.0F, ThemeColors.borderSubtle());
         FontRegistry.font5
            .process2(matrix, renderer, this.module.getDisplayName(), bounds.getX() + 8.0F, bounds.getY() + 6.0F, 6.5F, ThemeColors.textPrimary());
         String category = this.matchKind == ModuleSearchResults.MatchKind.SETTING ? "Setting" : this.module.getCategory().getName();
         String details = this.context.isBlank() ? category : category + "  ·  " + this.context;
         FontRegistry.font2.process2(matrix, renderer, details, bounds.getX() + 8.0F, bounds.getY() + 17.0F, 5.75F, ThemeColors.textPlaceholder());
         return bounds.getY() + 31.0F;
      }
   }
}
