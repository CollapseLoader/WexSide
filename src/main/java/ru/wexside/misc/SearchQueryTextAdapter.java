package ru.wexside.misc;

public final class SearchQueryTextAdapter implements TextInputModel {
   private final SearchQueryState searchQueryState;

   public SearchQueryTextAdapter(SearchQueryState searchQueryState) {
      this.searchQueryState = searchQueryState;
   }

   @Override
   public int getMaximumLength() {
      return 32;
   }

   @Override
   public String getText() {
      return this.searchQueryState.getQuery();
   }

   @Override
   public void setText(String text) {
      this.searchQueryState.setQuery(text == null ? "" : text);
   }
}
