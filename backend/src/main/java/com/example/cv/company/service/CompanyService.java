package com.example.cv.company.service;

import com.example.cv.common.api.ApiException;
import com.example.cv.common.model.AuditInfo;
import com.example.cv.common.security.CurrentUser;
import com.example.cv.common.service.PaginationService;
import com.example.cv.company.document.CompanyDocument;
import com.example.cv.company.dto.CompanyRequests;
import com.example.cv.company.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CompanyService {
    private final CompanyRepository repository;

    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }

    public CompanyDocument create(CompanyRequests.CreateRequest request, CurrentUser actor) {
        CompanyDocument company = new CompanyDocument();
        company.setName(request.name());
        company.setAddress(request.address());
        company.setDescription(request.description());
        company.setLogo(request.logo());
        company.setCreatedBy(audit(actor));
        return repository.save(company);
    }

    public Map<String, Object> findAll(Integer current, Integer pageSize, String name, String address) {
        var page = PaginationService.page(repository.findAllByIsDeletedFalse(), current, pageSize, company ->
                (name == null || name.isBlank() || contains(company.getName(), name))
                        && (address == null || address.isBlank() || contains(company.getAddress(), address)));
        return page.asMap();
    }

    public CompanyDocument findOne(String id) {
        return repository.findById(id).filter(company -> !company.isDeleted())
                .orElseThrow(() -> ApiException.notFound("Company not found"));
    }

    public CompanyDocument update(String id, CompanyRequests.UpdateRequest request, CurrentUser actor) {
        CompanyDocument company = findOne(id);
        if (request.name() != null) company.setName(request.name());
        if (request.address() != null) company.setAddress(request.address());
        if (request.description() != null) company.setDescription(request.description());
        if (request.logo() != null) company.setLogo(request.logo());
        company.setUpdatedBy(audit(actor));
        return repository.save(company);
    }

    public Map<String, Object> remove(String id, CurrentUser actor) {
        CompanyDocument company = findOne(id);
        company.setDeletedBy(audit(actor));
        company.setDeleted(true);
        repository.save(company);
        return Map.of("acknowledged", true, "deletedCount", 1);
    }

    private AuditInfo audit(CurrentUser actor) {
        return actor == null ? null : new AuditInfo(actor.id(), actor.email());
    }

    private boolean contains(String value, String search) {
        return value != null && value.toLowerCase().contains(search.toLowerCase());
    }
}
