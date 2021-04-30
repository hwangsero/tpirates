package com.example.tpirates.store;

import com.example.tpirates.exception.businessException.EntityNotFoundException;
import com.example.tpirates.store.model.RequestCreateStore;
import com.example.tpirates.store.model.RequestCreateStoreHolidays;
import com.example.tpirates.store.validator.RequestCreateValidator;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation("점포 추가")
    public ResponseEntity createStore(@RequestBody @Valid RequestCreateStore requestCreateStore) {
        return new ResponseEntity(storeService.saveStore(requestCreateStore), HttpStatus.OK);
    }

    @PostMapping("/holiday")
    @ApiOperation("점포 휴무일 등록")
    public ResponseEntity createStoreHoliday(@RequestBody @Valid RequestCreateStoreHolidays requestCreateStoreHolidays) {
        storeService.saveStoreHoliday(requestCreateStoreHolidays);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("")
    @ApiOperation("점포 목록 조회")
    public ResponseEntity getStores() {
        return new ResponseEntity(storeService.getStores(), HttpStatus.OK);
    }

    @GetMapping("/{id}/detail")
    @ApiOperation("점포 상세 조회")
    public ResponseEntity getStoreDetail(@PathVariable("id") Long id) {
        return new ResponseEntity(storeService.getStoreDetail(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/delete")
    @ApiOperation("점포 삭제")
    public ResponseEntity storeDelete(@PathVariable("id") Long id) {
        Store findStore = storeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("store"));
        storeRepository.delete(findStore);
        return new ResponseEntity(HttpStatus.OK);
    }
}
