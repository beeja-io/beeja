import { useEffect } from 'react';

const useKeyCtrl = (keyCode: string, callback: () => void) => {
  useEffect(() => {
    const keyPressSave = (event: KeyboardEvent) => {
      if (
        (event.ctrlKey || event.metaKey) &&
        (event.key === keyCode || event.key === keyCode.toLocaleUpperCase())
      ) {
        event.preventDefault();
        callback();
      }
    };
    document.addEventListener('keydown', keyPressSave);
    return () => {
      document.removeEventListener('keydown', keyPressSave);
    };
  }, [keyCode, callback]);
};

export default useKeyCtrl;
