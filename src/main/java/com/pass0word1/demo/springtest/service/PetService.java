package com.pass0word1.demo.springtest.service;

import com.pass0word1.demo.springtest.dao.PetDao;
import com.pass0word1.demo.springtest.domain.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PetService {

  private PetDao petDao;

  @Autowired
  public void setPetDao(PetDao petDao) {
    this.petDao = petDao;
  }

  public Pet findById(Long id) {
    return this.petDao.findById(id);
  }

  public void deleteById(Long id) {
    this.petDao.deleteById(id);
  }

  public void create(Pet pet) {
    this.petDao.insert(pet);
  }
}
