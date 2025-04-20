export function capitalizeFirstLetter(str: string) {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export function removeUnderScore(name: string): string {
  const words = name.split('_');
  const capitalizedWords = words.map(
    (word) => word.charAt(0).toUpperCase() + word.slice(1).toLocaleLowerCase()
  );
  const formattedName = capitalizedWords.join(' ');
  return formattedName;
}
