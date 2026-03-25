package com.eci;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class MathController {

    @GetMapping("/api/fibonacci")
    public List<Long> fibonacci(@RequestParam(name = "terms", defaultValue = "10") int terms) {
        if (terms <= 0) {
            return List.of();
        }
        List<Long> sequence = new ArrayList<>();
        long a = 0;
        long b = 1;
        for (int i = 0; i < terms; i++) {
            sequence.add(a);
            long next = a + b;
            a = b;
            b = next;
        }
        return sequence;
    }
}
