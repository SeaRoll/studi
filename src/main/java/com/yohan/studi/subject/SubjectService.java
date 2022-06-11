package com.yohan.studi.subject;

import com.yohan.studi.subject.SubjectForms.*;
import java.util.List;

public interface SubjectService {
    /**
     * Creates a subject from form and user from security.
     *
     * @param form form of subject to create
     * @return true if successfully created
     */
    boolean createSubject(SubjectForm form);

    /**
     * Gets all subjects as dtos belonging
     * to a specific user from security
     *
     * @return all subjects
     */
    List<SubjectDto> readSubjects();

    /**
     * Patches a subject from form and checks
     * that it is a correct user.
     *
     * @param id id of subject
     * @param form form of subject to update
     * @return true if successfully updated
     */
    boolean patchSubject(Integer id, SubjectForm form);

    /**
     * Deletes a subject from id and checks
     * that it is a correct user.
     *
     * @param id id of subject
     * @return true if successfully deleted
     */
    boolean deleteSubject(Integer id);
}
