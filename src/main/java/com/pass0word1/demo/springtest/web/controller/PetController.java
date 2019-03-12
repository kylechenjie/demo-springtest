package com.pass0word1.demo.springtest.web.controller;

import com.pass0word1.demo.springtest.domain.Pet;
import com.pass0word1.demo.springtest.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PetController {

  private PetService petService;

  @Autowired
  public void setPetService(PetService petService) {
    this.petService = petService;
  }

  @GetMapping(path = "/pet/{id}")
  public ResponseEntity<Pet> getById(@PathVariable("id") Long id) {
    Pet pet = this.petService.findById(id);
    if (pet != null) {
      return new ResponseEntity<>(pet, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping(path = "/pet/{id}")
  public ResponseEntity<Pet> deleteById(@PathVariable("id") Long id) {
    this.petService.deleteById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping(path = "/pet")
  public ResponseEntity<Pet> create(@RequestBody Pet pet) {
    try {
      this.petService.create(pet);
      return new ResponseEntity<>(pet, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }
  }
}
