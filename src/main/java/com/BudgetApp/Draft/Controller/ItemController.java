package com.BudgetApp.Draft.Controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.BudgetApp.Draft.Service.ItemService;
import com.BudgetApp.Draft.model.ItemRequest;
import com.BudgetApp.Draft.model.ItemSumResponse;

@RestController
@RequestMapping("/budgetApp")
public class ItemController {
@Autowired
private ItemService itemService;

@PostMapping("/addItem")
public String insertItem(@RequestBody ItemRequest itemRequest) throws IOException, GeneralSecurityException {

	return itemService.insertRows(itemRequest);
}
@PostMapping("/addPerticuler/{type}/{name}")
public String insertPerticuler(@PathVariable String type,@PathVariable String name) throws IOException, GeneralSecurityException {

	return itemService.addPerticular(type, name);
}

@GetMapping("/{filterBy}/{perticuler}")
public ItemSumResponse getItemsBy(@PathVariable String filterBy, @RequestParam String from, @RequestParam String to,
		@PathVariable String perticuler) throws IOException, GeneralSecurityException {
	perticuler=URLDecoder.decode(perticuler,"UTF-8".toString());
	return itemService.getItemsByFilter(perticuler, from, to, filterBy);
}

@GetMapping("/getPerticulerMap")
public Map<String, List<Object>> getPerticulerMap() throws IOException, GeneralSecurityException {
	return itemService.getPerticularMap();
}
}
