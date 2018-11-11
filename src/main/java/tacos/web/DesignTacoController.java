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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.Order;
import tacos.Taco;
import tacos.data.IngredientRepository;
import tacos.data.TacoRepository;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {
	
	private final IngredientRepository ingredientRepo;
	private TacoRepository designRepo;
	
	@ModelAttribute(name = "order")
	public Order order() {
		return new Order();
	}
	
	@ModelAttribute(name = "design")
	public Taco design() {
		return new Taco();
	}
	
	@Autowired
	DesignTacoController(IngredientRepository ingredientRepo, TacoRepository designRepo) {
		this.ingredientRepo = ingredientRepo;
		this.designRepo = designRepo;
	}
	
	@GetMapping
	public String showDesignForm(Model model) {
		
		List<Ingredient> ingredients = new ArrayList<>();
		ingredientRepo.findAll().forEach(i -> ingredients.add(i));
		
		Type[] types = Ingredient.Type.values();
		
		for (Type type : types) {
			model.addAttribute(type.toString().toLowerCase(), filterByType(ingredients, type));
		}
		
//		model.addAttribute("design", design()); // should this use taco() method ?
		
		log.info("showDesignForm model: " + model);
		return "design";
	}
	
	@PostMapping
	public String processDesign(@Valid Taco design, Errors errors, @ModelAttribute Order order) {
		log.info("processDesign errors: " + errors);
		log.info("processDesign design: " + design);
		log.info("processDesign order: " + order);
		
		if (errors.hasErrors()) {
			//TODO: how to do it better? we want to show the user what he has filled
//			showDesignForm(model);
			
			return "design";
		}

		// Save the taco design...
		Taco saved = designRepo.save(design);
		order.addDesign(saved);

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
