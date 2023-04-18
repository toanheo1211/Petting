package com.stc.petlove.controllers;

import com.stc.petlove.entities.TaiKhoan;
import com.stc.petlove.exception.BadRequestException;
import com.stc.petlove.exception.ResourceNotFoundException;
import com.stc.petlove.repositories.TaiKhoanRepository;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/taikhoan")
public class TaiKhoanController {
    private final TaiKhoanRepository taiKhoanRepository;

    public TaiKhoanController(TaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    @GetMapping("")
    public ResponseEntity<?> getPage(Pageable pageable) {
        return new ResponseEntity<>(taiKhoanRepository.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return (taiKhoanRepository.findById(id)).map(taiKhoan -> new ResponseEntity<>(taiKhoan, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable String id,
                                           @RequestBody TaiKhoan chiTietTaiKhoan) {
        TaiKhoan taiKhoan = taiKhoanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay id"+ id));

        // Kiểm tra email
        if (!taiKhoan.getEmail().equals(chiTietTaiKhoan.getEmail())) {
            if (taiKhoanRepository.existsByEmail(chiTietTaiKhoan.getEmail())) {
                throw new BadRequestException("Email đã được sử dụng.");
            }
        }

        // Cập nhật thông tin tài khoản
        taiKhoan.setName(chiTietTaiKhoan.getName());
        taiKhoan.setEmail(chiTietTaiKhoan.getEmail());
        taiKhoan.setPassword(chiTietTaiKhoan.getPassword());

        TaiKhoan updatedTaiKhoan = taiKhoanRepository.save(taiKhoan);
        return ResponseEntity.ok(updatedTaiKhoan);
    }

    @PostMapping(value = "")
    public ResponseEntity<?> insert(@RequestBody TaiKhoan newTaiKhoan) {
        if (taiKhoanRepository.existsByEmail(newTaiKhoan.getEmail())) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        val savedTaiKhoan = taiKhoanRepository.save(newTaiKhoan);
        return new ResponseEntity<>(savedTaiKhoan, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (taiKhoanRepository.existsById(id)) {
            taiKhoanRepository.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

}