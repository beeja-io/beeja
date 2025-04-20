export const keyPressFind = (
  searchInputRef: React.RefObject<HTMLInputElement>
) => {
  document.addEventListener('keydown', (event) => {
    if (
      (event.ctrlKey || event.metaKey) &&
      (event.key === 'F' || event.key === 'f')
    ) {
      event.preventDefault();
      if (searchInputRef.current) {
        searchInputRef.current.focus();
      }
    }
  });
};
export const keyPressCancel = (
  handleKeyPress: (event: KeyboardEvent) => void
) => {
  document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape') {
      event.preventDefault();
      handleKeyPress(event);
    }
  });
};
