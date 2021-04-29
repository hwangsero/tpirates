package com.example.tpirates.store;

import com.example.tpirates.store.model.RequestCreateStore;
import com.example.tpirates.store.model.RequestCreateStoreHolidays;
import com.example.tpirates.store.validator.RequestCreateValidator;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {

    private final StoreService storeService;
    private final StoreRepository storeRepository;
    private final RequestCreateValidator requestCreateValidator;

    @InitBinder("requestCreateStore")
    public void initCreateStoreValidator(WebDataBinder binder) {binder.addValidators(requestCreateValidator);}

    @PostMapping("")
    public ResponseEntity createStore(@RequestBody @Valid RequestCreateStore requestCreateStore) {
        return new ResponseEntity(storeService.saveStore(requestCreateStore), HttpStatus.OK);
    }

    @PostMapping("/holiday")
    public ResponseEntity createStoreHoliday(@RequestBody @Valid RequestCreateStoreHolidays requestCreateStoreHolidays) {
        storeService.saveStoreHoliday(requestCreateStoreHolidays);
        return new ResponseEntity(HttpStatus.OK);
    }
}
