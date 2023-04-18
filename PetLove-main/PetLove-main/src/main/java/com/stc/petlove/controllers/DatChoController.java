package com.stc.petlove.controllers;

import com.stc.petlove.entities.DatCho;
import com.stc.petlove.exception.ResourceNotFoundException;
import com.stc.petlove.repositories.DatChoRepository;
import com.stc.petlove.repositories.DichVuRepository;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/dat-cho")
public class DatChoController {
    private final DatChoRepository datChoRepository;
    private final DichVuRepository dichVuRepository;

    public DatChoController(DatChoRepository datChoRepository, DichVuRepository dichVuRepository) {
        this.datChoRepository = datChoRepository;
        this.dichVuRepository = dichVuRepository;
    }

    @GetMapping("")
    public ResponseEntity<?> getPage(Pageable pageable) {
        return new ResponseEntity<>(datChoRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return (datChoRepository.findById(id)).map(datCho -> new ResponseEntity<>(datCho, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody DatCho chiTietDatCho) {
        DatCho datCho = datChoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("không tìm thấy chỗ theo" + id));


        // Cập nhật thông tin dat cho
        if (chiTietDatCho.getThongTinDatChos() != null) {
            datCho.setThongTinDatChos(chiTietDatCho.getThongTinDatChos());
        }
        if (chiTietDatCho.getTrangThaiDatCho() != null) {
            datCho.setTrangThaiDatCho(chiTietDatCho.getTrangThaiDatCho());
        }
        if (chiTietDatCho.getEmail() != null) {
            datCho.setEmail(chiTietDatCho.getEmail());
        }
        if (chiTietDatCho.getCanDan() != null) {
            datCho.setCanDan(chiTietDatCho.getCanDan());
        }
        if (chiTietDatCho.getThoiGian() != null) {
            datCho.setThoiGian(chiTietDatCho.getThoiGian());
        }
        DatCho updated = datChoRepository.save(datCho);
        return ResponseEntity.ok(updated);
    }


    @PostMapping(value = "")
    public ResponseEntity<?> insert(@RequestBody DatCho newDatCho) {
        val validationOptional = validateDatCho(newDatCho);
        if (validationOptional.isPresent()) {
            return validationOptional.get();
        }

        val savedDatCho = datChoRepository.save(newDatCho);
        return new ResponseEntity<>(savedDatCho, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (datChoRepository.existsById(id)) {
            datChoRepository.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    private Optional<ResponseEntity<?>> validateDatCho(DatCho datCho) {
        for (val thongTin: datCho.getThongTinDatChos()) {
            if (!dichVuRepository.existsByMaDichVu(thongTin.getDichVu())) {
                return Optional.of(new ResponseEntity<>(String.format("dich vu %s khong ton tai", thongTin.getDichVu()), HttpStatus.UNPROCESSABLE_ENTITY));
            }
        }
        return Optional.empty();
    }
}
