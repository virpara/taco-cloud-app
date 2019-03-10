package tacos.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.slf4j.Slf4j;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.Taco;
import tacos.data.IngredientRepository;
import tacos.data.TacoRepository;

@Slf4j
@Controller
@RequestMapping("/design")
public class DesignTacoController {
	
	private final IngredientRepository ingredientRepo;
	private final TacoRepository tacoRepo;
	
	@Autowired
	DesignTacoController(IngredientRepository ingredientRepo, TacoRepository tacoRepo) {
		this.ingredientRepo = ingredientRepo;
		this.tacoRepo = tacoRepo;
	}
	
	@GetMapping
	public String showDesignForm(Model model) {
		
		List<Ingredient> ingredients = new ArrayList<>();
		ingredientRepo.findAll().forEach(i -> ingredients.add(i));
		
		Type[] types = Ingredient.Type.values();
		
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
		}
		
		model.addAttribute("design", new Taco());
		
		log.info(model.toString());
		return "design";
	}
	
	@PostMapping
	public String processDesign(Model model, @Valid Taco design, Errors errors) {
		log.info("errors: " + errors);
		if (errors.hasErrors()) {
			//TODO: how to do it better? we want to show the user what he has filled
//			showDesignForm(model);
			model.addAttribute("design", design);
			return "design";
		}
		// Save the taco design...
		tacoRepo.save(design);
		log.info("Processing design: " + design);
		return "redirect:/orders/current";
	}

	private List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
		List<Ingredient> typeList = new ArrayList<>();
		
		for (Ingredient i : ingredients) {
			if (i.getType() == type) {
				typeList.add(i);
			}
		}
		
		return typeList;
	}
}
