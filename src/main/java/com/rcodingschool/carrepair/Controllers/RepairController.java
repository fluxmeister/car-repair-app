package com.rcodingschool.carrepair.Controllers;

import com.rcodingschool.carrepair.Converters.RepairConverter;
import com.rcodingschool.carrepair.Domain.Repair;
import com.rcodingschool.carrepair.Model.RepairForm;
import com.rcodingschool.carrepair.Model.RepairSearchForm;
import com.rcodingschool.carrepair.Services.RepairService;
import com.rcodingschool.carrepair.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin")
public class RepairController {

    private static final String REPAIR_FORM = "repairForm";
    private static final String SEARCH_FORM = "RepairSearchForm";
    private static final String REPAIR_LIST = "repairList";
    private static final String NOT_FOUND = "searchNotFoundMessage";
    private static final String MESSAGE = "errorMessage";

    @Autowired
    private UserService userService;

    @Autowired
    private RepairService repairService;

    //We will use the @InitBinder annotation and the initBinder method to
    //trim all the user's input from spaces
    //For example if the user enters "    John     " it will be trimmed to "John"
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }


    @RequestMapping(value = "/repair", method = RequestMethod.GET)
    public String showRepairView(Model model) {
        Map<String, Object> map = model.asMap();
        //If our Model does not contain a repairForm, add a new RepairForm()
        if (!map.containsKey(REPAIR_FORM)) {
            model.addAttribute(REPAIR_FORM, new RepairForm());
        }
        //if our Model does not contain a searchForm, add a new SearchForm()
        if (!map.containsKey(SEARCH_FORM)) {
            model.addAttribute(SEARCH_FORM, new RepairSearchForm());
        }
        return "repair";
    }


    @RequestMapping(value = "/repair/create", method = RequestMethod.POST)
    public String processCreateRepair(@Valid @ModelAttribute(REPAIR_FORM) RepairForm repairForm,
                                    BindingResult bindingResult, Model model,
                                    RedirectAttributes redirectAttributes) {

        //If something does not pass our @Valid(ations), then this means that our BindingResult
        //object ".hasErrors()" so we will send the user again to the registration form to correct his mistakes
        if (bindingResult.hasErrors()) {
            //Also we will be adding repairForm to RedirectAttributes so that we can keep his valid inputs and reshow them
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult." + REPAIR_FORM, bindingResult);
            redirectAttributes.addFlashAttribute(REPAIR_FORM, repairForm);
            return "redirect:/admin/repair";
        }
        try {
            //Trying to build a repair from our RepairForm


            Repair repair = RepairConverter.buildInsertRepairObject(repairForm);
            //Save the repair
            repairService.save(repair);
            //Send information to the user
            redirectAttributes.addFlashAttribute(MESSAGE, "Repair was created!");
        } catch (Exception exception) {
            //if an error occurs show it to the repair
            redirectAttributes.addFlashAttribute(MESSAGE, exception.getMessage());
        }
        return "redirect:/admin/repair";
    }

    //The processDeleteRerair() method will map "/admin/repairs/delete/{id}" GET requests and
    //will delete a repair and redirect to "/admin/repairs"
    @RequestMapping(value = "/repairs/delete/{repairID}", method = RequestMethod.GET)
    public String processDeleteRepair(@PathVariable Long repairID,
                                    RedirectAttributes redirectAttributes) {
        //Delete the repair
        repairService.deleteByRepairID(repairID);
        //Send information to the user
        redirectAttributes.addFlashAttribute(MESSAGE, "Repair was deleted!");
        return "redirect:/admin/repair";
    }

    //The processSearchUser() method will map "/repairs/search" GET requests and
    //will search for a reapair by either AFM or Email
    @RequestMapping(value = "/repair/search", method = RequestMethod.GET)
    public String processSearchRepair(@Valid @ModelAttribute(SEARCH_FORM) RepairSearchForm repairSearchForm,
                                    BindingResult bindingResult, Model model,
                                    RedirectAttributes redirectAttributes) {

        //If something does not pass our @Valid(ations), then this means that our BindingResult
        //object ".hasErrors()" so we will send the user again to the registration form to correct his mistakes
        if (bindingResult.hasErrors()) {
            //Also we will be adding userForm to RedirectAttributes so that we can keep his valid inputs and reshow them
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult." + SEARCH_FORM, bindingResult);
            //Send information to the user
            redirectAttributes.addFlashAttribute(SEARCH_FORM, repairSearchForm);
            return "redirect:/admin/repair";
        }

        //Initialize a new list of Repairs to hold the results of the search
        List<Repair> repairList;
        //Getting the searchForm values and checking
        //If both are null
        if (repairSearchForm.getRepairID() == null && repairSearchForm.getVehicleID() == null) {
            //Then we retrieve all the repairs
            repairList = repairService.findAll();
            //If the AFM is not null
        } else if (repairSearchForm.getRepairID() != null) {
            //We search for Repairs based on AFM
            repairList = repairService.findByRepairID(repairSearchForm.getRepairID());
            //Else if AFM is null, means Email is not
        } else {
            //We search for Repairs based on Email
            repairList = repairService.findByVehicleID(repairSearchForm.getVehicleID());
        }
        //If the List is Empty
        if (repairList.isEmpty()) {
            //We send Information to the user
            redirectAttributes.addFlashAttribute(NOT_FOUND, "No records were found!");
        } else {

            redirectAttributes.addFlashAttribute(REPAIR_LIST, repairList);
        }
        return "redirect:/admin/repair";
    }


    @RequestMapping(value = "/repairs/edit/{repairID}", method = RequestMethod.GET)
    public String showEditRepair(@PathVariable Long repairID,
                               RedirectAttributes redirectAttributes) {
        //Find the repair
        Repair repair = repairService.findOne(repairID);
        //Build a repairForm Object based on the repair we found
        RepairForm repairForm = RepairConverter.buildRepairFormObject(repair);

        redirectAttributes.addFlashAttribute(repairForm);
        return "redirect:/admin/repair/editRepair";
    }

    //the showEditRepairView will map "/repairs/editRepair" GET requests
    @RequestMapping(value = "/repair/editRepair", method = RequestMethod.GET)
    public String showEditRepairView(Model model) {
        //Get the model
        Map<String, Object> map = model.asMap();
        //If there is not already a RepairForm something went wrong so we redirect
        if (!map.containsKey(REPAIR_FORM)){
            return "redirect:/admin/repair";
        }
        //If there is not RepairForm
        return "editRepair";
    }

    //The processEditRepair() method will map "/repairs/editRepair" POST requests
    //and will try to change the details of a Repair
    @RequestMapping(value = "/repair/editRepair", method = RequestMethod.POST)
    public String processEditRepair(@Valid @ModelAttribute(REPAIR_FORM) RepairForm repairForm,
                                  BindingResult bindingResult, Model model,
                                  RedirectAttributes redirectAttributes) {
        //If something does not pass our @Valid(ations), then this means that our BindingResult
        //object ".hasErrors()" so we will send the user again to the registration form to correct his mistakes
        if (bindingResult.hasErrors()) {
            //Also we will be adding userForm to RedirectAttributes so that we can keep his valid inputs and reshow them
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult." + REPAIR_FORM, bindingResult);
            redirectAttributes.addFlashAttribute(REPAIR_FORM, repairForm);
            return "redirect:/admin/repair/editRepair";
        }
        try {
            //Trying to build a repair from our RepairForm
            //Full means we include repairID also
            Repair repair = RepairConverter.buildUpdateRepairObject(repairForm);
            //Save the repair
            repairService.save(repair);
            return "redirect:/admin/repair";
        } catch (Exception exception) {
            //if an error occurs show it to the user
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/admin/repair/editRepair";
        }
    }


}
