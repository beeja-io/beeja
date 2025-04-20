export const formatToINR = (amount: number): string => {
  return new Intl.NumberFormat("en-IN", {
    currency: "INR",
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(amount);
};
