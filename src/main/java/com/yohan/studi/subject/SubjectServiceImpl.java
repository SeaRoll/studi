package com.yohan.studi.subject;

import com.yohan.studi.exception.BadRequestException;
import com.yohan.studi.exception.ForbiddenException;
import com.yohan.studi.subject.SubjectForms.*;
import com.yohan.studi.user.User;
import com.yohan.studi.user.UserService;
import com.yohan.studi.util.FormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserService userService;
    private final FormValidator formValidator;

    @Override
    public boolean createSubject(SubjectForm form) {
        User user = userService.getUserByContext();
        log.info("Creating subject by user {}", user.getEmail());
        formValidator.validateForm(form);
        subjectRepository.save(new Subject(form.getName(), user));
        log.info("Subject {} saved by user {}", form.getName(), user.getEmail());
        return true;
    }

    @Override
    public List<SubjectDto> readSubjects() {
        User user = userService.getUserByContext();
        log.info("Getting subjects for user {}", user.getEmail());
        List<Subject> subjects = subjectRepository.findAllByUser(user);
        log.info("Returning {} subjects for user {}", subjects.size(), user.getEmail());
        return subjects.stream().map(SubjectDto::new).toList();
    }

    @Override
    public boolean patchSubject(Integer id, SubjectForm form) {
        log.info("Patching subject id {}", id);
        User user = userService.getUserByContext();
        Subject subject = getSubjectById(id);
        formValidator.validateForm(form);
        checkAuthor(user, subject);
        subject.setName(form.getName());
        subjectRepository.save(subject);
        log.info("Patched subject id {}", id);
        return true;
    }

    @Override
    public boolean deleteSubject(Integer id) {
        log.info("Deleting subject {}", id);
        User user = userService.getUserByContext();
        Subject subject = getSubjectById(id);
        checkAuthor(user, subject);
        subjectRepository.delete(subject);
        log.info("Deleted subject {}", id);
        return true;
    }

    /**
     * Checks if user is the author of subject
     * @param user user
     * @param subject subject
     * @throws ForbiddenException throws if user is not the author
     */
    private void checkAuthor(User user, Subject subject) throws ForbiddenException {
        if(!subject.getUser().equals(user)) {
            log.warn("User {} is not the author of subject {}", user.getEmail(), subject.getId());
            throw new ForbiddenException("Not the author");
        }
    }

    private Subject getSubjectById(Integer id) throws BadRequestException {
        return subjectRepository.findById(id).orElseThrow(() -> new BadRequestException(String.format("Subject with id %d does not exist", id)));
    }
}
