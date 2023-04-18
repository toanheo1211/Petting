package com.stc.petlove.controllers;
import com.stc.petlove.entities.DichVu;
import com.stc.petlove.exception.ResourceNotFoundException;
import com.stc.petlove.repositories.DichVuRepository;
import com.stc.petlove.repositories.LoaiThuCungRepository;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/dich-vu")
public class DichVuController {
    private final DichVuRepository dichVuRepository;
    private final LoaiThuCungRepository loaiThuCungRepository;

    public DichVuController(DichVuRepository dichVuRepository, LoaiThuCungRepository loaiThuCungRepository) {
        this.dichVuRepository = dichVuRepository;
        this.loaiThuCungRepository = loaiThuCungRepository;
    }

    @GetMapping("")
    public ResponseEntity<?> getPage(Pageable pageable) {
        return new ResponseEntity<>(dichVuRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return (dichVuRepository.findById(id)).map(dichVu -> new ResponseEntity<>(dichVu, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody DichVu chiTietDichVu) {
        DichVu dichVu = dichVuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("không tìm thấy Dịch vụ theo" + id));


        // Cập nhật thông tin dịch vụ
        if (chiTietDichVu.getMaDichVu() != null) {
            dichVu.setMaDichVu(chiTietDichVu.getMaDichVu());
        }
        if (chiTietDichVu.getTenDichVu() != null) {
            dichVu.setTenDichVu(chiTietDichVu.getTenDichVu());
        }
        if (chiTietDichVu.getGiaDichVus() != null) {
            dichVu.setGiaDichVus(chiTietDichVu.getGiaDichVus());
        }
        if (chiTietDichVu.getNoiDung() != null) {
            dichVu.setNoiDung(chiTietDichVu.getNoiDung());
        }

        DichVu updated = dichVuRepository.save(dichVu);
        return ResponseEntity.ok(updated);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> insert(@RequestBody DichVu newDichVu) {
        val validationOptional = validateDichVu(newDichVu);
        if (validationOptional.isPresent()) {
            return validationOptional.get();
        }

        if (dichVuRepository.existsByMaDichVu(newDichVu.getMaDichVu())) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        val savedDichVu = dichVuRepository.save(newDichVu);
        return new ResponseEntity<>(savedDichVu, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (dichVuRepository.existsById(id)) {
            dichVuRepository.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/ma/{maDichVu}")
    public ResponseEntity<?> getByMaDichVu(@PathVariable String maDichVu) {
        return (dichVuRepository.findByMaDichVu(maDichVu)).map(dichVu -> new ResponseEntity<>(dichVu, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    private Optional<ResponseEntity<?>> validateDichVu(DichVu dichVu) {
        for (val gia: dichVu.getGiaDichVus()) {
            if (!loaiThuCungRepository.existsByMaLoaiThuCung(gia.getLoaiThuCung())) {
                return Optional.of(new ResponseEntity<>(String.format("loai thu cung %s khong ton tai", gia.getLoaiThuCung()), HttpStatus.UNPROCESSABLE_ENTITY));
            }
        }
        return Optional.empty();
    }
}
