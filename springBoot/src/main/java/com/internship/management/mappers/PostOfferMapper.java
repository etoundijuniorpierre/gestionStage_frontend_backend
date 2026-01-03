package com.internship.management.mappers;

import com.internship.management.dto.StudentResponseDto;
import com.internship.management.dto.TeacherResponseDto;
import com.internship.management.dto.application.*;
import com.internship.management.dto.postOffer.*;
import com.internship.management.entities.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", imports = { java.util.ArrayList.class })
public interface PostOfferMapper {

    Offer toEntity(OfferRequestDto dto);

    @Mapping(target = "convention", qualifiedByName = "conventionToDto")
    @Mapping(target = "enterprise", qualifiedByName = "enterpriseToDto")
    OfferResponseDto toDto(Offer offer);

    @Named("conventionToDto")
    default ConventionResponseDto mapConvention(Convention c) {

        if (c == null) {
            return null;
        }

        ConventionResponseDto dto = new ConventionResponseDto();
        dto.setState(c.getConventionState().name());
        dto.setHasFile(c.getPdfConvention() != null && c.getPdfConvention().length > 0);

        return dto;
    }

    @Named("enterpriseToDto")
    default EnterpriseResponseDto mapEnterpriseOffer(Enterprise e) {

        if (e == null) {
            return null;
        }

        EnterpriseResponseDto dto = new EnterpriseResponseDto();
        HasLogoDto hasLogoDto = new HasLogoDto();
        hasLogoDto.setHasLogo(e.getLogo() != null);

        dto.setId(e.getId());
        dto.setEmail(e.getEmail());
        dto.setName(e.getName());
        dto.setMatriculation(e.getMatriculation());
        dto.setCountry(e.getCountry());
        dto.setCity(e.getCity());
        dto.setContact(e.getContact());
        dto.setLocation(e.getLocation());
        dto.setHasLogo(hasLogoDto);
        dto.setInPartnership(e.isInPartnership());
        dto.setSectorOfActivity(e.getSectorOfActivity());

        return dto;
    }

    List<OfferResponseDto> toDtoList(List<Offer> offers);

    @Mapping(source = "coverLetter", target = "coverLetter", qualifiedByName = "multipartToBytes")
    @Mapping(source = "cv", target = "cv", qualifiedByName = "multipartToBytes")
    Application toEntity(ApplicationRequestDto dto);

    @Named("multipartToBytes")
    default byte[] map(MultipartFile file) {

        try {
            return file != null ? file.getBytes() : null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert MultipartFile to byte[]", e);
        }
    }

    @Mapping(target = "student", qualifiedByName = "studentToDto")
    @Mapping(target = "enterprise", qualifiedByName = "entToDto")
    @Mapping(target = "hasFiles", source = "application", qualifiedByName = "isFileExist")
    ApplicationResponseDto toDto(Application application);

    @Named("studentToDto")
    default StudentApplicationDto mapApplicationStudent(Student s) {

        StudentApplicationDto dto = new StudentApplicationDto();
        dto.setEmail(s.getEmail());
        dto.setName(s.getName());
        dto.setFirstName(s.getFirstName());

        return dto;
    }

    @Named("isFileExist")
    default HasFilesDto isFileExist(Application application) {

        HasFilesDto hasFile = new HasFilesDto();
        hasFile.setHasCoverLetter(application.getCoverLetter() != null);
        hasFile.setHasCV(application.getCv() != null);

        return hasFile;

    }

    @Named("entToDto")
    default ApplicationEnterpriseDto mapApplicationEnterprise(Enterprise enterprise) {

        ApplicationEnterpriseDto dto = new ApplicationEnterpriseDto();
        dto.setId(enterprise.getId());
        dto.setName(enterprise.getName());

        return dto;
    }

    List<ApplicationResponseDto> toDtoApplicationList(List<Application> applications);

    List<EnterpriseResponseDto> toDtoEnterpriseList(List<Enterprise> enterpriseList);

    @Mapping(target = "offers", source = "enterprise", qualifiedByName = "toMiniOfferResponseDtoList")
    @Mapping(target = "hasLogo", source = "enterprise", qualifiedByName = "toMapHasLogoDto")
    EnterpriseResponseDto toDtoEnterprise(Enterprise enterprise);

    @Named("toMiniOfferResponseDtoList")
    default List<MiniOfferResponseDto> mapEnterpriseToMiniOffer(Enterprise e) {
        if (e == null || e.getOffers() == null) {
            return new ArrayList<>();
        }

        List<MiniOfferResponseDto> list = new ArrayList<>(e.getOffers().size());
        for (Offer offer : e.getOffers()) {
            MiniOfferResponseDto dto = new MiniOfferResponseDto();
            dto.setId(offer.getId());
            dto.setTitle(offer.getTitle());
            dto.setDescription(offer.getDescription());
            dto.setDomain(offer.getDomain());
            dto.setJob(offer.getJob());
            dto.setNumberOfPlaces(offer.getNumberOfPlaces());
            list.add(dto);
        }
        return list;
    }

    @Named("toMapHasLogoDto")
    default HasLogoDto mapEnterpriseToHasLogoDto(Enterprise e) {
        if (e == null) {
            return null;
        }
        HasLogoDto hasLogoDto = new HasLogoDto();
        hasLogoDto.setHasLogo(e.getLogo() != null);
        return hasLogoDto;
    }

    @Mapping(target = "languages", source = "languages")
    StudentResponseDto toDtoStudent(Student student);

    List<StudentResponseDto> toDtoStudentList(List<Student> studentList);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "department", source = "department")
    TeacherResponseDto toDtoTeacher(Teacher teacher);

    List<TeacherResponseDto> toDtoTeacherList(List<Teacher> teacherList);

}
