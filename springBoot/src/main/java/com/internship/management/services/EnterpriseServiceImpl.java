package com.internship.management.services;

import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Logo;
import com.internship.management.interfaces.EnterpriseService;
import com.internship.management.repositories.EnterpriseRepository;
import com.internship.management.repositories.LogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnterpriseServiceImpl implements EnterpriseService {

    private final EnterpriseRepository enterpriseRepository;
    private final LogoRepository logoRepository;

    @Override
    public Enterprise getByEnterpriseEmail(String email) {
        return enterpriseRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Enterprise Not Found: " + email));
    }

    @Override
    public Enterprise getByEnterpriseId(Long id) {
        return enterpriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enterprise Not Found ID: " + id));
    }

    @Override
    public List<Enterprise> getEnterpriseByPartnershipFalse() {
        return enterpriseRepository.findByInPartnershipFalse();
    }

    @Override
    public List<Enterprise> getEnterpriseByPartnershipTrue() {
        return enterpriseRepository.findByInPartnershipTrue();
    }

    @Override
    public Logo getLogoByEnterprise(Enterprise enterprise) {
        return logoRepository.findByEnterprise(enterprise)
                .orElseThrow(() -> new RuntimeException("Logo not found for enterprise: " + enterprise.getName()));
    }

    @Override
    public void updateLogo(Long enterpriseId, MultipartFile file) throws IOException {
        Enterprise enterprise = getByEnterpriseId(enterpriseId);
        Logo logo = logoRepository.findByEnterprise(enterprise).orElse(new Logo());
        logo.setEnterprise(enterprise);
        logo.setLogo(file.getBytes());
        logo.setContentType(file.getContentType());
        logoRepository.save(logo);
    }
}
