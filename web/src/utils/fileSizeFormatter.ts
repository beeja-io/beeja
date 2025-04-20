export const formatFileSize = (fileSizeInBytes: number): string => {
  const kiloByte = 1024;
  const megaByte = kiloByte * 1024;
  const gigaByte = megaByte * 1024;

  if (fileSizeInBytes < kiloByte) {
    return fileSizeInBytes.toFixed(2) + " B";
  } else if (fileSizeInBytes < megaByte) {
    return (fileSizeInBytes / kiloByte).toFixed(2) + " KB";
  } else if (fileSizeInBytes < gigaByte) {
    return (fileSizeInBytes / megaByte).toFixed(2) + " MB";
  } else {
    return (fileSizeInBytes / gigaByte).toFixed(2) + " GB";
  }
};
