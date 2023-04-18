package com.stc.petlove.controllers;

import com.stc.petlove.entities.DatCho;
import com.stc.petlove.entities.LoaiThuCung;
import com.stc.petlove.exception.ResourceNotFoundException;
import com.stc.petlove.repositories.LoaiThuCungRepository;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loai_thu_cung")
public class LoaiThuCungController {
    private final LoaiThuCungRepository loaiThuCungRepository;

    public LoaiThuCungController(LoaiThuCungRepository loaiThuCungRepository) {
        this.loaiThuCungRepository = loaiThuCungRepository;
    }

    @GetMapping("")
    public ResponseEntity<?> getPage(Pageable pageable) {
        return new ResponseEntity<>(loaiThuCungRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return (loaiThuCungRepository.findById(id)).map(loaiThuCung -> new ResponseEntity<>(loaiThuCung, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody LoaiThuCung chiTietLoaiThuCung) {
        LoaiThuCung loaiThuCung = loaiThuCungRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("không tìm thấy loại thú cưng theo" + id));


        // Cập nhật thông tin loại thú cưng
        if (chiTietLoaiThuCung.getTenLoaiThuCung() != null) {
            loaiThuCung.setTenLoaiThuCung(chiTietLoaiThuCung.getTenLoaiThuCung());
        }
        if (chiTietLoaiThuCung.getMaLoaiThuCung() != null) {
            loaiThuCung.setMaLoaiThuCung(chiTietLoaiThuCung.getMaLoaiThuCung());
        }

        LoaiThuCung updated = loaiThuCungRepository.save(loaiThuCung);
        return ResponseEntity.ok(updated);
    }
    @PostMapping(value = "")
    public ResponseEntity<?> insert(@RequestBody LoaiThuCung newLoaiThuCung) {
        if (loaiThuCungRepository.existsByMaLoaiThuCung(newLoaiThuCung.getMaLoaiThuCung())) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        val savedLoaiThuCung = loaiThuCungRepository.save(newLoaiThuCung);
        return new ResponseEntity<>(savedLoaiThuCung, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (loaiThuCungRepository.existsById(id)) {
            loaiThuCungRepository.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

}
