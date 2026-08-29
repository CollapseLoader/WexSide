package ru.wexside.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.joml.Matrix4f;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.ui.ModalPopup;
import ru.wexside.ui.PopupPanel;

public final class PopupManager
   extends GuiElement
   implements CharacterInputHandler,
   MouseScrollHandler,
   LayoutUpdater,
   KeyPressHandler,
   GuiRenderable,
   MouseButtonHandler,
   BoundsProvider {
   private final Map<PopupOwner, PopupOwner> parentsByOwner;
   private final List<PopupOwner> owners = new ArrayList<>();

   public PopupManager() {
      super(new GuiBounds(0.0F, 0.0F, 0.0F, 0.0F));
      this.parentsByOwner = new IdentityHashMap<>();
   }

   @Override
   public void onMouseScroll(int n, int n2, double d) {
      for(int i = this.owners.size() - 1; i >= 0; --i) {
         PopupOwner owner = this.owners.get(i);
         owner.update2();
         PopupPanel popupPanel = owner.getPopup();
         if (popupPanel != null && popupPanel.isActive2()) {
            if (popupPanel.getBounds().contains((float)n, (float)n2)) {
               popupPanel.onMouseScroll(n, n2, d);
               return;
            }

            if (this.isInsideAncestorPopup(n, n2, owner)) {
               this.closeBranch(owner);
            } else {
               this.closeBranch(owner);
            }
         }
      }
   }

   @Override
   public void update() {
      for(PopupOwner owner : this.owners) {
         PopupPanel popupPanel = owner.getPopup();
         if (popupPanel != null) {
            popupPanel.update();
         }
      }
   }

   @Override
   public boolean onMousePressed(int n, int n2, int n3) {
      boolean handledPopup = false;

      for(int i = this.owners.size() - 1; i >= 0; --i) {
         PopupOwner owner = this.owners.get(i);
         owner.update2();
         PopupPanel popupPanel = owner.getPopup();
         if (popupPanel != null && popupPanel.isActive2()) {
            handledPopup = true;
            if (popupPanel.getBounds().contains((float)n, (float)n2)) {
               if (popupPanel.onMousePressed(n, n2, n3)) {
                  return true;
               }

               this.closeBranch(owner);
               return true;
            }

            if (this.isInsideAncestorPopup(n, n2, owner)) {
               this.closeBranch(owner);
               if (owner.process6(n, n2)) {
                  return true;
               }
            } else {
               this.closeBranch(owner);
            }
         }
      }

      return handledPopup;
   }

   @Override
   public float render(float f, Matrix4f matrix4f) {
      return this.renderOpenPopups(f, matrix4f);
   }

   @Override
   public void onMouseReleased(int n, int n2, int n3) {
      for(int i = this.owners.size() - 1; i >= 0; --i) {
         PopupPanel popupPanel = this.owners.get(i).getPopup();
         if (popupPanel != null && popupPanel.isActive2()) {
            popupPanel.onMouseReleased(n, n2, n3);
         }
      }
   }

   @Override
   public boolean onCharTyped(char c) {
      for(int i = this.owners.size() - 1; i >= 0; --i) {
         PopupPanel popupPanel = this.owners.get(i).getPopup();
         if (popupPanel != null && popupPanel.isActive2() && popupPanel.onCharTyped(c)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean onKeyPressed(int n) {
      for(int i = this.owners.size() - 1; i >= 0; --i) {
         PopupPanel popupPanel = this.owners.get(i).getPopup();
         if (popupPanel != null && popupPanel.isActive2() && popupPanel.onKeyPressed(n)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsOpenPopup(int n, int n2) {
      for(int i = this.owners.size() - 1; i >= 0; --i) {
         PopupOwner owner = this.owners.get(i);
         owner.update2();
         PopupPanel popupPanel = owner.getPopup();
         if (popupPanel != null && popupPanel.isActive2() && popupPanel.getBounds().contains((float)n, (float)n2)) {
            return true;
         }
      }

      return false;
   }

   public ModalPopup getModalPopup() {
      for(PopupOwner owner : this.owners) {
         PopupPanel popupPanel = owner.getPopup();
         if (popupPanel instanceof ModalPopup modalPopup && modalPopup.isActive()) {
            owner.update2();
            return modalPopup;
         }
      }

      return null;
   }

   public void closeAll() {
      for(PopupOwner owner : this.owners) {
         PopupPanel popupPanel = owner.getPopup();
         if (popupPanel != null) {
            popupPanel.setBooleanType(false);
         }
      }
   }

   public float renderOpenPopups(float delta, Matrix4f matrix) {
      float bottom = 0.0F;

      for(PopupOwner owner : this.owners) {
         owner.update2();
         PopupPanel popupPanel = owner.getPopup();
         if (popupPanel != null && !(popupPanel instanceof ModalPopup) && popupPanel.isActive()) {
            bottom = popupPanel.render(delta, matrix);
         }
      }

      return bottom;
   }

   public void registerRoot(PopupOwner owner) {
      this.register(owner, null);
   }

   public void closeDescendants(PopupOwner owner) {
      if (owner != null) {
         for(PopupOwner candidate : this.owners) {
            PopupPanel popupPanel;
            if (this.isDescendant(candidate, owner) && (popupPanel = candidate.getPopup()) != null) {
               popupPanel.setBooleanType(false);
            }
         }
      }
   }

   private boolean isInsideAncestorPopup(int x, int y, PopupOwner owner) {
      for(PopupOwner ancestor = this.parentsByOwner.get(owner); ancestor != null; ancestor = this.parentsByOwner.get(ancestor)) {
         PopupPanel popupPanel = ancestor.getPopup();
         if (popupPanel != null && popupPanel.isActive2() && popupPanel.getBounds().contains((float)x, (float)y)) {
            return true;
         }
      }

      return false;
   }

   public List<PopupOwner> getOwners() {
      return Collections.unmodifiableList(this.owners);
   }

   public void toggle(PopupOwner owner) {
      PopupPanel popupPanel = owner == null ? null : owner.getPopup();
      if (popupPanel != null) {
         if (popupPanel.isActive2()) {
            this.closeBranch(owner);
         } else {
            this.closeUnrelated(owner);
            popupPanel.setBooleanType(true);
         }
      }
   }

   public void closeBranch(PopupOwner owner) {
      this.closeOwnerAndDescendants(owner);
   }

   public void register(PopupOwner owner, PopupOwner parent) {
      if (owner != null && !this.owners.contains(owner)) {
         this.owners.add(owner);
         this.parentsByOwner.put(owner, parent);
         PopupPanel popupPanel = owner.getPopup();
         if (popupPanel != null && !this.owners.contains(popupPanel)) {
            this.addChild(popupPanel);
         }

         owner.setPopupManager(this);
      }
   }

   private boolean isDescendant(PopupOwner candidate, PopupOwner ancestor) {
      for(PopupOwner parent = this.parentsByOwner.get(candidate); parent != null; parent = this.parentsByOwner.get(parent)) {
         if (parent == ancestor) {
            return true;
         }
      }

      return false;
   }

   private void closeUnrelated(PopupOwner owner) {
      Set<PopupOwner> ownerChain = Collections.newSetFromMap(new IdentityHashMap<>());

      for(PopupOwner current = owner; current != null; current = this.parentsByOwner.get(current)) {
         ownerChain.add(current);
      }

      for(PopupOwner candidate : this.owners) {
         PopupPanel popupPanel;
         if (!ownerChain.contains(candidate) && (popupPanel = candidate.getPopup()) != null) {
            popupPanel.setBooleanType(false);
         }
      }
   }

   private void closeOwnerAndDescendants(PopupOwner owner) {
      if (owner != null) {
         for(PopupOwner candidate : this.owners) {
            PopupPanel popupPanel;
            if ((candidate == owner || this.isDescendant(candidate, owner)) && (popupPanel = candidate.getPopup()) != null) {
               popupPanel.setBooleanType(false);
            }
         }
      }
   }

   public Map<PopupOwner, PopupOwner> getParentMap() {
      return Collections.unmodifiableMap(this.parentsByOwner);
   }
}
