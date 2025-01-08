export const handleNumericInputKeyDown = (
  event: React.KeyboardEvent<HTMLInputElement>
): void => {
  const allowedCharacters = /^[0-9]+$/;
  const allowedKeys = ['ArrowLeft', 'ArrowRight', 'Backspace'];
  if (event.metaKey || event.ctrlKey || allowedKeys.includes(event.key)) {
    return;
  }
  if (!allowedCharacters.test(event.key) || event.key.toLowerCase() === 'e') {
    event.preventDefault();
  }
};
