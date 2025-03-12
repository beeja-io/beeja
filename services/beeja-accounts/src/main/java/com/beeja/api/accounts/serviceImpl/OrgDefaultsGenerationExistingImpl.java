package com.beeja.api.accounts.serviceImpl;

import com.beeja.api.accounts.clients.ExpenseClient;
import com.beeja.api.accounts.model.Organization.OrgDefaults;
import com.beeja.api.accounts.model.Organization.employeeSettings.OrgValues;
import com.beeja.api.accounts.repository.OrgDefaultsRepository;
import com.beeja.api.accounts.response.ExpenseValuesDTO;
import com.beeja.api.accounts.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@Slf4j
public class OrgDefaultsGenerationExistingImpl {

    @Autowired
    ExpenseClient expenseClient;

    @Autowired
    OrgDefaultsRepository orgDefaultsRepository;

    public void generateExistingValuesOfExpenseType() {
        try{
            ExpenseValuesDTO expenseValuesDTOResponseEntity = expenseClient.getExpenseValues("Bearer "+UserContext.getAccessToken());
            OrgDefaults existingExpenseTypes = orgDefaultsRepository.findByOrganizationIdAndKey(
                    UserContext.getLoggedInUserOrganization().getId(), "expenseTypes"
            );
            if(existingExpenseTypes == null){
                existingExpenseTypes = new OrgDefaults();
                existingExpenseTypes.setKey("expenseTypes");
            }
            if(existingExpenseTypes.getValues() == null){
                existingExpenseTypes.setValues(new HashSet<>());
            }
            if(expenseValuesDTOResponseEntity != null){
                for (String expenseType : expenseValuesDTOResponseEntity.getExpenseTypes()) {
                    OrgValues expTypeValues = new OrgValues();
                    expTypeValues.setValue(expenseType);
                    expTypeValues.setDescription("Auto Generated");
                    existingExpenseTypes.getValues().add(expTypeValues);
                }
            }
            existingExpenseTypes.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
            existingExpenseTypes.setValues(new HashSet<>(existingExpenseTypes.getValues()));
            orgDefaultsRepository.save(existingExpenseTypes);

            log.info("Existing Expense Types are generated successfully");

        }catch (Exception e){
            log.error("Error while generating existing expense types : {}", e.getMessage());
        }
    }

    public void generateExistingValuesOfExpenseCategories(){
        try{
            ExpenseValuesDTO expenseValuesDTOResponseEntity = expenseClient.getExpenseValues("Bearer "+UserContext.getAccessToken());
            OrgDefaults existingExpenseCategories = orgDefaultsRepository.findByOrganizationIdAndKey(
                    UserContext.getLoggedInUserOrganization().getId(), "expenseCategories"
            );
            if(existingExpenseCategories == null){
                existingExpenseCategories = new OrgDefaults();
                existingExpenseCategories.setKey("expenseCategories");
            }
            if(existingExpenseCategories.getValues() == null){
                existingExpenseCategories.setValues(new HashSet<>());
            }
            if(expenseValuesDTOResponseEntity != null){
                for (String expenseCategory : expenseValuesDTOResponseEntity.getExpenseCategories()) {
                    OrgValues expCategoryValues = new OrgValues();
                    expCategoryValues.setValue(expenseCategory);
                    expCategoryValues.setDescription("Auto Generated");
                    existingExpenseCategories.getValues().add(expCategoryValues);
                }
            }
            existingExpenseCategories.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
            existingExpenseCategories.setValues(new HashSet<>(existingExpenseCategories.getValues()));
            orgDefaultsRepository.save(existingExpenseCategories);

            log.info("Existing Expense Categories are generated successfully");

        }catch (Exception e){
            log.error("Error while generating existing expense categories : {}", e.getMessage());
        }
    }

    public void generateExistingPaymentModes () {
        try{
            ExpenseValuesDTO expenseValuesDTOResponseEntity = expenseClient.getExpenseValues("Bearer "+UserContext.getAccessToken());
            OrgDefaults existingPaymentModes = orgDefaultsRepository.findByOrganizationIdAndKey(
                    UserContext.getLoggedInUserOrganization().getId(), "paymentModes"
            );
            if(existingPaymentModes == null){
                existingPaymentModes = new OrgDefaults();
                existingPaymentModes.setKey("paymentModes");
            }
            if(existingPaymentModes.getValues() == null){
                existingPaymentModes.setValues(new HashSet<>());
            }
            if(expenseValuesDTOResponseEntity != null){
                for (String paymentMode : expenseValuesDTOResponseEntity.getExpenseModesOfPayment()) {
                    OrgValues paymentModeValues = new OrgValues();
                    paymentModeValues.setValue(paymentMode);
                    paymentModeValues.setDescription("Auto Generated");
                    existingPaymentModes.getValues().add(paymentModeValues);
                }
            }
            existingPaymentModes.setOrganizationId(UserContext.getLoggedInUserOrganization().getId());
            existingPaymentModes.setValues(new HashSet<>(existingPaymentModes.getValues()));
            orgDefaultsRepository.save(existingPaymentModes);

            log.info("Existing Payment Modes are generated successfully");

        }catch (Exception e){
            log.error("Error while generating existing payment modes : {}", e.getMessage());
        }
    }
}
