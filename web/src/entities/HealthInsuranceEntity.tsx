export interface HealthInsurance {
  id: string;
  employeeId: string;
  grossPremium: number;
  instalmentType: "MONTLHY" | "QUARTERLY";
  instalmentAmount: number | null;
  instalmentFrequency: number | null;
}
