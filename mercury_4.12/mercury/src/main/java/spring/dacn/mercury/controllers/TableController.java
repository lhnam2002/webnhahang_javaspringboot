package spring.dacn.mercury.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import spring.dacn.mercury.entities.DiningTable; // Assuming you have a Table entity
import spring.dacn.mercury.services.DiningTableService; // Assuming you have a TableService

import java.util.List;

@Controller
@RequestMapping("/diningtable")
@RequiredArgsConstructor
public class TableController {

    private final DiningTableService diningTableService;

    @GetMapping
    public String showAllTables(@NotNull Model model,
                                @RequestParam(defaultValue = "1") Integer pageNo,
                                @RequestParam(defaultValue = "6") Integer pageSize,
                                @RequestParam(defaultValue = "id") String sortBy) {

        // Fetch the list of tables and total number of tables from the service
        List<DiningTable> tables = diningTableService.getAllDiningTables(pageNo - 1, pageSize, sortBy);
        int totalItems = diningTableService.getTotalDiningTables(); // Assuming you have a method to get total tables

        // Calculate the total number of pages
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        // Add the data to the model
        model.addAttribute("diningTables", tables);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", pageSize);

        return "table/table_list";
    }
}