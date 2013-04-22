package org.ei.drishti.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.domain.Response;
import org.ei.drishti.repository.AllSettings;
import org.ei.drishti.repository.FormDataRepository;

import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;
import static org.ei.drishti.AllConstants.DRISHTI_BASE_URL;
import static org.ei.drishti.convertor.FormSubmissionConvertor.toDomain;
import static org.ei.drishti.util.Log.logError;
import static org.ei.drishti.util.Log.logInfo;

public class FormSubmissionSyncService {
    public static final String FORM_SUBMISSIONS_PATH = "/form-submissions";
    private final HTTPAgent httpAgent;
    private final FormDataRepository formDataRepository;
    private AllSettings allSettings;
    private FormSubmissionService formSubmissionService;

    public FormSubmissionSyncService(FormSubmissionService formSubmissionService, HTTPAgent httpAgent, FormDataRepository formDataRepository, AllSettings allSettings) {
        this.formSubmissionService = formSubmissionService;
        this.httpAgent = httpAgent;
        this.formDataRepository = formDataRepository;
        this.allSettings = allSettings;
    }

    public void sync() {
        pushToServer();
        pullFromServer();
    }

    public void pushToServer() {
        List<FormSubmission> pendingFormSubmissions = formDataRepository.getPendingFormSubmissions();
        if (pendingFormSubmissions.isEmpty()) {
            return;
        }
        String jsonPayload = mapToFormSubmissionDTO(pendingFormSubmissions);
        Response<String> response = httpAgent.post(DRISHTI_BASE_URL + FORM_SUBMISSIONS_PATH, jsonPayload);
        if (response.isFailure()) {
            logError(format("Form submissions sync failed. Submissions:  {0}", pendingFormSubmissions));
            return;
        }
        formDataRepository.markFormSubmissionAsSynced(pendingFormSubmissions);
        logInfo(format("Form submissions sync successfully. Submissions:  {0}", pendingFormSubmissions));
    }

    public void pullFromServer() {
        String uri = DRISHTI_BASE_URL + FORM_SUBMISSIONS_PATH + "?anm-id=" + allSettings.fetchRegisteredANM() + "&timestamp=" + allSettings.fetchPreviousFormSyncIndex();
        Response<String> response = httpAgent.fetch(uri);
        if (response.isFailure()) {
            logError(format("Form submissions pull failed."));
            return;
        }
        List<org.ei.drishti.dto.form.FormSubmission> formSubmissions = new Gson().fromJson(response.payload(), new TypeToken<List<org.ei.drishti.dto.form.FormSubmission>>() {
        }.getType());
        formSubmissionService.processSubmissions(toDomain(formSubmissions));
    }

    private String mapToFormSubmissionDTO(List<FormSubmission> pendingFormSubmissions) {
        List<org.ei.drishti.dto.form.FormSubmission> formSubmissions = new ArrayList<org.ei.drishti.dto.form.FormSubmission>();
        for (FormSubmission pendingFormSubmission : pendingFormSubmissions) {
            formSubmissions.add(new org.ei.drishti.dto.form.FormSubmission(allSettings.fetchRegisteredANM(), pendingFormSubmission.instanceId(),
                    pendingFormSubmission.entityId(), pendingFormSubmission.formName(), pendingFormSubmission.instance(), pendingFormSubmission.version()));
        }
        return new Gson().toJson(formSubmissions);
    }
}
