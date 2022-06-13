package com.yohan.studi.subject;

import com.yohan.studi.subject.SubjectForms.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("api/v1/subjects/")
@Slf4j
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping("")
    public ResponseEntity<HashMap<String, Object>> createSubject(@RequestBody SubjectForm form) {
        HashMap<String, Object> res = new HashMap<>();
        boolean result = subjectService.createSubject(form);
        res.put("success", result);
        return ResponseEntity.ok(res);
    }

    @GetMapping("")
    public ResponseEntity<HashMap<String, Object>> readSubjects() {
        HashMap<String, Object> res = new HashMap<>();
        List<SubjectDto> result = subjectService.readSubjects();
        res.put("data", result);
        return ResponseEntity.ok(res);
    }

    @PatchMapping("{id}")
    public ResponseEntity<HashMap<String, Object>> updateSubject(@PathVariable("id") Integer id, @RequestBody SubjectForm form) {
        HashMap<String, Object> res = new HashMap<>();
        boolean result = subjectService.patchSubject(id, form);
        res.put("success", result);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HashMap<String, Object>> deleteSubject(@PathVariable("id") Integer id) {
        HashMap<String, Object> res = new HashMap<>();
        boolean result = subjectService.deleteSubject(id);
        res.put("success", result);
        return ResponseEntity.ok(res);
    }
}
