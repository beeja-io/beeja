package tac.beeja.recruitmentapi.utils;

public class OrganizationCheck {
    public static boolean isValidOrganizationId(String loggedInUserOrganizationId, String organizationId) {
        if (loggedInUserOrganizationId == null || organizationId == null) {
            return false;
        }
        return loggedInUserOrganizationId.equals(organizationId);
    }
}
