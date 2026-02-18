package com.internship.management.interfaces;

import com.internship.management.entities.Enterprise;
import com.internship.management.entities.Logo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EnterpriseService {
    Enterprise getByEnterpriseEmail(String email);

    Enterprise getByEnterpriseId(Long id);

    List<Enterprise> getEnterpriseByPartnershipFalse();

    List<Enterprise> getEnterpriseByPartnershipTrue();

    Logo getLogoByEnterprise(Enterprise enterprise);

    void updateLogo(Long enterpriseId, MultipartFile file) throws IOException;
}
