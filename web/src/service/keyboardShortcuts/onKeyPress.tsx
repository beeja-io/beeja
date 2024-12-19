import { useEffect } from 'react';

const useKeyPress = (keyCode: number, callback: () => void) => {
  useEffect(() => {
    const handleKeyPress = (event: { keyCode: number }) => {
      const activeElement = document.activeElement;
      if (
        event.keyCode === keyCode &&
        activeElement &&
        activeElement.tagName !== 'INPUT' &&
        activeElement.tagName !== 'TEXTAREA'
      ) {
        callback();
      }
    };

    document.addEventListener('keydown', handleKeyPress);
    return () => {
      document.removeEventListener('keydown', handleKeyPress);
    };
  }, [keyCode, callback]);
};

export default useKeyPress;
